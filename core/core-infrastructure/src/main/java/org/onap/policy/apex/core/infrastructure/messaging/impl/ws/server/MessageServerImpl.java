/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.core.infrastructure.messaging.impl.ws.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.java_websocket.WebSocket;
import org.onap.policy.apex.core.infrastructure.messaging.MessageHolder;
import org.onap.policy.apex.core.infrastructure.messaging.util.MessagingUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * A messaging server implementation using web socket.
 *
 * @author Sajeevan Achuthan (sajeevan.achuthan@ericsson.com)
 * @param <M> the generic type of message being passed
 */
public class MessageServerImpl<M> extends InternalMessageBusServer<M> {
    // The logger for this class
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(MessageServerImpl.class);

    // The Web Socket protocol for URIs and URLs
    private static final String PROTOCOL = "ws://";

    // URI of this server
    private final String connectionUri;

    // Indicates if the web socket server is started or not
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    /**
     * Instantiates a new web socket messaging server for Apex.
     *
     * @param address the address of the server machine on which to start the server
     */
    public MessageServerImpl(final InetSocketAddress address) {
        // Call the super class to create the web socket and set up received message forwarding
        super(address);
        LOGGER.entry(address);

        // Compose the Web Socket URI
        connectionUri = PROTOCOL + address.getHostString() + ":" + address.getPort();
        LOGGER.debug("Server connection URI: {}", connectionUri);

        LOGGER.exit();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void startConnection() {
        // Start reception of connections on the web socket
        start();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void stopConnection() {
        // Stop message listening using our super class
        stopListener();

        // Stop the web socket server
        try {
            // Close all connections on this web socket server
            for (final WebSocket connection : getConnections()) {
                connection.closeConnection(0, "");
            }
            stop();
        } catch (final IOException ioe) {
            LOGGER.catching(ioe);
        } catch (final InterruptedException e) {
            // restore the interrupt status
            Thread.currentThread().interrupt();
            // This can happen in normal operation so ignore
        }
        isStarted.set(false);
    }

    /**
     * Return the current connection URI.
     *
     * @return connection URI
     */
    public String getConnectionUrl() {
        if (connectionUri == null) {
            throw new IllegalStateException("URI not set - The server is not started");
        }
        return connectionUri;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void send(final MessageHolder<M> message) {
        // Send the incoming message to all clients connected to this web socket
        final Collection<WebSocket> connections = getConnections();
        for (final WebSocket webSocket : connections) {
            webSocket.send(MessagingUtils.serializeObject(message));
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void send(final String messageString) {
        final Collection<WebSocket> connections = getConnections();
        for (final WebSocket webSocket : connections) {
            webSocket.send(messageString);
        }
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    @Override
    public void onStart() {
        isStarted.set(true);
        LOGGER.debug("started deployment server on URI: {}", connectionUri);
    }
}
