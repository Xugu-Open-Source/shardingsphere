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
import org.apache.shardingsphere.sql.parser.api.visitor.statement.type.RLStatementVisitor;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ChangeMasterToContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.ChangeReplicationSourceToContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.StartSlaveContext;
import org.apache.shardingsphere.sql.parser.autogen.XuguStatementParser.StopSlaveContext;
import org.apache.shardingsphere.sql.parser.statement.xugu.rl.XuguChangeMasterStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.rl.XuguChangeReplicationSourceToStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.rl.XuguStartReplicaStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.rl.XuguStartSlaveStatement;
import org.apache.shardingsphere.sql.parser.statement.xugu.rl.XuguStopSlaveStatement;
import org.apache.shardingsphere.sql.parser.xugu.visitor.statement.XuguStatementVisitor;

/**
 * RL statement visitor for Xugu.
 */
public final class XuguRLStatementVisitor extends XuguStatementVisitor implements RLStatementVisitor {
    
    @Override
    public ASTNode visitChangeMasterTo(final ChangeMasterToContext ctx) {
        return new XuguChangeMasterStatement();
    }
    
    @Override
    public ASTNode visitStartSlave(final StartSlaveContext ctx) {
        return new XuguStartSlaveStatement();
    }
    
    @Override
    public ASTNode visitStopSlave(final StopSlaveContext ctx) {
        return new XuguStopSlaveStatement();
    }
    
    @Override
    public ASTNode visitChangeReplicationSourceTo(final ChangeReplicationSourceToContext ctx) {
        return new XuguChangeReplicationSourceToStatement();
    }
    
    @Override
    public ASTNode visitStartReplica(final XuguStatementParser.StartReplicaContext ctx) {
        return new XuguStartReplicaStatement();
    }
}
