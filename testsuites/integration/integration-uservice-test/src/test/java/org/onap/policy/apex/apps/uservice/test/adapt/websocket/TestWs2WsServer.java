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

package org.onap.policy.apex.apps.uservice.test.adapt.websocket;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;

public class TestWs2WsServer {
    private static final long MAX_TEST_LENGTH = 10000;

    private static final int EVENT_COUNT = 100;
    private static final int EVENT_INTERVAL = 20;

    @Test
    public void testJsonWSEvents() throws MessagingException, ApexException {
        final String[] args = {"src/test/resources/prodcons/Ws2WsServerJsonEvent.json"};
        testWSEvents(args, false);
    }

    @Test
    public void testXMLWSEvents() throws MessagingException, ApexException {
        final String[] args = {"src/test/resources/prodcons/Ws2WsServerXMLEvent.json"};
        testWSEvents(args, true);
    }

    public void testWSEvents(final String[] args, final boolean xmlEvents) throws MessagingException, ApexException {
        final ApexMain apexMain = new ApexMain(args);

        final WebSocketEventSubscriberClient subClient = new WebSocketEventSubscriberClient("localhost", 42452);
        final WebSocketEventProducerClient prodClient =
                new WebSocketEventProducerClient("localhost", 42450, EVENT_COUNT, xmlEvents, EVENT_INTERVAL);

        prodClient.sendEvents();

        final long testStartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() < testStartTime + MAX_TEST_LENGTH
                && subClient.getEventsReceivedCount() < EVENT_COUNT) {
            ThreadUtilities.sleep(EVENT_INTERVAL);
        }

        assertEquals(subClient.getEventsReceivedCount(), prodClient.getEventsSentCount());

        prodClient.shutdown();
        subClient.shutdown();
        apexMain.shutdown();
    }
}
