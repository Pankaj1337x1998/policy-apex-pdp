/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2019 Nordix Foundation.
 *  Modifications Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.apex.services.onappf.comm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.services.onappf.ApexStarterActivator;
import org.onap.policy.apex.services.onappf.ApexStarterCommandLineArguments;
import org.onap.policy.apex.services.onappf.ApexStarterConstants;
import org.onap.policy.apex.services.onappf.exception.ApexStarterException;
import org.onap.policy.apex.services.onappf.handler.ApexEngineHandler;
import org.onap.policy.apex.services.onappf.handler.PdpMessageHandler;
import org.onap.policy.apex.services.onappf.parameters.ApexStarterParameterGroup;
import org.onap.policy.apex.services.onappf.parameters.ApexStarterParameterHandler;
import org.onap.policy.common.endpoints.event.comm.Topic.CommInfrastructure;
import org.onap.policy.common.utils.coder.CoderException;
import org.onap.policy.common.utils.services.Registry;
import org.onap.policy.models.pdp.concepts.PdpStateChange;
import org.onap.policy.models.pdp.concepts.PdpStatus;
import org.onap.policy.models.pdp.concepts.PdpUpdate;
import org.onap.policy.models.pdp.enums.PdpState;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;

/**
 * Class to perform unit test of {@link PdpUpdateListener}.
 *
 * @author Ajith Sreekumar (ajith.sreekumar@est.tech)
 */
public class TestPdpUpdateListener {
    private PdpUpdateListener pdpUpdateMessageListener;
    private PdpStateChangeListener pdpStateChangeListener;
    private static final CommInfrastructure INFRA = CommInfrastructure.NOOP;
    private static final String TOPIC = "my-topic";
    private ApexStarterActivator activator;
    private ApexEngineHandler apexEngineHandler;
    private PrintStream stdout = System.out;

    /**
     * Method for setup before each test.
     *
     * @throws ApexStarterException if some error occurs while starting up the apex starter
     * @throws FileNotFoundException if the file is missing
     * @throws IOException if IO exception occurs
     */
    @Before
    public void setUp() throws ApexStarterException, FileNotFoundException, IOException {
        Registry.newRegistry();
        final String[] apexStarterConfigParameters = {"-c", "src/test/resources/ApexStarterConfigParametersNoop.json"};
        final ApexStarterCommandLineArguments arguments = new ApexStarterCommandLineArguments();
        ApexStarterParameterGroup parameterGroup;
        // The arguments return a string if there is a message to print and we should
        // exit
        final String argumentMessage = arguments.parse(apexStarterConfigParameters);
        if (argumentMessage != null) {
            return;
        }
        // Validate that the arguments are sane
        arguments.validate();

        // Read the parameters
        parameterGroup = new ApexStarterParameterHandler().getParameters(arguments);

        activator = new ApexStarterActivator(parameterGroup);
        Registry.register(ApexStarterConstants.REG_APEX_STARTER_ACTIVATOR, activator);
        activator.initialize();
        pdpUpdateMessageListener = new PdpUpdateListener();
        pdpStateChangeListener = new PdpStateChangeListener();
    }

    /**
     * Method for cleanup after each test.
     *
     * @throws Exception if an error occurs
     */
    @After
    public void teardown() throws Exception {
        System.setOut(stdout);
        apexEngineHandler =
            Registry.getOrDefault(ApexStarterConstants.REG_APEX_ENGINE_HANDLER, ApexEngineHandler.class, null);
        if (null != apexEngineHandler && apexEngineHandler.isApexEngineRunning()) {
            apexEngineHandler.shutdown();
        }
        // clear the apex starter activator
        if (activator != null && activator.isAlive()) {
            activator.terminate();
        }
    }

    @Test
    public void testPdpUpdateMssageListener() throws CoderException {
        final PdpStatus pdpStatus = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT);
        final ToscaPolicy toscaPolicy =
            TestListenerUtils.createToscaPolicy("apex policy name", "1.0", "src/test/resources/dummyProperties.json");
        final List<ToscaPolicy> toscaPolicies = new ArrayList<ToscaPolicy>();
        toscaPolicies.add(toscaPolicy);
        final PdpUpdate pdpUpdateMsg = TestListenerUtils.createPdpUpdateMsg(pdpStatus, toscaPolicies);
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null, pdpUpdateMsg);
        assertEquals(pdpStatus.getPdpGroup(), pdpUpdateMsg.getPdpGroup());
        assertEquals(pdpStatus.getPdpSubgroup(), pdpUpdateMsg.getPdpSubgroup());
        assertEquals(pdpStatus.getPolicies(),
                new PdpMessageHandler().getToscaPolicyIdentifiers(pdpUpdateMsg.getPolicies()));
    }

    @Test
    public void testPdpUpdateMssageListener_success() throws InterruptedException, CoderException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        final PdpStatus pdpStatus = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT);
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null,
            TestListenerUtils.createPdpUpdateMsg(pdpStatus, new ArrayList<ToscaPolicy>()));
        PdpStateChange pdpStateChangeMsg =
            TestListenerUtils.createPdpStateChangeMsg(PdpState.ACTIVE, "pdpGroup", "pdpSubgroup", pdpStatus.getName());
        pdpStateChangeListener.onTopicEvent(INFRA, TOPIC, null, pdpStateChangeMsg);
        final ToscaPolicy toscaPolicy =
            TestListenerUtils.createToscaPolicy("apex policy name", "1.0", "src/test/resources/dummyProperties.json");
        final List<ToscaPolicy> toscaPolicies = new ArrayList<ToscaPolicy>();
        toscaPolicies.add(toscaPolicy);
        final PdpUpdate pdpUpdateMsg = TestListenerUtils.createPdpUpdateMsg(pdpStatus, toscaPolicies);
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null, pdpUpdateMsg);
        final String outString = outContent.toString();
        assertEquals(pdpStatus.getPdpGroup(), pdpUpdateMsg.getPdpGroup());
        assertEquals(pdpStatus.getPdpSubgroup(), pdpUpdateMsg.getPdpSubgroup());
        assertEquals(pdpStatus.getPolicies(),
                new PdpMessageHandler().getToscaPolicyIdentifiers(pdpUpdateMsg.getPolicies()));
        assertTrue(outString.contains("Apex engine started and policies are running."));
    }

    @Test
    public void testPdpUpdateMssageListener_undeploy() throws InterruptedException, CoderException {
        final PdpStatus pdpStatus = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT);
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null,
            TestListenerUtils.createPdpUpdateMsg(pdpStatus, new ArrayList<ToscaPolicy>()));
        PdpStateChange pdpStateChangeMsg =
            TestListenerUtils.createPdpStateChangeMsg(PdpState.ACTIVE, "pdpGroup", "pdpSubgroup", pdpStatus.getName());
        pdpStateChangeListener.onTopicEvent(INFRA, TOPIC, null, pdpStateChangeMsg);
        final ToscaPolicy toscaPolicy =
            TestListenerUtils.createToscaPolicy("apex policy name", "1.0", "src/test/resources/dummyProperties.json");
        final List<ToscaPolicy> toscaPolicies = new ArrayList<ToscaPolicy>();
        toscaPolicies.add(toscaPolicy);
        final PdpUpdate pdpUpdateMsg = TestListenerUtils.createPdpUpdateMsg(pdpStatus, toscaPolicies);
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null, pdpUpdateMsg);
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null,
            TestListenerUtils.createPdpUpdateMsg(pdpStatus, new ArrayList<ToscaPolicy>()));
        final String outString = outContent.toString();
        assertTrue(outString.contains("Pdp update successful. No policies are running."));

    }

    @Test
    public void testPdpUpdateMssageListener_multi_policy_duplicate()
        throws InterruptedException, ApexStarterException, CoderException {
        OutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        final PdpStatus pdpStatus = Registry.get(ApexStarterConstants.REG_PDP_STATUS_OBJECT);
        final ToscaPolicy toscaPolicy =
            TestListenerUtils.createToscaPolicy("apex policy name", "1.0", "src/test/resources/dummyProperties.json");
        final ToscaPolicy toscaPolicy2 =
            TestListenerUtils.createToscaPolicy("apexpolicy2", "1.0", "src/test/resources/dummyProperties.json");
        final List<ToscaPolicy> toscaPolicies = new ArrayList<ToscaPolicy>();
        toscaPolicies.add(toscaPolicy);
        toscaPolicies.add(toscaPolicy2);
        final PdpUpdate pdpUpdateMsg = TestListenerUtils.createPdpUpdateMsg(pdpStatus, toscaPolicies);
        pdpUpdateMessageListener.onTopicEvent(INFRA, TOPIC, null, pdpUpdateMsg);
        PdpStateChange pdpStateChangeMsg =
            TestListenerUtils.createPdpStateChangeMsg(PdpState.ACTIVE, "pdpGroup", "pdpSubgroup", pdpStatus.getName());
        pdpStateChangeListener.onTopicEvent(INFRA, TOPIC, null, pdpStateChangeMsg);
        final String outString = outContent.toString();
        assertTrue(outString.contains(
            "Apex engine started. But, only the following polices are running - apex policy name:1.0  . "
            + "Other policies failed execution. Please see the logs for more details."));
    }
}
