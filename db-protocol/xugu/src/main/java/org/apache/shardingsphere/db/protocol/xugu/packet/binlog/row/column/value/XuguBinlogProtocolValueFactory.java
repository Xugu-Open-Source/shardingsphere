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

package org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguBinaryColumnType;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.blob.XuguBlobBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.decimal.XuguDecimalBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.decimal.XuguDoubleBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.decimal.XuguFloatBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.integer.XuguBitBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.integer.XuguInt24BinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.integer.XuguLongBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.integer.XuguLongLongBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.integer.XuguShortBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.integer.XuguTinyBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.string.XuguJsonBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.string.XuguStringBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.string.XuguVarcharBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.time.XuguDateBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.time.XuguDatetime2BinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.time.XuguDatetimeBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.time.XuguTime2BinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.time.XuguTimeBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.time.XuguTimestamp2BinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.time.XuguTimestampBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.time.XuguYearBinlogProtocolValue;

import java.util.EnumMap;
import java.util.Map;

/**
 * Binlog protocol value factory of MySQL.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class XuguBinlogProtocolValueFactory {
    
    private static final Map<XuguBinaryColumnType, XuguBinlogProtocolValue> BINLOG_PROTOCOL_VALUES = new EnumMap<>(XuguBinaryColumnType.class);
    
    static {
        registerIntegerTypeValue();
        registerDecimalTypeValue();
        registerTimeTypeValue();
        registerStringTypeValue();
        registerBlobTypeValue();
    }
    
    private static void registerIntegerTypeValue() {
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.BIT, new XuguBitBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.TINY, new XuguTinyBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.SHORT, new XuguShortBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.INT24, new XuguInt24BinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.LONG, new XuguLongBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.LONGLONG, new XuguLongLongBinlogProtocolValue());
    }
    
    private static void registerDecimalTypeValue() {
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.NEWDECIMAL, new XuguDecimalBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.DOUBLE, new XuguDoubleBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.FLOAT, new XuguFloatBinlogProtocolValue());
    }
    
    private static void registerTimeTypeValue() {
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.YEAR, new XuguYearBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.DATE, new XuguDateBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.TIME, new XuguTimeBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.TIME2, new XuguTime2BinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.TIMESTAMP, new XuguTimestampBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.TIMESTAMP2, new XuguTimestamp2BinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.DATETIME, new XuguDatetimeBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.DATETIME2, new XuguDatetime2BinlogProtocolValue());
    }
    
    private static void registerStringTypeValue() {
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.STRING, new XuguStringBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.VARCHAR, new XuguVarcharBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.VAR_STRING, new XuguVarcharBinlogProtocolValue());
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.JSON, new XuguJsonBinlogProtocolValue());
    }
    
    private static void registerBlobTypeValue() {
        BINLOG_PROTOCOL_VALUES.put(XuguBinaryColumnType.BLOB, new XuguBlobBinlogProtocolValue());
    }
    
    /**
     * Get binlog protocol value.
     *
     * @param columnType column type
     * @return binlog protocol value
     */
    public static XuguBinlogProtocolValue getBinlogProtocolValue(final XuguBinaryColumnType columnType) {
        Preconditions.checkArgument(BINLOG_PROTOCOL_VALUES.containsKey(columnType), "Cannot find MySQL type '%s' in column type when process binlog protocol value", columnType);
        return BINLOG_PROTOCOL_VALUES.get(columnType);
    }
}
