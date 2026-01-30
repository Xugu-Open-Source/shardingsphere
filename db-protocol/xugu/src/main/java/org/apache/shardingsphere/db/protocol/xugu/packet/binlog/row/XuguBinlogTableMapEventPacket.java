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

package org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row;

import lombok.Getter;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguBinaryColumnType;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.AbstractXuguBinlogEventPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.XuguBinlogEventHeader;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.XuguBinlogColumnDef;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.XuguNullBitmap;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;

import java.util.LinkedList;
import java.util.List;

/**
 * MySQL binlog table map event packet.
 *
 * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/classbinary__log_1_1Table__map__event.html">TABLE_MAP_EVENT</a>
 */
@Getter
public final class XuguBinlogTableMapEventPacket extends AbstractXuguBinlogEventPacket {
    
    private final long tableId;
    
    private final int flags;
    
    private final String schemaName;
    
    private final String tableName;
    
    private final int columnCount;
    
    private final List<XuguBinlogColumnDef> columnDefs;
    
    private final XuguNullBitmap nullBitMap;
    
    public XuguBinlogTableMapEventPacket(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        super(binlogEventHeader);
        tableId = payload.readInt6();
        flags = payload.readInt2();
        schemaName = payload.readStringFix(payload.readInt1());
        payload.skipReserved(1);
        tableName = payload.readStringFix(payload.readInt1());
        payload.skipReserved(1);
        columnCount = (int) payload.readIntLenenc();
        columnDefs = new LinkedList<>();
        readColumnDefs(payload);
        readColumnMetaDefs(payload);
        nullBitMap = new XuguNullBitmap(columnCount, payload);
        // mysql 8 binlog table map event include column type def
        // for support lower than 8, read column type def by sql query
        // so skip readable bytes here
        int remainBytesLength = getRemainBytesLength(payload);
        if (remainBytesLength > 0) {
            payload.skipReserved(remainBytesLength);
        }
    }
    
    private void readColumnDefs(final XuguPacketPayload payload) {
        for (int i = 0; i < columnCount; i++) {
            columnDefs.add(new XuguBinlogColumnDef(XuguBinaryColumnType.valueOf(payload.readInt1())));
        }
    }
    
    private void readColumnMetaDefs(final XuguPacketPayload payload) {
        payload.readIntLenenc();
        for (XuguBinlogColumnDef each : columnDefs) {
            each.setColumnMeta(readColumnMetaDef(each.getColumnType(), payload));
        }
    }
    
    private int readColumnMetaDef(final XuguBinaryColumnType columnType, final XuguPacketPayload payload) {
        switch (columnType) {
            case STRING:
            case DECIMAL:
            case NEWDECIMAL:
                return payload.getByteBuf().readUnsignedShort();
            case BIT:
            case VAR_STRING:
            case VARCHAR:
            case ENUM:
                return payload.readInt2();
            case BLOB:
            case TINY_BLOB:
            case DOUBLE:
            case FLOAT:
            case TIME2:
            case TIMESTAMP2:
            case DATETIME2:
            case JSON:
                return payload.readInt1();
            default:
                return 0;
        }
    }
    
    @Override
    protected void writeEvent(final XuguPacketPayload payload) {
        // TODO
    }
}
