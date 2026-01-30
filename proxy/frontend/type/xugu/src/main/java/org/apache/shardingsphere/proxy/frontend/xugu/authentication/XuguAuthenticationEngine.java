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

package org.apache.shardingsphere.proxy.frontend.xugu.authentication;

import com.google.common.base.Strings;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.epoll.EpollDomainSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.authentication.Authenticator;
import org.apache.shardingsphere.authentication.AuthenticatorFactory;
import org.apache.shardingsphere.authentication.result.AuthenticationResult;
import org.apache.shardingsphere.authentication.result.AuthenticationResultBuilder;
import org.apache.shardingsphere.authority.checker.AuthorityChecker;
import org.apache.shardingsphere.authority.rule.AuthorityRule;
import org.apache.shardingsphere.db.protocol.constant.CommonConstants;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguCapabilityFlag;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguCharacterSet;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguConnectionPhase;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguConstants;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguStatusFlag;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguOKPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguAuthSwitchRequestPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguAuthSwitchResponsePacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguAuthenticationPluginData;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguHandshakePacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.handshake.XuguHandshakeResponse41Packet;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;
import org.apache.shardingsphere.db.protocol.payload.PacketPayload;
import org.apache.shardingsphere.infra.exception.dialect.exception.syntax.database.UnknownDatabaseException;
import org.apache.shardingsphere.infra.exception.mysql.exception.AccessDeniedException;
import org.apache.shardingsphere.infra.exception.mysql.exception.DatabaseAccessDeniedException;
import org.apache.shardingsphere.infra.exception.mysql.exception.HandshakeException;
import org.apache.shardingsphere.infra.metadata.user.Grantee;
import org.apache.shardingsphere.infra.metadata.user.ShardingSphereUser;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.proxy.frontend.authentication.AuthenticationEngine;
import org.apache.shardingsphere.proxy.frontend.connection.ConnectionIdGenerator;
import org.apache.shardingsphere.proxy.frontend.xugu.authentication.authenticator.XuguAuthenticatorType;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.binary.MySQLStatementIdGenerator;
import org.apache.shardingsphere.proxy.frontend.xugu.ssl.XuguSSLRequestHandler;
import org.apache.shardingsphere.proxy.frontend.ssl.ProxySSLContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Optional;

/**
 * Authentication engine for MySQL.
 */
@Slf4j
public final class XuguAuthenticationEngine implements AuthenticationEngine {
    
    private final XuguAuthenticationPluginData authPluginData = new XuguAuthenticationPluginData();
    
    private XuguConnectionPhase connectionPhase = XuguConnectionPhase.INITIAL_HANDSHAKE;
    
    private byte[] authResponse;
    
    private AuthenticationResult currentAuthResult;
    
    @Override
    public int handshake(final ChannelHandlerContext context) {
        int result = ConnectionIdGenerator.getInstance().nextId();
        connectionPhase = XuguConnectionPhase.AUTH_PHASE_FAST_PATH;
        boolean sslEnabled = ProxySSLContext.getInstance().isSSLEnabled();
        if (sslEnabled) {
            context.pipeline().addFirst(XuguSSLRequestHandler.class.getSimpleName(), new XuguSSLRequestHandler());
        }
        context.writeAndFlush(new XuguHandshakePacket(result, sslEnabled, authPluginData));
        MySQLStatementIdGenerator.getInstance().registerConnection(result);
        return result;
    }
    
    @Override
    public AuthenticationResult authenticate(final ChannelHandlerContext context, final PacketPayload payload) {
        AuthorityRule rule = ProxyContext.getInstance().getContextManager().getMetaDataContexts().getMetaData().getGlobalRuleMetaData().getSingleRule(AuthorityRule.class);
        if (XuguConnectionPhase.AUTH_PHASE_FAST_PATH == connectionPhase) {
            currentAuthResult = authenticatePhaseFastPath(context, payload, rule);
            if (!currentAuthResult.isFinished()) {
                return currentAuthResult;
            }
        } else if (XuguConnectionPhase.AUTHENTICATION_METHOD_MISMATCH == connectionPhase) {
            authenticateMismatchedMethod((XuguPacketPayload) payload);
        }
        Grantee grantee = new Grantee(currentAuthResult.getUsername(), getHostAddress(context));
        if (!login(rule, grantee, authResponse)) {
            throw new AccessDeniedException(currentAuthResult.getUsername(), grantee.getHostname(), 0 != authResponse.length);
        }
        if (!authorizeDatabase(rule, grantee, currentAuthResult.getDatabase())) {
            throw new DatabaseAccessDeniedException(currentAuthResult.getUsername(), grantee.getHostname(), currentAuthResult.getDatabase());
        }
        writeOKPacket(context);
        return AuthenticationResultBuilder.finished(grantee.getUsername(), grantee.getHostname(), currentAuthResult.getDatabase());
    }
    
    private AuthenticationResult authenticatePhaseFastPath(final ChannelHandlerContext context, final PacketPayload payload, final AuthorityRule rule) {
        XuguHandshakeResponse41Packet handshakeResponsePacket;
        try {
            handshakeResponsePacket = new XuguHandshakeResponse41Packet((XuguPacketPayload) payload);
        } catch (final IndexOutOfBoundsException ex) {
            if (log.isWarnEnabled()) {
                log.warn("Received bad handshake from client {}: \n{}", context.channel(), ByteBufUtil.prettyHexDump(payload.getByteBuf().resetReaderIndex()));
            }
            throw new HandshakeException();
        }
        authResponse = handshakeResponsePacket.getAuthResponse();
        setMultiStatementsOption(context, handshakeResponsePacket);
        setCharacterSet(context, handshakeResponsePacket);
        String database = handshakeResponsePacket.getDatabase();
        if (!Strings.isNullOrEmpty(database) && !ProxyContext.getInstance().databaseExists(database)) {
            throw new UnknownDatabaseException(database);
        }
        String username = handshakeResponsePacket.getUsername();
        String hostname = getHostAddress(context);
        ShardingSphereUser user = rule.findUser(new Grantee(username, hostname)).orElseGet(() -> new ShardingSphereUser(username, "", hostname));
        Authenticator authenticator = new AuthenticatorFactory<>(XuguAuthenticatorType.class, rule).newInstance(user);
        if (0 == authResponse.length || isClientPluginAuthenticate(handshakeResponsePacket) && !authenticator.getAuthenticationMethodName().equals(handshakeResponsePacket.getAuthPluginName())) {
            connectionPhase = XuguConnectionPhase.AUTHENTICATION_METHOD_MISMATCH;
            context.writeAndFlush(new XuguAuthSwitchRequestPacket(authenticator.getAuthenticationMethodName(), authPluginData));
            return AuthenticationResultBuilder.continued(username, hostname, database);
        }
        return AuthenticationResultBuilder.finished(username, hostname, database);
    }
    
    private void setMultiStatementsOption(final ChannelHandlerContext context, final XuguHandshakeResponse41Packet handshakeResponsePacket) {
        context.channel().attr(XuguConstants.OPTION_MULTI_STATEMENTS_ATTRIBUTE_KEY).set(handshakeResponsePacket.getMultiStatementsOption());
    }
    
    private void setCharacterSet(final ChannelHandlerContext context, final XuguHandshakeResponse41Packet handshakeResponsePacket) {
        XuguCharacterSet characterSet = XuguCharacterSet.findById(handshakeResponsePacket.getCharacterSet());
        context.channel().attr(CommonConstants.CHARSET_ATTRIBUTE_KEY).set(characterSet.getCharset());
        context.channel().attr(XuguConstants.CHARACTER_SET_ATTRIBUTE_KEY).set(characterSet);
    }
    
    private boolean isClientPluginAuthenticate(final XuguHandshakeResponse41Packet packet) {
        return 0 != (packet.getCapabilityFlags() & XuguCapabilityFlag.CLIENT_PLUGIN_AUTH.getValue());
    }
    
    private void authenticateMismatchedMethod(final XuguPacketPayload payload) {
        authResponse = new XuguAuthSwitchResponsePacket(payload).getAuthPluginResponse();
    }
    
    private boolean login(final AuthorityRule rule, final Grantee grantee, final byte[] authenticationResponse) {
        Optional<ShardingSphereUser> user = rule.findUser(grantee);
        return user.isPresent()
                && new AuthenticatorFactory<>(XuguAuthenticatorType.class, rule).newInstance(user.get()).authenticate(user.get(), new Object[]{authenticationResponse, authPluginData});
    }
    
    private boolean authorizeDatabase(final AuthorityRule rule, final Grantee grantee, final String databaseName) {
        return null == databaseName || new AuthorityChecker(rule, grantee).isAuthorized(databaseName);
    }
    
    private String getHostAddress(final ChannelHandlerContext context) {
        if (context.channel() instanceof EpollDomainSocketChannel) {
            return context.channel().parent().localAddress().toString();
        }
        SocketAddress socketAddress = context.channel().remoteAddress();
        return socketAddress instanceof InetSocketAddress ? ((InetSocketAddress) socketAddress).getAddress().getHostAddress() : socketAddress.toString();
    }
    
    private void writeOKPacket(final ChannelHandlerContext context) {
        context.writeAndFlush(new XuguOKPacket(XuguStatusFlag.SERVER_STATUS_AUTOCOMMIT.getValue()));
    }
}
