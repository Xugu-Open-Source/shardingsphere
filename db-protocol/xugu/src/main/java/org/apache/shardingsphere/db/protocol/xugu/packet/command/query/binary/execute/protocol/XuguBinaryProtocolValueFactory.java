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

package org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.protocol;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.db.protocol.binary.BinaryColumnType;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguBinaryColumnType;

import java.util.HashMap;
import java.util.Map;

/**
 * Binary protocol value factory for MySQL.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class XuguBinaryProtocolValueFactory {
    
    private static final Map<BinaryColumnType, XuguBinaryProtocolValue> BINARY_PROTOCOL_VALUES = new HashMap<>();
    
    static {
        setStringLenencBinaryProtocolValue();
        setByteLenencBinaryProtocolValue();
        setInt8BinaryProtocolValue();
        setInt4BinaryProtocolValue();
        setInt2BinaryProtocolValue();
        setInt1BinaryProtocolValue();
        setDoubleBinaryProtocolValue();
        setFloatBinaryProtocolValue();
        setDateBinaryProtocolValue();
        setTimeBinaryProtocolValue();
        setNullBinaryProtocolValue();
    }
    
    private static void setStringLenencBinaryProtocolValue() {
        XuguStringLenencBinaryProtocolValue binaryProtocolValue = new XuguStringLenencBinaryProtocolValue();
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.VARCHAR, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.VAR_STRING, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.ENUM, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.SET, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.GEOMETRY, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.BIT, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.DECIMAL, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.NEWDECIMAL, binaryProtocolValue);
    }
    
    private static void setByteLenencBinaryProtocolValue() {
        XuguByteLenencBinaryProtocolValue binaryProtocolValue = new XuguByteLenencBinaryProtocolValue();
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.STRING, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.LONG_BLOB, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.MEDIUM_BLOB, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.BLOB, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.TINY_BLOB, binaryProtocolValue);
    }
    
    private static void setInt8BinaryProtocolValue() {
        XuguInt8BinaryProtocolValue binaryProtocolValue = new XuguInt8BinaryProtocolValue();
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.LONGLONG, binaryProtocolValue);
    }
    
    private static void setInt4BinaryProtocolValue() {
        XuguInt4BinaryProtocolValue binaryProtocolValue = new XuguInt4BinaryProtocolValue();
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.LONG, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.INT24, binaryProtocolValue);
    }
    
    private static void setInt2BinaryProtocolValue() {
        XuguInt2BinaryProtocolValue binaryProtocolValue = new XuguInt2BinaryProtocolValue();
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.SHORT, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.YEAR, binaryProtocolValue);
    }
    
    private static void setInt1BinaryProtocolValue() {
        XuguInt1BinaryProtocolValue binaryProtocolValue = new XuguInt1BinaryProtocolValue();
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.TINY, binaryProtocolValue);
    }
    
    private static void setDoubleBinaryProtocolValue() {
        XuguDoubleBinaryProtocolValue binaryProtocolValue = new XuguDoubleBinaryProtocolValue();
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.DOUBLE, binaryProtocolValue);
    }
    
    private static void setFloatBinaryProtocolValue() {
        XuguFloatBinaryProtocolValue binaryProtocolValue = new XuguFloatBinaryProtocolValue();
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.FLOAT, binaryProtocolValue);
    }
    
    private static void setDateBinaryProtocolValue() {
        XuguDateBinaryProtocolValue binaryProtocolValue = new XuguDateBinaryProtocolValue();
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.DATE, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.DATETIME, binaryProtocolValue);
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.TIMESTAMP, binaryProtocolValue);
    }
    
    private static void setTimeBinaryProtocolValue() {
        XuguTimeBinaryProtocolValue binaryProtocolValue = new XuguTimeBinaryProtocolValue();
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.TIME, binaryProtocolValue);
    }
    
    private static void setNullBinaryProtocolValue() {
        BINARY_PROTOCOL_VALUES.put(XuguBinaryColumnType.NULL, null);
    }
    
    /**
     * Get binary protocol value.
     *
     * @param binaryColumnType binary column type
     * @return binary protocol value
     */
    public static XuguBinaryProtocolValue getBinaryProtocolValue(final BinaryColumnType binaryColumnType) {
        Preconditions.checkArgument(BINARY_PROTOCOL_VALUES.containsKey(binaryColumnType), "Cannot find MySQL type '%s' in column type when process binary protocol value", binaryColumnType);
        return BINARY_PROTOCOL_VALUES.get(binaryColumnType);
    }
}
