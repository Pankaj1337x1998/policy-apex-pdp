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

package org.onap.policy.apex.service.engine.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.consumer.ApexFileEventConsumer;
import org.onap.policy.apex.service.engine.event.impl.filecarrierplugin.producer.ApexFileEventProducer;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperCarrierTechnologyParameters;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperEventProducer;
import org.onap.policy.apex.service.engine.parameters.dummyclasses.SuperDooperEventSubscriber;
import org.onap.policy.apex.service.parameters.ApexParameterException;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.apex.service.parameters.carriertechnology.CarrierTechnologyParameters;
import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolParameters;

/**
 * Test for an empty parameter file
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ParameterTests {
    @Test
    public void invalidParametersNoFileTest() throws ApexParameterException {
        final String[] args = {"-c", "src/test/resources/parameters/invalidNoFile.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertTrue(e.getMessage().startsWith("error reading parameters from \"src"));
            assertTrue(e.getMessage().contains("FileNotFoundException"));
        }
    }

    @Test
    public void invalidParametersEmptyTest() {
        final String[] args = {"-c", "src/test/resources/parameters/empty.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertTrue(e.getMessage()
                    .startsWith("validation error(s) on parameters from \"src/test/resources/parameters/empty.json\""));
        }
    }

    @Test
    public void invalidParametersNoParamsTest() {
        final String[] args = {"-c", "src/test/resources/parameters/noParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertEquals("validation error(s) on parameters from \"src/test/resources/parameters/noParams.json\"\n"
                    + "Apex parameters invalid\n" + " engine service parameters are not specified\n"
                    + " at least one event output and one event input must be specified", e.getMessage());
        }
    }

    @Test
    public void invalidParametersBlankParamsTest() {
        final String[] args = {"-c", "src/test/resources/parameters/blankParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {

            assertEquals("validation error(s) on parameters from \"src/test/resources/parameters/blankParams.json\"\n"
                    + "Apex parameters invalid\n" + " engine service parameters invalid\n"
                    + "  id not specified or specified value [-1] invalid, must be specified as id >= 0\n"
                    + " at least one event output and one event input must be specified", e.getMessage());
        }
    }

    @Test
    public void invalidParametersTest() {
        final String[] args = {"-c", "src/test/resources/parameters/badParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("This test should throw an exception");
        } catch (final ApexParameterException e) {
            assertEquals(
                    "validation error(s) on parameters from \"src/test/resources/parameters/badParams.json\"\n"
                            + "Apex parameters invalid\n" + " engine service parameters invalid\n"
                            + "  name [hello there] and/or version [PA1] invalid\n"
                            + "   parameter \"name\": value \"hello there\", does not match regular expression \"[A-Za-z0-9\\-_\\.]+\"\n"
                            + "  id not specified or specified value [-45] invalid, must be specified as id >= 0\n"
                            + "  instanceCount [-345] invalid, must be specified as instanceCount >= 1\n"
                            + "  deploymentPort [65536] invalid, must be specified as 1024 <= port <= 65535\n"
                            + "  policyModelFileName [/some/file/name.xml] not found or is not a plain file\n"
                            + " event input (TheFileConsumer1) parameters invalid\n"
                            + "  fileName not specified or is blank or null, it must be specified as a valid file location\n"
                            + " event output (FirstProducer) parameters invalid\n"
                            + "  fileName not specified or is blank or null, it must be specified as a valid file location",
                    e.getMessage());
        }
    }

    @Test
    public void goodParametersTest() {
        final String[] args = {"-c", "src/test/resources/parameters/goodParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

            assertEquals(2, parameters.getEventInputParameters().size());
            assertEquals(2, parameters.getEventOutputParameters().size());

            assertTrue(parameters.getEventOutputParameters().containsKey("FirstProducer"));
            assertTrue(parameters.getEventOutputParameters().containsKey("MyOtherProducer"));
            assertEquals("FILE", parameters.getEventOutputParameters().get("FirstProducer")
                    .getCarrierTechnologyParameters().getLabel());
            assertEquals("FILE", parameters.getEventOutputParameters().get("MyOtherProducer")
                    .getCarrierTechnologyParameters().getLabel());
            assertEquals(ApexFileEventProducer.class.getCanonicalName(), parameters.getEventOutputParameters()
                    .get("MyOtherProducer").getCarrierTechnologyParameters().getEventProducerPluginClass());
            assertEquals(ApexFileEventConsumer.class.getCanonicalName(), parameters.getEventOutputParameters()
                    .get("MyOtherProducer").getCarrierTechnologyParameters().getEventConsumerPluginClass());
            assertEquals("JSON",
                    parameters.getEventOutputParameters().get("FirstProducer").getEventProtocolParameters().getLabel());
            assertEquals("JSON", parameters.getEventOutputParameters().get("MyOtherProducer")
                    .getEventProtocolParameters().getLabel());

            assertTrue(parameters.getEventInputParameters().containsKey("TheFileConsumer1"));
            assertTrue(parameters.getEventInputParameters().containsKey("MySuperDooperConsumer1"));
            assertEquals("FILE", parameters.getEventInputParameters().get("TheFileConsumer1")
                    .getCarrierTechnologyParameters().getLabel());
            assertEquals("SUPER_DOOPER", parameters.getEventInputParameters().get("MySuperDooperConsumer1")
                    .getCarrierTechnologyParameters().getLabel());
            assertEquals("JSON", parameters.getEventInputParameters().get("TheFileConsumer1")
                    .getEventProtocolParameters().getLabel());
            assertEquals("SUPER_TOK_DEL", parameters.getEventInputParameters().get("MySuperDooperConsumer1")
                    .getEventProtocolParameters().getLabel());
            assertEquals(ApexFileEventProducer.class.getCanonicalName(), parameters.getEventInputParameters()
                    .get("TheFileConsumer1").getCarrierTechnologyParameters().getEventProducerPluginClass());
            assertEquals(ApexFileEventConsumer.class.getCanonicalName(), parameters.getEventInputParameters()
                    .get("TheFileConsumer1").getCarrierTechnologyParameters().getEventConsumerPluginClass());
            assertEquals(SuperDooperEventProducer.class.getCanonicalName(), parameters.getEventInputParameters()
                    .get("MySuperDooperConsumer1").getCarrierTechnologyParameters().getEventProducerPluginClass());
            assertEquals(SuperDooperEventSubscriber.class.getCanonicalName(), parameters.getEventInputParameters()
                    .get("MySuperDooperConsumer1").getCarrierTechnologyParameters().getEventConsumerPluginClass());
        } catch (final ApexParameterException e) {
            fail("This test should not throw an exception");
        }
    }

    @Test
    public void superDooperParametersTest() {
        final String[] args = {"-c", "src/test/resources/parameters/superDooperParams.json"};
        final ApexCommandLineArguments arguments = new ApexCommandLineArguments(args);

        try {
            final ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

            assertEquals("MyApexEngine", parameters.getEngineServiceParameters().getName());
            assertEquals("0.0.1", parameters.getEngineServiceParameters().getVersion());
            assertEquals(45, parameters.getEngineServiceParameters().getId());
            assertEquals(345, parameters.getEngineServiceParameters().getInstanceCount());
            assertEquals(65522, parameters.getEngineServiceParameters().getDeploymentPort());

            final CarrierTechnologyParameters prodCT =
                    parameters.getEventOutputParameters().get("FirstProducer").getCarrierTechnologyParameters();
            final EventProtocolParameters prodEP =
                    parameters.getEventOutputParameters().get("FirstProducer").getEventProtocolParameters();
            final CarrierTechnologyParameters consCT =
                    parameters.getEventInputParameters().get("MySuperDooperConsumer1").getCarrierTechnologyParameters();
            final EventProtocolParameters consEP =
                    parameters.getEventInputParameters().get("MySuperDooperConsumer1").getEventProtocolParameters();

            assertEquals("SUPER_DOOPER", prodCT.getLabel());
            assertEquals("SUPER_TOK_DEL", prodEP.getLabel());
            assertEquals("SUPER_DOOPER", consCT.getLabel());
            assertEquals("JSON", consEP.getLabel());

            assertTrue(prodCT instanceof SuperDooperCarrierTechnologyParameters);

            final SuperDooperCarrierTechnologyParameters superDooperParameters =
                    (SuperDooperCarrierTechnologyParameters) prodCT;
            assertEquals("somehost:12345", superDooperParameters.getBootstrapServers());
            assertEquals("0", superDooperParameters.getAcks());
            assertEquals(25, superDooperParameters.getRetries());
            assertEquals(98765, superDooperParameters.getBatchSize());
            assertEquals(21, superDooperParameters.getLingerTime());
            assertEquals(50505050, superDooperParameters.getBufferMemory());
            assertEquals("first-group-id", superDooperParameters.getGroupId());
            assertFalse(superDooperParameters.isEnableAutoCommit());
            assertEquals(441, superDooperParameters.getAutoCommitTime());
            assertEquals(987, superDooperParameters.getSessionTimeout());
            assertEquals("producer-out", superDooperParameters.getProducerTopic());
            assertEquals(101, superDooperParameters.getConsumerPollTime());
            assertEquals("some.key.serailizer", superDooperParameters.getKeySerializer());
            assertEquals("some.value.serailizer", superDooperParameters.getValueSerializer());
            assertEquals("some.key.deserailizer", superDooperParameters.getKeyDeserializer());
            assertEquals("some.value.deserailizer", superDooperParameters.getValueDeserializer());

            final String[] consumerTopics = {"consumer-out-0", "consumer-out-1", "consumer-out-2"};
            assertEquals(Arrays.asList(consumerTopics), superDooperParameters.getConsumerTopicList());
        } catch (final ApexParameterException e) {
            fail("This test should not throw an exception");
        }
    }
}