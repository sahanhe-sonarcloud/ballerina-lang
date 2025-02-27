/*
 * Copyright (c) 2022, WSO2 LLC. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.langserver.completion;

import org.ballerinalang.langserver.commons.workspace.WorkspaceDocumentException;
import org.ballerinalang.langserver.completions.providers.context.ClientDeclarationNodeContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Completion tests for {@link ClientDeclarationNodeContext}.
 *
 * @since 2201.3.0
 */
public class ClientDeclarationContextTest extends CompletionTest {

    @Test(dataProvider = "completion-data-provider")
    public void test(String config, String configPath) throws IOException, WorkspaceDocumentException {
        super.test(config, configPath);
    }

    @DataProvider(name = "completion-data-provider")
    @Override
    public Object[][] dataProvider() {
        return this.getConfigsList();
    }

    @Override
    public String getTestResourceDir() {
        return "client_declaration_context";
    }

    @Override
    public List<String> skipList() {
        List<String> skipList = new ArrayList<>();
        // Skipping due to https://github.com/ballerina-platform/ballerina-lang/issues/38234
        skipList.add("completions_inside_generated_idl_client.json");
        return skipList;
    }
}
