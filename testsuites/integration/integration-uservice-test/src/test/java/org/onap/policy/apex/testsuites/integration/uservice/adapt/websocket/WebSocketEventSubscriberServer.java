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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.websocket;

import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageListener;
import org.onap.policy.apex.core.infrastructure.messaging.stringmessaging.WsStringMessageServer;

/**
 * The Class WebSocketEventSubscriberServer.
 */
public class WebSocketEventSubscriberServer implements WsStringMessageListener {
    private final int port;
    private long eventsReceivedCount = 0;

    private final WsStringMessageServer server;

    /**
     * Instantiates a new web socket event subscriber server.
     *
     * @param port the port
     * @throws MessagingException the messaging exception
     */
    public WebSocketEventSubscriberServer(final int port) throws MessagingException {
        this.port = port;

        server = new WsStringMessageServer(port);
        server.start(this);

        System.out.println(
                WebSocketEventSubscriberServer.class.getCanonicalName() + ": port " + port + ", waiting for events");
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void receiveString(final String eventString) {
        System.out.println(WebSocketEventSubscriberServer.class.getCanonicalName() + ": port " + port
                + ", received event " + eventString);
        eventsReceivedCount++;
    }

    /**
     * Gets the events received count.
     *
     * @return the events received count
     */
    public long getEventsReceivedCount() {
        return eventsReceivedCount;
    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        server.stop();
        System.out.println(WebSocketEventSubscriberServer.class.getCanonicalName() + ": stopped");
    }

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws MessagingException the messaging exception
     */
    public static void main(final String[] args) throws MessagingException {
        if (args.length != 1) {
            System.err.println("usage WebSocketEventSubscriberClient port");
            return;
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[0]);
        } catch (final Exception e) {
            System.err.println("usage WebSocketEventSubscriberClient port");
            e.printStackTrace();
            return;
        }

        new WebSocketEventSubscriberServer(port);
    }
}
