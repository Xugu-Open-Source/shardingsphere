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

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.Promise;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.shardingsphere.data.pipeline.core.exception.PipelineInternalException;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.client.PasswordEncryption;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.client.XuguServerVersion;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguAuthenticationMethod;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguAuthenticationPlugin;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguCapabilityFlag;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguErrPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguOKPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguAuthMoreDataPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguAuthSwitchRequestPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguAuthSwitchResponsePacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguHandshakePacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguHandshakeResponse41Packet;

import java.security.NoSuchAlgorithmException;

/**
 * MySQL negotiate handler.
 */
@RequiredArgsConstructor
public final class XuguNegotiateHandler extends ChannelInboundHandlerAdapter {
    
    private static final int MAX_PACKET_SIZE = 1 << 24;
    
    private static final int CHARACTER_SET = 33;
    
    private static final int REQUEST_PUBLIC_KEY = 2;
    
    private static final int PERFORM_FULL_AUTHENTICATION = 4;
    
    private final String username;
    
    private final String password;
    
    private final Promise<Object> authResultCallback;
    
    private XuguServerVersion serverVersion;
    
    private byte[] seed;
    
    private boolean publicKeyRequested;
    
    @SneakyThrows(NoSuchAlgorithmException.class)
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        if (msg instanceof XuguHandshakePacket) {
            XuguHandshakePacket handshake = (XuguHandshakePacket) msg;
            XuguHandshakeResponse41Packet handshakeResponsePacket = new XuguHandshakeResponse41Packet(MAX_PACKET_SIZE, CHARACTER_SET, username);
            handshakeResponsePacket.setAuthResponse(generateAuthResponse(handshake.getAuthPluginData().getAuthenticationPluginData()));
            handshakeResponsePacket.setCapabilityFlags(generateClientCapability());
            handshakeResponsePacket.setAuthPluginName(XuguAuthenticationMethod.NATIVE);
            ctx.channel().writeAndFlush(handshakeResponsePacket);
            serverVersion = new XuguServerVersion(handshake.getServerVersion());
            return;
        }
        if (msg instanceof XuguAuthSwitchRequestPacket) {
            XuguAuthSwitchRequestPacket authSwitchRequest = (XuguAuthSwitchRequestPacket) msg;
            ctx.channel().writeAndFlush(new XuguAuthSwitchResponsePacket(getAuthPluginResponse(authSwitchRequest)));
            seed = authSwitchRequest.getAuthPluginData().getAuthenticationPluginData();
            return;
        }
        if (msg instanceof XuguAuthMoreDataPacket) {
            XuguAuthMoreDataPacket authMoreData = (XuguAuthMoreDataPacket) msg;
            handleCachingSha2Auth(ctx, authMoreData);
            return;
        }
        if (msg instanceof XuguOKPacket) {
            ctx.channel().pipeline().remove(this);
            authResultCallback.setSuccess(serverVersion);
            return;
        }
        XuguErrPacket error = (XuguErrPacket) msg;
        ctx.channel().close();
        throw new PipelineInternalException(error.getErrorMessage());
    }
    
    private byte[] getAuthPluginResponse(final XuguAuthSwitchRequestPacket authSwitchRequest) throws NoSuchAlgorithmException {
        // TODO not support sha256_password now
        switch (XuguAuthenticationPlugin.getPluginByName(authSwitchRequest.getAuthPluginName())) {
            case NATIVE:
                return PasswordEncryption.encryptWithMySQL41(password.getBytes(), authSwitchRequest.getAuthPluginData().getAuthenticationPluginData());
            case CACHING_SHA2:
                return PasswordEncryption.encryptWithSha2(password.getBytes(), authSwitchRequest.getAuthPluginData().getAuthenticationPluginData());
            default:
                return password.getBytes();
        }
    }
    
    private void handleCachingSha2Auth(final ChannelHandlerContext ctx, final XuguAuthMoreDataPacket authMoreData) {
        if (publicKeyRequested) {
            ctx.channel().writeAndFlush(new XuguAuthSwitchResponsePacket(PasswordEncryption.encryptWithRSAPublicKey(
                    password, seed, serverVersion.greaterThanOrEqualTo(8, 0, 5) ? "RSA/ECB/OAEPWithSHA-1AndMGF1Padding" : "RSA/ECB/PKCS1Padding", new String(authMoreData.getPluginData()))));
        } else {
            if (PERFORM_FULL_AUTHENTICATION == authMoreData.getPluginData()[0]) {
                publicKeyRequested = true;
                ctx.channel().writeAndFlush(new XuguAuthSwitchResponsePacket(new byte[]{REQUEST_PUBLIC_KEY}));
            }
        }
    }
    
    private int generateClientCapability() {
        return XuguCapabilityFlag.calculateCapabilityFlags(XuguCapabilityFlag.CLIENT_LONG_PASSWORD, XuguCapabilityFlag.CLIENT_LONG_FLAG,
                XuguCapabilityFlag.CLIENT_PROTOCOL_41, XuguCapabilityFlag.CLIENT_INTERACTIVE, XuguCapabilityFlag.CLIENT_TRANSACTIONS,
                XuguCapabilityFlag.CLIENT_SECURE_CONNECTION, XuguCapabilityFlag.CLIENT_MULTI_STATEMENTS, XuguCapabilityFlag.CLIENT_PLUGIN_AUTH);
    }
    
    @SneakyThrows(NoSuchAlgorithmException.class)
    private byte[] generateAuthResponse(final byte[] authPluginData) {
        return null == password || password.isEmpty() ? new byte[0] : PasswordEncryption.encryptWithMySQL41(password.getBytes(), authPluginData);
    }
}
