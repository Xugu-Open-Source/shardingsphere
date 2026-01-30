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

package org.apache.shardingsphere.proxy.frontend.xugu.command.query.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguBinaryColumnType;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguCharacterSet;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.XuguColumnDefinition41Packet;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.XuguColumnDefinitionFlag;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.XuguFieldCountPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguEofPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.generic.XuguOKPacket;
import org.apache.shardingsphere.db.protocol.packet.DatabasePacket;
import org.apache.shardingsphere.proxy.backend.response.header.query.QueryHeader;
import org.apache.shardingsphere.proxy.backend.response.header.query.QueryResponseHeader;
import org.apache.shardingsphere.proxy.backend.response.header.update.UpdateResponseHeader;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Response packet builder.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponsePacketBuilder {
    
    private static final String BINARY_COLUMN_TYPE_KEYWORD = "BINARY";
    
    private static final String BLOB_COLUMN_TYPE_KEYWORD = "BLOB";
    
    private static final Collection<Integer> BINARY_TYPES = new HashSet<>(Arrays.asList(Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY));
    
    /**
     * Build query response packets.
     *
     * @param queryResponseHeader query response header
     * @param sessionCharacterSet MySQL character set id
     * @param statusFlags server status flags
     * @return query response packets
     */
    public static Collection<DatabasePacket> buildQueryResponsePackets(final QueryResponseHeader queryResponseHeader, final int sessionCharacterSet, final int statusFlags) {
        Collection<DatabasePacket> result = new LinkedList<>();
        List<QueryHeader> queryHeaders = queryResponseHeader.getQueryHeaders();
        result.add(new XuguFieldCountPacket(queryHeaders.size()));
        for (QueryHeader each : queryHeaders) {
            int characterSet = BINARY_TYPES.contains(each.getColumnType()) ? XuguCharacterSet.BINARY.getId() : sessionCharacterSet;
            result.add(new XuguColumnDefinition41Packet(characterSet, getColumnDefinitionFlag(each), each.getSchema(), each.getTable(), each.getTable(),
                    each.getColumnLabel(), each.getColumnName(), each.getColumnLength(), XuguBinaryColumnType.valueOfJDBCType(each.getColumnType()), each.getDecimals(), false));
        }
        result.add(new XuguEofPacket(statusFlags));
        return result;
    }
    
    private static int getColumnDefinitionFlag(final QueryHeader header) {
        int result = 0;
        if (header.isPrimaryKey()) {
            result += XuguColumnDefinitionFlag.PRIMARY_KEY.getValue();
        }
        if (header.isNotNull()) {
            result += XuguColumnDefinitionFlag.NOT_NULL.getValue();
        }
        if (!header.isSigned()) {
            result += XuguColumnDefinitionFlag.UNSIGNED.getValue();
        }
        if (header.isAutoIncrement()) {
            result += XuguColumnDefinitionFlag.AUTO_INCREMENT.getValue();
        }
        if (header.getColumnTypeName().contains(BINARY_COLUMN_TYPE_KEYWORD) || header.getColumnTypeName().contains(BLOB_COLUMN_TYPE_KEYWORD)) {
            result += XuguColumnDefinitionFlag.BINARY_COLLATION.getValue();
        }
        if (BINARY_TYPES.contains(header.getColumnType())) {
            result += XuguColumnDefinitionFlag.BLOB.getValue();
        }
        return result;
    }
    
    /**
     * Build update response packets.
     *
     * @param updateResponseHeader update response header
     * @param serverStatusFlag server status flag
     * @return update response packets
     */
    public static Collection<DatabasePacket> buildUpdateResponsePackets(final UpdateResponseHeader updateResponseHeader, final int serverStatusFlag) {
        return Collections.singleton(new XuguOKPacket(updateResponseHeader.getUpdateCount(), updateResponseHeader.getLastInsertId(), serverStatusFlag));
    }
}
