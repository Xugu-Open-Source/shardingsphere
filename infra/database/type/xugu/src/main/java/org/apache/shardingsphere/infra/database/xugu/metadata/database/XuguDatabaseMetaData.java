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

package org.apache.shardingsphere.infra.database.xugu.metadata.database;

import org.apache.shardingsphere.infra.database.core.metadata.database.DialectDatabaseMetaData;
import org.apache.shardingsphere.infra.database.core.metadata.database.enums.NullsOrderType;
import org.apache.shardingsphere.infra.database.core.metadata.database.enums.QuoteCharacter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Database meta data of XuguDB.
 */
public final class XuguDatabaseMetaData implements DialectDatabaseMetaData {
    
    private static final Set<String> RESERVED_WORDS = new HashSet<>(Arrays.asList("ABORT", "ABOVE", "ABSOLUTE", "ACCESS", "ACCOUNT", "ACTION", "ADD", "AFTER", "AGGREGATE", "ALL", "ALL_ROWS", "ALTER",
            "ANALYSE", "ANALYZE", "AND", "ANY", "AOVERLAPS", "APPEND", "ARCHIVELOG", "ARE", "ARRAY", "AS", "ASC", "AT", "AUDIT", "AUDITOR", "AUTHID", "AUTHORIZATION", "AUTO", "AUTO_INCREMENT",
            "BACKUP", "BACKWARD", "BADFILE", "BCONTAINS", "BEFORE", "BEGIN", "BETWEEN", "BINARY", "BINTERSECTS", "BIT", "BLOCK", "BLOCKS", "BODY", "BOTH", "BOUND", "BOVERLAPS", "BREAK", "BUFFER_POOL",
            "BUILD", "BULK", "BWITHIN", "BY", "CACHE", "CALL", "CASCADE", "CASE", "CAST", "CATCH", "CATEGORY", "CHAIN", "CHAR", "CHARACTER", "CHARACTERISTICS", "CHECK", "CHECKPOINT", "CHOOSE",
            "CHUNK",
            "CLOSE", "CLUSTER", "COALESCE", "COLLATE", "COLLECT", "COLUMN", "COLUMNS", "COMMENT", "COMMIT", "COMMITTED", "COMPLETE", "COMPRESS", "COMPUTE", "CONNECT", "CONNECT_NODES", "CONSTANT",
            "CONSTRAINT",
            "CONSTRAINTS", "CONSTRUCTOR", "CONTAINS", "CONTENT", "CONTEXT", "CONTINUE", "COPY", "CORRESPONDING", "CPU_PER_CALL", "CPU_PER_SESSION", "CREATE", "CREATEDB", "CREATEUSER", "CROSS",
            "CROSSES", "CUBE",
            "CURRENT", "CURSOR", "CURSOR_QUOTA", "CYCLE", "DATABASE", "DATAFILE", "DATE", "DATETIME", "DAY", "DBA", "DBA_RECYCLEBIN", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DECODE", "DECRYPT",
            "DEFAULT",
            "DEFERRABLE", "DEFERRED", "DELETE", "DELIMITED", "DELIMITERS", "DEMAND", "DESC", "DESCRIBE", "DETERMINISTIC", "DIR", "DISABLE", "DISASSEMBLE", "DISCORDFILE", "DISJOINT", "DISTINCT", "DO",
            "DOMAIN",
            "DOUBLE", "DRIVEN", "DROP", "EACH", "ELEMENT", "ELSE", "ELSEIF", "ELSIF", "EMPTY", "ENABLE", "ENCODING", "ENCRYPT", "ENCRYPTOR", "END", "ENDCASE", "ENDFOR", "ENDIF", "ENDLOOP", "EQUALS",
            "ERROR",
            "ESCAPE", "EVERY", "EXCEPT", "EXCEPTION", "EXCEPTIONS", "EXCEPTION_INIT", "EXCLUDE", "EXCLUSIVE", "EXEC", "EXECUTE", "EXISTS", "EXIT", "EXPIRE", "EXPLAIN", "EXPORT", "EXTEND", "EXTERNAL",
            "EXTRACT",
            "FAILED_LOGIN_ATTEMPTS", "FALSE", "FAST", "FETCH", "FIELD", "FIELDS", "FILTER", "FINAL", "FINALLY", "FIRST", "FIRST_ROWS", "FLASHBACK", "FLOAT", "FOLLOWING", "FOR", "FORALL", "FORCE",
            "FOREIGN",
            "FORWARD", "FOUND", "FREELIST", "FREELISTS", "FROM", "FULL", "FUNCTION", "G", "GENERATED", "GET", "GET_FORMAT", "GLOBAL", "GOTO", "GRANT", "GREATEST", "GROUP", "GROUPING", "GROUPS",
            "GROUP_CONCAT",
            "HANDLER", "HASH", "HAVING", "HEAP", "HIDE", "HINT", "HOTSPOT", "HOUR", "IDENTIFIED", "IDENTIFIER", "IDENTITY", "IF", "IFNULL", "IGNORE", "ILIKE", "IMMEDIATE", "IMPORT", "IN", "INCLUDE",
            "INCREMENT", "INDEX", "INDEXTYPE", "INDEX_ASC", "INDEX_DESC", "INDEX_FSS", "INDEX_JOIN", "INDICATOR", "INDICES", "INHERITS", "INIT", "INITIAL", "INITIALLY", "INITRANS", "INNER", "INOUT",
            "INSENSITIVE",
            "INSERT", "INSTANTIABLE", "INSTEAD", "INTERSECT", "INTERSECTS", "INTERVAL", "INTO", "IO", "IS", "ISNULL", "ISOLATION", "ISOPEN", "JOB", "JOIN", "JSON_ARRAYAGG", "JSON_OBJECTAGG",
            "JSON_VALUE",
            "K", "KEEP", "KEY", "KEYSET", "LABEL", "LANGUAGE", "LAST", "LEADING", "LEAST", "LEAVE", "LEFT", "LEFTOF", "LENGTH", "LESS", "LEVEL", "LEVELS", "LEXER", "LIBRARY", "LIKE", "LIMIT", "LINK",
            "LIST",
            "LISTAGG", "LISTEN", "LOAD", "LOB", "LOCAL", "LOCATION", "LOCATOR", "LOCK", "LOGFILE", "LOGGING", "LOGIN", "LOGOFF", "LOGON", "LOGOUT", "LOOP", "LOVERLAPS", "M", "MATCH", "MATCHED",
            "MATERIALIZED",
            "MAX", "MAXEXTENTS", "MAXSIZE", "MAXTRANS", "MAXVALUE", "MAXVALUES", "MAX_CONNECT_TIME", "MAX_IDLE_TIME", "MAX_STORE_NUM", "MEMBER", "MEMORY", "MERGE", "MICROSECOND", "MINEXTENTS",
            "MINUS",
            "MINUTE", "MINVALUE", "MISSING", "MODE", "MODIFY", "MONTH", "MOVEMENT", "NAME", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NESTED", "NEW", "NEWLINE", "NEXT", "NO", "NOAPPEND",
            "NOARCHIVELOG", "NOAUDIT",
            "NOCACHE", "NOCOMPRESS", "NOCREATEDB", "NOCREATEUSER", "NOCYCLE", "NODE", "NOFORCE", "NOFOUND", "NOINDEX", "NOLOGGING", "NOMAXVALUE", "NOMINVALUE", "NONE", "NOORDER", "NOPARALLEL", "NOT",
            "NOTFOUND",
            "NOTHING", "NOTIFY", "NOTNULL", "NOVALIDATE", "NOWAIT", "NULL", "NULLIF", "NULLS", "NUMBER", "NUMERIC", "NVARCHAR", "NVARCHAR2", "NVL", "NVL2", "OBJECT", "OF", "OFF", "OFFLINE", "OFFSET",
            "OIDINDEX",
            "OIDS", "OLD", "ON", "ONLINE", "ONLY", "OPEN", "OPERATOR", "OPTION", "OR", "ORDER", "ORDERD", "ORGANIZATION", "OTHERVALUES", "OUT", "OUTER", "OVER", "OVERLAPS", "OWNER", "PACKAGE",
            "PARALLEL",
            "PARAMETERS", "PARTIAL", "PARTITION", "PARTITIONS", "PASSING", "PASSWORD", "PASSWORD_LIFE_PERIOD", "PASSWORD_LOCK_TIME", "PATH", "PCTFREE", "PCTINCREASE", "PCTUSED", "PCTVERSION",
            "PERIOD", "PIPE",
            "PIPELINED", "PIVOT", "PLACING", "POLICY", "PRAGMA", "PREBUILT", "PRECEDING", "PRECISION", "PREPARE", "PRESERVE", "PRIMARY", "PRIOR", "PRIORITY", "PRIVATE_SGA", "PRIVILEGES", "PROCEDURAL",
            "PROCEDURE",
            "PROFILE", "PROTECTED", "PUBLIC", "PURGE", "QUARTER", "QUERY", "QUOTA", "RAISE", "RANGE", "RAW", "READ", "READS", "READS_PER_CALL", "READS_PER_SESSION", "REBUILD", "RECOMPILE", "RECORD",
            "RECORDS",
            "RECYCLE", "RECYCLEBIN", "REDUCED", "REF", "REFERENCES", "REFERENCING", "REFRESH", "REINDEX", "RELATIVE", "RELEASE", "RENAME", "REOPEN", "REPEATABLE", "REPLACE", "REPLICATION", "RESOURCE",
            "RESTART",
            "RESTORE", "RESTRICT", "RESULT", "RESULT_CACHE", "RETURN", "RETURNING", "REVERSE", "REVOKE", "REWRITE", "RIGHT", "RIGHTOF", "ROLE", "ROLLBACK", "ROLLUP", "ROVERLAPS", "ROW", "ROWCOUNT",
            "ROWID", "ROWS",
            "ROWTYPE", "RULE", "RUN", "SAVEPOINT", "SCHEMA", "SCROLL", "SECOND", "SEGMENT", "SELECT", "SELF", "SEPARATOR", "SEQUENCE", "SERIALIZABLE", "SESSION", "SESSION_PER_USER", "SET", "SETOF",
            "SETS", "SHARE",
            "SHOW", "SHUTDOWN", "SIBLINGS", "SIZE", "SLOW", "SNAPSHOT", "SOME", "SPATIAL", "SPLIT", "SSO", "STANDBY", "START", "STATEMENT", "STATIC", "STATISTICS", "STEP", "STOP", "STORAGE", "STORE",
            "STORE_NODES",
            "STREAM", "SUBPARTITION", "SUBPARTITIONS", "SUBTYPE", "SUCCESSFUL", "SYNONYM", "SYSARGS", "SYSTEM", "TABLE", "TABLESPACE", "TEMP", "TEMPLATE", "TEMPORARY", "TEMPSPACE_QUOTA", "TERMINATED",
            "THAN",
            "THEN", "THROW", "TIME", "TIMESTAMP", "TIMESTAMPADD", "TIMESTAMPDIFF", "TO", "TOP", "TOPOVERLAPS", "TOTAL_RESOURCE_LIMIT", "TOUCHES", "TRACE", "TRAILING", "TRAN", "TRANSACTION", "TRIGGER",
            "TRUE",
            "TRUNCATE", "TRUSTED", "TRY", "TYPE", "UNBOUNDED", "UNDER", "UNDO", "UNIFORM", "UNION", "UNIQUE", "UNLIMITED", "UNLISTEN", "UNLOCK", "UNPIVOT", "UNPROTECTED", "UNTIL", "UOVERLAPS",
            "UPDATE", "USE",
            "USER", "USE_HASH", "USING", "VACUUM", "VALID", "VALIDATE", "VALUE", "VALUES", "VARBIT", "VARCHAR", "VARCHAR2", "VARRAY", "VARYING", "VERBOSE", "VERSION", "VIEW", "VOCABLE", "WAIT",
            "WEEK", "WHEN",
            "WHENEVER", "WHERE", "WHILE", "WITH", "WITHIN", "WITHOUT", "WORK", "WRITE", "XML", "XMLATTRIBUTES", "XMLCAST", "XMLELEMENT", "XMLEXISTS", "XMLFOREST", "XMLQUERY", "XMLTABLE", "YEAR",
            "ZONE"));
    
    @Override
    public QuoteCharacter getQuoteCharacter() {
        return QuoteCharacter.BACK_QUOTE;
    }
    
    @Override
    public Map<String, Integer> getExtraDataTypes() {
        Map<String, Integer> result = new HashMap<>(16, 1F);
        result.put("TINYINT", Types.TINYINT);
        result.put("SMALLINT", Types.SMALLINT);
        result.put("SHORT", Types.SMALLINT);
        result.put("INT", Types.INTEGER);
        result.put("BINARY_INTEGER", Types.INTEGER);
        result.put("LONGINT", Types.BIGINT);
        result.put("TEXT", Types.LONGVARCHAR);
        result.put("CHARACTER", Types.CHAR);
        result.put("NCHAR", Types.CHAR);
        result.put("VARCHAR2", Types.VARCHAR);
        result.put("DATETIME", Types.TIMESTAMP);
        result.put("YEAR", Types.DATE);
        result.put("DOUBLE", Types.DOUBLE);
        result.put("FLOAT", Types.FLOAT);
        result.put("NUMBER", Types.NUMERIC);
        return result;
    }
    
    @Override
    public NullsOrderType getDefaultNullsOrderType() {
        return NullsOrderType.HIGH;
    }
    
    @Override
    public boolean isReservedWord(final String identifier) {
        return RESERVED_WORDS.contains(identifier.toUpperCase());
    }
    
    @Override
    public boolean isSchemaAvailable() {
        return true;
    }
    
    @Override
    public String getSchema(final Connection connection) {
        try {
            return Optional.ofNullable(connection.getMetaData().getUserName()).map(String::toUpperCase).orElse(null);
        } catch (final SQLException ignored) {
            return null;
        }
    }
    
    @Override
    public String formatTableNamePattern(final String tableNamePattern) {
        return tableNamePattern.toUpperCase();
    }
    
    @Override
    public String getDatabaseType() {
        return "XuGu";
    }
}
