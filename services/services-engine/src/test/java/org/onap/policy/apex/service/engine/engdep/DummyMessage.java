/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.service.engine.engdep;

import org.onap.policy.apex.core.protocols.Action;
import org.onap.policy.apex.core.protocols.Message;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Bad protocol message.
 */
public class DummyMessage extends Message {
    private static final long serialVersionUID = 3827403727783909797L;

    /**
     * Constructor.
     * @param action the message action
     * @param targetKey the message target key
     */
    public DummyMessage(Action action, AxArtifactKey targetKey) {
        super(action, targetKey);
    }
}