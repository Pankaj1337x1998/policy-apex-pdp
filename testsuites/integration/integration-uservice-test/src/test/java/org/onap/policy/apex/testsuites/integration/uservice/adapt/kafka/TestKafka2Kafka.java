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

package org.onap.policy.apex.testsuites.integration.uservice.adapt.kafka;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.TestUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import kafka.zk.EmbeddedZookeeper;

import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.service.engine.main.ApexMain;


/**
 * The Class TestKafka2Kafka.
 */
public class TestKafka2Kafka {
    // The method of starting an embedded Kafka server used in this example is based on the method
    // on stack overflow at
    // https://github.com/asmaier/mini-kafka

    private static final long MAX_TEST_LENGTH = 20000;

    private static final int EVENT_COUNT = 10;
    private static final int EVENT_INTERVAL = 20;

    private static final String ZKHOST = "127.0.0.1";
    private static final String BROKERHOST = "127.0.0.1";
    private static final String BROKERPORT = "39902";

    private static EmbeddedZookeeper zkServer;
    private static ZkClient zkClient;
    private static KafkaServer kafkaServer;

    /**
     * Setup dummy kafka server.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @BeforeClass
    public static void setupDummyKafkaServer() throws IOException {
        // setup Zookeeper
        zkServer = new EmbeddedZookeeper();
        final String zkConnect = ZKHOST + ":" + zkServer.port();
        zkClient = new ZkClient(zkConnect, 30000, 30000, ZKStringSerializer$.MODULE$);
        final ZkUtils zkUtils = ZkUtils.apply(zkClient, false);

        // setup Broker
        final Properties brokerProps = new Properties();
        brokerProps.setProperty("zookeeper.connect", zkConnect);
        brokerProps.setProperty("broker.id", "0");
        brokerProps.setProperty("log.dirs", Files.createTempDirectory("kafka-").toAbsolutePath().toString());
        brokerProps.setProperty("listeners", "PLAINTEXT://" + BROKERHOST + ":" + BROKERPORT);
        brokerProps.setProperty("offsets.topic.replication.factor", "1");
        brokerProps.setProperty("transaction.state.log.replication.factor", "1");
        brokerProps.setProperty("transaction.state.log.min.isr", "1");
        final KafkaConfig config = new KafkaConfig(brokerProps);
        final Time mock = new MockTime();
        kafkaServer = TestUtils.createServer(config, mock);
        kafkaServer.startup();
        
        // create topics
        AdminUtils.createTopic(zkUtils, "apex-in-0", 1, 1, new Properties(), RackAwareMode.Disabled$.MODULE$);
        AdminUtils.createTopic(zkUtils, "apex-in-1", 1, 1, new Properties(), RackAwareMode.Disabled$.MODULE$);
        AdminUtils.createTopic(zkUtils, "apex-out", 1, 1, new Properties(), RackAwareMode.Disabled$.MODULE$);
        
    }

    /**
     * Shutdown dummy kafka server.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @AfterClass
    public static void shutdownDummyKafkaServer() throws IOException {
        if (kafkaServer != null) {
            kafkaServer.shutdown();
        }
        if (zkClient != null) {
            zkClient.close();
        }
        if (zkServer != null) {
            zkServer.shutdown();
        }
    }

    /**
     * Test json kafka events.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     */
    @Test
    public void testJsonKafkaEvents() throws MessagingException, ApexException {
        final String[] args = {"src/test/resources/prodcons/Kafka2KafkaJsonEvent.json"};
        testKafkaEvents(args, false, "json");
    }

    /**
     * Test XML kafka events.
     *
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     */
    @Test
    public void testXmlKafkaEvents() throws MessagingException, ApexException {
        final String[] args = {"src/test/resources/prodcons/Kafka2KafkaXmlEvent.json"};
        testKafkaEvents(args, true, "xml");
    }

    /**
     * Test kafka events.
     *
     * @param args the args
     * @param xmlEvents the xml events
     * @param topicSuffix the topic suffix
     * @throws MessagingException the messaging exception
     * @throws ApexException the apex exception
     */
    private void testKafkaEvents(final String[] args, final Boolean xmlEvents, final String topicSuffix)
            throws MessagingException, ApexException {
        final KafkaEventSubscriber subscriber =
                new KafkaEventSubscriber("apex-out-" + topicSuffix, "localhost:" + BROKERPORT);

        final ApexMain apexMain = new ApexMain(args);
        ThreadUtilities.sleep(3000);

        final KafkaEventProducer producer = new KafkaEventProducer("apex-in-" + topicSuffix, "localhost:" + BROKERPORT,
                        EVENT_COUNT, xmlEvents, EVENT_INTERVAL);

        producer.sendEvents();

        final long testStartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() < testStartTime + MAX_TEST_LENGTH
                && subscriber.getEventsReceivedCount() < EVENT_COUNT) {
            ThreadUtilities.sleep(EVENT_INTERVAL);
        }

        ThreadUtilities.sleep(1000);

        assertEquals(producer.getEventsSentCount(), subscriber.getEventsReceivedCount());

        apexMain.shutdown();
        subscriber.shutdown();
        producer.shutdown();
        ThreadUtilities.sleep(1000);
    }
}