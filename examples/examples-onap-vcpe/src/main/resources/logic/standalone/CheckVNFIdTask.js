/*
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2020 Nordix Foundation.
 *  Modifications Copyright (C) 2020 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

executor.logger.info(executor.subject.id);
executor.logger.info(executor.inFields);

var vnfId = executor.inFields.get("vnfID").toString();
var blackFlag = executor.getContextAlbum("VnfIdWhiteBlackListAlbum").get(vnfId);

executor.logger.info("vnfId=" + vnfId + ", blackFlag=" + blackFlag);

if (blackFlag != null && blackFlag == true) {
    var vcpeClosedLoopStatus = executor.getContextAlbum("ControlLoopStatusAlbum").get(vnfId);
    vcpeClosedLoopStatus.put("notificationTime",  java.lang.Long.valueOf(Date.now()));
    vcpeClosedLoopStatus.put("notification", "BLACKLIST");
    var message = vcpeClosedLoopStatus.get("message");

    if (message != null) {
        vcpeClosedLoopStatus.put("message", message + ":VNF ID blacklisted");
    }
    else {
        vcpeClosedLoopStatus.put("message", "VNF ID blacklisted");
    }
}

executor.logger.info(executor.outFields);

true;
