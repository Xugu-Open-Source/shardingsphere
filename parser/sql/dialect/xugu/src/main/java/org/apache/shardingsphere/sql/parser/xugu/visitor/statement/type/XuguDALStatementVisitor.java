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

package org.apache.shardingsphere.sql.parser.xugu.visitor.statement.type;

import org.apache.shardingsphere.sql.parser.api.ASTNode;
import org.apache.shardingsphere.sql.parser.api.visitor.statement.type.DALStatementVisitor;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.AlterResourceGroupContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.AnalyzeTableContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.BinaryLogFileIndexNumberContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.BinlogContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.CacheIndexContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.CacheTableIndexListContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ChannelOptionContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.CheckTableContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ChecksumTableContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.CloneActionContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.CloneContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.CloneInstanceContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ComponentNameContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.CreateLoadableFunctionContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.CreateResourceGroupContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.DelimiterContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.DropResourceGroupContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ExplainContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ExplainableStatementContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.FlushContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.FromDatabaseContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.FromTableContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.HelpContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.IndexNameContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.InstallComponentContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.InstallPluginContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.KillContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.LoadIndexInfoContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.LoadTableIndexListContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.OptimizeTableContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.OptionTypeContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.OptionValueContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.OptionValueListContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.OptionValueNoOptionTypeContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.PartitionListContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.PartitionNameContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.RepairTableContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ResetOptionContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ResetPersistContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ResetStatementContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.RestartContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.SetCharacterContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.SetResourceGroupContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.SetVariableContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowBinaryLogsContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowBinlogEventsContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCharacterSetContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCharsetContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCollationContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowColumnsContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCreateDatabaseContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCreateEventContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCreateFunctionContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCreateProcedureContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCreateTableContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCreateTriggerContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCreateUserContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowCreateViewContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowDatabasesContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowEngineContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowEnginesContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowErrorsContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowEventsContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowFilterContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowFunctionCodeContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowFunctionStatusContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowGrantsContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowIndexContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowLikeContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowMasterStatusContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowOpenTablesContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowPluginsContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowPrivilegesContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowProcedureCodeContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowProcedureStatusContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowProcesslistContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowProfileContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowProfilesContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowRelaylogEventContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowReplicaStatusContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowReplicasContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowSlaveHostsContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowSlaveStatusContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowStatusContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowTableStatusContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowTablesContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowTriggersContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowVariablesContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowWarningsContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowWhereClauseContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShowXuguVariableContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ShutdownContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.TableNameContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.TablesOptionContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.UninstallComponentContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.UninstallPluginContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.UseContext;
import org.apache.shardingsphere.sql.parser.xugu.visitor.statement.XuguStatementVisitor;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.CacheTableIndexSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.CloneActionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.CloneInstanceSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.FromDatabaseSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.FromTableSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.LoadTableIndexSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.PartitionDefinitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.PartitionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.ResetMasterOptionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.ResetOptionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.ResetSlaveOptionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.ShowFilterSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.ShowLikeSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.VariableAssignSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dal.VariableSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.ddl.index.IndexSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.column.ColumnSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.expr.ExpressionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.expr.FunctionSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.pagination.limit.LimitSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.dml.predicate.WhereSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.DatabaseSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table.SimpleTableSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.SQLStatement;
import org.apache.shardingsphere.sql.parser.statement.core.value.collection.CollectionValue;
import org.apache.shardingsphere.sql.parser.statement.core.value.identifier.IdentifierValue;
import org.apache.shardingsphere.sql.parser.statement.core.value.literal.impl.NumberLiteralValue;
import org.apache.shardingsphere.sql.parser.statement.core.value.literal.impl.StringLiteralValue;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguAlterResourceGroupStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguAnalyzeTableStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguBinlogStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguCacheIndexStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguCheckTableStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguChecksumTableStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguCloneStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguCreateLoadableFunctionStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguCreateResourceGroupStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguDelimiterStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguDropResourceGroupStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguExplainStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguFlushStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguHelpStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguInstallComponentStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguInstallPluginStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguKillStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguLoadIndexInfoStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguOptimizeTableStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguRepairTableStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguResetPersistStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguResetStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguRestartStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguSetResourceGroupStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguSetStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowBinaryLogsStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowBinlogEventsStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowCharacterSetStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowCollationStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowColumnsStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowCreateDatabaseStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowCreateEventStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowCreateFunctionStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowCreateProcedureStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowCreateTableStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowCreateTriggerStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowCreateUserStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowCreateViewStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowDatabasesStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowEngineStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowErrorsStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowEventsStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowFunctionCodeStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowFunctionStatusStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowGrantsStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowIndexStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowMasterStatusStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowOpenTablesStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowOtherStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowPluginsStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowPrivilegesStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowProcedureCodeStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowProcedureStatusStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowProcessListStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowProfileStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowProfilesStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowRelayLogEventsStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowReplicaStatusStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowReplicasStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowSlaveHostsStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowSlaveStatusStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowStatusStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowTableStatusStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowTablesStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowTriggersStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowVariablesStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShowWarningsStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguShutdownStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguUninstallComponentStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguUninstallPluginStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.dal.XuguUseStatement;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * DAL statement visitor for MySQL.
 */
public final class XuguDALStatementVisitor extends XuguStatementVisitor implements DALStatementVisitor {
    
    @Override
    public ASTNode visitUninstallPlugin(final UninstallPluginContext ctx) {
        XuguUninstallPluginStatement result = new XuguUninstallPluginStatement();
        result.setPluginName(((IdentifierValue) visit(ctx.pluginName())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitShowCreateDatabase(final ShowCreateDatabaseContext ctx) {
        XuguShowCreateDatabaseStatement result = new XuguShowCreateDatabaseStatement();
        result.setDatabaseName(((DatabaseSegment) visit(ctx.databaseName())).getIdentifier().getValue());
        return result;
    }
    
    @Override
    public ASTNode visitShowBinaryLogs(final ShowBinaryLogsContext ctx) {
        return new XuguShowBinaryLogsStatement();
    }
    
    @Override
    public ASTNode visitShowStatus(final ShowStatusContext ctx) {
        XuguShowStatusStatement result = new XuguShowStatusStatement();
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowCreateView(final ShowCreateViewContext ctx) {
        return new XuguShowCreateViewStatement();
    }
    
    @Override
    public ASTNode visitShowEngines(final ShowEnginesContext ctx) {
        return new XuguShowOtherStatement();
    }
    
    @Override
    public ASTNode visitShowEngine(final ShowEngineContext ctx) {
        XuguShowEngineStatement result = new XuguShowEngineStatement();
        result.setEngineName(ctx.engineRef().getText());
        return result;
    }
    
    @Override
    public ASTNode visitShowCharset(final ShowCharsetContext ctx) {
        return new XuguShowOtherStatement();
    }
    
    @Override
    public ASTNode visitShowCreateEvent(final ShowCreateEventContext ctx) {
        XuguShowCreateEventStatement result = new XuguShowCreateEventStatement();
        result.setEventName(((IdentifierValue) visit(ctx.eventName())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitShowCreateFunction(final ShowCreateFunctionContext ctx) {
        XuguShowCreateFunctionStatement result = new XuguShowCreateFunctionStatement();
        result.setFunctionName(((FunctionSegment) visit(ctx.functionName())).getFunctionName());
        return result;
    }
    
    @Override
    public ASTNode visitShowCreateProcedure(final ShowCreateProcedureContext ctx) {
        XuguShowCreateProcedureStatement result = new XuguShowCreateProcedureStatement();
        result.setProcedureName(((IdentifierValue) visit(ctx.procedureName())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitShowBinlogEvents(final ShowBinlogEventsContext ctx) {
        XuguShowBinlogEventsStatement result = new XuguShowBinlogEventsStatement();
        if (null != ctx.logName()) {
            result.setLogName(ctx.logName().getText());
        }
        if (null != ctx.limitClause()) {
            result.setLimit((LimitSegment) visit(ctx.limitClause()));
        }
        return result;
    }
    
    @Override
    public ASTNode visitShowErrors(final ShowErrorsContext ctx) {
        XuguShowErrorsStatement result = new XuguShowErrorsStatement();
        if (null != ctx.limitClause()) {
            result.setLimit((LimitSegment) visit(ctx.limitClause()));
        }
        return result;
    }
    
    @Override
    public ASTNode visitShowWarnings(final ShowWarningsContext ctx) {
        XuguShowWarningsStatement result = new XuguShowWarningsStatement();
        if (null != ctx.limitClause()) {
            result.setLimit((LimitSegment) visit(ctx.limitClause()));
        }
        return result;
    }
    
    @Override
    public ASTNode visitResetStatement(final ResetStatementContext ctx) {
        ResetPersistContext persistContext = ctx.resetPersist();
        if (null != persistContext) {
            return visit(persistContext);
        }
        XuguResetStatement result = new XuguResetStatement();
        for (ResetOptionContext each : ctx.resetOption()) {
            if (null != each.MASTER() || null != each.SLAVE()) {
                result.getOptions().add((ResetOptionSegment) visit(each));
            }
        }
        return result;
    }
    
    @Override
    public ASTNode visitResetPersist(final ResetPersistContext ctx) {
        return new XuguResetPersistStatement(null != ctx.ifExists(), null == ctx.identifier() ? null : new IdentifierValue(ctx.identifier().getText()));
    }
    
    @Override
    public ASTNode visitResetOption(final ResetOptionContext ctx) {
        if (null != ctx.MASTER()) {
            ResetMasterOptionSegment result = new ResetMasterOptionSegment();
            if (null != ctx.binaryLogFileIndexNumber()) {
                result.setBinaryLogFileIndexNumber(((NumberLiteralValue) visit(ctx.binaryLogFileIndexNumber())).getValue().longValue());
            }
            result.setStartIndex(ctx.start.getStartIndex());
            result.setStopIndex(ctx.stop.getStopIndex());
            return result;
        }
        ResetSlaveOptionSegment result = new ResetSlaveOptionSegment();
        if (null != ctx.ALL()) {
            result.setAll(true);
        }
        if (null != ctx.channelOption()) {
            result.setChannelOption(((StringLiteralValue) visit(ctx.channelOption())).getValue());
        }
        result.setStartIndex(ctx.start.getStartIndex());
        result.setStopIndex(ctx.stop.getStopIndex());
        return result;
    }
    
    @Override
    public ASTNode visitChannelOption(final ChannelOptionContext ctx) {
        return visit(ctx.string_());
    }
    
    @Override
    public ASTNode visitBinaryLogFileIndexNumber(final BinaryLogFileIndexNumberContext ctx) {
        return new NumberLiteralValue(ctx.getText());
    }
    
    @Override
    public ASTNode visitShowReplicas(final ShowReplicasContext ctx) {
        return new XuguShowReplicasStatement();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitRepairTable(final RepairTableContext ctx) {
        XuguRepairTableStatement result = new XuguRepairTableStatement();
        result.getTables().addAll(((CollectionValue<SimpleTableSegment>) visit(ctx.tableList())).getValue());
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitAnalyzeTable(final AnalyzeTableContext ctx) {
        XuguAnalyzeTableStatement result = new XuguAnalyzeTableStatement();
        result.getTables().addAll(((CollectionValue<SimpleTableSegment>) visit(ctx.tableList())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitCacheIndex(final CacheIndexContext ctx) {
        XuguCacheIndexStatement result = new XuguCacheIndexStatement();
        if (null != ctx.cacheTableIndexList()) {
            for (CacheTableIndexListContext each : ctx.cacheTableIndexList()) {
                result.getTableIndexes().add((CacheTableIndexSegment) visit(each));
            }
        }
        if (null != ctx.partitionList()) {
            SimpleTableSegment table = (SimpleTableSegment) visit(ctx.tableName());
            PartitionDefinitionSegment segment = new PartitionDefinitionSegment(ctx.tableName().getStart().getStartIndex(), ctx.partitionList().getStop().getStopIndex(), table);
            segment.getPartitions().addAll(((CollectionValue<PartitionSegment>) visit(ctx.partitionList())).getValue());
            result.setPartitionDefinition(segment);
        }
        if (null != ctx.DEFAULT()) {
            result.setName(new IdentifierValue(ctx.DEFAULT().getText()));
        } else {
            result.setName((IdentifierValue) visit(ctx.identifier()));
        }
        return result;
    }
    
    @Override
    public ASTNode visitCacheTableIndexList(final CacheTableIndexListContext ctx) {
        CacheTableIndexSegment result = new CacheTableIndexSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), (SimpleTableSegment) visit(ctx.tableName()));
        for (IndexNameContext each : ctx.indexName()) {
            result.getIndexes().add((IndexSegment) visitIndexName(each));
        }
        return result;
    }
    
    @Override
    public ASTNode visitPartitionList(final PartitionListContext ctx) {
        CollectionValue<PartitionSegment> result = new CollectionValue<>();
        for (PartitionNameContext each : ctx.partitionName()) {
            result.getValue().add((PartitionSegment) visit(each));
        }
        return result;
    }
    
    @Override
    public ASTNode visitPartitionName(final PartitionNameContext ctx) {
        return new PartitionSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), (IdentifierValue) visit(ctx.identifier()));
    }
    
    @Override
    public ASTNode visitChecksumTable(final ChecksumTableContext ctx) {
        XuguChecksumTableStatement result = new XuguChecksumTableStatement();
        result.getTables().addAll(((CollectionValue<SimpleTableSegment>) visit(ctx.tableList())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitFlush(final FlushContext ctx) {
        if (null != ctx.tablesOption()) {
            return visit(ctx.tablesOption());
        }
        return new XuguFlushStatement();
    }
    
    @Override
    public ASTNode visitTablesOption(final TablesOptionContext ctx) {
        XuguFlushStatement result = new XuguFlushStatement();
        result.setFlushTable(true);
        for (TableNameContext each : ctx.tableName()) {
            result.getTables().add((SimpleTableSegment) visit(each));
        }
        return result;
    }
    
    @Override
    public ASTNode visitKill(final KillContext ctx) {
        XuguKillStatement result = new XuguKillStatement();
        if (null != ctx.AT_()) {
            result.setProcessId(ctx.AT_().getText() + ctx.IDENTIFIER_().getText());
        } else {
            result.setProcessId(ctx.IDENTIFIER_().getText());
        }
        if (null != ctx.QUERY()) {
            result.setScope(ctx.QUERY().getText());
        }
        if (null != ctx.CONNECTION()) {
            result.setScope(ctx.CONNECTION().getText());
        }
        return result;
    }
    
    @Override
    public ASTNode visitLoadIndexInfo(final LoadIndexInfoContext ctx) {
        XuguLoadIndexInfoStatement result = new XuguLoadIndexInfoStatement();
        for (LoadTableIndexListContext each : ctx.loadTableIndexList()) {
            result.getTableIndexes().add((LoadTableIndexSegment) visit(each));
        }
        return result;
    }
    
    @Override
    public ASTNode visitLoadTableIndexList(final LoadTableIndexListContext ctx) {
        LoadTableIndexSegment result = new LoadTableIndexSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), (SimpleTableSegment) visit(ctx.tableName()));
        if (null != ctx.indexName()) {
            for (IndexNameContext each : ctx.indexName()) {
                result.getIndexes().add((IndexSegment) visitIndexName(each));
            }
        }
        if (null != ctx.partitionList()) {
            result.getPartitions().addAll(((CollectionValue<PartitionSegment>) visit(ctx.partitionList())).getValue());
        }
        return result;
    }
    
    @Override
    public ASTNode visitInstallPlugin(final InstallPluginContext ctx) {
        XuguInstallPluginStatement result = new XuguInstallPluginStatement();
        result.setPluginName(((IdentifierValue) visit(ctx.pluginName())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitClone(final CloneContext ctx) {
        XuguCloneStatement result = new XuguCloneStatement();
        result.setCloneActionSegment((CloneActionSegment) visit(ctx.cloneAction()));
        return result;
    }
    
    @Override
    public ASTNode visitCloneAction(final CloneActionContext ctx) {
        CloneActionSegment result = new CloneActionSegment(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
        if (null != ctx.cloneInstance()) {
            CloneInstanceContext cloneInstance = ctx.cloneInstance();
            CloneInstanceSegment cloneInstanceSegment = new CloneInstanceSegment(cloneInstance.start.getStartIndex(), cloneInstance.stop.getStopIndex());
            cloneInstanceSegment.setUsername(((StringLiteralValue) visitUsername(cloneInstance.username())).getValue());
            cloneInstanceSegment.setHostname(((StringLiteralValue) visit(cloneInstance.hostname())).getValue());
            cloneInstanceSegment.setPort(new NumberLiteralValue(cloneInstance.port().NUMBER_().getText()).getValue().intValue());
            cloneInstanceSegment.setPassword(((StringLiteralValue) visit(ctx.string_())).getValue());
            if (null != ctx.SSL() && null == ctx.NO()) {
                cloneInstanceSegment.setSslRequired(true);
            }
            result.setCloneInstance(cloneInstanceSegment);
        }
        if (null != ctx.cloneDir()) {
            result.setCloneDir(((StringLiteralValue) visit(ctx.cloneDir())).getValue());
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public ASTNode visitOptimizeTable(final OptimizeTableContext ctx) {
        XuguOptimizeTableStatement result = new XuguOptimizeTableStatement();
        result.getTables().addAll(((CollectionValue<SimpleTableSegment>) visit(ctx.tableList())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitUse(final UseContext ctx) {
        XuguUseStatement result = new XuguUseStatement();
        result.setDatabase(((DatabaseSegment) visit(ctx.databaseName())).getIdentifier().getValue());
        return result;
    }
    
    @Override
    public ASTNode visitExplain(final ExplainContext ctx) {
        XuguExplainStatement result = new XuguExplainStatement();
        if (null != ctx.tableName()) {
            result.setSimpleTable((SimpleTableSegment) visit(ctx.tableName()));
            if (null != ctx.columnRef()) {
                result.setColumnWild((ColumnSegment) visit(ctx.columnRef()));
            } else if (null != ctx.textString()) {
                result.setColumnWild((ColumnSegment) visit(ctx.textString()));
            }
        } else if (null != ctx.explainableStatement()) {
            result.setSqlStatement((SQLStatement) visit(ctx.explainableStatement()));
        } else if (null != ctx.select()) {
            result.setSqlStatement((SQLStatement) visit(ctx.select()));
        } else if (null != ctx.delete()) {
            result.setSqlStatement((SQLStatement) visit(ctx.delete()));
        } else if (null != ctx.update()) {
            result.setSqlStatement((SQLStatement) visit(ctx.update()));
        } else if (null != ctx.insert()) {
            result.setSqlStatement((SQLStatement) visit(ctx.insert()));
        }
        return result;
    }
    
    @Override
    public ASTNode visitExplainableStatement(final ExplainableStatementContext ctx) {
        if (null != ctx.select()) {
            return visit(ctx.select());
        }
        if (null != ctx.delete()) {
            return visit(ctx.delete());
        }
        if (null != ctx.insert()) {
            return visit(ctx.insert());
        }
        if (null != ctx.replace()) {
            return visit(ctx.replace());
        }
        return visit(ctx.update());
    }
    
    @Override
    public ASTNode visitShowProcedureCode(final ShowProcedureCodeContext ctx) {
        XuguShowProcedureCodeStatement result = new XuguShowProcedureCodeStatement();
        result.setFunction((FunctionSegment) visit(ctx.functionName()));
        return result;
    }
    
    @Override
    public ASTNode visitShowProfile(final ShowProfileContext ctx) {
        XuguShowProfileStatement result = new XuguShowProfileStatement();
        if (null != ctx.limitClause()) {
            result.setLimit((LimitSegment) visit(ctx.limitClause()));
        }
        return result;
    }
    
    @Override
    public ASTNode visitShowProfiles(final ShowProfilesContext ctx) {
        return new XuguShowProfilesStatement();
    }
    
    @Override
    public ASTNode visitShowDatabases(final ShowDatabasesContext ctx) {
        XuguShowDatabasesStatement result = new XuguShowDatabasesStatement();
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowEvents(final ShowEventsContext ctx) {
        XuguShowEventsStatement result = new XuguShowEventsStatement();
        if (null != ctx.fromDatabase()) {
            result.setFromDatabase((FromDatabaseSegment) visit(ctx.fromDatabase()));
        }
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowTables(final ShowTablesContext ctx) {
        XuguShowTablesStatement result = new XuguShowTablesStatement();
        if (null != ctx.fromDatabase()) {
            result.setFromDatabase((FromDatabaseSegment) visit(ctx.fromDatabase()));
        }
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.setContainsFull(null != ctx.FULL());
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowTriggers(final ShowTriggersContext ctx) {
        XuguShowTriggersStatement result = new XuguShowTriggersStatement();
        if (null != ctx.fromDatabase()) {
            result.setFromDatabase((FromDatabaseSegment) visit(ctx.fromDatabase()));
        }
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowWhereClause(final ShowWhereClauseContext ctx) {
        return new WhereSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), (ExpressionSegment) visit(ctx.expr()));
    }
    
    @Override
    public ASTNode visitShowTableStatus(final ShowTableStatusContext ctx) {
        XuguShowTableStatusStatement result = new XuguShowTableStatusStatement();
        if (null != ctx.fromDatabase()) {
            result.setFromDatabase((FromDatabaseSegment) visit(ctx.fromDatabase()));
        }
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowColumns(final ShowColumnsContext ctx) {
        XuguShowColumnsStatement result = new XuguShowColumnsStatement();
        if (null != ctx.fromTable()) {
            result.setTable(((FromTableSegment) visit(ctx.fromTable())).getTable());
        }
        if (null != ctx.fromDatabase()) {
            result.setFromDatabase((FromDatabaseSegment) visit(ctx.fromDatabase()));
        }
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowFilter(final ShowFilterContext ctx) {
        ShowFilterSegment result = new ShowFilterSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
        if (null != ctx.showLike()) {
            result.setLike((ShowLikeSegment) visit(ctx.showLike()));
        }
        if (null != ctx.showWhereClause()) {
            result.setWhere((WhereSegment) visit(ctx.showWhereClause()));
        }
        return result;
    }
    
    @Override
    public ASTNode visitShowIndex(final ShowIndexContext ctx) {
        XuguShowIndexStatement result = new XuguShowIndexStatement();
        if (null != ctx.fromDatabase()) {
            result.setFromDatabase((FromDatabaseSegment) visit(ctx.fromDatabase()));
        }
        if (null != ctx.fromTable()) {
            result.setTable(((FromTableSegment) visitFromTable(ctx.fromTable())).getTable());
        }
        return result;
    }
    
    @Override
    public ASTNode visitShowCreateTable(final ShowCreateTableContext ctx) {
        XuguShowCreateTableStatement result = new XuguShowCreateTableStatement();
        result.setTable((SimpleTableSegment) visit(ctx.tableName()));
        return result;
    }
    
    @Override
    public ASTNode visitShowCreateTrigger(final ShowCreateTriggerContext ctx) {
        XuguShowCreateTriggerStatement result = new XuguShowCreateTriggerStatement();
        result.setName(((IdentifierValue) visit(ctx.triggerName())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitShowRelaylogEvent(final ShowRelaylogEventContext ctx) {
        XuguShowRelayLogEventsStatement result = new XuguShowRelayLogEventsStatement();
        if (null != ctx.logName()) {
            result.setLogName(((StringLiteralValue) visit(ctx.logName().stringLiterals().string_())).getValue());
        }
        if (null != ctx.limitClause()) {
            result.setLimit((LimitSegment) visit(ctx.limitClause()));
        }
        if (null != ctx.channelName()) {
            result.setChannel(((IdentifierValue) visit(ctx.channelName())).getValue());
        }
        return result;
    }
    
    @Override
    public ASTNode visitShowFunctionCode(final ShowFunctionCodeContext ctx) {
        XuguShowFunctionCodeStatement result = new XuguShowFunctionCodeStatement();
        result.setFunctionName(((FunctionSegment) visit(ctx.functionName())).getFunctionName());
        return result;
    }
    
    @Override
    public ASTNode visitShowGrants(final ShowGrantsContext ctx) {
        return new XuguShowGrantsStatement();
    }
    
    @Override
    public ASTNode visitShowMasterStatus(final ShowMasterStatusContext ctx) {
        return new XuguShowMasterStatusStatement();
    }
    
    @Override
    public ASTNode visitShowSlaveHosts(final ShowSlaveHostsContext ctx) {
        return new XuguShowSlaveHostsStatement();
    }
    
    @Override
    public ASTNode visitShowReplicaStatus(final ShowReplicaStatusContext ctx) {
        XuguShowReplicaStatusStatement result = new XuguShowReplicaStatusStatement();
        if (null != ctx.channelName()) {
            result.setChannel(((IdentifierValue) visit(ctx.channelName())).getValue());
        }
        return result;
    }
    
    @Override
    public ASTNode visitShowSlaveStatus(final ShowSlaveStatusContext ctx) {
        XuguShowSlaveStatusStatement result = new XuguShowSlaveStatusStatement();
        if (null != ctx.channelName()) {
            result.setChannel(((IdentifierValue) visit(ctx.channelName())).getValue());
        }
        return result;
    }
    
    @Override
    public ASTNode visitCreateResourceGroup(final CreateResourceGroupContext ctx) {
        XuguCreateResourceGroupStatement result = new XuguCreateResourceGroupStatement();
        result.setGroupName(((IdentifierValue) visit(ctx.groupName())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitBinlog(final BinlogContext ctx) {
        return new XuguBinlogStatement(((StringLiteralValue) visit(ctx.stringLiterals())).getValue());
    }
    
    @Override
    public ASTNode visitFromTable(final FromTableContext ctx) {
        FromTableSegment result = new FromTableSegment();
        result.setTable((SimpleTableSegment) visit(ctx.tableName()));
        return result;
    }
    
    @Override
    public ASTNode visitShowVariables(final ShowVariablesContext ctx) {
        XuguShowVariablesStatement result = new XuguShowVariablesStatement();
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }

    @Override
    public ASTNode visitShowXuguVariable(final ShowXuguVariableContext ctx) {
        return new XuguShowStatement();
    }
    
    @Override
    public ASTNode visitShowCharacterSet(final ShowCharacterSetContext ctx) {
        XuguShowCharacterSetStatement result = new XuguShowCharacterSetStatement();
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowCollation(final ShowCollationContext ctx) {
        XuguShowCollationStatement result = new XuguShowCollationStatement();
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowFunctionStatus(final ShowFunctionStatusContext ctx) {
        XuguShowFunctionStatusStatement result = new XuguShowFunctionStatusStatement();
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowProcedureStatus(final ShowProcedureStatusContext ctx) {
        XuguShowProcedureStatusStatement result = new XuguShowProcedureStatusStatement();
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowOpenTables(final ShowOpenTablesContext ctx) {
        XuguShowOpenTablesStatement result = new XuguShowOpenTablesStatement();
        if (null != ctx.fromDatabase()) {
            result.setFromDatabase((FromDatabaseSegment) visit(ctx.fromDatabase()));
        }
        if (null != ctx.showFilter()) {
            result.setFilter((ShowFilterSegment) visit(ctx.showFilter()));
        }
        result.addParameterMarkerSegments(getParameterMarkerSegments());
        return result;
    }
    
    @Override
    public ASTNode visitShowPlugins(final ShowPluginsContext ctx) {
        return new XuguShowPluginsStatement();
    }
    
    @Override
    public ASTNode visitShowPrivileges(final ShowPrivilegesContext ctx) {
        return new XuguShowPrivilegesStatement();
    }
    
    @Override
    public ASTNode visitShutdown(final ShutdownContext ctx) {
        return new XuguShutdownStatement();
    }
    
    @Override
    public ASTNode visitShowProcesslist(final ShowProcesslistContext ctx) {
        return new XuguShowProcessListStatement(null != ctx.FULL());
    }
    
    @Override
    public ASTNode visitShowCreateUser(final ShowCreateUserContext ctx) {
        XuguShowCreateUserStatement result = new XuguShowCreateUserStatement();
        result.setName(((IdentifierValue) visit(ctx.username())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitSetVariable(final SetVariableContext ctx) {
        XuguSetStatement result = new XuguSetStatement();
        Collection<VariableAssignSegment> variableAssigns = getVariableAssigns(ctx.optionValueList());
        result.getVariableAssigns().addAll(variableAssigns);
        return result;
    }
    
    private Collection<VariableAssignSegment> getVariableAssigns(final OptionValueListContext ctx) {
        Collection<VariableAssignSegment> result = new LinkedList<>();
        result.add(null == ctx.optionValueNoOptionType() ? getVariableAssignSegment(ctx) : getVariableAssignSegment(ctx.optionValueNoOptionType()));
        for (OptionValueContext each : ctx.optionValue()) {
            result.add(getVariableAssignSegment(each));
        }
        return result;
    }
    
    private VariableAssignSegment getVariableAssignSegment(final OptionValueContext ctx) {
        if (null != ctx.optionValueNoOptionType()) {
            return getVariableAssignSegment(ctx.optionValueNoOptionType());
        }
        VariableSegment variable = new VariableSegment(
                ctx.internalVariableName().start.getStartIndex(), ctx.internalVariableName().stop.getStopIndex(), ctx.internalVariableName().getText(), ctx.optionType().getText());
        return new VariableAssignSegment(ctx.start.getStartIndex(), ctx.stop.getStopIndex(), variable, ctx.setExprOrDefault().getText());
    }
    
    private VariableAssignSegment getVariableAssignSegment(final OptionValueListContext ctx) {
        VariableSegment variable = new VariableSegment(
                ctx.internalVariableName().start.getStartIndex(), ctx.internalVariableName().stop.getStopIndex(), ctx.internalVariableName().getText(), ctx.optionType().getText());
        return new VariableAssignSegment(ctx.start.getStartIndex(), ctx.setExprOrDefault().stop.getStopIndex(), variable, ctx.setExprOrDefault().getText());
    }
    
    private VariableAssignSegment getVariableAssignSegment(final OptionValueNoOptionTypeContext ctx) {
        return new VariableAssignSegment(ctx.start.getStartIndex(), ctx.stop.getStopIndex(), getVariableSegment(ctx), getAssignValue(ctx));
    }
    
    private VariableSegment getVariableSegment(final OptionValueNoOptionTypeContext ctx) {
        if (null != ctx.NAMES()) {
            // TODO Consider setting all three system variables: character_set_client, character_set_results, character_set_connection
            return new VariableSegment(ctx.NAMES().getSymbol().getStartIndex(), ctx.NAMES().getSymbol().getStopIndex(), "character_set_client");
        }
        if (null != ctx.internalVariableName()) {
            return new VariableSegment(ctx.internalVariableName().start.getStartIndex(), ctx.internalVariableName().stop.getStopIndex(), ctx.internalVariableName().getText());
        }
        if (null != ctx.userVariable()) {
            return new VariableSegment(ctx.userVariable().start.getStartIndex(), ctx.userVariable().stop.getStopIndex(), ctx.userVariable().getText());
        }
        if (null != ctx.setSystemVariable()) {
            VariableSegment result = new VariableSegment(
                    ctx.setSystemVariable().start.getStartIndex(), ctx.setSystemVariable().stop.getStopIndex(), ctx.setSystemVariable().internalVariableName().getText());
            OptionTypeContext optionType = ctx.setSystemVariable().optionType();
            result.setScope(null == optionType ? "SESSION" : optionType.getText());
            return result;
        }
        return null;
    }
    
    private String getAssignValue(final OptionValueNoOptionTypeContext ctx) {
        if (null != ctx.NAMES()) {
            return ctx.charsetName().getText();
        } else if (null != ctx.internalVariableName()) {
            return ctx.setExprOrDefault().getText();
        } else if (null != ctx.userVariable()) {
            return ctx.expr().getText();
        } else if (null != ctx.setSystemVariable()) {
            return ctx.setExprOrDefault().getText();
        }
        return null;
    }
    
    @Override
    public ASTNode visitSetCharacter(final SetCharacterContext ctx) {
        int startIndex = null == ctx.CHARSET() ? ctx.CHARACTER().getSymbol().getStartIndex() : ctx.CHARSET().getSymbol().getStartIndex();
        int stopIndex = null == ctx.CHARSET() ? ctx.SET(1).getSymbol().getStopIndex() : ctx.CHARSET().getSymbol().getStopIndex();
        // TODO Consider setting all three system variables: character_set_client, character_set_results, character_set_connection
        String variableName = (null == ctx.CHARSET()) ? "character_set_client" : ctx.CHARSET().getText();
        VariableSegment variable = new VariableSegment(startIndex, stopIndex, variableName);
        String assignValue = (null == ctx.DEFAULT()) ? ctx.charsetName().getText() : ctx.DEFAULT().getText();
        VariableAssignSegment characterSet = new VariableAssignSegment(startIndex, stopIndex, variable, assignValue);
        XuguSetStatement result = new XuguSetStatement();
        result.getVariableAssigns().add(characterSet);
        return result;
    }
    
    @Override
    public ASTNode visitFromDatabase(final FromDatabaseContext ctx) {
        return new FromDatabaseSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), (DatabaseSegment) visit(ctx.databaseName()));
    }
    
    @Override
    public ASTNode visitShowLike(final ShowLikeContext ctx) {
        StringLiteralValue literalValue = (StringLiteralValue) visit(ctx.stringLiterals());
        return new ShowLikeSegment(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex(), literalValue.getValue());
    }
    
    @Override
    public ASTNode visitCreateLoadableFunction(final CreateLoadableFunctionContext ctx) {
        return new XuguCreateLoadableFunctionStatement();
    }
    
    @Override
    public ASTNode visitInstallComponent(final InstallComponentContext ctx) {
        XuguInstallComponentStatement result = new XuguInstallComponentStatement();
        List<String> components = new LinkedList<>();
        for (ComponentNameContext each : ctx.componentName()) {
            components.add(((StringLiteralValue) visit(each.string_())).getValue());
        }
        result.getComponents().addAll(components);
        return result;
    }
    
    @Override
    public ASTNode visitUninstallComponent(final UninstallComponentContext ctx) {
        XuguUninstallComponentStatement result = new XuguUninstallComponentStatement();
        List<String> components = new LinkedList<>();
        for (ComponentNameContext each : ctx.componentName()) {
            components.add(((StringLiteralValue) visit(each.string_())).getValue());
        }
        result.getComponents().addAll(components);
        return result;
    }
    
    @Override
    public ASTNode visitRestart(final RestartContext ctx) {
        return new XuguRestartStatement();
    }
    
    @Override
    public ASTNode visitSetResourceGroup(final SetResourceGroupContext ctx) {
        XuguSetResourceGroupStatement result = new XuguSetResourceGroupStatement();
        result.setGroupName(((IdentifierValue) visit(ctx.groupName())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitCheckTable(final CheckTableContext ctx) {
        XuguCheckTableStatement result = new XuguCheckTableStatement();
        result.getTables().addAll(((CollectionValue<SimpleTableSegment>) visit(ctx.tableList())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitDropResourceGroup(final DropResourceGroupContext ctx) {
        XuguDropResourceGroupStatement result = new XuguDropResourceGroupStatement();
        result.setGroupName(((IdentifierValue) visit(ctx.groupName())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitAlterResourceGroup(final AlterResourceGroupContext ctx) {
        XuguAlterResourceGroupStatement result = new XuguAlterResourceGroupStatement();
        result.setGroupName(((IdentifierValue) visit(ctx.groupName())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitDelimiter(final DelimiterContext ctx) {
        XuguDelimiterStatement result = new XuguDelimiterStatement();
        result.setDelimiterName(ctx.delimiterName().getText());
        return result;
    }
    
    @Override
    public ASTNode visitHelp(final HelpContext ctx) {
        XuguHelpStatement result = new XuguHelpStatement();
        result.setSearchString(ctx.textOrIdentifier().getText());
        return result;
    }
}
