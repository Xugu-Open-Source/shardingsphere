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

package org.apache.shardingsphere.sqlfederation.optimizer.sql.dialect;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.sql.SqlAlienSystemTypeNameSpec;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.dialect.MysqlSqlDialect;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Custom xugu SQL dialect.
 */
public final class CustomXuguSQLDialect extends MysqlSqlDialect {

    public static final SqlDialect DEFAULT = new CustomXuguSQLDialect(DEFAULT_CONTEXT);

    public CustomXuguSQLDialect(final Context context) {
        super(context);
    }
    
    @Override
    public void quoteStringLiteral(final StringBuilder builder, final String charsetName, final String value) {
        builder.append(literalQuoteString);
        builder.append(value.replace(literalEndQuoteString, literalEscapedQuote));
        builder.append(literalEndQuoteString);
    }

    @Override
    public @Nullable SqlNode getCastSpec(final RelDataType type) {
        switch (type.getSqlTypeName()) {
            case INTEGER:
            case BIGINT:
                return new SqlDataTypeSpec(
                        new SqlAlienSystemTypeNameSpec(
                                "BIGINT",
                                type.getSqlTypeName(),
                                SqlParserPos.ZERO),
                        SqlParserPos.ZERO);
            default:
                return super.getCastSpec(type);
        }
    }
}
