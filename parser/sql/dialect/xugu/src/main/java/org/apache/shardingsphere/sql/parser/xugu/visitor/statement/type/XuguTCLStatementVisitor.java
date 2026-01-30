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

import org.antlr.v4.runtime.Token;
import org.apache.shardingsphere.sql.parser.api.ASTNode;
import org.apache.shardingsphere.sql.parser.api.visitor.statement.type.TCLStatementVisitor;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.BeginTransactionContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.CommitContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.LockContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ReleaseSavepointContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.RollbackContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.SavepointContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.SetAutoCommitContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.SetTransactionContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.TableLockContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.UnlockContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.XaBeginContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.XaCommitContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.XaEndContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.XaPrepareContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.XaRecoveryContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.XaRollbackContext;
import org.apache.shardingsphere.sql.parser.statement.core.enums.OperationScope;
import org.apache.shardingsphere.sql.parser.statement.core.enums.TransactionAccessType;
import org.apache.shardingsphere.sql.parser.statement.core.enums.TransactionIsolationLevel;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.AliasSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.generic.table.SimpleTableSegment;
import org.apache.shardingsphere.sql.parser.statement.core.segment.tcl.AutoCommitSegment;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.xa.XABeginStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.xa.XACommitStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.xa.XAEndStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.xa.XAPrepareStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.xa.XARecoveryStatement;
import org.apache.shardingsphere.sql.parser.statement.core.statement.tcl.xa.XARollbackStatement;
import org.apache.shardingsphere.sql.parser.statement.core.value.identifier.IdentifierValue;
import org.apache.shardingsphere.sql.parser.statement.xugu.tcl.XuguBeginTransactionStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.tcl.XuguCommitStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.tcl.XuguLockStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.tcl.XuguReleaseSavepointStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.tcl.XuguRollbackStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.tcl.XuguSavepointStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.tcl.XuguSetAutoCommitStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.tcl.XuguSetTransactionStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.tcl.XuguUnlockStatement;
import org.apache.shardingsphere.sql.parser.xugu.visitor.statement.XuguStatementVisitor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * TCL statement visitor for Xugu.
 */
public final class XuguTCLStatementVisitor extends XuguStatementVisitor implements TCLStatementVisitor {
    
    @Override
    public ASTNode visitSetTransaction(final SetTransactionContext ctx) {
        XuguSetTransactionStatement result = new XuguSetTransactionStatement();
        if (null != ctx.optionType()) {
            OperationScope scope = null;
            if (null != ctx.optionType().SESSION()) {
                scope = OperationScope.SESSION;
            } else if (null != ctx.optionType().GLOBAL()) {
                scope = OperationScope.GLOBAL;
            }
            result.setScope(scope);
        }
        if (null != ctx.transactionCharacteristics().isolationLevel()) {
            TransactionIsolationLevel isolationLevel = null;
            if (null != ctx.transactionCharacteristics().isolationLevel().isolationTypes().SERIALIZABLE()) {
                isolationLevel = TransactionIsolationLevel.SERIALIZABLE;
            } else if (null != ctx.transactionCharacteristics().isolationLevel().isolationTypes().COMMITTED()) {
                isolationLevel = TransactionIsolationLevel.READ_COMMITTED;
            } else if (null != ctx.transactionCharacteristics().isolationLevel().isolationTypes().UNCOMMITTED()) {
                isolationLevel = TransactionIsolationLevel.READ_UNCOMMITTED;
            } else if (null != ctx.transactionCharacteristics().isolationLevel().isolationTypes().REPEATABLE()) {
                isolationLevel = TransactionIsolationLevel.REPEATABLE_READ;
            }
            result.setIsolationLevel(isolationLevel);
        }
        if (null != ctx.transactionCharacteristics().transactionAccessMode()) {
            TransactionAccessType accessType = null;
            if (null != ctx.transactionCharacteristics().transactionAccessMode().ONLY()) {
                accessType = TransactionAccessType.READ_ONLY;
            } else if (null != ctx.transactionCharacteristics().transactionAccessMode().WRITE()) {
                accessType = TransactionAccessType.READ_WRITE;
            }
            result.setAccessMode(accessType);
        }
        return result;
    }
    
    @Override
    public ASTNode visitSetAutoCommit(final SetAutoCommitContext ctx) {
        XuguSetAutoCommitStatement result = new XuguSetAutoCommitStatement();
        result.setAutoCommit(generateAutoCommitSegment(ctx.autoCommitValue).isAutoCommit());
        return result;
    }
    
    private AutoCommitSegment generateAutoCommitSegment(final Token ctx) {
        boolean autoCommit = "1".equals(ctx.getText()) || "ON".equals(ctx.getText());
        return new AutoCommitSegment(ctx.getStartIndex(), ctx.getStopIndex(), autoCommit);
    }
    
    @Override
    public ASTNode visitBeginTransaction(final BeginTransactionContext ctx) {
        return new XuguBeginTransactionStatement();
    }
    
    @Override
    public ASTNode visitCommit(final CommitContext ctx) {
        return new XuguCommitStatement();
    }
    
    @Override
    public ASTNode visitRollback(final RollbackContext ctx) {
        XuguRollbackStatement result = new XuguRollbackStatement();
        if (null != ctx.identifier()) {
            result.setSavepointName(((IdentifierValue) visit(ctx.identifier())).getValue());
        }
        return result;
    }
    
    @Override
    public ASTNode visitSavepoint(final SavepointContext ctx) {
        XuguSavepointStatement result = new XuguSavepointStatement();
        result.setSavepointName(((IdentifierValue) visit(ctx.identifier())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitReleaseSavepoint(final ReleaseSavepointContext ctx) {
        XuguReleaseSavepointStatement result = new XuguReleaseSavepointStatement();
        result.setSavepointName(((IdentifierValue) visit(ctx.identifier())).getValue());
        return result;
    }
    
    @Override
    public ASTNode visitXaBegin(final XaBeginContext ctx) {
        return new XABeginStatement(ctx.xid().getText());
    }
    
    @Override
    public ASTNode visitXaPrepare(final XaPrepareContext ctx) {
        return new XAPrepareStatement(ctx.xid().getText());
    }
    
    @Override
    public ASTNode visitXaCommit(final XaCommitContext ctx) {
        return new XACommitStatement(ctx.xid().getText());
    }
    
    @Override
    public ASTNode visitXaRollback(final XaRollbackContext ctx) {
        return new XARollbackStatement(ctx.xid().getText());
    }
    
    @Override
    public ASTNode visitXaEnd(final XaEndContext ctx) {
        return new XAEndStatement(ctx.xid().getText());
    }
    
    @Override
    public ASTNode visitXaRecovery(final XaRecoveryContext ctx) {
        return new XARecoveryStatement();
    }
    
    @Override
    public ASTNode visitLock(final LockContext ctx) {
        XuguLockStatement result = new XuguLockStatement();
        if (null != ctx.tableLock()) {
            result.getTables().addAll(getLockTables(ctx.tableLock()));
        }
        return result;
    }
    
    private Collection<SimpleTableSegment> getLockTables(final List<TableLockContext> tableLockContexts) {
        Collection<SimpleTableSegment> result = new LinkedList<>();
        for (TableLockContext each : tableLockContexts) {
            SimpleTableSegment simpleTableSegment = (SimpleTableSegment) visit(each.tableName());
            if (null != each.alias()) {
                simpleTableSegment.setAlias((AliasSegment) visit(each.alias()));
            }
            result.add(simpleTableSegment);
        }
        return result;
    }
    
    @Override
    public ASTNode visitUnlock(final UnlockContext ctx) {
        return new XuguUnlockStatement();
    }
}
