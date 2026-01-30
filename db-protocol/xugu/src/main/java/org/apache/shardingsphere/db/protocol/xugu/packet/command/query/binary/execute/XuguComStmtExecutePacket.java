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

package org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguBinaryColumnType;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguNewParametersBoundFlag;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.XuguCommandPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.XuguCommandPacketType;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.XuguColumnDefinitionFlag;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.XuguPreparedStatementParameterType;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.protocol.XuguBinaryProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.protocol.XuguBinaryProtocolValueFactory;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * COM_STMT_EXECUTE command packet for MySQL.
 *
 * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_stmt_execute.html">COM_STMT_EXECUTE</a>
 */
@Getter
public final class XuguComStmtExecutePacket extends XuguCommandPacket {
    
    private static final int ITERATION_COUNT = 1;
    
    private static final int NULL_BITMAP_OFFSET = 0;
    
    private final XuguPacketPayload payload;
    
    private final int statementId;
    
    private final int flags;
    
    @Getter(AccessLevel.NONE)
    private final XuguNullBitmap nullBitmap;
    
    private final XuguNewParametersBoundFlag newParametersBoundFlag;
    
    private final List<XuguPreparedStatementParameterType> newParameterTypes;
    
    public XuguComStmtExecutePacket(final XuguPacketPayload payload, final int paramCount) {
        super(XuguCommandPacketType.COM_STMT_EXECUTE);
        this.payload = payload;
        statementId = payload.readInt4();
        flags = payload.readInt1();
        Preconditions.checkArgument(ITERATION_COUNT == payload.readInt4());
        if (paramCount > 0) {
            nullBitmap = new XuguNullBitmap(paramCount, NULL_BITMAP_OFFSET);
            for (int i = 0; i < nullBitmap.getNullBitmap().length; i++) {
                nullBitmap.getNullBitmap()[i] = payload.readInt1();
            }
            newParametersBoundFlag = XuguNewParametersBoundFlag.valueOf(payload.readInt1());
            newParameterTypes = XuguNewParametersBoundFlag.PARAMETER_TYPE_EXIST == newParametersBoundFlag ? getNewParameterTypes(paramCount) : Collections.emptyList();
        } else {
            nullBitmap = null;
            newParametersBoundFlag = null;
            newParameterTypes = Collections.emptyList();
        }
    }
    
    private List<XuguPreparedStatementParameterType> getNewParameterTypes(final int paramCount) {
        List<XuguPreparedStatementParameterType> result = new ArrayList<>(paramCount);
        for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
            XuguBinaryColumnType columnType = XuguBinaryColumnType.valueOf(payload.readInt1());
            int unsignedFlag = payload.readInt1();
            result.add(new XuguPreparedStatementParameterType(columnType, unsignedFlag));
        }
        return result;
    }
    
    /**
     * Read parameter values from packet.
     *
     * @param paramTypes parameter type of values
     * @param longDataIndexes indexes of long data
     * @param parameterFlags column definition flag of parameters
     * @return parameter values
     * @throws SQLException SQL exception
     */
    public List<Object> readParameters(final List<XuguPreparedStatementParameterType> paramTypes, final Set<Integer> longDataIndexes,
                                       final List<Integer> parameterFlags) throws SQLException {
        List<Object> result = new ArrayList<>(paramTypes.size());
        for (int paramIndex = 0; paramIndex < paramTypes.size(); paramIndex++) {
            if (longDataIndexes.contains(paramIndex)) {
                result.add(null);
                continue;
            }
            XuguBinaryProtocolValue binaryProtocolValue = XuguBinaryProtocolValueFactory.getBinaryProtocolValue(paramTypes.get(paramIndex).getColumnType());
            Object value = nullBitmap.isNullParameter(paramIndex) ? null
                    : binaryProtocolValue.read(payload, (parameterFlags.get(paramIndex) & XuguColumnDefinitionFlag.UNSIGNED.getValue()) == XuguColumnDefinitionFlag.UNSIGNED.getValue());
            result.add(value);
        }
        return result;
    }
}
