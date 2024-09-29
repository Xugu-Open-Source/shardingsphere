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

package org.apache.shardingsphere.metadata.persist.service.database;

import org.apache.shardingsphere.metadata.persist.service.table.TableMetaDataPersistService;
import org.apache.shardingsphere.metadata.persist.service.table.ViewMetaDataPersistService;
import org.apache.shardingsphere.metadata.persist.service.version.MetaDataVersionPersistService;
import org.apache.shardingsphere.mode.spi.PersistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.configuration.plugins.Plugins;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseMetaDataPersistServiceTest {
    
    private DatabaseMetaDataPersistService persistService;
    
    @Mock
    private PersistRepository repository;
    
    @Mock
    private TableMetaDataPersistService tableMetaDataPersistService;
    
    @Mock
    private ViewMetaDataPersistService viewMetaDataPersistService;
    
    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        persistService = new DatabaseMetaDataPersistService(repository, mock(MetaDataVersionPersistService.class));
        Plugins.getMemberAccessor().set(DatabaseMetaDataPersistService.class.getDeclaredField("tableMetaDataPersistService"), persistService, tableMetaDataPersistService);
        Plugins.getMemberAccessor().set(DatabaseMetaDataPersistService.class.getDeclaredField("viewMetaDataPersistService"), persistService, viewMetaDataPersistService);
    }
    
    @Test
    void assertAdd() {
        persistService.add("foo_db");
        verify(repository).persist("/metadata/foo_db", "");
    }
    
    @Test
    void assertDrop() {
        persistService.drop("foo_db");
        verify(repository).delete("/metadata/foo_db");
    }
    
    @Test
    void assertLoadAllDatabaseNames() {
        when(repository.getChildrenKeys("/metadata")).thenReturn(Collections.singletonList("foo"));
        assertThat(persistService.loadAllDatabaseNames(), is(Collections.singletonList("foo")));
    }
}
