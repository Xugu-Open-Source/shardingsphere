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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.shardingsphere.db.protocol.constant.CommonConstants;
import org.apache.shardingsphere.db.protocol.xugu.packet.XuguPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguErrPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguOKPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguAuthMoreDataPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguAuthSwitchRequestPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguHandshakePacket;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;
import org.apache.shardingsphere.infra.exception.generic.UnsupportedSQLOperationException;

import java.util.List;

/**
 * MySQL negotiate package decoder.
 */
public final class XuguNegotiatePackageDecoder extends ByteToMessageDecoder {
    
    private volatile boolean handshakeReceived;
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        XuguPacketPayload payload = new XuguPacketPayload(in, ctx.channel().attr(CommonConstants.CHARSET_ATTRIBUTE_KEY).get());
        if (handshakeReceived) {
            XuguPacket responsePacket = decodeResponsePacket(payload);
            if (responsePacket instanceof XuguOKPacket) {
                ctx.channel().pipeline().remove(this);
            }
            out.add(responsePacket);
        } else {
            out.add(decodeHandshakePacket(payload));
            handshakeReceived = true;
        }
    }
    
    private XuguHandshakePacket decodeHandshakePacket(final XuguPacketPayload payload) {
        return new XuguHandshakePacket(payload);
    }
    
    private XuguPacket decodeResponsePacket(final XuguPacketPayload payload) {
        int header = payload.getByteBuf().getByte(0) & 0xff;
        switch (header) {
            case XuguErrPacket.HEADER:
                return new XuguErrPacket(payload);
            case XuguOKPacket.HEADER:
                return new XuguOKPacket(payload);
            case XuguAuthSwitchRequestPacket.HEADER:
                return new XuguAuthSwitchRequestPacket(payload);
            case XuguAuthMoreDataPacket.HEADER:
                return new XuguAuthMoreDataPacket(payload);
            default:
                throw new UnsupportedSQLOperationException(String.format("Unsupported negotiate response header: %X", header));
        }
    }
}
