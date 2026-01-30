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

package org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.blob;

import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.XuguBinlogColumnDef;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.XuguBinlogProtocolValue;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;
import org.apache.shardingsphere.infra.exception.generic.UnsupportedSQLOperationException;

import java.io.Serializable;

/**
 * BLOB type value of MySQL binlog protocol.
 */
public final class XuguBlobBinlogProtocolValue implements XuguBinlogProtocolValue {
    
    @Override
    public Serializable read(final XuguBinlogColumnDef columnDef, final XuguPacketPayload payload) {
        return payload.readStringFixByBytes(readLengthFromMeta(columnDef.getColumnMeta(), payload));
    }
    
    private int readLengthFromMeta(final int columnMeta, final XuguPacketPayload payload) {
        switch (columnMeta) {
            case 1:
                return payload.getByteBuf().readUnsignedByte();
            case 2:
                return payload.getByteBuf().readUnsignedShortLE();
            case 3:
                return payload.getByteBuf().readUnsignedMediumLE();
            case 4:
                return payload.readInt4();
            default:
                throw new UnsupportedSQLOperationException(String.format("MySQL BLOB type meta in binlog should be range 1 to 4, but actual value is: %s", columnMeta));
        }
    }
}
