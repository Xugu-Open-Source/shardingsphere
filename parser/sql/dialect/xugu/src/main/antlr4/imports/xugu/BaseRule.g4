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

grammar BaseRule;

import Comments, Symbol, Keyword, XuguKeyword, Literals;

parameterMarker
    : QUESTION_
    ;

customKeyword
    : MAX
    | MIN
    | SUM
    | COUNT
    | GROUP_CONCAT
    | CAST
    | POSITION
    | SUBSTRING
    | SUBSTR
    | MID
    | EXTRACT
    | TRIM
    | LAST_DAY
    | TRADITIONAL
    | TREE
    | MYSQL_ADMIN
    | INSTANT
    | INPLACE
    | COPY
    | UL_BINARY
    | AUTOCOMMIT
    | ARCHIVE
    | BLACKHOLE
    | CSV
    | FEDERATED
    | INNODB
    | MEMORY
    | MRG_MYISAM
    | MYISAM
    | NDB
    | NDBCLUSTER
    | PERFORMANCE_SCHEMA
    | TOKUDB
    | REDO_LOG
    | LAST_VALUE
    | PRIMARY
    | MAXVALUE
    | BIT_AND
    | BIT_OR
    | BIT_XOR
    | MYSQL_MAIN
    | RANGE
    | UTC_DATE
    | UTC_TIME
    | UTC_TIMESTAMP
    | UTC_TIMESTAMP
    ;

literals
    : stringLiterals
    | numberLiterals
    | temporalLiterals
    | hexadecimalLiterals
    | bitValueLiterals
    | booleanLiterals
    | nullValueLiterals
    ;

string_
    : DOUBLE_QUOTED_TEXT | SINGLE_QUOTED_TEXT
    ;

stringLiterals
    : (UNDERSCORE_CHARSET | UL_BINARY )? string_ | NCHAR_TEXT
    ;

numberLiterals
    : (PLUS_ | MINUS_)? NUMBER_
    ;

temporalLiterals
    : (DATE | TIME | TIMESTAMP) textString
    ;

hexadecimalLiterals
    : UNDERSCORE_CHARSET? UL_BINARY? HEX_DIGIT_ collateClause?
    ;

bitValueLiterals
    : UNDERSCORE_CHARSET? BIT_NUM_ collateClause?
    ;

booleanLiterals
    : TRUE | FALSE
    ;

nullValueLiterals
    : NULL
    ;

collationName
    : textOrIdentifier | BINARY
    ;

identifier
    : IDENTIFIER_
    | identifierKeywordsUnambiguous
    | identifierKeywordsAmbiguous1RolesAndLabels
    | identifierKeywordsAmbiguous2Labels
    | identifierKeywordsAmbiguous3Roles
    | identifierKeywordsAmbiguous4SystemVariables
    | customKeyword
    | DOUBLE_QUOTED_TEXT
    | UNDERSCORE_CHARSET
    | BQUOTA_STRING
    ;

identifierKeywordsUnambiguous
    : ABORT
    | ACTION
    | ACCESS
    | ACCOUNT
    | ACCOUNT
    | ACTIVE
//    | ADDDATE
    | ADD
    | ADMIN
    | AFTER
    | AGAINST
    | AGGREGATE
    | ALGORITHM
    | ALWAYS
    | ANALYZE
    | ANY
    | ARRAY
    | AT
    | ATTRIBUTE
    | AUTHID
    | AUTOEXTEND_SIZE
    | AUTO_INCREMENT
    | AUTHENTICATION
    | AUTO
    | AVG_ROW_LENGTH
    | AVG
    | BACKUP
    | BEFORE
    | BERNOULLI
    | BINARY
    | BINLOG
    | BIT
    | BLOCK
    | BODY
    | BOOLEAN
    | BOOL
    | BOTH
    | BTREE
    | BUCKETS
    | BULK
    | BY
    | CALL
    | CASCADE
    | CASCADED
    | CATALOG_NAME
    | CHAIN
    | CHALLENGE_RESPONSE
    | CHANGED
    | CHANNEL
    | CHECKPOINT
    | CIPHER
    | CLASS_ORIGIN
    | CLIENT
    | CLOSE
    | COALESCE
    | CODE
    | COLLATE
    | COLLATION
    | COLLECT
    | COLUMN
    | COLUMNS
    | COLUMN_FORMAT
    | COLUMN_NAME
    | COMMITTED
    | COMPACT
    | COMPLETION
    | COMPONENT
    | COMPRESSED
    | COMPRESSION
    | CONCURRENT
    | CONNECTION
    | CONSISTENT
    | CONSTRAINTS
    | CONSTRAINT_CATALOG
    | CONSTRAINT_NAME
    | CONSTRAINT_SCHEMA
    | CONTEXT
    | CPU
    | CREATE
    | CURRENT
    | CURSOR_NAME
    | CYCLE
    | DATABASE
    | DATAFILE
    | DATA
    | DATETIME
    | DATE
    | DAY
    | DAY_MINUTE
    | DEFAULT_AUTH
    | DEFAULT
    | DEFINER
    | DEFINITION
    | DELAY_KEY_WRITE
    | DELIMITED
    | DESCRIPTION
    | DETERMINISTIC
    | DIAGNOSTICS
    | DIR
    | DIRECTORY
    | DISABLE
    | DISASSEMBLE
    | DISCARD
    | DISK
    | DUMPFILE
    | DUPLICATE
    | DROP
    | DYNAMIC
    | EACH
    | EMPTY
    | ENABLE
    | ENCRYPT
    | ENCRYPTION
    | ENDS
    | ENFORCED
    | ENGINES
    | ENGINE
    | ENGINE_ATTRIBUTE
    | ENUM
    | ERRORS
    | ERROR
    | ESCAPE
    | EVENTS
    | EVERY
    | EXCHANGE
    | EXCLUDE
    | EXCLUSIVE
    | EXEC
    | EXPANSION
    | EXPIRE
    | EXPORT
    | EXTENDED
    | EXTENT_SIZE
    | EXTERNAL
    | FACTOR
    | FAILED_LOGIN_ATTEMPTS
    | FAST
    | FAULTS
    | FIELDS
    | FILE_BLOCK_SIZE
    | FILTER
    | FINISH
    | FIRST
    | FIXED
    | FLASHBACK
    | FOLLOWING
    | FORALL
    | FORCE
    | FORMAT
    | FOUND
    | FULL
    | GENERAL
    | GENERATED
    | GEOMETRYCOLLECTION
    | GEOMETRY
    | GET
    | GET_FORMAT
    | GET_MASTER_PUBLIC_KEY
    | GLOBAL
    | GOTO
    | GRANTS
    | GROUP_REPLICATION
    | GROUPS
    | GTIDS
    | HASH
    | HEAP
    | HISTOGRAM
    | HISTORY
    | HOSTS
    | HOST
    | HOTSPOT
    | HOUR
    | IDENTIFIED
    | IDENTITY
    | IGNORE
    | IGNORE_SERVER_IDS
    | INACTIVE
    | INCLUDE
    | INCREMENT
    | INDEXES
    | INDEXTYPE
    | INDICES
    | INITIAL
    | INITIAL_SIZE
    | INOUT
    | INSENSITIVE
    | INSERT
    | INSERT_METHOD
    | INSTANCE
    | INSTEAD
    | INVISIBLE
    | INVOKER
    | IO
    | IPC
    | ISOLATION
    | ISSUER
    | JOB
    | JSON
    | JSON_VALUE
    | KEEP
    | KEY
    | KEYS
    | KEY_BLOCK_SIZE
    | KEYRING
    | KILL
    | LAST
    | LEAVES
    | LESS
    | LEVEL
    | LINESTRING
    | LIST
    | LOAD
    | LOCATION
    | LOCKED
    | LOCKS
    | LOGFILE
    | LOGS
    | LOOP
    | MANUAL
    | MATCH
    | MAXVALUE
    | MASTER_AUTO_POSITION
    | MASTER_COMPRESSION_ALGORITHM
    | MASTER_CONNECT_RETRY
    | MASTER_DELAY
    | MASTER_HEARTBEAT_PERIOD
    | MASTER_HOST
    | NETWORK_NAMESPACE
    | MASTER_LOG_FILE
    | MASTER_LOG_POS
    | MASTER_PASSWORD
    | MASTER_PORT
    | MASTER_PUBLIC_KEY_PATH
    | MASTER_RETRY_COUNT
    | MASTER_SERVER_ID
    | MASTER_SSL_CAPATH
    | MASTER_SSL_CA
    | MASTER_SSL_CERT
    | MASTER_SSL_CIPHER
    | MASTER_SSL_CRLPATH
    | MASTER_SSL_CRL
    | MASTER_SSL_KEY
    | MASTER_SSL
    | MASTER
    | MASTER_TLS_CIPHERSUITES
    | MASTER_TLS_VERSION
    | MASTER_USER
    | MASTER_ZSTD_COMPRESSION_LEVEL
    | MAX_CONNECTIONS_PER_HOUR
    | MAX_QUERIES_PER_HOUR
    | MAX_ROWS
    | MAX_SIZE
    | MAX_UPDATES_PER_HOUR
    | MAX_USER_CONNECTIONS
    | MEDIUM
    | MEMBER
    | MEMORY
    | MERGE
    | MESSAGE_TEXT
    | MICROSECOND
    | MIGRATE
    | MINUTE
    | MINVALUE
    | MIN_ROWS
    | MODE
    | MODIFY
    | MODIFIES
    | MONTH
    | MULTILINESTRING
    | MULTIPOINT
    | MULTIPOLYGON
    | MUTEX
    | MYSQL_ERRNO
    | NAMES
    | NAME
    | NATIONAL
    | NATURAL
    | NCHAR
    | NDBCLUSTER
    | NESTED
    | NEVER
    | NEW
    | NEXT
    | NOCACHE
    | NOCOMPRESS
    | NODE
    | NODEGROUP
    | NOFORCE
    | NOMAXVALUE
    | NOMINVALUE
    | NOWAIT
    | NO_WAIT
    | NULLS
    | NUMBER
    | NVARCHAR
    | OBJECT
    | OF
    | OFF
    | OFFLINE
    | OFFSET
    | OJ
    | OLD
    | ONE
    | ONLINE
    | ONLY
    | OPEN
    | OPTION
    | OPTIONAL
    | OPTIONS
    | ORDINALITY
    | ORGANIZATION
    | OTHERS
    | OUTER
    | OWNER
    | PACKAGE
    | PACK_KEYS
    | PAGE
    | PARALLEL
    | PARAMETERS
    | PARSER
    | PARTIAL
    | PARSE_TREE
    | PARTITIONING
    | PARTITIONS
    | PASSING
    | PASSWORD
    | PASSWORD_LOCK_TIME
    | PATH
    | PCTFREE
    | PCTUSED
    | PHASE
    | PIPELINED
    | PIVOT
    | PLUGINS
    | PLUGIN_DIR
    | PLUGIN
    | POINT
    | POLYGON
    | PORT
    | PRECEDING
    | PRECISION
    | PRESERVE
    | PREV
    | PRIVILEGES
    | PRIVILEGE_CHECKS_USER
    | PROCESSLIST
    | PROFILES
    | PROFILE
    | PUBLIC
    | QUALIFY
    | QUARTER
    | QUERY
    | QUICK
    | RANDOM
    | RANK
    | READ
    | READS
    | READ_ONLY
    | REBUILD
    | RECOMPILE
    | RECORD
    | RECORDS
    | RECOVER
    | REDO_BUFFER_SIZE
    | REDUNDANT
    | REF
    | REFERENCE
    | REFERENCING
    | REINDEX
    | RELAY
    | RELAYLOG
    | RELAY_LOG_FILE
    | RELAY_LOG_POS
    | RELAY_THREAD
    | RELEASE
    | REMOVE
    | REOPEN
    | REORGANIZE
    | REPEATABLE
    | REPLACE
    | REPLICATE_DO_DB
    | REPLICATE_DO_TABLE
    | REPLICATE_IGNORE_DB
    | REPLICATE_IGNORE_TABLE
    | REPLICATE_REWRITE_DB
    | REPLICATE_WILD_DO_TABLE
    | REPLICATE_WILD_IGNORE_TABLE
    | REQUIRE_ROW_FORMAT
//    | REQUIRE_TABLE_PRIMARY_KEY_CHECK
    | USER_RESOURCES
    | RESPECT
    | RESTORE
    | RESULT
    | RESUME
    | RETAIN
    | REGISTRATION
    | RETURNED_SQLSTATE
    | RETURNING
    | RETURNS
    | REUSE
    | REVERSE
    | REVOKE
    | ROLE
    | ROLLUP
    | ROTATE
    | ROUTINE
    | ROWS
    | ROWTYPE
    | ROW_COUNT
    | ROW_FORMAT
    | RTREE
    | S3
    | SCHEDULE
    | SCHEMA
    | SCHEMA_NAME
    | SECONDARY_ENGINE
    | SECONDARY_ENGINE_ATTRIBUTE
    | SECONDARY_LOAD
    | SECONDARY
    | SECONDARY_UNLOAD
    | SECOND
    | SECURITY
    | SEQUENCE
    | SERIALIZABLE
    | SERIAL
    | SERVER
    | SETS
    | SHARE
    | SHOW
    | SIMPLE
    | SKIP_SYMBOL
    | SLOW
    | SNAPSHOT
    | SOCKET
    | SONAME
    | SOUNDS
    | SOURCE
    | SPATIAL
    | SQL_AFTER_GTIDS
    | SQL_AFTER_MTS_GAPS
    | SQL_BEFORE_GTIDS
    | SQL_BUFFER_RESULT
    | SQL_NO_CACHE
    | SQL_THREAD
    | SRID
    | STACKED
    | STARTS
    | STATEMENT
    | STATS_AUTO_RECALC
    | STATS_PERSISTENT
    | STATS_SAMPLE_PAGES
    | STATUS
    | STORAGE
    | STREAM
    | STRING
    | SUBCLASS_ORIGIN
//    | SUBDATE
    | SUBJECT
    | SUBPARTITIONS
    | SUBPARTITION
    | SUBTYPE
    | SUSPEND
    | SWAPS
    | SWITCHES
    | SYNONYM
    | SYSTEM
    | TABLE
    | TABLES
    | TABLESPACE
    | TABLE_CHECKSUM
    | TABLE_NAME
    | TEMP
    | TEMPORARY
    | TEMPTABLE
    | TERMINATED
    | TEXT
    | THAN
    | THREAD_PRIORITY
    | THROW
    | TIES
    | TIMESTAMP_ADD
    | TIMESTAMP_DIFF
    | TIMESTAMP
    | TIME
    | TLS
    | TRANSACTION
    | TRIGGERS
    | TYPES
    | TYPE
    | UNBOUNDED
    | UNCOMMITTED
    | UNDEFINED
    | UNDO
    | UNDOFILE
    | UNDO_BUFFER_SIZE
    | UNKNOWN
    | UNLOCK
    | UNPIVOT
    | UNTIL
    | UPGRADE
    | URL
    | USER
    | USE_FRM
    | VALIDATION
    | VALUE
    | VARIABLES
    | VARRAY
    | VCPU
    | VIEW
    | VISIBLE
    | WAIT
    | WARNINGS
    | WEEK
    | WEIGHT_STRING
    | WITHOUT
    | WORK
    | WRAPPER
    | WRITE
    | X509
    | XID
    | XML
    | YEAR
    | YEAR_MONTH
    | CONDITION
    | DESCRIBE
    | ZONE
    ;

identifierKeywordsAmbiguous1RolesAndLabels
    : EXECUTE
    | RESTART
    | SHUTDOWN
    ;

identifierKeywordsAmbiguous2Labels
    : ALL
    | ALTER
    | AND
    | AS
    | ASC
    | ASCII
    | BEGIN
    | BETWEEN
    | BYTE
    | CACHE
    | CASE
    | CHAR
    | CHARACTER
    | CHARSET
    | CHECK
    | CHECKSUM
    | CLONE
    | COMMENT
    | COMMIT
    | COMPRESS
    | CONSTANT
    | CONSTRAINT
    | CONTAINS
    | CROSS
    | CUBE
    | DBA_RECYCLEBIN
    | DEALLOCATE
    | DEC
    | DECIMAL
    | DECLARE
    | DELETE
    | DESC
    | DISTINCT
    | DO
    | DOUBLE
    | ELSE
    | END
    | EXCEPT
    | EXISTS
    | EXPLAIN
    | FETCH
    | FINALLY
    | FLOAT
    | FLUSH
    | FOLLOWS
    | FOREIGN
    | FROM
    | FUNCTION
    | GRANT
    | GROUP
    | GROUPING
    | HANDLER
    | HAVING
    | HELP
    | IMMEDIATE
    | IMPORT
    | IN
    | INDEX
    | INNER
    | INSTALL
    | INTERSECT
    | INTO
    | IS
    | JOIN
    | LANGUAGE
    | LEFT
    | LIKE
    | LIMIT
    | LOCK
    | MINUS
    | NO
    | NOCYCLE
    | NOT
    | NOTNULL
    | NULL
    | NUMERIC
    | NVARCHAR2
    | ON
    | OR
    | ORDER
    | OUT
    | PRAGMA
    | PRECEDES
    | PREPARE
    | PROCEDURE
    | RECYCLEBIN
    | REFERENCES
    | RENAME
    | REPAIR
    | RESET
    | RIGHT
    | ROLLBACK
    | SAVEPOINT
    | SELECT
    | SELF
    | SEPARATOR
    | SET
    | SIGNED
    | SLAVE
    | SQL
    | START
    | STATIC
    | STOP
    | THEN
    | TIMESTAMPDIFF
    | TO
    | TOP
    | TRAILING
    | TRIGGER
    | TRUE
    | UNION
    | UNIQUE
    | UPDATE
    | USING
    | VALUES
    | VARBIT
    | VARCHAR
    | VARCHAR2
    | WHEN
    | WHERE
    | TRUNCATE
    | UNICODE
    | UNINSTALL
    | XA
    ;

identifierKeywordsAmbiguous3Roles
    : EVENT
    | FILE
    | NONE
    | PROCESS
    | PROXY
    | RELOAD
    | REPLICATION
    | RESOURCE
    | SUPER
    ;

identifierKeywordsAmbiguous4SystemVariables
    : GLOBAL
    | LOCAL
    | PERSIST
    | PERSIST_ONLY
    | SESSION
    ;

textOrIdentifier
    : identifier | string_ | ipAddress
    ;

ipAddress
    : IP_ADDRESS
    ;

variable
    : userVariable | systemVariable
    ;

userVariable
    : AT_ textOrIdentifier
    | textOrIdentifier
    ;

systemVariable
    : AT_ AT_ (systemVariableScope=(GLOBAL | SESSION | LOCAL) DOT_)? rvalueSystemVariable
    ;

rvalueSystemVariable
    : textOrIdentifier
    | textOrIdentifier DOT_ identifier
    ;

setSystemVariable
    : AT_ AT_ (optionType DOT_)? internalVariableName
    ;

optionType
    : GLOBAL | PERSIST | PERSIST_ONLY | SESSION | LOCAL
    ;

internalVariableName
    : identifier
    | DEFAULT DOT_ identifier
    | identifier DOT_ identifier
    ;

setExprOrDefault
    : expr | DEFAULT | ALL | ON | OFF | BINARY | ROW | SYSTEM
    ;

transactionCharacteristics
    : transactionAccessMode (COMMA_ isolationLevel)?
    | isolationLevel (COMMA_ transactionAccessMode)?
    ;

isolationLevel
    : ISOLATION LEVEL isolationTypes
    ;

isolationTypes
    : REPEATABLE READ | READ COMMITTED | READ UNCOMMITTED | SERIALIZABLE
    ;

transactionAccessMode
    : READ (WRITE | ONLY)
    ;

databaseName
    : identifier
    ;

schemaName
    : identifier
    ;

databaseNames
    : databaseName (COMMA_ databaseName)*
    ;

charsetName
    : textOrIdentifier | BINARY | DEFAULT
    ;

databasePairs
    : databasePair (COMMA_ databasePair)*
    ;

databasePair
    : LP_ databaseName COMMA_ databaseName RP_
    ;

tableName
    : (owner DOT_)? name
    ;

dblinkName
    : identifier
    ;

columnName
    : identifier
    ;

indexName
    : identifier
    ;

newIndexName
    : identifier
    ;

constraintName
    : identifier
    ;

oldColumn
    : columnName
    ;

newColumn
    : columnName
    ;

delimiterName
    : textOrIdentifier | ('\\'. | ~('\'' | '"' | '`' | '\\'))+
    ; 

userIdentifierOrText
    : textOrIdentifier (AT_ textOrIdentifier)?
    ;

username
    : userIdentifierOrText | CURRENT_USER (LP_ RP_)?
    ;

eventName
    : (owner DOT_)? identifier
    ;

serverName
    : textOrIdentifier
    ; 

wrapperName
    : textOrIdentifier
    ;

functionName
    : (owner DOT_)? identifier
    ;

procedureName
    : (owner DOT_)? identifier
    ;

packageName
    : (owner DOT_)? identifier
    ;

jobName
    : (owner DOT_)? identifier
    ;

objectName
    : (owner DOT_)? identifier
    ;

sequenceName
    : identifier
    ;

synonymName
    : identifier
    ;

viewName
    : (owner DOT_)? identifier
    ;

typeName
    : (owner DOT_)? name
    ;

owner
    : identifier
    ;

alias
    : textOrIdentifier
    ;

name
    : identifier
    ;

tableList
    : tableName (COMMA_ tableName)*
    ;

viewNames
    : viewName (COMMA_ viewName)*
    ;

columnNames
    : columnName (COMMA_ columnName)*
    ;

groupName
    : identifier
    ;

routineName
    : identifier
    ;

shardLibraryName
    : stringLiterals
    ;

componentName
    : string_
    ;

pluginName
    : textOrIdentifier
    ;

hostname
    : string_
    ;

port
    : NUMBER_
    ;

cloneInstance
    : username AT_ hostname COLON_ port
    ;

cloneDir
    : string_
    ;

channelName
    : identifier (DOT_ identifier)?
    ;

logName
    : stringLiterals
    ;

roleName
    : roleIdentifierOrText (AT_ textOrIdentifier)?
    ;

roleIdentifierOrText
    : identifier | string_
    ;

engineRef
    : textOrIdentifier
    ;

triggerName
    : identifier (DOT_ identifier)?
    ;

triggerTime
    : BEFORE | AFTER | INSTEAD OF
    ;

tableOrTables
    : TABLE | TABLES
    ;

userOrRole
    : username | roleName
    ;

partitionName
    : identifier
    ;

identifierList
    : identifier (COMMA_ identifier)*
    ;

allOrPartitionNameList
    : ALL | identifierList
    ;

triggerEvent
    : INSERT | UPDATE | DELETE | UPDATE OF (columnNames | LP_ columnNames RP_)
    ;

triggerOrder
    : (FOLLOWS | PRECEDES) triggerName
    ;

exprs
    : expr (COMMA_ expr)*
    ;

exprList
    : LP_ exprs RP_
    ;

expr
    : booleanPrimary
    | expr andOperator expr
    | expr orOperator expr
    | expr XOR expr
    | notOperator expr
    ;

andOperator
    : AND | AND_
    ;

orOperator
    : OR | OR_
    ;

notOperator
    : NOT | NOT_
    ;

booleanPrimary
    : booleanPrimary IS NOT? (TRUE | FALSE | UNKNOWN | NULL)
    | booleanPrimary SAFE_EQ_ predicate
    | booleanPrimary MEMBER OF LP_ (expr) RP_
    | booleanPrimary comparisonOperator predicate
    | booleanPrimary comparisonOperator (ALL | ANY) subquery
    | booleanPrimary assignmentOperator predicate
    | predicate
    ;

assignmentOperator
    : EQ_ | ASSIGNMENT_
    ;

comparisonOperator
    : EQ_ | GTE_ | GT_ | LTE_ | LT_ | NEQ_
    ;

predicate
    : bitExpr NOT? IN subquery
    | bitExpr NOT? IN LP_ expr (COMMA_ expr)* RP_
    | bitExpr NOT? BETWEEN bitExpr AND predicate
    | bitExpr SOUNDS LIKE bitExpr
    | bitExpr NOT? LIKE simpleExpr (ESCAPE simpleExpr)?
    | bitExpr NOT? (REGEXP | RLIKE) bitExpr
    | bitExpr
    ;

bitExpr
    : bitExpr VERTICAL_BAR_ bitExpr
    | bitExpr AMPERSAND_ bitExpr
    | bitExpr SIGNED_LEFT_SHIFT_ bitExpr
    | bitExpr SIGNED_RIGHT_SHIFT_ bitExpr
    | bitExpr PLUS_ bitExpr
    | bitExpr MINUS_ bitExpr
    | bitExpr ASTERISK_ bitExpr
    | bitExpr SLASH_ bitExpr
    | bitExpr DIV bitExpr
    | bitExpr MOD bitExpr
    | bitExpr MOD_ bitExpr
    | bitExpr CARET_ bitExpr
    | bitExpr PLUS_ intervalExpression
    | bitExpr MINUS_ intervalExpression
    | bitExpr DOT_ bitExpr
    | simpleExpr
    ;

simpleExpr
    : functionCall
    | parameterMarker
    | literals
    | columnRef
    | simpleExpr collateClause
    | variable
    | simpleExpr VERTICAL_BAR_ VERTICAL_BAR_ simpleExpr
    | (PLUS_ | MINUS_ | TILDE_ | notOperator | BINARY) simpleExpr
    | ROW? LP_ expr (COMMA_ expr)* RP_
    | EXISTS? subquery
    | LBE_ identifier expr RBE_
    | identifier (JSON_SEPARATOR | JSON_UNQUOTED_SEPARATOR) string_
    | path (RETURNING dataType)? onEmpty? onError?
    | matchExpression
    | caseExpression
    | intervalExpression
    ;

path
    : string_
    ;

onEmpty
    : (NULL | ERROR | DEFAULT literals) ON EMPTY
    ;

onError
    : (NULL | ERROR | DEFAULT literals) ON ERROR
    ;

columnRef
    : identifier (DOT_ identifier)? (DOT_ identifier)?
    ;

columnRefList
    : columnRef (COMMA_ columnRef)*
    ;

functionCall
    : aggregationFunction | specialFunction | jsonFunction | regularFunction | udfFunction
    ;

udfFunction
    : functionName LP_ (expr? | expr (COMMA_ expr)*) RP_
    ;

separatorName
    : SEPARATOR string_
    ;

aggregationExpression
    : expr (COMMA_ expr)* | ASTERISK_
    ;

aggregationFunction
    : aggregationFunctionName LP_ distinct? aggregationExpression? collateClause? separatorName? RP_ overClause?
    ;

jsonFunction
    : jsonTableFunction
    | jsonFunctionName LP_ (expr? | expr (COMMA_ expr)*) RP_
    | columnRef (JSON_SEPARATOR | JSON_UNQUOTED_SEPARATOR) path
    ;

jsonTableFunction
    : JSON_TABLE LP_ expr COMMA_ path jsonTableColumns RP_
    ;

jsonTableColumns
    : COLUMNS LP_ jsonTableColumn (COMMA_ jsonTableColumn)* RP_
    ;

jsonTableColumn
    : name FOR ORDINALITY
    | name dataType PATH path jsonTableColumnOnEmpty? jsonTableColumnOnError?
    | name dataType EXISTS PATH string_ path
    | NESTED PATH? path COLUMNS
    ;

jsonTableColumnOnEmpty
    : (NULL | DEFAULT string_ | ERROR) ON EMPTY
    ;

jsonTableColumnOnError
    : (NULL | DEFAULT string_ | ERROR) ON ERROR
    ;

jsonFunctionName
    : JSON_ARRAY | JSON_ARRAY_APPEND |  JSON_ARRAY_INSERT |  JSON_CONTAINS
    | JSON_CONTAINS_PATH | JSON_DEPTH | JSON_EXTRACT | JSON_INSERT | JSON_KEYS | JSON_LENGTH | JSON_MERGE | JSON_MERGE_PATCH
    | JSON_MERGE_PRESERVE | JSON_OBJECT | JSON_OVERLAPS | JSON_PRETTY | JSON_QUOTE | JSON_REMOVE | JSON_REPLACE
    | JSON_SCHEMA_VALID | JSON_SCHEMA_VALIDATION_REPORT | JSON_SEARCH | JSON_SET | JSON_STORAGE_FREE | JSON_STORAGE_SIZE
    | JSON_TYPE | JSON_UNQUOTE | JSON_VALID | JSON_VALUE | MEMBER OF
    ;

aggregationFunctionName
    : MAX | MIN | SUM | COUNT | AVG | BIT_AND | BIT_OR | BIT_XOR | GROUP_CONCAT
    ;

distinct
    : DISTINCT
    ;

overClause
    : OVER (windowSpecification | identifier)
    ;

windowSpecification
    : LP_ identifier? (PARTITION BY expr (COMMA_ expr)*)? orderByClause? frameClause? RP_
    ;

frameClause
    : (ROWS | RANGE) (frameStart | frameBetween)
    ;

frameStart
    : CURRENT ROW | UNBOUNDED PRECEDING | UNBOUNDED FOLLOWING | expr PRECEDING | expr FOLLOWING
    ;

frameEnd
    : frameStart
    ;

frameBetween
    : BETWEEN frameStart AND frameEnd
    ;

specialFunction
    : castFunction
    | convertFunction
    | currentUserFunction
    | charFunction
    | extractFunction
    | groupConcatFunction
    | positionFunction
    | substringFunction
    | trimFunction
    | valuesFunction
    | weightStringFunction
    | windowFunction
    | groupingFunction
    | timeStampDiffFunction
    ;

currentUserFunction
    : CURRENT_USER (LP_ RP_)?
    ;

groupingFunction
    : GROUPING LP_ expr (COMMA_ expr)* RP_
    ;

timeStampDiffFunction
    : TIMESTAMPDIFF LP_ intervalUnit COMMA_ expr COMMA_ expr RP_
    ;

groupConcatFunction
    : GROUP_CONCAT LP_ distinct? (expr (COMMA_ expr)* | ASTERISK_)? (orderByClause)? (SEPARATOR expr)? RP_
    ;

windowFunction
    : funcName = (ROW_NUMBER | RANK | DENSE_RANK | CUME_DIST | PERCENT_RANK) LP_ RP_ windowingClause
    | funcName = NTILE (simpleExpr) windowingClause
    | funcName = (LEAD | LAG) LP_ expr leadLagInfo? RP_ nullTreatment? windowingClause
    | funcName = (FIRST_VALUE | LAST_VALUE) LP_ expr RP_ nullTreatment? windowingClause
    | funcName = NTH_VALUE LP_ expr COMMA_ simpleExpr RP_ (FROM (FIRST | LAST))? nullTreatment? windowingClause
    ;

windowingClause
    : OVER (windowName=identifier | windowSpecification)
    ;

leadLagInfo
    : COMMA_ (NUMBER_ | QUESTION_) (COMMA_ expr)?
    ;

nullTreatment
    : (RESPECT | IGNORE) NULLS
    ;

checkType
    : FOR UPGRADE | QUICK | FAST | MEDIUM | EXTENDED | CHANGED
    ;

repairType
    : QUICK | EXTENDED | USE_FRM
    ;

castFunction
    : CAST LP_ expr AS castType ARRAY? RP_
    | CAST LP_ expr AT TIME ZONE expr AS DATETIME typeDatetimePrecision? RP_
    ;

convertFunction
    : CONVERT LP_ expr COMMA_ castType RP_
    | CONVERT LP_ expr USING charsetName RP_
    ;

castType
    : castTypeName = BINARY fieldLength?
    | castTypeName = CHAR fieldLength? charsetWithOptBinary?
    | (castTypeName = NCHAR | castTypeName = NATIONAL_CHAR) fieldLength?
    | castTypeName = (SIGNED | SIGNED_INT | SIGNED_INTEGER)
    | castTypeName = (UNSIGNED | UNSIGNED_INT | UNSIGNED_INTEGER)
    | castTypeName = (INTEGER | INT | BIGINT)
    | castTypeName = DATE
    | castTypeName = TIME typeDatetimePrecision?
    | castTypeName = DATETIME typeDatetimePrecision?
    | castTypeName = DECIMAL (fieldLength | precision)?
    | castTypeName = JSON
    | castTypeName = REAL
    | castTypeName = DOUBLE PRECISION
    | castTypeName = FLOAT precision?
    ;

positionFunction
    : POSITION LP_ expr IN expr RP_
    ;

substringFunction
    : (SUBSTRING | SUBSTR | MID) LP_ expr FROM NUMBER_ (FOR NUMBER_)? RP_
    | (SUBSTRING | SUBSTR | MID) LP_ expr COMMA_ NUMBER_ (COMMA_ NUMBER_)? RP_
    ;

extractFunction
    : EXTRACT LP_ intervalUnit FROM expr RP_
    ;

charFunction
    : CHAR LP_ expr (COMMA_ expr)* (USING charsetName)? RP_
    ;

trimFunction
    : TRIM LP_ ((LEADING | BOTH | TRAILING) expr? FROM)? expr RP_
    | TRIM LP_ (expr FROM)? expr RP_
    ;

valuesFunction
    : VALUES LP_ columnRefList RP_
    ;

weightStringFunction
    : WEIGHT_STRING LP_ expr (AS dataType)? levelClause? RP_
    ;

levelClause
    : LEVEL (levelInWeightListElement (COMMA_ levelInWeightListElement)* | NUMBER_ MINUS_ NUMBER_)
    ;

levelInWeightListElement
    : NUMBER_ direction? REVERSE?
    ;

regularFunction
    : completeRegularFunction
    | shorthandRegularFunction
    ;

shorthandRegularFunction
    : CURRENT_DATE | CURRENT_TIME (LP_ NUMBER_? RP_)? | CURRENT_TIMESTAMP | LAST_DAY | LOCALTIME | LOCALTIMESTAMP
    ;

completeRegularFunction
    : regularFunctionName ((LP_ (expr (COMMA_ expr)* | ASTERISK_)? RP_) | (LP_ expr RP_)+)
    ;

regularFunctionName
    : IF | LOCALTIME | LOCALTIMESTAMP | REPLACE | INSERT | INTERVAL | MOD
    | DATABASE | SCHEMA | LEFT | RIGHT | DATE | DAY | GEOMETRYCOLLECTION | REPEAT
    | LINESTRING | MULTILINESTRING | MULTIPOINT | MULTIPOLYGON | POINT | POLYGON
    | TIME | TIMESTAMP | TIMESTAMP_ADD | TIMESTAMP_DIFF | DATE | CURRENT_TIMESTAMP 
    | CURRENT_DATE | CURRENT_TIME | UTC_TIMESTAMP | identifier
    ;

matchExpression
    : MATCH (columnRefList | LP_ columnRefList RP_ ) AGAINST LP_ expr matchSearchModifier? RP_
    ;

matchSearchModifier
    : IN NATURAL LANGUAGE MODE | IN NATURAL LANGUAGE MODE WITH QUERY EXPANSION | IN BOOLEAN MODE | WITH QUERY EXPANSION
    ;

caseExpression
    : CASE expr? caseWhen+ caseElse? END
    ;

datetimeExpr
    : expr
    ;

binaryLogFileIndexNumber
    : NUMBER_
    ;

caseWhen
    : WHEN expr THEN expr
    ;

caseElse
    : ELSE expr
    ;

intervalExpression
    : INTERVAL intervalValue
    ;

intervalValue
    : expr intervalUnit
    ;

intervalUnit
    : MICROSECOND | SECOND | MINUTE | HOUR | DAY | WEEK | MONTH
    | QUARTER | YEAR | SECOND_MICROSECOND | MINUTE_MICROSECOND | MINUTE_SECOND | HOUR_MICROSECOND | HOUR_SECOND
    | HOUR_MINUTE | DAY_MICROSECOND | DAY_SECOND | DAY_MINUTE | DAY_HOUR | YEAR_MONTH
    ;

subquery
    : 'refer subquery in DMStement.g4'
    ;

orderByClause
    : ORDER BY orderByItem (COMMA_ orderByItem)*
    ;

orderByItem
    : (numberLiterals | expr) direction?
    ;

xmlTableFunction
    : XMLTABLE LP_ string_ xmlTableOptions RP_
    ;

xmlTableOptions
    : PASSING expr COLUMNS xmlTableColumn (COMMA_ xmlTableColumn)*
    ;

xmlTableColumn
    : columnName dataType PATH string_
    ;

dataType
    : dataTypeName = (INTEGER | INT | TINYINT | SMALLINT | MIDDLEINT | MEDIUMINT | BIGINT) fieldLength? fieldOptions?
    | (dataTypeName = REAL | dataTypeName = DOUBLE PRECISION?) precision? fieldOptions?
    | dataTypeName = (FLOAT | DECIMAL | DEC | NUMERIC | NUMBER | FIXED) (fieldLength | precision)? fieldOptions?
    | dataTypeName = BIT fieldLength?
    | dataTypeName = BIT VARYING fieldLength?
    | dataTypeName = VARBIT fieldLength?
    | dataTypeName = (BOOL | BOOLEAN)
    | dataTypeName = CHAR fieldLength? charsetWithOptBinary?
    | (dataTypeName = NCHAR | dataTypeName = NATIONAL_CHAR) fieldLength? BINARY?
    | dataTypeName = (SIGNED | SIGNED_INT | SIGNED_INTEGER)
    | dataTypeName = BINARY fieldLength?
    | dataTypeName = GUID
    | (dataTypeName = CHAR_VARYING | dataTypeName = CHARACTER_VARYING | dataTypeName = VARCHAR | dataTypeName = VARCHAR2) fieldLength? charsetWithOptBinary?
    | (dataTypeName = NATIONAL VARCHAR | dataTypeName = NVARCHAR | dataTypeName = NVARCHAR2| dataTypeName = NCHAR VARCHAR | dataTypeName = NATIONAL_CHAR_VARYING | dataTypeName = NCHAR VARYING) fieldLength? BINARY?
    | dataTypeName = VARBINARY fieldLength?
    | dataTypeName = YEAR fieldLength? fieldOptions?
    | dataTypeName = DATE
    | (dataTypeName = TIME typeDatetimePrecision? | dataTypeName = TIME_WITH_TIME_ZONE)
    | dataTypeName = (UNSIGNED | UNSIGNED_INT | UNSIGNED_INTEGER)
    | (dataTypeName = TIMESTAMP typeDatetimePrecision? | dataTypeName = TIMESTAMP_WITH_TIME_ZONE)
    | (dataTypeName = DATETIME typeDatetimePrecision? | dataTypeName = DATETIME_WITH_TIME_ZONE)
    | dataTypeName = (INTERVAL_YEAR | INTERVAL_MONTH | INTERVAL_DAY | INTERVAL_HOUR | INTERVAL_MINUTE | INTERVAL_SECOND | INTERVAL_YEAR_TO_MONTH | INTERVAL_DAY_TO_HOUR
                    | INTERVAL_DAY_TO_MINUTE | INTERVAL_DAY_TO_SECOND | INTERVAL_HOUR_TO_MINUTE | INTERVAL_HOUR_TO_SECOND | INTERVAL_MINUTE_TO_SECOND)
    | dataTypeName = TINYBLOB
    | (dataTypeName = BLOB fieldLength? | dataTypeName = CLOB)
    | dataTypeName = (MEDIUMBLOB | LONGBLOB)
    | dataTypeName = LONG VARBINARY
    | dataTypeName = (LONG_CHAR_VARYING | LONG_VARCHAR)? charsetWithOptBinary?
    | dataTypeName = TINYTEXT charsetWithOptBinary?
    | dataTypeName = TEXT fieldLength? charsetWithOptBinary?
    | dataTypeName = MEDIUMTEXT charsetWithOptBinary?
    | dataTypeName = LONGTEXT charsetWithOptBinary?
    | dataTypeName = ENUM stringList charsetWithOptBinary?
    | dataTypeName = SET stringList charsetWithOptBinary?
    | dataTypeName = (SERIAL | JSON | GEOMETRY | GEOMCOLLECTION | GEOMETRYCOLLECTION | POINT | MULTIPOINT | LINESTRING | MULTILINESTRING | POLYGON | MULTIPOLYGON)
    | dataTypeName = XML
    | rowtype
    | dataTypeName = IDENTIFIER_
    ;

stringList
    : LP_ textString (COMMA_ textString)* RP_
    ;

textString
    : string_
    | HEX_DIGIT_
    | BIT_NUM_
    ;

rowtype
    : (tableName DOT_)? columnName MOD_ TYPE
    | tableName MOD_ ROWTYPE
    | tableName MOD_ ROW TYPE
    ;

textStringHash
    : string_ | HEX_DIGIT_
    ;

fieldOptions
    : (UNSIGNED | SIGNED | ZEROFILL)+
    ;

precision
    : LP_ NUMBER_ COMMA_ NUMBER_ RP_
    ;

typeDatetimePrecision
    : LP_ NUMBER_ RP_
    ;

charsetWithOptBinary
    : ascii
    | unicode
    | BYTE
    | charset charsetName BINARY?
    | BINARY (charset charsetName)?
    ;

ascii
    : ASCII BINARY?
    | BINARY ASCII
    ;

unicode
    : UNICODE BINARY?
    | BINARY UNICODE
    ;

charset
    : (CHAR | CHARACTER) SET
    | CHARSET
    ;

defaultCollation
    : DEFAULT? COLLATE EQ_? collationName
    ;

defaultEncryption
    : DEFAULT? ENCRYPTION EQ_? string_
    ;

defaultCharset
    : DEFAULT? charset EQ_? charsetName
    ;

now
    : (CURRENT_TIMESTAMP | LOCALTIME | LOCALTIMESTAMP) (LP_ NUMBER_? RP_)?
    ;

columnFormat
    : FIXED
    | DYNAMIC
    | DEFAULT
    ;

storageMedia
    : DISK
    | MEMORY
    | DEFAULT
    ;

direction
    : ASC | DESC
    ;

keyOrIndex
    : KEY | INDEX
    ;

fieldLength
    : LP_ length=NUMBER_ RP_
    ;

characterSet
    : charset charsetName
    ;

collateClause
    : COLLATE (collationName | parameterMarker)
    ;

fieldOrVarSpec
    : LP_ (userVariable (COMMA_ userVariable)*)? RP_
    ;

ifNotExists
    : IF NOT EXISTS
    ;

ifExists
    : IF EXISTS
    ;

connectionId
    : NUMBER_
    ;

labelName
    : identifier
    ;

cursorName
    : identifier
    ;

conditionName
    : identifier
    ;

combineOption
    : ALL | DISTINCT
    ;

noWriteToBinLog
    : LOCAL
    | NO_WRITE_TO_BINLOG
    ;

channelOption
    : FOR CHANNEL string_
    ;
