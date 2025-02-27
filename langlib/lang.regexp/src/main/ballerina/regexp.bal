// Copyright (c) 2022 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
// These functions will all be isolated and public
// regexp module

import ballerina/jballerina.java;

# The type RegExp refers to the tagged data basic type with tag `re`.
@builtinSubtype
public type RegExp any;

public type Span readonly & object {
    public int startIndex;
    public int endIndex;
    // This avoids constructing a potentially long string unless and until it is needed.
    public isolated function substring() returns string;
};

public type Groups readonly & [Span, Span?...];

type SpanAsTupleType [int, int, string];

type GroupsAsSpanArrayType SpanAsTupleType[];

type GroupsArrayType GroupsAsSpanArrayType[];

# Returns the span of the first match that starts at or after startIndex.
public isolated function find(RegExp re, string str, int startIndex = 0) returns Span? {
    SpanAsTupleType? resultArr = findImpl(re, str, startIndex);
    if (resultArr is SpanAsTupleType) {
        Span spanObj = new java:SpanImpl(resultArr[0], resultArr[1], resultArr[2]);
        return spanObj;
    }
}

isolated function findImpl(RegExp reExp, string str, int startIndex = 0) returns SpanAsTupleType? = @java:Method {
    'class: "org.ballerinalang.langlib.regexp.Find",
    name: "find"
} external;

public isolated function findGroups(RegExp re, string str, int startIndex = 0) returns Groups? {
    GroupsAsSpanArrayType? resultArr = findGroupsImpl(re, str, startIndex);
    if (resultArr is GroupsAsSpanArrayType) {
        SpanAsTupleType firstMatch = resultArr[0];
        Span firstMatchSpan = new java:SpanImpl(firstMatch[0], firstMatch[1], firstMatch[2]);
        Span[] spanArr = [];
        foreach int index in 1 ..< resultArr.length() {
            SpanAsTupleType matchGroup = resultArr[index];
            Span spanObj = new java:SpanImpl(matchGroup[0], matchGroup[1], matchGroup[2]);
            spanArr.push(spanObj);
        }
        return [firstMatchSpan, ...spanArr];
    }
}

isolated function findGroupsImpl(RegExp reExp, string str, int startIndex = 0) returns GroupsAsSpanArrayType? = @java:Method {
    'class: "org.ballerinalang.langlib.regexp.Find",
    name: "findGroups"
} external;

# Return all non-overlapping matches.
public isolated function findAll(RegExp re, string str, int startIndex = 0) returns Span[] {
    Span[] spanArr = [];
    GroupsAsSpanArrayType? resultArr = findGroupsImpl(re, str, startIndex);
    if (resultArr is GroupsAsSpanArrayType) {
        foreach SpanAsTupleType tpl in resultArr {
            spanArr.push(new java:SpanImpl(tpl[0], tpl[1], tpl[2]));
        }
    }
    return spanArr;
}

# Return all non-overlapping matches.
public isolated function findAllGroups(RegExp re, string str, int startIndex = 0) returns Groups[] {
    GroupsArrayType? resultArr = findAllGroupsImpl(re, str, startIndex);
    if (resultArr is GroupsArrayType) {
        Groups[] groupArrRes = [];
        foreach GroupsAsSpanArrayType groupArr in resultArr {
            int resultArrLength = groupArr.length();
            SpanAsTupleType firstMatch = groupArr[0];
            Span firstMatchSpan = new java:SpanImpl(firstMatch[0], firstMatch[1], firstMatch[2]);
            Span[] spanArr = [];
            foreach int index in 1 ..< resultArrLength {
                SpanAsTupleType matchGroup = groupArr[index];
                Span spanObj = new java:SpanImpl(matchGroup[0], matchGroup[1], matchGroup[2]);
                spanArr.push(spanObj);
            }
            Groups g = [firstMatchSpan, ...spanArr];
            groupArrRes.push(g);
        }
        return groupArrRes;
    }
    return [];
}

isolated function findAllGroupsImpl(RegExp reExp, string str, int startIndex = 0) returns GroupsArrayType? = @java:Method {
    'class: "org.ballerinalang.langlib.regexp.Find",
    name: "findAllGroups"
} external;

public isolated function matchAt(RegExp re, string str, int startIndex = 0) returns Span? {
    SpanAsTupleType? resultArr = matchAtImpl(re, str, startIndex);
    if (resultArr is SpanAsTupleType) {
        Span spanObj = new java:SpanImpl(resultArr[0], resultArr[1], resultArr[2]);
        return spanObj;
    }
}

isolated function matchAtImpl(RegExp reExp, string str, int startIndex = 0) returns [int, int, string]? = @java:Method {
    'class: "org.ballerinalang.langlib.regexp.Matches",
    name: "matchAt"
} external;

public isolated function matchGroupsAt(RegExp re, string str, int startIndex = 0) returns Groups? {
    GroupsAsSpanArrayType? resultArr = matchGroupsAtImpl(re, str, startIndex);
    if (resultArr is GroupsAsSpanArrayType) {
        SpanAsTupleType firstMatch = resultArr[0];
        Span firstMatchSpan = new java:SpanImpl(firstMatch[0], firstMatch[1], firstMatch[2]);
        Span[] spanArr = [];
        foreach int index in 1 ..< resultArr.length() {
            SpanAsTupleType matchGroup = resultArr[index];
            Span spanObj = new java:SpanImpl(matchGroup[0], matchGroup[1], matchGroup[2]);
            spanArr.push(spanObj);
        }
        return [firstMatchSpan, ...spanArr];
    }
}

isolated function matchGroupsAtImpl(RegExp reExp, string str, int startIndex = 0) returns GroupsAsSpanArrayType? = @java:Method {
    'class: "org.ballerinalang.langlib.regexp.Matches",
    name: "matchGroupsAt"
} external;

# Says whether there is a match of the RegExp that starts at the beginning of the string and ends at the end of the string.
public isolated function isFullMatch(RegExp re, string str) returns boolean {
    return isFullMatchImpl(re, str);
}

isolated function isFullMatchImpl(RegExp reExp, string str) returns boolean = @java:Method {
    'class: "org.ballerinalang.langlib.regexp.Matches",
    name: "isFullMatch"
} external;

# Says whether there is a match of the RegExp that starts at the beginning of the string and ends at the end of the string.
public isolated function fullMatchGroups(RegExp re, string str) returns Groups? {
    return matchGroupsAt(re, str);
}

public type ReplacerFunction function (Groups groups) returns string;

public type Replacement ReplacerFunction|string;

# Replaces the first occurrence of a regular expression.
public isolated function replace(RegExp re, string str, Replacement replacement, int startIndex = 0) returns string {
    string replacementStr = "";
    Groups? findResult = findGroups(re, str, startIndex);
    if findResult is () {
        return str;
    }
    if (replacement is ReplacerFunction) {
        replacementStr = replacement(findResult);
    } else {
        replacementStr = replacement;
    }
    return replaceFromString(re, str, replacementStr, startIndex);
}

isolated function replaceFromString(RegExp reExp, string str, string replacementStr, int startIndex) returns string = @java:Method {
    'class: "org.ballerinalang.langlib.regexp.Replace",
    name: "replaceFromString"
} external;

# Replaces all occurrences of a regular expression.
public isolated function replaceAll(RegExp re, string str, Replacement replacement, int startIndex = 0) returns string {
    Groups? findResult = findGroups(re, str, startIndex);
    if findResult is () {
        return str;
    }
    string replacementStr = replacement is ReplacerFunction ? replacement(findResult) : replacement;
    return replaceAllFromString(re, str, replacementStr, startIndex);
}

isolated function replaceAllFromString(RegExp reExp, string str, string replacementStr, int startIndex) returns string = @java:Method {
    'class: "org.ballerinalang.langlib.regexp.Replace",
    name: "replaceAllFromString"
} external;

public isolated function fromString(string str) returns RegExp|error = @java:Method {
    'class: "org.ballerinalang.langlib.regexp.FromString",
    name: "fromString"
} external;
