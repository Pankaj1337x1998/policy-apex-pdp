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

package org.onap.policy.apex.client.editor.rest;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jpa.jpql.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.apex.client.editor.rest.ApexEditorMain.EditorState;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelReader;
import org.onap.policy.apex.model.basicmodel.handling.ApexModelStringWriter;
import org.onap.policy.apex.model.modelapi.ApexApiResult;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicy;
import org.onap.policy.apex.model.policymodel.concepts.AxPolicyModel;
import org.onap.policy.common.utils.resources.ResourceUtils;

/**
 * The RestInterface Test.
 */
public class RestInterfaceTest {
    // CHECKSTYLE:OFF: MagicNumber

    private static final String TESTMODELFILE = "models/PolicyModel.json";
    private static final String TESTPORTNUM = "18989";
    private static final long MAX_WAIT = 15000; // 15 sec
    private static final InputStream SYSIN = System.in;
    private static final String[] EDITOR_MAIN_ARGS = new String[] { "-p", TESTPORTNUM };

    private static ApexEditorMain editorMain;
    private static WebTarget target;

    private static AxPolicyModel localmodel = null;
    private static String localmodelString = null;

    /**
     * Sets up the tests.
     *
     * @throws Exception if an error happens
     */
    @BeforeClass
    public static void setUp() throws Exception {
        // Start the editor
        editorMain = new ApexEditorMain(EDITOR_MAIN_ARGS, System.out);
        // prevent a stray stdin value from killing the editor
        final ByteArrayInputStream input = new ByteArrayInputStream("".getBytes());
        System.setIn(input);
        // Init the editor in a separate thread
        final Runnable testThread = new Runnable() {
            @Override
            public void run() {
                editorMain.init();
            }
        };
        new Thread(testThread).start();
        // wait until editorMain is in state RUNNING
        await().atMost(MAX_WAIT, TimeUnit.MILLISECONDS).until(() -> !(editorMain.getState().equals(EditorState.READY)
                || editorMain.getState().equals(EditorState.INITIALIZING)));

        if (editorMain.getState().equals(EditorState.STOPPED)) {
            Assert.fail("Rest endpoint (" + editorMain + ") shut down before it could be used");
        }

        // create the client
        final Client c = ClientBuilder.newClient();
        // Create the web target
        target = c.target(new ApexEditorParameters().getBaseUri());

        // load a test model locally
        localmodel = new ApexModelReader<>(AxPolicyModel.class, false)
                .read(ResourceUtils.getResourceAsStream(TESTMODELFILE));
        localmodelString =
                new ApexModelStringWriter<AxPolicyModel>(false).writeJsonString(localmodel, AxPolicyModel.class);

        // initialize a session ID
        createNewSession();
    }

    /**
     * Clean up streams.
     *
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws InterruptedException the interrupted exception
     */
    @AfterClass
    public static void cleanUpStreams() throws IOException, InterruptedException {
        editorMain.shutdown();
        // wait until editorMain is in state STOPPED
        await().atMost(MAX_WAIT, TimeUnit.MILLISECONDS).until(() -> editorMain.getState().equals(EditorState.STOPPED));
        System.setIn(SYSIN);
    }

    /**
     * Test to see that the message create Model with model id -1 .
     */
    @Test
    public void createSession() {
        createNewSession();
    }

    /**
     * Creates a new session.
     *
     * @return the session ID
     */
    private static int createNewSession() {
        final ApexApiResult responseMsg = target.path("editor/-1/Session/Create").request().get(ApexApiResult.class);
        assertEquals(ApexApiResult.Result.SUCCESS, responseMsg.getResult());
        assertTrue(responseMsg.getMessages().size() == 1);
        return Integer.parseInt(responseMsg.getMessages().get(0));
    }

    /**
     * Upload policy.
     *
     * @param sessionId the session ID
     * @param modelAsJsonString the model as json string
     */
    private void uploadPolicy(final int sessionId, final String modelAsJsonString) {
        final Builder requestbuilder = target.path("editor/" + sessionId + "/Model/Load").request();
        final ApexApiResult responseMsg = requestbuilder.put(Entity.json(modelAsJsonString), ApexApiResult.class);
        assertTrue(responseMsg.isOk());
    }

    /**
     * Create a new session, Upload a test policy model, then get a policy, parse it, and compare it to the same policy
     * in the original model.
     *
     * @throws ApexException if there is an Apex Error
     * @throws JAXBException if there is a JaxB Error
     **/
    @Test
    public void testUploadThenGet() throws ApexException, JAXBException {

        final int sessionId = createNewSession();

        uploadPolicy(sessionId, localmodelString);

        final ApexApiResult responseMsg = target.path("editor/" + sessionId + "/Policy/Get")
                .queryParam("name", "policy").queryParam("version", "0.0.1").request().get(ApexApiResult.class);
        assertTrue(responseMsg.isOk());

        // The string in responseMsg.Messages[0] is a JSON representation of a AxPolicy object. Lets parse it
        final String returnedPolicyAsString = responseMsg.getMessages().get(0);
        ApexModelReader<AxPolicy> apexPolicyReader = new ApexModelReader<>(AxPolicy.class, false);
        final AxPolicy returnedpolicy = apexPolicyReader.read(returnedPolicyAsString);
        // AxPolicy returnedpolicy = RestUtils.getConceptFromJSON(returnedPolicyAsString, AxPolicy.class);

        // Extract the local copy of that policy from the local Apex Policy Model
        final AxPolicy localpolicy = localmodel.getPolicies().get("policy", "0.0.1");

        // Write that local copy of the AxPolicy object to a Json String, ten parse it again
        final ApexModelStringWriter<AxPolicy> apexModelWriter = new ApexModelStringWriter<>(false);
        final String localPolicyString = apexModelWriter.writeJsonString(localpolicy, AxPolicy.class);
        apexPolicyReader = new ApexModelReader<>(AxPolicy.class, false);
        final AxPolicy localpolicyReparsed = apexPolicyReader.read(localPolicyString);
        // AxPolicy localpolicy_reparsed = RestUtils.getConceptFromJSON(returnedPolicyAsString, AxPolicy.class);

        assertNotNull(returnedpolicy);
        assertNotNull(localpolicy);
        assertNotNull(localpolicyReparsed);
        assertEquals(localpolicy, localpolicyReparsed);
        assertEquals(localpolicy, returnedpolicy);
    }

    // TODO Full unit testing of REST interface

}
