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

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.db.protocol.binary.BinaryCell;
import org.apache.shardingsphere.db.protocol.binary.BinaryRow;
import org.apache.shardingsphere.db.protocol.xugu.packet.XuguPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.protocol.XuguBinaryProtocolValueFactory;
import org.apache.shardingsphere.db.protocol.xugu.payload.XuguPacketPayload;

/**
 * Binary result set row packet for MySQL.
 * 
 * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_binary_resultset.html#sect_protocol_binary_resultset_row">Binary Protocol Resultset Row</a>
 */
@RequiredArgsConstructor
public final class XuguBinaryResultSetRowPacket extends XuguPacket {
    
    private static final int PACKET_HEADER = 0x00;
    
    private static final int NULL_BITMAP_OFFSET = 2;
    
    private final BinaryRow row;
    
    @Override
    protected void write(final XuguPacketPayload payload) {
        payload.writeInt1(PACKET_HEADER);
        writeNullBitmap(payload);
        writeValues(payload);
    }
    
    private void writeNullBitmap(final XuguPacketPayload payload) {
        for (int each : getNullBitmap().getNullBitmap()) {
            payload.writeInt1(each);
        }
    }
    
    private XuguNullBitmap getNullBitmap() {
        XuguNullBitmap result = new XuguNullBitmap(row.getCells().size(), NULL_BITMAP_OFFSET);
        int index = 0;
        for (BinaryCell each : row.getCells()) {
            if (null == each.getData()) {
                result.setNullBit(index);
            }
            index++;
        }
        return result;
    }
    
    private void writeValues(final XuguPacketPayload payload) {
        for (BinaryCell each : row.getCells()) {
            Object data = each.getData();
            if (null != data) {
                XuguBinaryProtocolValueFactory.getBinaryProtocolValue(each.getColumnType()).write(payload, data);
            }
        }
    }
}
