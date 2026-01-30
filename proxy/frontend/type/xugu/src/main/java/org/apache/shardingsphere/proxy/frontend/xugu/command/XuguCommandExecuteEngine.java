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

import io.netty.channel.ChannelHandlerContext;
import org.apache.shardingsphere.db.protocol.xugu.packet.XuguPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.XuguCommandPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.XuguCommandPacketType;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguEofPacket;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;
import org.apache.shardingsphere.db.protocol.packet.DatabasePacket;
import org.apache.shardingsphere.db.protocol.packet.command.CommandPacket;
import org.apache.shardingsphere.db.protocol.packet.command.CommandPacketType;
import org.apache.shardingsphere.db.protocol.payload.PacketPayload;
import org.apache.shardingsphere.infra.config.props.ConfigurationPropertyKey;
import org.apache.shardingsphere.proxy.backend.connector.ProxyDatabaseConnectionManager;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.proxy.frontend.command.CommandExecuteEngine;
import org.apache.shardingsphere.proxy.frontend.command.executor.CommandExecutor;
import org.apache.shardingsphere.proxy.frontend.command.executor.QueryCommandExecutor;
import org.apache.shardingsphere.proxy.frontend.command.executor.ResponseType;
import org.apache.shardingsphere.proxy.frontend.xugu.err.XuguErrorPacketFactory;

import java.sql.SQLException;

/**
 * Command execute engine for MySQL.
 */
public final class XuguCommandExecuteEngine implements CommandExecuteEngine {
    
    @Override
    public XuguCommandPacketType getCommandPacketType(final PacketPayload payload) {
        return XuguCommandPacketType.valueOf(((XuguPacketPayload) payload).readInt1());
    }
    
    @Override
    public XuguCommandPacket getCommandPacket(final PacketPayload payload, final CommandPacketType type, final ConnectionSession connectionSession) {
        return XuguCommandPacketFactory.newInstance((XuguCommandPacketType) type, (XuguPacketPayload) payload, connectionSession);
    }
    
    @Override
    public CommandExecutor getCommandExecutor(final CommandPacketType type, final CommandPacket packet, final ConnectionSession connectionSession) throws SQLException {
        return XuguCommandExecutorFactory.newInstance((XuguCommandPacketType) type, packet, connectionSession);
    }
    
    @Override
    public XuguPacket getErrorPacket(final Exception cause) {
        return XuguErrorPacketFactory.newInstance(cause);
    }
    
    @Override
    public void writeQueryData(final ChannelHandlerContext context,
                               final ProxyDatabaseConnectionManager databaseConnectionManager, final QueryCommandExecutor queryCommandExecutor, final int headerPackagesCount) throws SQLException {
        if (ResponseType.QUERY != queryCommandExecutor.getResponseType() || !context.channel().isActive()) {
            return;
        }
        int count = 0;
        int flushThreshold = ProxyContext.getInstance().getContextManager().getMetaDataContexts().getMetaData().getProps().<Integer>getValue(ConfigurationPropertyKey.PROXY_FRONTEND_FLUSH_THRESHOLD);
        while (queryCommandExecutor.next()) {
            count++;
            databaseConnectionManager.getConnectionResourceLock().doAwait(context);
            DatabasePacket dataValue = queryCommandExecutor.getQueryRowPacket();
            context.write(dataValue);
            if (flushThreshold == count) {
                context.flush();
                count = 0;
            }
        }
        context.write(new XuguEofPacket(ServerStatusFlagCalculator.calculateFor(databaseConnectionManager.getConnectionSession(), true)));
    }
}
