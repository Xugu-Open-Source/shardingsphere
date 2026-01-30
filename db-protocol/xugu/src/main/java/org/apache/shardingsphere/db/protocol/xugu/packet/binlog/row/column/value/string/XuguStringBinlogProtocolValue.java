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

package org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.string;

import org.apache.shardingsphere.db.protocol.xugu.constant.XuguBinaryColumnType;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.XuguBinlogColumnDef;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.XuguBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;
import org.apache.shardingsphere.infra.exception.generic.UnsupportedSQLOperationException;

import java.io.Serializable;

/**
 * STRING type value of MySQL binlog protocol.
 */
public final class XuguStringBinlogProtocolValue implements XuguBinlogProtocolValue {
    
    @Override
    public Serializable read(final XuguBinlogColumnDef columnDef, final XuguPacketPayload payload) {
        int type = columnDef.getColumnMeta() >> 8;
        int length = columnDef.getColumnMeta() & 0xff;
        // unpack type & length, see https://bugs.mysql.com/bug.php?id=37426.
        if (0x30 != (type & 0x30)) {
            length += ((type & 0x30) ^ 0x30) << 4;
            type |= 0x30;
        }
        switch (XuguBinaryColumnType.valueOf(type)) {
            case ENUM:
                return readEnumValue(length, payload);
            case SET:
                return payload.getByteBuf().readByte();
            case STRING:
                return new XuguBinaryString(payload.readStringFixByBytes(readActualLength(length, payload)));
            default:
                throw new UnsupportedSQLOperationException(XuguBinaryColumnType.valueOf(type).toString());
        }
    }
    
    private int readActualLength(final int length, final XuguPacketPayload payload) {
        return length < 256 ? payload.getByteBuf().readUnsignedByte() : payload.getByteBuf().readUnsignedShortLE();
    }
    
    private Serializable readEnumValue(final int meta, final XuguPacketPayload payload) {
        switch (meta) {
            case 1:
                return payload.readInt1();
            case 2:
                return payload.readInt2();
            default:
                throw new UnsupportedSQLOperationException(String.format("MySQL Enum meta in binlog only include value 1 or 2, but actual is %s", meta));
        }
    }
}
