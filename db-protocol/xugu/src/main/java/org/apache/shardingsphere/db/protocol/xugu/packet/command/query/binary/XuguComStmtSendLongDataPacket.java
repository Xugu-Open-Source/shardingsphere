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

package org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary;

import lombok.Getter;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.XuguCommandPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.XuguCommandPacketType;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;

/**
 * COM_STMT_SEND_LONG_DATA command packet for MySQL.
 */
@Getter
public final class XuguComStmtSendLongDataPacket extends XuguCommandPacket {
    
    private final int statementId;
    
    private final int paramId;
    
    private final byte[] data;
    
    public XuguComStmtSendLongDataPacket(final XuguPacketPayload payload) {
        super(XuguCommandPacketType.COM_STMT_SEND_LONG_DATA);
        statementId = payload.readInt4();
        paramId = payload.readInt2();
        data = payload.readStringEOFByBytes();
    }
}
