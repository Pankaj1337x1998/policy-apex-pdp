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

package org.onap.policy.apex.core.protocols.engdep.messages;

import org.onap.policy.apex.core.protocols.Message;
import org.onap.policy.apex.core.protocols.engdep.EngDepAction;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * The Class GetEngineInfo is a message that requests information on Apex engines and the policies they are running.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class GetEngineStatus extends Message {
    private static final long serialVersionUID = 5885214410842753037L;

    /**
     * Instantiates a new GetEngineStatus message.
     *
     * @param engineKey the key of the engine for which the status information is requested
     */
    public GetEngineStatus(final AxArtifactKey engineKey) {
        this(engineKey, null);
    }

    /**
     * Instantiates a new GetEngineStatus message.
     *
     * @param engineKey the key of the engine for which the status information is requested
     * @param messageData the message data that may give specifics on what information to return
     */
    public GetEngineStatus(final AxArtifactKey engineKey, final String messageData) {
        super(EngDepAction.GET_ENGINE_STATUS, engineKey, messageData);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "GetEngineStatus {" + super.toString() + "}[]";
    }
}
