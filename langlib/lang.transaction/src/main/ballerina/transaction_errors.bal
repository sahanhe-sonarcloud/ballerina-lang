// Copyright (c) 2020 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

# Type of error returned by commit action.
public type Error distinct error;

# Log and prepare `error` as a `Error`.
#
# + message - Error message
# + err - `error` instance
# + return - Prepared `Error` instance
public isolated function prepareError(string message, error? err = ()) returns Error {
    Error trxError;
    if (err is error) {
        trxError = error Error(message, err);
    } else {
        trxError = error Error(message);
    }
    return trxError;
}
