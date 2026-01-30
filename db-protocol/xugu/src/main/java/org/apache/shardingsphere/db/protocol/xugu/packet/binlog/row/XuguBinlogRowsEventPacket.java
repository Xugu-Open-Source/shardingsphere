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
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguBinlogEventType;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.AbstractXuguBinlogEventPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.XuguBinlogEventHeader;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.XuguBinlogColumnDef;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.column.value.XuguBinlogProtocolValueFactory;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.XuguNullBitmap;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * MySQL binlog rows event packet.
 *
 * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/classbinary__log_1_1Rows__event.html">ROWS_EVENT</a>
 * @see <a href="https://mariadb.com/kb/en/rows_event_v1v2-rows_compressed_event_v1/">ROWS_EVENT</a>
 */
@Getter
public final class XuguBinlogRowsEventPacket extends AbstractXuguBinlogEventPacket {
    
    private final long tableId;
    
    private final int flags;
    
    private final int columnNumber;
    
    private final XuguNullBitmap columnsPresentBitmap;
    
    private final XuguNullBitmap columnsPresentBitmap2;
    
    private final List<Serializable[]> rows = new LinkedList<>();
    
    private final List<Serializable[]> rows2 = new LinkedList<>();
    
    public XuguBinlogRowsEventPacket(final XuguBinlogEventHeader binlogEventHeader, final XuguPacketPayload payload) {
        super(binlogEventHeader);
        tableId = payload.readInt6();
        flags = payload.readInt2();
        skipExtraData(payload);
        columnNumber = (int) payload.readIntLenenc();
        columnsPresentBitmap = new XuguNullBitmap(columnNumber, payload);
        columnsPresentBitmap2 = readUpdateColumnsPresentBitmap(payload);
    }
    
    private void skipExtraData(final XuguPacketPayload payload) {
        if (isRowsEventVersion2(getBinlogEventHeader().getEventType())) {
            int extraDataLength = payload.readInt2() - 2;
            payload.skipReserved(extraDataLength);
        }
    }
    
    private boolean isRowsEventVersion2(final int eventType) {
        return XuguBinlogEventType.WRITE_ROWS_EVENT_V2.getValue() == eventType || XuguBinlogEventType.UPDATE_ROWS_EVENT_V2.getValue() == eventType
                || XuguBinlogEventType.DELETE_ROWS_EVENT_V2.getValue() == eventType;
    }
    
    private XuguNullBitmap readUpdateColumnsPresentBitmap(final XuguPacketPayload payload) {
        return isUpdateRowsEvent(getBinlogEventHeader().getEventType()) ? new XuguNullBitmap(columnNumber, payload) : null;
    }
    
    private boolean isUpdateRowsEvent(final int eventType) {
        return XuguBinlogEventType.UPDATE_ROWS_EVENT_V2.getValue() == eventType || XuguBinlogEventType.UPDATE_ROWS_EVENT_V1.getValue() == eventType;
    }
    
    /**
     * Read rows in binlog.
     *
     * @param tableMapEventPacket TABLE_MAP_EVENT packet before this ROWS_EVENT
     * @param payload ROWS_EVENT packet payload
     */
    public void readRows(final XuguBinlogTableMapEventPacket tableMapEventPacket, final XuguPacketPayload payload) {
        List<XuguBinlogColumnDef> columnDefs = tableMapEventPacket.getColumnDefs();
        while (getRemainBytesLength(payload) > 0) {
            rows.add(readRow(columnDefs, payload));
            if (isUpdateRowsEvent(getBinlogEventHeader().getEventType())) {
                rows2.add(readRow(columnDefs, payload));
            }
        }
    }
    
    private Serializable[] readRow(final List<XuguBinlogColumnDef> columnDefs, final XuguPacketPayload payload) {
        XuguNullBitmap nullBitmap = new XuguNullBitmap(columnNumber, payload);
        Serializable[] result = new Serializable[columnNumber];
        for (int i = 0; i < columnNumber; i++) {
            XuguBinlogColumnDef columnDef = columnDefs.get(i);
            result[i] = nullBitmap.isNullParameter(i) ? null : XuguBinlogProtocolValueFactory.getBinlogProtocolValue(columnDef.getColumnType()).read(columnDef, payload);
        }
        return result;
    }
    
    @Override
    protected void writeEvent(final XuguPacketPayload payload) {
        // TODO
    }
}
