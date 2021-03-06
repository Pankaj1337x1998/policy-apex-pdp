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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.onap.policy.apex.service.engine.event.ApexEventException;
import org.onap.policy.apex.service.engine.event.ApexEventProducer;
import org.onap.policy.apex.service.engine.event.ApexEventReceiver;
import org.onap.policy.apex.service.engine.event.PeeredReference;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerParameters;
import org.onap.policy.apex.service.parameters.eventhandler.EventHandlerPeeredMode;

public class ApexWebSocketConsumerTest {

    ApexWebSocketConsumer apexWebSocketConsumer = null;
    EventHandlerParameters consumerParameters = null;
    ApexEventReceiver incomingEventReceiver = null;
    ApexEventProducer apexWebSocketProducer = null;
    WebSocketCarrierTechnologyParameters webSocketCarrierTechnologyParameters = null;

    /**
     * Set up testing.
     *
     * @throws Exception on test set up errors.
     */
    @Before
    public void setUp() throws Exception {
        apexWebSocketConsumer = new ApexWebSocketConsumer();
        consumerParameters = new EventHandlerParameters();
        apexWebSocketProducer = new ApexWebSocketProducer();
        apexWebSocketConsumer.start();
    }

    @After
    public void tearDown() {
        apexWebSocketConsumer.stop();
    }

    @Test(expected = ApexEventException.class)
    public void testInitWithNonWebSocketCarrierTechnologyParameters() throws ApexEventException {
        consumerParameters.setCarrierTechnologyParameters(new CarrierTechnologyParameters() {});
        apexWebSocketConsumer.init("TestApexWebSocketConsumer", consumerParameters,
                incomingEventReceiver);
    }

    @Test
    public void testInitWithWebSocketCarrierTechnologyParameters() throws ApexEventException {
        webSocketCarrierTechnologyParameters = new WebSocketCarrierTechnologyParameters();
        consumerParameters.setCarrierTechnologyParameters(webSocketCarrierTechnologyParameters);
        apexWebSocketConsumer.init("TestApexWebSocketConsumer", consumerParameters,
                incomingEventReceiver);
        assertEquals("TestApexWebSocketConsumer", apexWebSocketConsumer.getName());
    }

    @Test
    public void testGetName() {
        assertNull(apexWebSocketConsumer.getName());
    }

    @Test
    public void testGetPeeredReference() {
        assertNull(apexWebSocketConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testSetPeeredReference() {
        PeeredReference peeredReference = new PeeredReference(EventHandlerPeeredMode.REQUESTOR,
                apexWebSocketConsumer, apexWebSocketProducer);
        apexWebSocketConsumer.setPeeredReference(EventHandlerPeeredMode.REQUESTOR, peeredReference);
        assertNotNull(apexWebSocketConsumer.getPeeredReference(EventHandlerPeeredMode.REQUESTOR));
    }

    @Test
    public void testReceiveString() {
        MockitoAnnotations.initMocks(this);
        ApexWebSocketConsumer apexWebSocketConsumerMock = Mockito.mock(ApexWebSocketConsumer.class);

        Mockito.doAnswer((Answer<?>) invocation -> {
            Object[] args = invocation.getArguments();
            assertEquals("TestRecieveString", args[0]);
            return null;
        }).when(apexWebSocketConsumerMock).receiveString("TestRecieveString");

        apexWebSocketConsumerMock.receiveString("TestRecieveString");

    }
}
