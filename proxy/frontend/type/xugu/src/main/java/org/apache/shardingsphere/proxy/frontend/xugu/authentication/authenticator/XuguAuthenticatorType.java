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

package org.apache.shardingsphere.proxy.frontend.xugu.authentication.authenticator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.authentication.AuthenticatorType;
import org.apache.shardingsphere.proxy.frontend.xugu.authentication.authenticator.impl.XuguCachingSha2PasswordAuthenticator;
import org.apache.shardingsphere.proxy.frontend.xugu.authentication.authenticator.impl.XuguClearPasswordAuthenticator;
import org.apache.shardingsphere.proxy.frontend.xugu.authentication.authenticator.impl.XuguNativePasswordAuthenticator;

/**
 * Authenticator type for MySQL.
 */
@RequiredArgsConstructor
@Getter
public enum XuguAuthenticatorType implements AuthenticatorType {
    
    // TODO impl OLD_PASSWORD Authenticator
    OLD_PASSWORD(XuguNativePasswordAuthenticator.class),
    
    NATIVE(XuguNativePasswordAuthenticator.class, true),
    
    CLEAR_TEXT(XuguClearPasswordAuthenticator.class),
    
    // TODO impl WINDOWS_NATIVE Authenticator
    WINDOWS_NATIVE(XuguNativePasswordAuthenticator.class),
    
    // TODO impl SHA256 Authenticator
    SHA256(XuguNativePasswordAuthenticator.class),
    
    CACHING_SHA2_PASSWORD(XuguCachingSha2PasswordAuthenticator.class);
    
    private final Class<? extends XuguAuthenticator> authenticatorClass;
    
    private final boolean isDefault;
    
    XuguAuthenticatorType(final Class<? extends XuguAuthenticator> authenticatorClass) {
        this(authenticatorClass, false);
    }
}
