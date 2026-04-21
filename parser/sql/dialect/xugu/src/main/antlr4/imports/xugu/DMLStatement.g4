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

grammar DMLStatement;

import BaseRule;

insert
    : INSERT (insertSingleTable | insertMultiTable)
    ;

insertSingleTable
    : insertSpecification INTO? tableName (partitionNames | AT_ dblinkName)? (insertValuesClause | setAssignmentsClause | insertSelectClause | insertDefaultValue) onDuplicateKeyClause? returningClause?
    ;

insertMultiTable
    : (ALL insertIntoClause+ | conditionalInsertClause) select
    ;

conditionalInsertClause
    : (ALL | FIRST)? conditionalInsertWhenPart+ conditionalInsertElsePart?
    ;

conditionalInsertWhenPart
    : WHEN expr THEN insertIntoClause+
    ;

conditionalInsertElsePart
    : ELSE insertIntoClause+
    ;

insertIntoClause
    : INTO tableName (partitionNames | AT_ dblinkName)? ((LP_ fields RP_)? (VALUES assignmentValues) | LP_ fields RP_)
    ;

insertSpecification
    : (LOW_PRIORITY | DELAYED | HIGH_PRIORITY)? IGNORE?
    ;

insertValuesClause
    : (LP_ fields? RP_ )? (VALUES | VALUE) (assignmentValues (COMMA_? assignmentValues)* | rowConstructorList | expr) valueReference?
    ;

fields
    : insertIdentifier (COMMA_ insertIdentifier)*
    ;

insertIdentifier
    : columnRef | tableWild
    ;

tableWild
    : identifier DOT_ (identifier DOT_)? ASTERISK_
    ;

insertSelectClause
    : valueReference? (LP_ fields? RP_)? select
    ;

insertDefaultValue
    : DEFAULT VALUES
    ;

onDuplicateKeyClause
    : (AS identifier derivedColumns?)?  ON DUPLICATE KEY UPDATE assignment (COMMA_ assignment)*
    ;

valueReference
    : AS alias derivedColumns?
    ;

derivedColumns
    : LP_ alias (COMMA_ alias)* RP_
    ;

replace
    : REPLACE replaceSpecification? INTO? tableName partitionNames? (replaceValuesClause | setAssignmentsClause | replaceSelectClause) returningClause?
    ;

replaceSpecification
    : LOW_PRIORITY | DELAYED
    ;

replaceValuesClause
    : (LP_ fields? RP_)? (VALUES | VALUE) (assignmentValues (COMMA_ assignmentValues)* | rowConstructorList) valueReference?
    ;

replaceSelectClause
    : valueReference? (LP_ fields? RP_)? select
    ;

update
    : withClause? UPDATE updateSpecification_ tableReferences setAssignmentsClause whereClause? orderByClause? limitClause?
    ;

updateSpecification_
    : LOW_PRIORITY? IGNORE?
    ;

assignment
    : columnRef EQ_ assignmentValue
    ;

setAssignmentsClause
    : valueReference? SET assignment (COMMA_ assignment)*
    ;

assignmentValues
    : LP_ assignmentValue (COMMA_ assignmentValue)* RP_
    | LP_ RP_
    ;

assignmentValue
    : blobValue | expr | DEFAULT
    ;

blobValue
    : UL_BINARY string_
    ;

delete
    : DELETE deleteSpecification (singleTableClause | multipleTablesClause) whereClause? orderByClause? limitClause? returningClause?
    ;

deleteSpecification
    : LOW_PRIORITY? QUICK? IGNORE?
    ;

singleTableClause
    : FROM tableName (AS? alias)? partitionNames?
    ;

multipleTablesClause
    : tableAliasRefList FROM tableReferences | FROM tableAliasRefList USING tableReferences
    ;

select
    : queryExpression lockClauseList?
    | queryExpressionParens
    | selectWithInto
    ;

selectWithInto
    : LP_ selectWithInto RP_
    | queryExpression selectIntoExpression lockClauseList?
    | queryExpression lockClauseList selectIntoExpression
    ;

queryExpression
    : withClause? (queryExpressionBody | queryExpressionParens) orderByClause? limitClause?
    ;

queryExpressionBody
    : queryPrimary
    | queryExpressionParens combineClause
    | queryExpressionBody combineClause
    ;

combineClause
    : INTERSECT combineOption? (queryPrimary | queryExpressionParens)
    | UNION combineOption? (queryPrimary | queryExpressionParens)
    | (EXCEPT | MINUS) combineOption? (queryPrimary | queryExpressionParens)
    ;

queryExpressionParens
    : LP_ (queryExpressionParens | queryExpression lockClauseList?) RP_
    ;

queryPrimary
    : querySpecification
    | tableValueConstructor
    | tableStatement
    ;

querySpecification
    : SELECT hint? top? selectSpecification* projections selectIntoExpression? fromClause? whereClause? hierarchicalQueryClause? groupByClause? havingClause? windowClause?
    ;

hint
    : BLOCK_COMMENT | INLINE_COMMENT
    ;

top
    : TOP NUMBER_
    ;

call
    : (CALL | EXECUTE | EXEC)? (owner DOT_)? (identifier | functionName) (LP_ exprs? RP_)?
    ;

doStatement
    : DO expr (AS? alias)? (COMMA_ expr (AS? alias)?)*
    ;

handlerStatement
    : handlerOpenStatement | handlerReadIndexStatement | handlerReadStatement | handlerCloseStatement
    ;

handlerOpenStatement
    : HANDLER tableName OPEN (AS? identifier)?
    ;

handlerReadIndexStatement
    : HANDLER tableName READ indexName ( comparisonOperator LP_ identifier RP_ | (FIRST | NEXT | PREV | LAST) )
    whereClause? limitClause?
    ;

handlerReadStatement
    : HANDLER tableName READ (FIRST | NEXT)
    whereClause? limitClause?
    ;

handlerCloseStatement
    : HANDLER tableName CLOSE
    ;

importStatement
    : IMPORT TABLE FROM textString (COMMA_ textString)?
    ;

loadStatement
    : loadDataStatement | loadXmlStatement
    ;

loadDataStatement
    : LOAD DATA
      (LOW_PRIORITY | CONCURRENT)? LOCAL? 
      INFILE string_
      (REPLACE | IGNORE)?
      INTO TABLE tableName partitionNames?
      (CHARACTER SET identifier)?
      (COLUMNS selectFieldsInto+ )?
      ( LINES selectLinesInto+ )?
      ( IGNORE numberLiterals (LINES | ROWS) )?
      fieldOrVarSpec?
      (setAssignmentsClause)?
    ;

loadXmlStatement
    : LOAD XML
      (LOW_PRIORITY | CONCURRENT)? LOCAL? 
      INFILE string_
      (REPLACE | IGNORE)?
      INTO TABLE tableName
      (CHARACTER SET identifier)?
      (ROWS IDENTIFIED BY LT_ string_ GT_)?
      ( IGNORE numberLiterals (LINES | ROWS) )?
      fieldOrVarSpec?
      (setAssignmentsClause)?
    ;

tableStatement
    : TABLE tableName
    ;

tableValueConstructor
    : VALUES rowConstructorList
    ;

rowConstructorList
    : ROW assignmentValues (COMMA_ ROW assignmentValues)*
    ;

withClause
    : WITH RECURSIVE? cteClause (COMMA_ cteClause)*
    ;

cteClause
    : alias (LP_ columnNames RP_)? AS subquery
    ;

selectSpecification
    : duplicateSpecification | HIGH_PRIORITY | STRAIGHT_JOIN | SQL_SMALL_RESULT | SQL_BIG_RESULT | SQL_BUFFER_RESULT | SQL_NO_CACHE | SQL_CALC_FOUND_ROWS
    ;

duplicateSpecification
    : ALL | DISTINCT | DISTINCTROW
    ;

projections
    : (unqualifiedShorthand | projection) (COMMA_ projection)*
    ;

projection
    : expr (AS? alias)? | qualifiedShorthand
    ;

unqualifiedShorthand
    : ASTERISK_
    ;

qualifiedShorthand
    : (identifier DOT_)? identifier DOT_ASTERISK_
    ;

fromClause
    : FROM (DUAL | tableReferences)
    ;

tableReferences
    : tableReference (COMMA_ tableReference)*
    ;

escapedTableReference
    : tableFactor joinedTable*
    ;

tableReference
    : (tableFactor | LBE_ OJ escapedTableReference RBE_) joinedTable*
    ;

tableFactor
    : tableName (partitionNames | AT_ dblinkName)? aliasClause? (pivotClause | unpivotClause)? indexHintList?
    | LATERAL? subquery (AS? alias)? (LP_ columnNames RP_)? (pivotClause | unpivotClause)?
    | regularFunction (AS? alias)?
    | xmlTableFunction aliasClause?
    | LP_ tableReferences RP_
    ;

partitionNames
    : (PARTITION | SUBPARTITION) LP_ identifier (COMMA_ identifier)* RP_
    ;

aliasClause
    : AS? tableAlias = alias (LP_ alias (COMMA_ alias)* RP_)?
    ;

pivotClause
    : PIVOT XML?
    LP_ aggregationFunction (AS? alias)? (COMMA_ aggregationFunction (AS? alias)?)* pivotForClause pivotInClause RP_ aliasClause?
    ;

pivotForClause
    : FOR (columnName | LP_ columnNames RP_)
    ;

pivotInClause
    : IN LP_ (pivotInClauseExpr (COMMA_ pivotInClauseExpr)*
    | queryExpression
    | ANY (COMMA_ ANY)*) RP_
    ;

pivotInClauseExpr
    : (expr | exprList) (AS? alias)?
    ;

unpivotClause
    : UNPIVOT ((INCLUDE | EXCLUDE) NULLS)? LP_ (columnName | LP_ columnNames RP_) pivotForClause unpivotInClause RP_ aliasClause?
    ;

unpivotInClause
    : IN LP_ unpivotInClauseExpr (COMMA_ unpivotInClauseExpr)* RP_
    ;

unpivotInClauseExpr
    : (columnName | LP_ columnNames RP_) (AS (literals | LP_ literals (COMMA_ literals)* RP_))?
    ;

indexHintList
    : indexHint (indexHint)*
    ;

indexHint
    : USE (INDEX | KEY) indexHintClause LP_ (indexNameList)? RP_
    | (IGNORE | FORCE) (INDEX | KEY) indexHintClause LP_ indexNameList RP_
    ;

indexHintClause
    : (FOR (JOIN | ORDER BY | GROUP BY))?
    ;

indexNameList
    : indexName (COMMA_ indexName)*
    ;

joinedTable
    : innerJoinType tableReference joinSpecification? (pivotClause | unpivotClause)?
    | outerJoinType tableReference joinSpecification (pivotClause | unpivotClause)?
    | naturalJoinType tableFactor
    ;

innerJoinType
    : (INNER | CROSS)? JOIN
    | STRAIGHT_JOIN
    ;

outerJoinType
    : (FULL | LEFT | RIGHT) OUTER? JOIN
    ;

naturalJoinType
    : NATURAL INNER? JOIN
    | NATURAL (LEFT | RIGHT | FULL) OUTER? JOIN
    ;

joinSpecification
    : ON expr | USING LP_ columnNames RP_
    ;

whereClause
    : WHERE expr
    ;

hierarchicalQueryClause
    : CONNECT BY NOCYCLE? expr (START WITH expr)? (KEEP expr)?
    | START WITH expr CONNECT BY NOCYCLE? expr (KEEP expr)?
    ;

groupByClause
    : GROUP BY groupByItem (COMMA_ groupByItem)*
    ;

groupByItem
    : expr
    | cubeRollupGroupingSetsClause
    | emptyGroupingSet
    ;

cubeRollupGroupingSetsClause
    : (CUBE | ROLLUP | GROUPING SETS) LP_ groupByItem (COMMA_ groupByItem)* RP_
    ;

emptyGroupingSet
    : LP_ (expr (COMMA_ expr)*)? RP_
    ;

havingClause
    : HAVING expr
    ;

limitClause
    : LIMIT ((limitOffset COMMA_)? limitRowCount | limitRowCount OFFSET limitOffset)
    ;

limitRowCount
    : numberLiterals | parameterMarker | ALL
    ;

limitOffset
    : numberLiterals | parameterMarker
    ;

windowClause
    : WINDOW windowItem (COMMA_ windowItem)*
    ;

windowItem
    : identifier AS windowSpecification
    ;

subquery
    : queryExpressionParens
    ;

selectLinesInto
    : STARTING BY string_ | TERMINATED BY string_
    ;

selectFieldsInto
    : TERMINATED BY string_ | OPTIONALLY? ENCLOSED BY string_ | ESCAPED BY string_
    ;

selectIntoExpression
    : (BULK COLLECT)? INTO expr (COMMA_ expr )* | INTO DUMPFILE string_
    | (INTO OUTFILE string_ (CHARACTER SET charsetName)?(COLUMNS selectFieldsInto+)? (LINES selectLinesInto+)?)
    ;

lockClause
    : FOR lockStrength tableLockingList? lockedRowAction?
    | LOCK IN SHARE MODE
    ;

lockClauseList
    : lockClause+
    ;

lockStrength
    : UPDATE | SHARE
    ;

lockedRowAction
    : SKIP_SYMBOL LOCKED | NOWAIT
    ;

tableLockingList
    : OF tableAliasRefList
    ;

tableIdentOptWild
    : tableName DOT_ASTERISK_?
    ;

tableAliasRefList
    : tableIdentOptWild (COMMA_ tableIdentOptWild)*
    ;

returningClause
    : RETURNING targetList (BULK COLLECT)? INTO expr (COMMA_ expr )*
    ;

targetList
    : projection (COMMA_ projection)*
    ;