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

package org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.data.pipeline.core.exception.PipelineInternalException;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.XuguBinlogContext;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.event.PlaceholderBinlogEvent;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.event.XuguBaseBinlogEvent;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.event.query.XuguQueryBinlogEvent;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.event.rows.XuguDeleteRowsBinlogEvent;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.event.rows.XuguUpdateRowsBinlogEvent;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.event.rows.XuguWriteRowsBinlogEvent;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.event.transaction.XuguXidBinlogEvent;
import org.apache.shardingsphere.db.protocol.constant.CommonConstants;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguBinlogEventType;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.XuguBinlogEventHeader;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.management.XuguBinlogFormatDescriptionEventPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.management.XuguBinlogRotateEventPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.XuguBinlogRowsEventPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.XuguBinlogTableMapEventPacket;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * MySQL binlog event packet decoder.
 */
@Slf4j
public final class XuguBinlogEventPacketDecoder extends ByteToMessageDecoder {
    
    private static final String TX_BEGIN_SQL = "BEGIN";
    
    private final XuguBinlogContext binlogContext;
    
    private final boolean decodeWithTX;
    
    private List<XuguBaseBinlogEvent> records = new LinkedList<>();
    
    public XuguBinlogEventPacketDecoder(final int checksumLength, final Map<Long, XuguBinlogTableMapEventPacket> tableMap, final boolean decodeWithTX) {
        this.decodeWithTX = decodeWithTX;
        binlogContext = new XuguBinlogContext(checksumLength, tableMap);
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        while (in.readableBytes() >= 1 + XuguBinlogEventHeader.MYSQL_BINLOG_EVENT_HEADER_LENGTH) {
            in.markReaderIndex();
            XuguPacketPayload payload = new XuguPacketPayload(in, ctx.channel().attr(CommonConstants.CHARSET_ATTRIBUTE_KEY).get());
            checkPayload(payload);
            XuguBinlogEventHeader binlogEventHeader = new XuguBinlogEventHeader(payload, binlogContext.getChecksumLength());
            if (!checkEventIntegrity(in, binlogEventHeader)) {
                return;
            }
            Optional<XuguBaseBinlogEvent> binlogEvent = decodeEvent(binlogEventHeader, payload);
            if (!binlogEvent.isPresent()) {
                skipChecksum(binlogEventHeader.getEventType(), in);
                return;
            }
            if (binlogEvent.get() instanceof PlaceholderBinlogEvent) {
                out.add(binlogEvent.get());
                skipChecksum(binlogEventHeader.getEventType(), in);
                return;
            }
            if (decodeWithTX) {
                processEventWithTX(binlogEvent.get(), out);
            } else {
                processEventIgnoreTX(binlogEvent.get(), out);
            }
            skipChecksum(binlogEventHeader.getEventType(), in);
        }
    }
    
    private void checkPayload(final XuguPacketPayload payload) {
        int statusCode = payload.readInt1();
        if (255 == statusCode) {
            int errorNo = payload.readInt2();
            payload.skipReserved(1);
            String sqlState = payload.readStringFix(5);
            throw new PipelineInternalException("Decode binlog event failed, errorCode: %d, sqlState: %s, errorMessage: %s", errorNo, sqlState, payload.readStringEOF());
        }
        if (0 != statusCode) {
            log.debug("Illegal binlog status code {}, remaining packet \n{}", statusCode, readRemainPacket(payload));
        }
    }
    
    private String readRemainPacket(final XuguPacketPayload payload) {
        return ByteBufUtil.hexDump(payload.readStringFixByBytes(payload.getByteBuf().readableBytes()));
    }
    
    private boolean checkEventIntegrity(final ByteBuf in, final XuguBinlogEventHeader binlogEventHeader) {
        if (in.readableBytes() < binlogEventHeader.getEventSize() - XuguBinlogEventHeader.MYSQL_BINLOG_EVENT_HEADER_LENGTH) {
            log.debug("the event body is not complete, event size={}, readable bytes={}", binlogEventHeader.getEventSize(), in.readableBytes());
            in.resetReaderIndex();
            return false;
        }
        return true;
    }
    
    private void processEventWithTX(final XuguBaseBinlogEvent binlogEvent, final List<Object> out) {
        if (binlogEvent instanceof XuguQueryBinlogEvent) {
            XuguQueryBinlogEvent queryEvent = (XuguQueryBinlogEvent) binlogEvent;
            if (TX_BEGIN_SQL.equals(queryEvent.getSql())) {
                records = new LinkedList<>();
            } else {
                out.add(binlogEvent);
            }
        } else if (binlogEvent instanceof XuguXidBinlogEvent) {
            records.add(binlogEvent);
            out.add(records);
        } else {
            records.add(binlogEvent);
        }
    }
    
    private void processEventIgnoreTX(final XuguBaseBinlogEvent binlogEvent, final List<Object> out) {
        if (binlogEvent instanceof XuguQueryBinlogEvent) {
            XuguQueryBinlogEvent queryEvent = (XuguQueryBinlogEvent) binlogEvent;
            if (TX_BEGIN_SQL.equals(queryEvent.getSql())) {
                return;
            }
        }
        out.add(binlogEvent);
    }
    
    private Optional<XuguBaseBinlogEvent> decodeEvent(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        switch (XuguBinlogEventType.valueOf(binlogEventHeader.getEventType()).orElse(XuguBinlogEventType.UNKNOWN_EVENT)) {
            case ROTATE_EVENT:
                decodeRotateEvent(binlogEventHeader, payload);
                return Optional.empty();
            case FORMAT_DESCRIPTION_EVENT:
                decodeFormatDescriptionEvent(binlogEventHeader, payload);
                return Optional.empty();
            case TABLE_MAP_EVENT:
                decodeTableMapEvent(binlogEventHeader, payload);
                return Optional.empty();
            case WRITE_ROWS_EVENT_V1:
            case WRITE_ROWS_EVENT_V2:
                return Optional.of(decodeWriteRowsEventV2(binlogEventHeader, payload));
            case UPDATE_ROWS_EVENT_V1:
            case UPDATE_ROWS_EVENT_V2:
                return Optional.of(decodeUpdateRowsEventV2(binlogEventHeader, payload));
            case DELETE_ROWS_EVENT_V1:
            case DELETE_ROWS_EVENT_V2:
                return Optional.of(decodeDeleteRowsEventV2(binlogEventHeader, payload));
            case QUERY_EVENT:
                return Optional.of(decodeQueryEvent(binlogEventHeader, payload));
            case XID_EVENT:
                return Optional.of(decodeXidEvent(binlogEventHeader, payload));
            default:
                return Optional.of(decodePlaceholderEvent(binlogEventHeader, payload));
        }
    }
    
    private void decodeRotateEvent(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        XuguBinlogRotateEventPacket packet = new XuguBinlogRotateEventPacket(binlogEventHeader, payload);
        binlogContext.setFileName(packet.getNextBinlogName());
    }
    
    private void decodeFormatDescriptionEvent(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        XuguBinlogFormatDescriptionEventPacket packet = new XuguBinlogFormatDescriptionEventPacket(binlogEventHeader, payload);
        // MySQL MGR checksum length is 0, but the event ends up with 4 extra bytes, need to skip them.
        int readableBytes = payload.getByteBuf().readableBytes();
        if (binlogEventHeader.getChecksumLength() <= 0 && readableBytes > 0) {
            if (readableBytes != 4) {
                log.warn("the format description event has extra bytes, readable bytes length={}, binlogEventHeader={}, formatDescriptionEvent={}", readableBytes, binlogEventHeader, packet);
            }
            payload.getByteBuf().skipBytes(readableBytes);
        }
    }
    
    private void decodeTableMapEvent(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        binlogContext.putTableMapEvent(new XuguBinlogTableMapEventPacket(binlogEventHeader, payload));
    }
    
    private XuguWriteRowsBinlogEvent decodeWriteRowsEventV2(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        XuguBinlogRowsEventPacket packet = new XuguBinlogRowsEventPacket(binlogEventHeader, payload);
        XuguBinlogTableMapEventPacket tableMapEventPacket = binlogContext.getTableMapEvent(packet.getTableId());
        packet.readRows(tableMapEventPacket, payload);
        return new XuguWriteRowsBinlogEvent(binlogContext.getFileName(),
                binlogEventHeader.getLogPos(), binlogEventHeader.getTimestamp(), tableMapEventPacket.getSchemaName(), tableMapEventPacket.getTableName(), packet.getRows());
    }
    
    private XuguUpdateRowsBinlogEvent decodeUpdateRowsEventV2(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        XuguBinlogRowsEventPacket packet = new XuguBinlogRowsEventPacket(binlogEventHeader, payload);
        XuguBinlogTableMapEventPacket tableMapEventPacket = binlogContext.getTableMapEvent(packet.getTableId());
        packet.readRows(tableMapEventPacket, payload);
        return new XuguUpdateRowsBinlogEvent(binlogContext.getFileName(),
                binlogEventHeader.getLogPos(), binlogEventHeader.getTimestamp(), tableMapEventPacket.getSchemaName(), tableMapEventPacket.getTableName(), packet.getRows(), packet.getRows2());
    }
    
    private XuguDeleteRowsBinlogEvent decodeDeleteRowsEventV2(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        XuguBinlogRowsEventPacket packet = new XuguBinlogRowsEventPacket(binlogEventHeader, payload);
        XuguBinlogTableMapEventPacket tableMapEventPacket = binlogContext.getTableMapEvent(packet.getTableId());
        packet.readRows(tableMapEventPacket, payload);
        return new XuguDeleteRowsBinlogEvent(binlogContext.getFileName(),
                binlogEventHeader.getLogPos(), binlogEventHeader.getTimestamp(), tableMapEventPacket.getSchemaName(), tableMapEventPacket.getTableName(), packet.getRows());
    }
    
    private PlaceholderBinlogEvent decodePlaceholderEvent(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        PlaceholderBinlogEvent result = new PlaceholderBinlogEvent(binlogContext.getFileName(), binlogEventHeader.getLogPos(), binlogEventHeader.getTimestamp());
        int remainDataLength = binlogEventHeader.getEventSize() + 1 - binlogEventHeader.getChecksumLength() - payload.getByteBuf().readerIndex();
        if (remainDataLength > 0) {
            payload.skipReserved(remainDataLength);
        }
        return result;
    }
    
    private XuguQueryBinlogEvent decodeQueryEvent(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        int threadId = payload.readInt4();
        int executionTime = payload.readInt4();
        payload.skipReserved(1);
        int errorCode = payload.readInt2();
        payload.skipReserved(payload.readInt2());
        String databaseName = payload.readStringNul();
        String sql = payload.readStringFix(payload.getByteBuf().readableBytes() - binlogEventHeader.getChecksumLength());
        return new XuguQueryBinlogEvent(binlogContext.getFileName(), binlogEventHeader.getLogPos(), binlogEventHeader.getTimestamp(), threadId, executionTime, errorCode, databaseName, sql);
    }
    
    private XuguXidBinlogEvent decodeXidEvent(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        return new XuguXidBinlogEvent(binlogContext.getFileName(), binlogEventHeader.getLogPos(), binlogEventHeader.getTimestamp(), payload.readInt8());
    }
    
    private void skipChecksum(final int eventType, final ByteBuf in) {
        if (0 < binlogContext.getChecksumLength() && XuguBinlogEventType.FORMAT_DESCRIPTION_EVENT.getValue() != eventType) {
            in.skipBytes(binlogContext.getChecksumLength());
        }
    }
}
