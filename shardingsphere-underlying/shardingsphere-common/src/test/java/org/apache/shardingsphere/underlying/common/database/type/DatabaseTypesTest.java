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

package org.apache.shardingsphere.underlying.common.database.type;

import org.apache.shardingsphere.underlying.common.exception.ShardingSphereException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public final class DatabaseTypesTest {
    
    @Test
    public void assertGetActualDatabaseType() {
        assertThat(DatabaseTypes.getActualDatabaseType("MySQL").getName(), is("MySQL"));
    }

    @Test
    public void assertGetActualDatabaseTypeForXuGu() {
        assertThat(DatabaseTypes.getActualDatabaseType("XuGu").getName(), is("XuGu"));
    }

    @Test
    public void assertGetActualDatabaseTypeForCAE() {
        assertThat(DatabaseTypes.getActualDatabaseType("CAE").getName(), is("CAE"));
    }
    
    @Test(expected = ShardingSphereException.class)
    public void assertGetActualDatabaseTypeWithNotExistedDatabaseType() {
        DatabaseTypes.getActualDatabaseType("Invalid");
    }
    
    @Test
    public void assertGetTrunkDatabaseTypeWithTrunkDatabaseType() {
        assertThat(DatabaseTypes.getTrunkDatabaseType("MySQL").getName(), is("MySQL"));
    }

    @Test
    public void assertGetTrunkDatabaseTypeWithTrunkDatabaseTypeForXuGu() {
        assertThat(DatabaseTypes.getTrunkDatabaseType("XuGu").getName(), is("MySQL"));
    }

    @Test
    public void assertGetTrunkDatabaseTypeWithTrunkDatabaseTypeForCAE() {
        assertThat(DatabaseTypes.getTrunkDatabaseType("CAE").getName(), is("MySQL"));
    }
    
    @Test
    public void assertGetTrunkDatabaseTypeWithBranchDatabaseType() {
        assertThat(DatabaseTypes.getTrunkDatabaseType("H2").getName(), is("MySQL"));
    }
    
    @Test
    public void assertGetDatabaseTypeByStandardURL() {
        assertThat(DatabaseTypes.getDatabaseTypeByURL("jdbc:mysql://localhost:3306/test").getName(), is("MySQL"));
    }

    @Test
    public void assertGetDatabaseTypeByStandardURLForXuGu() {
        assertThat(DatabaseTypes.getDatabaseTypeByURL("jdbc:xugu://localhost:5138/db_0").getName(), is("XuGu"));
    }

    @Test
    public void assertGetDatabaseTypeByStandardURLForCAE() {
        assertThat(DatabaseTypes.getDatabaseTypeByURL("jdbc:cae://localhost:5135/test").getName(), is("CAE"));
    }
    
    @Test
    public void assertGetDatabaseTypeByURLAlias() {
        assertThat(DatabaseTypes.getDatabaseTypeByURL("jdbc:mysqlx://localhost:3306/test").getName(), is("MySQL"));
    }
    
    @Test
    public void assertGetDatabaseTypeSQL92() {
        assertThat(DatabaseTypes.getDatabaseTypeByURL("jdbc:sqlite:test").getName(), is("SQL92"));
    }
}
