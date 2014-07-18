/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.ribbon.proxy;

import com.netflix.client.config.IClientConfig;
import com.netflix.ribbon.ClientConfigFactory;
import com.netflix.ribbon.DefaultHttpResourceGroupFactory;
import com.netflix.ribbon.HttpResourceGroupFactory;
import com.netflix.ribbon.RibbonTransportFactory;
import com.netflix.ribbon.http.HttpResourceGroup;

import static java.lang.String.*;

/**
 * @author Tomasz Bak
 */
class ProxyHttpResourceGroupFactory<T> {
    private final ClassTemplate<T> classTemplate;
    private final HttpResourceGroupFactory httpResourceGroupFactory;
    private final IClientConfig clientConfig;

    ProxyHttpResourceGroupFactory(ClassTemplate<T> classTemplate) {
        this(classTemplate, new DefaultHttpResourceGroupFactory(ClientConfigFactory.DEFAULT, RibbonTransportFactory.DEFAULT),
                ClientConfigFactory.DEFAULT.newConfig(), RibbonTransportFactory.DEFAULT);
    }
    
    ProxyHttpResourceGroupFactory(ClassTemplate<T> classTemplate, HttpResourceGroupFactory httpResourceGroupFactory, IClientConfig clientConfig, 
            RibbonTransportFactory transportFactory) {
        this.classTemplate = classTemplate;
        this.httpResourceGroupFactory = httpResourceGroupFactory;
        this.clientConfig = clientConfig;
    }

    public HttpResourceGroup createResourceGroup() {
        String name = classTemplate.getResourceGroupName();
        Class<? extends HttpResourceGroup> resourceClass = classTemplate.getResourceGroupClass();
        if (name != null) {
            return httpResourceGroupFactory.createHttpResourceGroup(name, clientConfig);
        }
        if (resourceClass == null) {
            throw new RibbonProxyException(format(
                    "ResourceGroup not defined for interface %s - must be provided by annotation or passed explicitly during dynamic proxy creation",
                    classTemplate.getClientInterface()));
        }
        return Utils.newInstance(resourceClass);
    }
}
