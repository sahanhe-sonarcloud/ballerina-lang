/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.semantic.api.test.symbols;

import io.ballerina.compiler.api.SemanticModel;
import io.ballerina.compiler.api.impl.values.BallerinaConstantValue;
import io.ballerina.compiler.api.symbols.AnnotationAttachmentSymbol;
import io.ballerina.compiler.api.symbols.AnnotationSymbol;
import io.ballerina.compiler.api.symbols.ConstantSymbol;
import io.ballerina.compiler.api.symbols.Documentation;
import io.ballerina.compiler.api.symbols.IntersectionTypeSymbol;
import io.ballerina.compiler.api.symbols.Qualifier;
import io.ballerina.compiler.api.symbols.RecordTypeSymbol;
import io.ballerina.compiler.api.symbols.Symbol;
import io.ballerina.compiler.api.symbols.SymbolKind;
import io.ballerina.compiler.api.symbols.TypeDefinitionSymbol;
import io.ballerina.compiler.api.symbols.TypeDescKind;
import io.ballerina.compiler.api.values.ConstantValue;
import io.ballerina.projects.Document;
import io.ballerina.projects.Project;
import io.ballerina.tools.text.LinePosition;
import org.ballerinalang.test.BCompileUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static io.ballerina.semantic.api.test.util.SemanticAPITestUtils.assertBasicsAndGetSymbol;
import static io.ballerina.semantic.api.test.util.SemanticAPITestUtils.getDefaultModulesSemanticModel;
import static io.ballerina.semantic.api.test.util.SemanticAPITestUtils.getDocumentForSingleSource;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Test cases for constant declaration symbols.
 *
 * @since 2.0.0
 */
public class ConstDeclSymbolTest {

    private SemanticModel model;
    private Document srcFile;

    @BeforeClass
    public void setup() {
        Project project = BCompileUtil.loadProject("test-src/symbols/const_decl_symbol_test.bal");
        model = getDefaultModulesSemanticModel(project);
        srcFile = getDocumentForSingleSource(project);
    }

    @Test(dataProvider = "ConstDeclarationProvider")
    public void testConstDeclarations(int line, int col, String expName, String expDoc, String expAnnot,
                                      List<Qualifier> expQuals) {
        ConstantSymbol symbol = (ConstantSymbol) assertBasicsAndGetSymbol(model, srcFile, line, col,
                                                                          expName, SymbolKind.CONSTANT);
        // check docs (metadata)
        assertTrue(symbol.documentation().isPresent());
        Documentation fieldDocs = symbol.documentation().get();
        if (expDoc != null) {
            assertTrue(fieldDocs.description().isPresent());
            assertEquals(fieldDocs.description().get(), expDoc);
        } else {
            assertTrue(fieldDocs.description().isEmpty());
        }

        // check annotations (metadata)
        List<AnnotationSymbol> fieldAnnots = symbol.annotations();
        if (expAnnot != null) {
            assertEquals(fieldAnnots.size(), 1);
            assertTrue(fieldAnnots.get(0).getName().isPresent());
            assertEquals(fieldAnnots.get(0).getName().get(), expAnnot);
        } else {
            assertEquals(fieldAnnots.size(), 0);
        }

        // check qualifiers
        List<Qualifier> qualifiers = symbol.qualifiers();
        if (expQuals.size() > 0) {
            expQuals.forEach(expQual -> assertTrue(qualifiers.contains(expQual)));
        } else {
            assertTrue(qualifiers.isEmpty());
        }

        // check type
        assertEquals(symbol.typeDescriptor().typeKind(), TypeDescKind.SINGLETON);
    }

    @DataProvider(name = "ConstDeclarationProvider")
    public Object[][] getConstDeclInfo() {
        return new Object[][]{
                {16, 6, "constValue", null, null, List.of()},
                {19, 20, "strConst", "String", null, List.of(Qualifier.PUBLIC)},
                {23, 17, "intConst", "Int", "constDecl", List.of(Qualifier.PUBLIC)},
                {27, 12, "floatConst", "Float", "constDecl", List.of()},
                {31, 11, "byteConst", "Byte", "constDecl", List.of()},
                {35, 14, "boolConst", "Boolean", "constDecl", List.of()},
        };
    }

    @Test(dataProvider = "PosProvider")
    public void testConstValues(int line, int col, String expVal, TypeDescKind typeDescKind) {
        Optional<Symbol> constSym = model.symbol(srcFile, LinePosition.from(line, col));

        assertTrue(constSym.isPresent());
        assertEquals(constSym.get().kind(), SymbolKind.CONSTANT);
        assertTrue(((ConstantSymbol) constSym.get()).resolvedValue().isPresent());
        assertEquals(((ConstantSymbol) constSym.get()).resolvedValue().get(), expVal);

        BallerinaConstantValue constValue = (BallerinaConstantValue) ((ConstantSymbol) constSym.get()).constValue();
        assertEquals(constValue.valueType().typeKind(), typeDescKind);
    }

    @DataProvider(name = "PosProvider")
    public Object[][] getConsts() {
        return new Object[][]{
                {16, 6, "1000", TypeDescKind.INT},
                {19, 20, "\"Value\"", TypeDescKind.STRING},
                {23, 17, "10", TypeDescKind.INT},
                {27, 12, "12.3", TypeDescKind.FLOAT},
                {31, 11, "2", TypeDescKind.BYTE},
                {35, 14, "true", TypeDescKind.BOOLEAN},
                {37, 10, "1010", TypeDescKind.INT},
                {38, 18, "{foo: \"Value\", bar: \"BAR\"}", TypeDescKind.INTERSECTION},
                {39, 20, "{foo: {a: 10, b: 100}}", TypeDescKind.INTERSECTION}
        };
    }

    @Test
    public void testValuesInConstantAnnotationAttachment() {
        Optional<Symbol> symbol = model.symbol(srcFile, LinePosition.from(47, 12));
        assertTrue(symbol.isPresent());
        TypeDefinitionSymbol typeDefSym = (TypeDefinitionSymbol) symbol.get();

        List<AnnotationAttachmentSymbol> attachments = typeDefSym.annotAttachments();
        assertTrue(attachments.size() > 0);

        AnnotationAttachmentSymbol annotAttachment = attachments.get(0);
        assertTrue(annotAttachment.isConstAnnotation());

        assertTrue(annotAttachment.attachmentValue().isPresent());
        ConstantValue constVal = annotAttachment.attachmentValue().get();

        // Test type-descriptor
        assertEquals(constVal.valueType().typeKind(), TypeDescKind.INTERSECTION);
        assertEquals(((IntersectionTypeSymbol) constVal.valueType()).memberTypeDescriptors().get(0).typeKind(),
                TypeDescKind.RECORD);
        assertEquals(((IntersectionTypeSymbol) constVal.valueType()).memberTypeDescriptors().get(1).typeKind(),
                TypeDescKind.READONLY);
        RecordTypeSymbol recTypeSymbol =
                (RecordTypeSymbol) ((IntersectionTypeSymbol) constVal.valueType()).memberTypeDescriptors().get(0);
        assertEquals(recTypeSymbol.signature(), "record {|1 id; record {|1 a; 2 b;|} perm;|}");

        // Test const value
        assertTrue(constVal.value() instanceof HashMap);
        HashMap valueMap = (HashMap) constVal.value();

        assertTrue(valueMap.get("id") instanceof BallerinaConstantValue);
        BallerinaConstantValue idValue =
                (BallerinaConstantValue) valueMap.get("id");
        assertEquals(idValue.valueType().typeKind(), TypeDescKind.INT);
        assertEquals(idValue.value(), 1L);

        assertTrue(valueMap.get("perm") instanceof BallerinaConstantValue);
        BallerinaConstantValue permValue =
                (BallerinaConstantValue) valueMap.get("perm");
        assertEquals(permValue.valueType().typeKind(), TypeDescKind.INTERSECTION);
        assertEquals(((IntersectionTypeSymbol) permValue.valueType()).effectiveTypeDescriptor().typeKind(),
                TypeDescKind.RECORD);
        assertTrue(permValue.value() instanceof HashMap);
        HashMap permMap = (HashMap) permValue.value();
        assertEquals(((BallerinaConstantValue) permMap.get("a")).value(), 1L);
        assertEquals(((BallerinaConstantValue) permMap.get("a")).valueType().typeKind(), TypeDescKind.INT);
        assertEquals(((BallerinaConstantValue) permMap.get("b")).value(), 2L);
        assertEquals(((BallerinaConstantValue) permMap.get("b")).valueType().typeKind(), TypeDescKind.INT);
    }

    @Test
    public void testMissingConstExpr() {
        Optional<Symbol> constSym = model.symbol(srcFile, LinePosition.from(40, 6));
        assertTrue(constSym.isPresent());
        assertEquals(constSym.get().kind(), SymbolKind.CONSTANT);
        assertTrue(((ConstantSymbol) constSym.get()).resolvedValue().isEmpty());
    }
}
