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
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.client.InternalResultSet;
import org.apache.shardingsphere.db.protocol.constant.CommonConstants;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.XuguColumnDefinition41Packet;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.XuguFieldCountPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.text.XuguTextResultSetRowPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguEofPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguErrPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguOKPacket;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;
import org.apache.shardingsphere.infra.annotation.HighFrequencyInvocation;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MySQL command packet decoder.
 */
@HighFrequencyInvocation
public final class XuguCommandPacketDecoder extends ByteToMessageDecoder {
    
    private final AtomicReference<States> currentState = new AtomicReference<>(States.RESPONSE_PACKET);
    
    private final AtomicReference<InternalResultSet> internalResultSet = new AtomicReference<>();
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        XuguPacketPayload payload = new XuguPacketPayload(in, ctx.channel().attr(CommonConstants.CHARSET_ATTRIBUTE_KEY).get());
        decodeCommandPacket(payload, out);
    }
    
    private void decodeCommandPacket(final XuguPacketPayload payload, final List<Object> out) {
        if (States.FIELD_PACKET == currentState.get()) {
            decodeFieldPacket(payload);
            return;
        }
        if (States.ROW_DATA_PACKET == currentState.get()) {
            decodeRowDataPacket(payload, out);
            return;
        }
        decodeResponsePacket(payload, out);
    }
    
    private void decodeFieldPacket(final XuguPacketPayload payload) {
        if (XuguEofPacket.HEADER == (payload.getByteBuf().getByte(0) & 0xff)) {
            new XuguEofPacket(payload);
            currentState.set(States.ROW_DATA_PACKET);
        } else {
            internalResultSet.get().getFieldDescriptors().add(new XuguColumnDefinition41Packet(payload));
        }
    }
    
    private void decodeRowDataPacket(final XuguPacketPayload payload, final List<Object> out) {
        if (XuguEofPacket.HEADER == (payload.getByteBuf().getByte(0) & 0xff)) {
            new XuguEofPacket(payload);
            out.add(internalResultSet.get());
            currentState.set(States.RESPONSE_PACKET);
            internalResultSet.set(null);
        } else {
            internalResultSet.get().getFieldValues().add(new XuguTextResultSetRowPacket(payload, internalResultSet.get().getHeader().getColumnCount()));
        }
    }
    
    private void decodeResponsePacket(final XuguPacketPayload payload, final List<Object> out) {
        switch (payload.getByteBuf().getByte(0) & 0xff) {
            case XuguErrPacket.HEADER:
                out.add(new XuguErrPacket(payload));
                break;
            case XuguOKPacket.HEADER:
                out.add(new XuguOKPacket(payload));
                break;
            default:
                XuguFieldCountPacket fieldCountPacket = new XuguFieldCountPacket(payload);
                currentState.set(States.FIELD_PACKET);
                internalResultSet.set(new InternalResultSet(fieldCountPacket));
                break;
        }
    }
    
    private enum States {
        
        RESPONSE_PACKET, FIELD_PACKET, ROW_DATA_PACKET
    }
}
