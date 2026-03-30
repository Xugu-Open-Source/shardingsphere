/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.data.pipeline.core.exception.PipelineInternalException;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.event.PlaceholderBinlogEvent;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.event.XuguBaseBinlogEvent;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.client.netty.XuguBinlogEventPacketDecoder;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.client.netty.XuguCommandPacketDecoder;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.client.netty.XuguNegotiateHandler;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.client.netty.XuguNegotiatePackageDecoder;
import org.apache.shardingsphere.db.protocol.codec.PacketCodec;
import org.apache.shardingsphere.db.protocol.netty.ChannelAttrInitializer;
import org.apache.shardingsphere.db.protocol.xugu.codec.XuguPacketCodecEngine;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguConstants;
import org.apache.shardingsphere.db.protocol.xugu.netty.XuguSequenceIdInboundHandler;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.binlog.XuguComBinlogDumpCommandPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.binlog.XuguComRegisterSlaveCommandPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.text.query.XuguComQueryPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguErrPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguOKPacket;
import org.apache.shardingsphere.infra.exception.generic.UnsupportedSQLOperationException;
import org.apache.shardingsphere.infra.util.json.JsonUtils;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MySQL binlog client.
 */
@RequiredArgsConstructor
@Slf4j
public final class XuguBinlogClient {
    
    private final ConnectInfo connectInfo;
    
    private final boolean decodeWithTX;
    
    private final ArrayBlockingQueue<List<XuguBaseBinlogEvent>> blockingEventQueue = new ArrayBlockingQueue<>(2500);
    
    private EventLoopGroup eventLoopGroup;
    
    private Channel channel;
    
    private Promise<Object> responseCallback;
    
    private XuguServerVersion serverVersion;
    
    private volatile boolean running = true;
    
    /**
     * Connect to MySQL.
     */
    public synchronized void connect() {
        eventLoopGroup = new NioEventLoopGroup(1);
        responseCallback = new DefaultPromise<>(eventLoopGroup.next());
        channel = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.AUTO_READ, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    
                    @Override
                    protected void initChannel(final SocketChannel socketChannel) {
                        socketChannel.pipeline().addLast(new ChannelAttrInitializer());
                        socketChannel.pipeline().addLast(new PacketCodec(new XuguPacketCodecEngine()));
                        socketChannel.pipeline().addLast(new XuguSequenceIdInboundHandler(socketChannel));
                        socketChannel.pipeline().addLast(new XuguNegotiatePackageDecoder());
                        socketChannel.pipeline().addLast(new XuguCommandPacketDecoder());
                        socketChannel.pipeline().addLast(new XuguNegotiateHandler(connectInfo.getUsername(), connectInfo.getPassword(), responseCallback));
                        socketChannel.pipeline().addLast(new XuguCommandResponseHandler());
                    }
                }).connect(connectInfo.getHost(), connectInfo.getPort()).channel();
        serverVersion = waitExpectedResponse(XuguServerVersion.class).orElse(null);
        running = true;
    }
    
    /**
     * Execute command.
     *
     * @param queryString query string
     * @return true if execute successfully, otherwise false
     */
    public synchronized boolean execute(final String queryString) {
        responseCallback = new DefaultPromise<>(eventLoopGroup.next());
        XuguComQueryPacket comQueryPacket = new XuguComQueryPacket(queryString);
        resetSequenceID();
        channel.writeAndFlush(comQueryPacket);
        return waitExpectedResponse(XuguOKPacket.class).isPresent();
    }
    
    /**
     * Execute update.
     *
     * @param queryString query string
     * @return affected rows
     * @throws PipelineInternalException if could not get MySQL OK packet
     */
    public synchronized int executeUpdate(final String queryString) {
        responseCallback = new DefaultPromise<>(eventLoopGroup.next());
        XuguComQueryPacket comQueryPacket = new XuguComQueryPacket(queryString);
        resetSequenceID();
        channel.writeAndFlush(comQueryPacket);
        Optional<XuguOKPacket> packet = waitExpectedResponse(XuguOKPacket.class);
        if (!packet.isPresent()) {
            throw new PipelineInternalException("Could not get Xugu OK packet");
        }
        return (int) packet.get().getAffectedRows();
    }
    
    /**
     * Execute query.
     *
     * @param queryString query string
     * @return result set
     * @throws PipelineInternalException if getting MySQL packet failed
     */
    public synchronized InternalResultSet executeQuery(final String queryString) {
        responseCallback = new DefaultPromise<>(eventLoopGroup.next());
        XuguComQueryPacket comQueryPacket = new XuguComQueryPacket(queryString);
        resetSequenceID();
        channel.writeAndFlush(comQueryPacket);
        Optional<InternalResultSet> result = waitExpectedResponse(InternalResultSet.class);
        if (!result.isPresent()) {
            throw new PipelineInternalException("Could not get Xugu FieldCount/ColumnDefinition/TextResultSetRow packet");
        }
        return result.get();
    }
    
    /**
     * Start dump binlog.
     *
     * @param binlogFileName binlog file name
     * @param binlogPosition binlog position
     */
    public synchronized void subscribe(final String binlogFileName, final long binlogPosition) {
        initDumpConnectSession();
        registerSlave();
        dumpBinlog(binlogFileName, binlogPosition, queryChecksumLength());
        log.info("subscribe binlog file: {}, position: {}", binlogFileName, binlogPosition);
    }
    
    private void initDumpConnectSession() {
        if (serverVersion.greaterThanOrEqualTo(5, 6, 0)) {
            execute("SET @MASTER_BINLOG_CHECKSUM= @@GLOBAL.BINLOG_CHECKSUM");
        }
    }
    
    private void registerSlave() {
        responseCallback = new DefaultPromise<>(eventLoopGroup.next());
        InetSocketAddress localAddress = (InetSocketAddress) channel.localAddress();
        XuguComRegisterSlaveCommandPacket packet = new XuguComRegisterSlaveCommandPacket(
                connectInfo.getServerId(), localAddress.getHostName(), connectInfo.getUsername(), connectInfo.getPassword(), localAddress.getPort());
        resetSequenceID();
        channel.writeAndFlush(packet);
        waitExpectedResponse(XuguOKPacket.class);
    }
    
    private int queryChecksumLength() {
        if (!serverVersion.greaterThanOrEqualTo(5, 6, 0)) {
            return 0;
        }
        InternalResultSet resultSet = executeQuery("SELECT @@GLOBAL.BINLOG_CHECKSUM");
        String checksumType = resultSet.getFieldValues().get(0).getData().iterator().next().toString();
        switch (checksumType.toUpperCase()) {
            case "NONE":
                return 0;
            case "CRC32":
                return 4;
            default:
                throw new UnsupportedSQLOperationException(checksumType);
        }
    }
    
    private void dumpBinlog(final String binlogFileName, final long binlogPosition, final int checksumLength) {
        responseCallback = null;
        channel.pipeline().remove(XuguCommandPacketDecoder.class);
        channel.pipeline().remove(XuguCommandResponseHandler.class);
        String tableKey = String.join(":", connectInfo.getHost(), String.valueOf(connectInfo.getPort()));
        channel.pipeline().addLast(new XuguBinlogEventPacketDecoder(checksumLength, GlobalTableMapEventMapping.getTableMapEventMap(tableKey), decodeWithTX));
        channel.pipeline().addLast(new XuguBinlogEventHandler(new PlaceholderBinlogEvent(binlogFileName, binlogPosition, 0L)));
        resetSequenceID();
        channel.writeAndFlush(new XuguComBinlogDumpCommandPacket((int) binlogPosition, connectInfo.getServerId(), binlogFileName));
    }
    
    private void resetSequenceID() {
        channel.attr(XuguConstants.SEQUENCE_ID_ATTRIBUTE_KEY).get().set(0);
    }
    
    /**
     * Poll binlog event.
     *
     * @return binlog event
     */
    public synchronized List<XuguBaseBinlogEvent> poll() {
        if (!running) {
            return Collections.emptyList();
        }
        try {
            List<XuguBaseBinlogEvent> result = blockingEventQueue.poll(100L, TimeUnit.MILLISECONDS);
            return null == result ? Collections.emptyList() : result;
        } catch (final InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return Collections.emptyList();
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T> Optional<T> waitExpectedResponse(final Class<T> type) {
        try {
            Object response = responseCallback.get(5L, TimeUnit.SECONDS);
            if (null == response) {
                return Optional.empty();
            }
            if (type.equals(response.getClass())) {
                return Optional.of((T) response);
            }
            if (response instanceof XuguErrPacket) {
                throw new PipelineInternalException(((XuguErrPacket) response).getErrorMessage());
            }
            throw new PipelineInternalException("unexpected response type");
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new PipelineInternalException(ex);
        } catch (final ExecutionException | TimeoutException ex) {
            throw new PipelineInternalException(ex);
        }
    }
    
    /**
     * Close netty channel.
     *
     * @return channel future
     */
    public Optional<ChannelFuture> closeChannel() {
        if (null == channel || !channel.isOpen()) {
            return Optional.empty();
        }
        running = false;
        ChannelFuture future = channel.close();
        if (null != eventLoopGroup) {
            eventLoopGroup.shutdownGracefully();
        }
        return Optional.of(future);
    }
    
    private final class XuguCommandResponseHandler extends ChannelInboundHandlerAdapter {
        
        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
            if (null != responseCallback) {
                responseCallback.setSuccess(msg);
            }
        }
        
        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
            if (null != responseCallback) {
                responseCallback.setFailure(cause);
                log.error("XuguCommandResponseHandler protocol resolution error", cause);
            }
        }
    }
    
    private final class XuguBinlogEventHandler extends ChannelInboundHandlerAdapter {
        
        private final AtomicReference<XuguBaseBinlogEvent> lastBinlogEvent;
        
        private final AtomicBoolean reconnectRequested = new AtomicBoolean(false);
        
        XuguBinlogEventHandler(final XuguBaseBinlogEvent lastBinlogEvent) {
            this.lastBinlogEvent = new AtomicReference<>(lastBinlogEvent);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
            if (!running) {
                return;
            }
            if (msg instanceof List) {
                List<XuguBaseBinlogEvent> records = (List<XuguBaseBinlogEvent>) msg;
                if (records.isEmpty()) {
                    log.warn("The records is empty");
                    return;
                }
                lastBinlogEvent.set(records.get(records.size() - 1));
                blockingEventQueue.put(records);
                return;
            }
            if (msg instanceof XuguBaseBinlogEvent) {
                lastBinlogEvent.set((XuguBaseBinlogEvent) msg);
                blockingEventQueue.put(Collections.singletonList(lastBinlogEvent.get()));
            }
        }
        
        @Override
        public void channelInactive(final ChannelHandlerContext ctx) {
            log.warn("Xugu binlog channel inactive, channel: {}, running: {}", ctx.channel(), running);
            if (!running) {
                return;
            }
            tryReconnect();
        }
        
        @Override
        public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
            log.error("XuguBinlogEventHandler protocol resolution error, channel: {}, lastBinlogEvent: {}", ctx.channel(), JsonUtils.toJsonString(lastBinlogEvent.get()), cause);
        }
        
        private void tryReconnect() {
            if (reconnectRequested.compareAndSet(false, true)) {
                CompletableFuture.runAsync(this::reconnect).whenComplete((result, ex) -> reconnectRequested.set(false));
            }
        }
        
        @SneakyThrows(InterruptedException.class)
        private synchronized void reconnect() {
            for (int reconnectTimes = 0; reconnectTimes < 3; reconnectTimes++) {
                try {
                    connect();
                    log.info("Reconnect times {}", reconnectTimes);
                    subscribe(lastBinlogEvent.get().getFileName(), lastBinlogEvent.get().getPosition());
                    break;
                    // CHECKSTYLE:OFF
                } catch (final RuntimeException ex) {
                    // CHECKSTYLE:ON
                    log.error("Reconnect failed, reconnect times: {}, lastBinlogEvent: {}", reconnectTimes, JsonUtils.toJsonString(lastBinlogEvent.get()), ex);
                    this.wait(1000L << reconnectTimes);
                }
            }
        }
    }
}
