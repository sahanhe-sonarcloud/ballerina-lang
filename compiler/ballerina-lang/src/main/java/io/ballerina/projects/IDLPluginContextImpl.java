/*
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.ballerina.projects;

import io.ballerina.projects.plugins.IDLClientGenerator;
import io.ballerina.projects.plugins.IDLPluginContext;

import java.util.ArrayList;
import java.util.List;

/**
 * The default implementation of the {@code IDLPluginContext}.
 *
 * @since 2.3.0
 */
public class IDLPluginContextImpl implements IDLPluginContext {
    List<IDLClientGenerator> idlClientGenerators = new ArrayList<>();

    @Override
    public void addCodeGenerator(IDLClientGenerator codeGenerator) {
        idlClientGenerators.add(codeGenerator);
    }

    public List<IDLClientGenerator> idlClientGenerators() {
        return idlClientGenerators;
    }
}
