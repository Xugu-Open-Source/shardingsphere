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
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.XuguCommandPacketType;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.admin.XuguComSetOptionPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.admin.initdb.XuguComInitDbPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.XuguComStmtSendLongDataPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.close.XuguComStmtClosePacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.XuguComStmtExecutePacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.prepare.XuguComStmtPreparePacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.reset.XuguComStmtResetPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.text.fieldlist.XuguComFieldListPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.text.query.XuguComQueryPacket;
import org.apache.shardingsphere.db.protocol.packet.command.CommandPacket;
import org.apache.shardingsphere.db.protocol.packet.sql.SQLReceivedPacket;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.proxy.frontend.command.executor.CommandExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.admin.XuguComResetConnectionExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.admin.XuguComSetOptionExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.admin.initdb.XuguComInitDbExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.admin.ping.XuguComPingExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.admin.quit.MySQLComQuitExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.generic.XuguUnsupportedCommandExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.binary.MySQLComStmtSendLongDataExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.binary.close.MySQLComStmtCloseExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.binary.execute.MySQLComStmtExecuteExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.binary.prepare.MySQLComStmtPrepareExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.binary.reset.MySQLComStmtResetExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.text.fieldlist.MySQLComFieldListPacketExecutor;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.text.query.XuguComQueryPacketExecutor;

import java.sql.SQLException;

/**
 * Command executor factory for MySQL.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class XuguCommandExecutorFactory {
    
    /**
     * Create new instance of packet executor.
     *
     * @param commandPacketType command packet type for MySQL
     * @param commandPacket command packet for MySQL
     * @param connectionSession connection session
     * @return created instance
     * @throws SQLException SQL exception
     */
    @SuppressWarnings("DataFlowIssue")
    public static CommandExecutor newInstance(final XuguCommandPacketType commandPacketType, final CommandPacket commandPacket, final ConnectionSession connectionSession) throws SQLException {
        if (commandPacket instanceof SQLReceivedPacket) {
            log.debug("Execute packet type: {}, sql: {}", commandPacketType, ((SQLReceivedPacket) commandPacket).getSQL());
        } else {
            log.debug("Execute packet type: {}", commandPacketType);
        }
        switch (commandPacketType) {
            case COM_QUIT:
                return new MySQLComQuitExecutor();
            case COM_INIT_DB:
                return new XuguComInitDbExecutor((XuguComInitDbPacket) commandPacket, connectionSession);
            case COM_FIELD_LIST:
                return new MySQLComFieldListPacketExecutor((XuguComFieldListPacket) commandPacket, connectionSession);
            case COM_QUERY:
                return new XuguComQueryPacketExecutor((XuguComQueryPacket) commandPacket, connectionSession);
            case COM_PING:
                return new XuguComPingExecutor(connectionSession);
            case COM_STMT_PREPARE:
                return new MySQLComStmtPrepareExecutor((XuguComStmtPreparePacket) commandPacket, connectionSession);
            case COM_STMT_EXECUTE:
                return new MySQLComStmtExecuteExecutor((XuguComStmtExecutePacket) commandPacket, connectionSession);
            case COM_STMT_SEND_LONG_DATA:
                return new MySQLComStmtSendLongDataExecutor((XuguComStmtSendLongDataPacket) commandPacket, connectionSession);
            case COM_STMT_RESET:
                return new MySQLComStmtResetExecutor((XuguComStmtResetPacket) commandPacket, connectionSession);
            case COM_STMT_CLOSE:
                return new MySQLComStmtCloseExecutor((XuguComStmtClosePacket) commandPacket, connectionSession);
            case COM_SET_OPTION:
                return new XuguComSetOptionExecutor((XuguComSetOptionPacket) commandPacket, connectionSession);
            case COM_RESET_CONNECTION:
                return new XuguComResetConnectionExecutor(connectionSession);
            default:
                return new XuguUnsupportedCommandExecutor(commandPacketType);
        }
    }
}
