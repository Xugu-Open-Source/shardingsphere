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

package org.apache.shardingsphere.db.protocol.xugu.packet.handshake;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.shardingsphere.db.protocol.constant.DatabaseProtocolServerInfo;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguAuthenticationMethod;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguCapabilityFlag;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguConstants;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguStatusFlag;
import org.apache.shardingsphere.db.protocol.xugu.packet.XuguPacket;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;

/**
 * Handshake packet protocol for MySQL.
 * 
 * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_connection_phase_packets_protocol_handshake_v10.html">Handshake</a>
 */
@Getter
public final class XuguHandshakePacket extends XuguPacket {
    
    private final int protocolVersion = XuguConstants.PROTOCOL_VERSION;
    
    private final String serverVersion;
    
    private final int connectionId;
    
    private final int capabilityFlagsLower;
    
    private final int characterSet;
    
    private final XuguStatusFlag statusFlag;
    
    private final XuguAuthenticationPluginData authPluginData;
    
    private int capabilityFlagsUpper;
    
    private String authPluginName;
    
    public XuguHandshakePacket(final int connectionId, final boolean sslEnabled, final XuguAuthenticationPluginData authPluginData) {
        serverVersion = DatabaseProtocolServerInfo.getDefaultProtocolVersion(TypedSPILoader.getService(DatabaseType.class, "XuGu"));
        this.connectionId = connectionId;
        capabilityFlagsLower = XuguCapabilityFlag.calculateHandshakeCapabilityFlagsLower() | (sslEnabled ? XuguCapabilityFlag.CLIENT_SSL.getValue() : 0);
        characterSet = XuguConstants.DEFAULT_CHARSET.getId();
        statusFlag = XuguStatusFlag.SERVER_STATUS_AUTOCOMMIT;
        capabilityFlagsUpper = XuguCapabilityFlag.calculateHandshakeCapabilityFlagsUpper();
        this.authPluginData = authPluginData;
        authPluginName = XuguAuthenticationMethod.CACHING_SHA2_PASSWORD.getMethodName();
    }
    
    public XuguHandshakePacket(final XuguPacketPayload payload) {
        Preconditions.checkArgument(protocolVersion == payload.readInt1());
        serverVersion = payload.readStringNul();
        connectionId = payload.readInt4();
        final byte[] authPluginDataPart1 = payload.readStringNulByBytes();
        capabilityFlagsLower = payload.readInt2();
        characterSet = payload.readInt1();
        statusFlag = XuguStatusFlag.valueOf(payload.readInt2());
        capabilityFlagsUpper = payload.readInt2();
        payload.readInt1();
        payload.skipReserved(10);
        authPluginData = new XuguAuthenticationPluginData(authPluginDataPart1, readAuthPluginDataPart2(payload));
        authPluginName = readAuthPluginName(payload);
    }
    
    /**
     * There are some different between implement of handshake initialization packet and document.
     * In source code of 5.7 version, authPluginDataPart2 should be at least 12 bytes,
     * and then follow a nul byte.
     * But in document, authPluginDataPart2 is at least 13 bytes, and not nul byte.
     * From test, the 13th byte is nul byte and should be excluded from authPluginDataPart2.
     *
     * @param payload MySQL packet payload
     * @return auth plugin data part2
     */
    private byte[] readAuthPluginDataPart2(final XuguPacketPayload payload) {
        return isClientSecureConnection() ? payload.readStringNulByBytes() : new byte[0];
    }
    
    private String readAuthPluginName(final XuguPacketPayload payload) {
        return isClientPluginAuth() ? payload.readStringNul() : null;
    }
    
    /**
     * Set authentication plugin name.
     *
     * @param authenticationMethod MySQL authentication method
     */
    public void setAuthPluginName(final XuguAuthenticationMethod authenticationMethod) {
        authPluginName = authenticationMethod.getMethodName();
        capabilityFlagsUpper |= XuguCapabilityFlag.CLIENT_PLUGIN_AUTH.getValue() >> 16;
    }
    
    @Override
    protected void write(final XuguPacketPayload payload) {
        payload.writeInt1(protocolVersion);
        payload.writeStringNul(serverVersion);
        payload.writeInt4(connectionId);
        payload.writeStringNul(new String(authPluginData.getAuthenticationPluginDataPart1()));
        payload.writeInt2(capabilityFlagsLower);
        payload.writeInt1(characterSet);
        payload.writeInt2(statusFlag.getValue());
        payload.writeInt2(capabilityFlagsUpper);
        payload.writeInt1(isClientPluginAuth() ? authPluginData.getAuthenticationPluginData().length + 1 : 0);
        payload.writeReserved(10);
        writeAuthPluginDataPart2(payload);
        writeAuthPluginName(payload);
    }
    
    private void writeAuthPluginDataPart2(final XuguPacketPayload payload) {
        if (isClientSecureConnection()) {
            payload.writeStringNul(new String(authPluginData.getAuthenticationPluginDataPart2()));
        }
    }
    
    private void writeAuthPluginName(final XuguPacketPayload payload) {
        if (isClientPluginAuth()) {
            payload.writeStringNul(authPluginName);
        }
    }
    
    private boolean isClientSecureConnection() {
        return 0 != (capabilityFlagsLower & XuguCapabilityFlag.CLIENT_SECURE_CONNECTION.getValue() & 0x00000ffff);
    }
    
    private boolean isClientPluginAuth() {
        return 0 != (capabilityFlagsUpper & XuguCapabilityFlag.CLIENT_PLUGIN_AUTH.getValue() >> 16);
    }
}
