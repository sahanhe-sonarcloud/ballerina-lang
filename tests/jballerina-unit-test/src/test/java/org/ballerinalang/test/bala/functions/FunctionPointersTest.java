/*
*   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.ballerinalang.test.bala.functions;

import io.ballerina.runtime.api.values.BArray;
import org.ballerinalang.test.BAssertUtil;
import org.ballerinalang.test.BCompileUtil;
import org.ballerinalang.test.BRunUtil;
import org.ballerinalang.test.CompileResult;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for Function pointers and lambda in BALA.
 *
 * @since 0.975.0
 */
public class FunctionPointersTest {

    CompileResult result;

    @BeforeClass
    public void setup() {
        BCompileUtil.compileAndCacheBala("test-src/bala/test_projects/test_project");
        result = BCompileUtil.compile("test-src/bala/test_bala/functions/test_global_function_pointers.bal");
    }

    @Test
    public void testAnyFunctionTypeNegative() {
        CompileResult negativeCompileResult =
                BCompileUtil.compile("test-src/bala/test_bala/functions/test_global_function_pointers_negative.bal");
        int i = 0;
        BAssertUtil.validateError(negativeCompileResult, i++,
                "incompatible types: expected 'function', found 'string'", 20, 12);
        BAssertUtil.validateError(negativeCompileResult, i++,
                "incompatible types: expected 'isolated function', found 'function () returns " +
                        "(function (string,int) returns (string))'", 24, 30);
        BAssertUtil.validateError(negativeCompileResult, i++,
                "incompatible types: expected 'function (int) returns (string)', found 'function'", 28, 41);
        BAssertUtil.validateError(negativeCompileResult, i++,
                "cannot call function pointer of type 'function'", 30, 22);
        Assert.assertEquals(negativeCompileResult.getErrorCount(), i);
    }

    @Test
    public void testGlobalFP() {
        // testing function pointer.
        Object returns = BRunUtil.invoke(result, "test1");
        Assert.assertNotNull(returns);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.toString(), "test1");
    }

    @Test
    public void testGlobalFPAsLambda() {
        // lambda.
        Object returns = BRunUtil.invoke(result, "test2");
        Assert.assertNotNull(returns);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.toString(), "test2true");
    }

    @Test
    public void testGlobalFPAssignment() {
        // assign function pointer and invoke.
        Object resultArr = BRunUtil.invoke(result, "test3");
        BArray returns = (BArray) resultArr;
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.size(), 3);
        Assert.assertNotNull(returns.get(0));
        Assert.assertEquals(returns.get(0).toString(), "test3");
        Assert.assertNotNull(returns.get(1));
        Assert.assertEquals(returns.get(1).toString(), "test3");
        Assert.assertNotNull(returns.get(2));
        Assert.assertEquals(returns.get(2).toString(), "3test");
    }

    @Test
    public void testGlobalFPWithLocalFP() {
        // Check global and local variable.
        Object returns = BRunUtil.invoke(result, "test5");
        Assert.assertNotNull(returns);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.toString(), "falsetest5");
    }

    @Test
    public void testGlobalFPByAssigningLocalFP() {
        // assign local ref to global and invoke.
        Object returns = BRunUtil.invoke(result, "test6");
        Assert.assertNotNull(returns);
        Assert.assertNotNull(returns);
        Assert.assertEquals(returns.toString(), "truetest6");
    }

    @Test
    public void testAnyFunction() {
        // test any function type descriptor.
        BRunUtil.invoke(result, "test7");
    }

    @Test
    public void testGlobalFPWithDefaultValues() {
        BRunUtil.invoke(result, "test8");
    }
}
