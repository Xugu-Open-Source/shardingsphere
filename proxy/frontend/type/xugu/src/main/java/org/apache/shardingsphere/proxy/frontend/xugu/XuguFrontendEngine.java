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

package org.apache.shardingsphere.proxy.frontend.xugu;

import io.netty.channel.Channel;
import lombok.Getter;
import org.apache.shardingsphere.db.protocol.codec.DatabasePacketCodecEngine;
import org.apache.shardingsphere.db.protocol.xugu.codec.XuguPacketCodecEngine;
import org.apache.shardingsphere.db.protocol.xugu.netty.XuguSequenceIdInboundHandler;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.proxy.frontend.authentication.AuthenticationEngine;
import org.apache.shardingsphere.proxy.frontend.xugu.authentication.XuguAuthenticationEngine;
import org.apache.shardingsphere.proxy.frontend.xugu.command.XuguCommandExecuteEngine;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.binary.XuguStatementIdGenerator;
import org.apache.shardingsphere.proxy.frontend.netty.FrontendChannelInboundHandler;
import org.apache.shardingsphere.proxy.frontend.spi.DatabaseProtocolFrontendEngine;

/**
 * Frontend engine for XuguDB.
 */
@Getter
public final class XuguFrontendEngine implements DatabaseProtocolFrontendEngine {
    
    private final AuthenticationEngine authenticationEngine = new XuguAuthenticationEngine();
    
    private final XuguCommandExecuteEngine commandExecuteEngine = new XuguCommandExecuteEngine();
    
    private final DatabasePacketCodecEngine codecEngine = new XuguPacketCodecEngine();
    
    @Override
    public void initChannel(final Channel channel) {
        channel.pipeline().addBefore(FrontendChannelInboundHandler.class.getSimpleName(), XuguSequenceIdInboundHandler.class.getSimpleName(), new XuguSequenceIdInboundHandler(channel));
    }
    
    @Override
    public void release(final ConnectionSession connectionSession) {
        XuguStatementIdGenerator.getInstance().unregisterConnection(connectionSession.getConnectionId());
    }
    
    @Override
    public void handleException(final ConnectionSession connectionSession, final Exception exception) {
    }
    
    @Override
    public String getDatabaseType() {
        return "XuGu";
    }
}
