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

package org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.shardingsphere.db.protocol.xugu.packet.binlog.row.XuguBinlogTableMapEventPacket;

import java.util.Map;

/**
 * MySQL binlog context.
 */
@RequiredArgsConstructor
@Getter
@Setter
public final class XuguBinlogContext {
    
    private final int checksumLength;
    
    private final Map<Long, XuguBinlogTableMapEventPacket> tableMap;
    
    private volatile String fileName;
    
    /**
     * Cache table map event.
     *
     * @param tableMapEventPacket table map event
     */
    public void putTableMapEvent(final XuguBinlogTableMapEventPacket tableMapEventPacket) {
        tableMap.put(tableMapEventPacket.getTableId(), tableMapEventPacket);
    }
    
    /**
     * Get table map event by table id.
     *
     * @param tableId table id
     * @return table map event
     */
    public XuguBinlogTableMapEventPacket getTableMapEvent(final long tableId) {
        return tableMap.get(tableId);
    }
}
