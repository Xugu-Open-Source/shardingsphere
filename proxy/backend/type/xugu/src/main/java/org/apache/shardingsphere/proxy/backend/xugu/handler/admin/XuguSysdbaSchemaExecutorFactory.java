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

package org.apache.shardingsphere.proxy.backend.xugu.handler.admin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.infra.metadata.database.schema.manager.SystemSchemaManager;
import org.apache.shardingsphere.proxy.backend.handler.admin.executor.AbstractDatabaseMetaDataExecutor.DefaultDatabaseMetaDataExecutor;
import org.apache.shardingsphere.proxy.backend.handler.admin.executor.DatabaseAdminExecutor;
import org.apache.shardingsphere.proxy.backend.xugu.handler.admin.executor.sysdba.SelectSysdbaSchemataExecutor;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table.JoinTableSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table.SimpleTableSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table.TableSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.dml.SelectStatement;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Construct the sysdba schema executor's factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class XuguSysdbaSchemaExecutorFactory {

    private static final String[] SCHEMATA_TABLE = new String[]{"all_schemas", "dba_schemas", "user_schemas"};

    private static final String[] SYSTEM_TABLES = new String[]{
            "all_columns", "all_tables", "all_schemas", "all_databases",
            "all_indexes", "all_constraints", "all_views", "all_view_columns",
            "dba_columns", "dba_tables", "dba_schemas", "dba_databases",
            "user_columns", "user_tables", "user_schemas", "user_databases"
    };

    /**
     * Create executor.
     *
     * @param sqlStatement SQL statement
     * @param sql SQL being executed
     * @param parameters parameters
     * @return executor
     */
    public static Optional<DatabaseAdminExecutor> newInstance(final SelectStatement sqlStatement, final String sql, final List<Object> parameters) {
        if (!sqlStatement.getFrom().isPresent()) {
            return Optional.empty();
        }
        if (sqlStatement.getFrom().get() instanceof SimpleTableSegment) {
            String tableName = ((SimpleTableSegment) sqlStatement.getFrom().get()).getTableName().getIdentifier().getValue();
            if (Arrays.stream(SCHEMATA_TABLE).anyMatch(e -> e.equalsIgnoreCase(tableName))) {
                return Optional.of(new SelectSysdbaSchemataExecutor(sqlStatement, sql, parameters));
            }
            if (SystemSchemaManager.isSystemTable("xugu", "sysdba", tableName)) {
                return Optional.of(new DefaultDatabaseMetaDataExecutor(sql, parameters));
            }
        }
        if (isSystemTableQuery(sqlStatement)) {
            return Optional.of(new DefaultDatabaseMetaDataExecutor(sql, parameters));
        }
        return Optional.empty();
    }

    private static boolean isSystemTableQuery(final SelectStatement sqlStatement) {
        Collection<String> tableNames = getTableNames(sqlStatement.getFrom().get());
        return tableNames.stream().anyMatch(each -> Arrays.stream(SYSTEM_TABLES).anyMatch(systemTable -> systemTable.equalsIgnoreCase(each))
                || SystemSchemaManager.isSystemTable("xugu", "sysdba", each));
    }

    private static Collection<String> getTableNames(final TableSegment tableSegment) {
        Collection<String> result = new LinkedList<>();
        if (tableSegment instanceof SimpleTableSegment) {
            result.add(((SimpleTableSegment) tableSegment).getTableName().getIdentifier().getValue());
        }
        if (tableSegment instanceof JoinTableSegment) {
            JoinTableSegment joinTableSegment = (JoinTableSegment) tableSegment;
            result.addAll(getTableNames(joinTableSegment.getLeft()));
            result.addAll(getTableNames(joinTableSegment.getRight()));
        }
        return result;
    }
}
