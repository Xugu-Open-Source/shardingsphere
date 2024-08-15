package org.apache.shardingsphere.underlying.common.metadata.datasource;

import org.apache.shardingsphere.underlying.common.config.DatabaseAccessConfiguration;
import org.apache.shardingsphere.underlying.common.database.type.DatabaseTypes;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class XuGuDataSourceMetasTest {

    private DataSourceMetas dataSourceMetas;

    @Before
    public void setUp() {
        Map<String, DatabaseAccessConfiguration> databaseAccessConfigurationMap = new HashMap<>(2, 1);
        databaseAccessConfigurationMap.put("ds_0", new DatabaseAccessConfiguration("jdbc:xugu://127.0.0.1:5138/db_0", "test", null));
        databaseAccessConfigurationMap.put("ds_1", new DatabaseAccessConfiguration("jdbc:xugu://127.0.0.1:5138/db_1", "test", null));
        dataSourceMetas = new DataSourceMetas(DatabaseTypes.getActualDatabaseType("XuGu"), databaseAccessConfigurationMap);
    }

    @Test
    public void assertGetAllInstanceDataSourceNamesForShardingRule() {
        assertNotNull(dataSourceMetas.getAllInstanceDataSourceNames());
    }

    @Test
    public void assertGetActualCatalogForShardingRule() {
        assertThat(dataSourceMetas.getDataSourceMetaData("ds_0").getCatalog(), is("db_0"));
    }

    @Test
    public void assertGetActualSchemaNameForShardingRuleForXuGu() {
        assertNotNull(dataSourceMetas.getDataSourceMetaData("ds_0").getSchema());
    }
}
