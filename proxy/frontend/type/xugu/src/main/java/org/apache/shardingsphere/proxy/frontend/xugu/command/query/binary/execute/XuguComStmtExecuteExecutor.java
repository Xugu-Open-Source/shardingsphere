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

package org.apache.shardingsphere.proxy.frontend.xugu.command.query.binary.execute;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.db.protocol.binary.BinaryCell;
import org.apache.shardingsphere.db.protocol.binary.BinaryRow;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguBinaryColumnType;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguConstants;
import org.apache.shardingsphere.db.protocol.xugu.constant.XuguNewParametersBoundFlag;
import org.apache.shardingsphere.db.protocol.xugu.packet.XuguPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.XuguBinaryResultSetRowPacket;
import org.apache.shardingsphere.db.protocol.xugu.packet.command.query.binary.execute.XuguComStmtExecutePacket;
import org.apache.shardingsphere.db.protocol.packet.DatabasePacket;
import org.apache.shardingsphere.infra.binder.context.aware.ParameterAware;
import org.apache.shardingsphere.infra.binder.context.statement.SQLStatementContext;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.session.query.QueryContext;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.proxy.backend.context.ProxyContext;
import org.apache.shardingsphere.proxy.backend.handler.ProxyBackendHandler;
import org.apache.shardingsphere.proxy.backend.handler.ProxyBackendHandlerFactory;
import org.apache.shardingsphere.proxy.backend.response.data.QueryResponseCell;
import org.apache.shardingsphere.proxy.backend.response.data.QueryResponseRow;
import org.apache.shardingsphere.proxy.backend.response.header.ResponseHeader;
import org.apache.shardingsphere.proxy.backend.response.header.query.QueryResponseHeader;
import org.apache.shardingsphere.proxy.backend.response.header.update.UpdateResponseHeader;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.proxy.frontend.command.executor.QueryCommandExecutor;
import org.apache.shardingsphere.proxy.frontend.command.executor.ResponseType;
import org.apache.shardingsphere.proxy.frontend.xugu.command.ServerStatusFlagCalculator;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.binary.XuguServerPreparedStatement;
import org.apache.shardingsphere.proxy.frontend.xugu.command.query.builder.ResponsePacketBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * COM_STMT_EXECUTE command executor for MySQL.
 */
@RequiredArgsConstructor
public final class XuguComStmtExecuteExecutor implements QueryCommandExecutor {
    
    private final XuguComStmtExecutePacket packet;
    
    private final ConnectionSession connectionSession;
    
    private ProxyBackendHandler proxyBackendHandler;
    
    @Getter
    private ResponseType responseType;
    
    @Override
    public Collection<DatabasePacket> execute() throws SQLException {
        XuguServerPreparedStatement preparedStatement = updateAndGetPreparedStatement();
        List<Object> params = packet.readParameters(preparedStatement.getParameterTypes(), preparedStatement.getLongData().keySet(), preparedStatement.getParameterColumnDefinitionFlags());
        preparedStatement.getLongData().forEach(params::set);
        SQLStatementContext sqlStatementContext = preparedStatement.getSqlStatementContext();
        if (sqlStatementContext instanceof ParameterAware) {
            ((ParameterAware) sqlStatementContext).setUpParameters(params);
        }
        QueryContext queryContext = new QueryContext(sqlStatementContext, preparedStatement.getSql(), params, preparedStatement.getHintValueContext(), connectionSession.getConnectionContext(),
                ProxyContext.getInstance().getContextManager().getMetaDataContexts().getMetaData(), true);
        connectionSession.setQueryContext(queryContext);
        proxyBackendHandler = ProxyBackendHandlerFactory.newInstance(TypedSPILoader.getService(DatabaseType.class, "XuGu"), queryContext, connectionSession, true);
        ResponseHeader responseHeader = proxyBackendHandler.execute();
        return responseHeader instanceof QueryResponseHeader ? processQuery((QueryResponseHeader) responseHeader) : processUpdate((UpdateResponseHeader) responseHeader);
    }
    
    private XuguServerPreparedStatement updateAndGetPreparedStatement() {
        XuguServerPreparedStatement result = connectionSession.getServerPreparedStatementRegistry().getPreparedStatement(packet.getStatementId());
        if (XuguNewParametersBoundFlag.PARAMETER_TYPE_EXIST == packet.getNewParametersBoundFlag()) {
            result.getParameterTypes().clear();
            result.getParameterTypes().addAll(packet.getNewParameterTypes());
        }
        return result;
    }
    
    private Collection<DatabasePacket> processQuery(final QueryResponseHeader queryResponseHeader) {
        responseType = ResponseType.QUERY;
        int characterSet = connectionSession.getAttributeMap().attr(XuguConstants.CHARACTER_SET_ATTRIBUTE_KEY).get().getId();
        return ResponsePacketBuilder.buildQueryResponsePackets(queryResponseHeader, characterSet, ServerStatusFlagCalculator.calculateFor(connectionSession, true));
    }
    
    private Collection<DatabasePacket> processUpdate(final UpdateResponseHeader updateResponseHeader) {
        responseType = ResponseType.UPDATE;
        return ResponsePacketBuilder.buildUpdateResponsePackets(updateResponseHeader, ServerStatusFlagCalculator.calculateFor(connectionSession, true));
    }
    
    @Override
    public boolean next() throws SQLException {
        return proxyBackendHandler.next();
    }
    
    @Override
    public XuguPacket getQueryRowPacket() throws SQLException {
        QueryResponseRow queryResponseRow = proxyBackendHandler.getRowData();
        return new XuguBinaryResultSetRowPacket(createBinaryRow(queryResponseRow));
    }
    
    private BinaryRow createBinaryRow(final QueryResponseRow queryResponseRow) {
        List<BinaryCell> result = new ArrayList<>(queryResponseRow.getCells().size());
        for (QueryResponseCell each : queryResponseRow.getCells()) {
            result.add(new BinaryCell(XuguBinaryColumnType.valueOfJDBCType(each.getJdbcType()), each.getData()));
        }
        return new BinaryRow(result);
    }
    
    @Override
    public void close() throws SQLException {
        if (null != proxyBackendHandler) {
            proxyBackendHandler.close();
        }
    }
}
