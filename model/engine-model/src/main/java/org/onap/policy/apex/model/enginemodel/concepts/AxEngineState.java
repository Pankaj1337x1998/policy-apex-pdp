/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.model.enginemodel.concepts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * This enumeration indicates the execution state of an Apex engine.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AxEngineState", namespace = "http://www.onap.org/policy/apex-pdp")
public enum AxEngineState {
    /** The state of the engine is not known. */
    UNDEFINED,
    /** The engine is stopped. */
    STOPPED,
    /** The engine is running and is waiting to execute a policy. */
    READY,
    /** The engine is running and is executing a policy. */
    EXECUTING,
    /** The engine has been ordered to stop and is stoping. */
    STOPPING;
}
