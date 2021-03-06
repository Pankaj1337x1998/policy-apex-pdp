/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Samsung. All rights reserved.
 *  Modifications Copyright (C) 2019 Nordix Foundation.
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

package org.onap.policy.apex.plugins.event.carrier.websocket;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.onap.policy.common.parameters.GroupValidationResult;

public class WebSocketCarrierTechnologyParametersTest {

    WebSocketCarrierTechnologyParameters webSocketCarrierTechnologyParameters = null;
    GroupValidationResult result = null;

    /**
     * Set up testing.
     *
     * @throws Exception on test set up errors.
     */
    @Before
    public void setUp() throws Exception {
        webSocketCarrierTechnologyParameters = new WebSocketCarrierTechnologyParameters();
    }

    @Test
    public void testWebSocketCarrierTechnologyParameters() {
        assertNotNull(webSocketCarrierTechnologyParameters);
    }

    @Test
    public void testValidate() {
        result = webSocketCarrierTechnologyParameters.validate();
        assertNotNull(result);
        assertFalse(result.getStatus().isValid());
    }

}
