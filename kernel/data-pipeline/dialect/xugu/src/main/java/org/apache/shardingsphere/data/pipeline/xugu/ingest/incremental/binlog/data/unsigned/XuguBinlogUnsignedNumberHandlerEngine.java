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

package org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.data.unsigned;

import lombok.NoArgsConstructor;
import org.apache.shardingsphere.data.pipeline.core.metadata.model.PipelineColumnMetaData;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.data.unsigned.impl.XuguBinlogUnsignedBigintHandler;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.data.unsigned.impl.XuguBinlogUnsignedIntHandler;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.data.unsigned.impl.XuguBinlogUnsignedMediumintHandler;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.data.unsigned.impl.XuguBinlogUnsignedSmallintHandler;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.binlog.data.unsigned.impl.XuguBinlogUnsignedTinyintHandler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * MySQL binlog unsigned number handler engine.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class XuguBinlogUnsignedNumberHandlerEngine {
    
    @SuppressWarnings("rawtypes")
    private static final Map<String, XuguBinlogUnsignedNumberHandler> HANDLERS = new HashMap<>();
    
    static {
        HANDLERS.put("TINYINT UNSIGNED", new XuguBinlogUnsignedTinyintHandler());
        HANDLERS.put("SMALLINT UNSIGNED", new XuguBinlogUnsignedSmallintHandler());
        HANDLERS.put("MEDIUMINT UNSIGNED", new XuguBinlogUnsignedMediumintHandler());
        HANDLERS.put("INT UNSIGNED", new XuguBinlogUnsignedIntHandler());
        HANDLERS.put("BIGINT UNSIGNED", new XuguBinlogUnsignedBigintHandler());
    }
    
    /**
     * Handle column value.
     *
     * @param columnMetaData column meta data
     * @param value column value
     * @return handled column value
     */
    @SuppressWarnings("unchecked")
    public static Optional<Serializable> handle(final PipelineColumnMetaData columnMetaData, final Serializable value) {
        String dataTypeName = columnMetaData.getDataTypeName();
        return HANDLERS.containsKey(dataTypeName) ? Optional.of(HANDLERS.get(dataTypeName).handle(value)) : Optional.empty();
    }
}
