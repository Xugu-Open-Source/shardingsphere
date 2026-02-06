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

package org.apache.shardingsphere.proxy.frontend.xugu.command;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.XuguCommandPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.XuguCommandPacketType;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.admin.XuguComResetConnectionPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.admin.XuguComSetOptionPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.admin.XuguUnsupportedCommandPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.admin.initdb.XuguComInitDbPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.admin.ping.XuguComPingPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.admin.quit.XuguComQuitPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.XuguComStmtSendLongDataPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.close.XuguComStmtClosePacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.XuguComStmtExecutePacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.prepare.XuguComStmtPreparePacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.reset.XuguComStmtResetPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.text.fieldlist.XuguComFieldListPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.text.query.XuguComQueryPacket;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.binary.XuguServerPreparedStatement;

/**
 * Command packet factory for MySQL.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class XuguCommandPacketFactory {
    
    /**
     * Create new instance of command packet.
     *
     * @param commandPacketType command packet type for MySQL
     * @param payload packet payload for MySQL
     * @param connectionSession connection session
     * @return created instance
     */
    public static XuguCommandPacket newInstance(final XuguCommandPacketType commandPacketType, final XuguPacketPayload payload,
                                                final ConnectionSession connectionSession) {
        switch (commandPacketType) {
            case COM_QUIT:
                return new XuguComQuitPacket();
            case COM_INIT_DB:
                return new XuguComInitDbPacket(payload);
            case COM_FIELD_LIST:
                return new XuguComFieldListPacket(payload);
            case COM_QUERY:
                return new XuguComQueryPacket(payload);
            case COM_STMT_PREPARE:
                return new XuguComStmtPreparePacket(payload);
            case COM_STMT_EXECUTE:
                XuguServerPreparedStatement serverPreparedStatement =
                        connectionSession.getServerPreparedStatementRegistry().getPreparedStatement(payload.getByteBuf().getIntLE(payload.getByteBuf().readerIndex()));
                return new XuguComStmtExecutePacket(payload, serverPreparedStatement.getSqlStatementContext().getSqlStatement().getParameterCount());
            case COM_STMT_SEND_LONG_DATA:
                return new XuguComStmtSendLongDataPacket(payload);
            case COM_STMT_RESET:
                return new XuguComStmtResetPacket(payload);
            case COM_STMT_CLOSE:
                return new XuguComStmtClosePacket(payload);
            case COM_SET_OPTION:
                return new XuguComSetOptionPacket(payload);
            case COM_PING:
                return new XuguComPingPacket();
            case COM_RESET_CONNECTION:
                return new XuguComResetConnectionPacket();
            default:
                return new XuguUnsupportedCommandPacket(commandPacketType);
        }
    }
}
