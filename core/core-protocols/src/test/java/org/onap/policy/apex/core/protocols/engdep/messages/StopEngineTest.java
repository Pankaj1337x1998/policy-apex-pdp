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

package org.onap.policy.apex.core.protocols.engdep.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;

/**
 * Test the start engine message.
 */
public class StopEngineTest {

    @Test
    public void test() {
        assertNotNull(new StopEngine(new AxArtifactKey()));
        assertNotNull(new StopEngine(new AxArtifactKey(), "Stop Engine Data"));
        assertEquals("StopEngine {Message [action=STOP_ENGINE, "
                        + "targetKey=AxArtifactKey:(name=NULL,version=0.0.0), data=null]}[]",
                        new StopEngine(new AxArtifactKey()).toString());
    }
}
