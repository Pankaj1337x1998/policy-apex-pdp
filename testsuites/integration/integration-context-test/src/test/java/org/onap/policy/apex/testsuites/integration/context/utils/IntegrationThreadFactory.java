/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.testsuites.integration.context.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * A factory for creating IntegrationThread objects.
 */
public class IntegrationThreadFactory implements ThreadFactory {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(IntegrationThreadFactory.class);


    private final String threadFactoryName;

    private final AtomicInteger counter = new AtomicInteger();

    /**
     * Instantiates a new integration thread factory.
     *
     * @param threadFactoryName the thread factory name
     */
    public IntegrationThreadFactory(final String threadFactoryName) {
        this.threadFactoryName = threadFactoryName;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Thread newThread(final Runnable runnable) {
        final Thread thread = new Thread(runnable);
        thread.setName(threadFactoryName + "_" + counter.getAndIncrement());
        LOGGER.debug("started thread " + thread.getName());
        return thread;
    }
}
