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

package org.apache.shardingsphere.data.pipeline.xugu.datasource;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.data.pipeline.spi.JdbcQueryPropertiesExtension;
import org.apache.shardingsphere.data.pipeline.xugu.ingest.incremental.client.XuguServerVersion;

import java.util.Properties;

/**
 * JDBC query properties extension of MySQL.
 */
@Slf4j
public final class XuguJdbcQueryPropertiesExtension implements JdbcQueryPropertiesExtension {
    
    private static final String XUGU_CONNECTOR_VERSION = initXuguConnectorVersion();
    
    private final Properties toBeOverrideQueryProps = new Properties();
    
    private final Properties completeIfMissedQueryProps = new Properties();
    
    public XuguJdbcQueryPropertiesExtension() {
        toBeOverrideQueryProps.setProperty("useSSL", Boolean.FALSE.toString());
        toBeOverrideQueryProps.setProperty("useServerPrepStmts", Boolean.FALSE.toString());
        toBeOverrideQueryProps.setProperty("rewriteBatchedStatements", Boolean.TRUE.toString());
        toBeOverrideQueryProps.setProperty("yearIsDateType", Boolean.FALSE.toString());
        toBeOverrideQueryProps.setProperty("zeroDateTimeBehavior", getZeroDateTimeBehavior());
        toBeOverrideQueryProps.setProperty("noDatetimeStringSync", Boolean.TRUE.toString());
        toBeOverrideQueryProps.setProperty("jdbcCompliantTruncation", Boolean.FALSE.toString());
        completeIfMissedQueryProps.setProperty("netTimeoutForStreamingResults", "600");
    }
    
    private String getZeroDateTimeBehavior() {
        // refer https://bugs.mysql.com/bug.php?id=91065
        return null != XUGU_CONNECTOR_VERSION && new XuguServerVersion(XUGU_CONNECTOR_VERSION).greaterThanOrEqualTo(8, 0, 0) ? "CONVERT_TO_NULL" : "convertToNull";
    }
    
    private static String initXuguConnectorVersion() {
        try {
            Class<?> driverClass = Thread.currentThread().getContextClassLoader().loadClass("com.xugu.cloudjdbc.Driver");
            return driverClass.getPackage().getImplementationVersion();
        } catch (final ClassNotFoundException ex) {
            log.warn("Can not find `com.xugu.cloudjdbc.Driver` class.");
            return null;
        }
    }
    
    @Override
    public void extendQueryProperties(final Properties props) {
        for (String each : toBeOverrideQueryProps.stringPropertyNames()) {
            props.setProperty(each, toBeOverrideQueryProps.getProperty(each));
        }
        for (String each : completeIfMissedQueryProps.stringPropertyNames()) {
            if (!props.containsKey(each)) {
                props.setProperty(each, completeIfMissedQueryProps.getProperty(each));
            }
        }
    }
    
    @Override
    public String getDatabaseType() {
        return "XuGu";
    }
}
