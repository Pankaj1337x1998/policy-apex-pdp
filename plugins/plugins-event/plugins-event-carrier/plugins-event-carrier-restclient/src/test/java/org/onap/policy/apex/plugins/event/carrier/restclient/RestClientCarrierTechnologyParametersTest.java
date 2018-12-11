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

package org.onap.policy.apex.plugins.event.carrier.restclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.onap.policy.apex.service.engine.main.ApexCommandLineArguments;
import org.onap.policy.apex.service.parameters.ApexParameterHandler;
import org.onap.policy.apex.service.parameters.ApexParameters;
import org.onap.policy.common.parameters.ParameterException;

/**
 * Test REST Requestor carrier technology parameters.
 */
public class RestClientCarrierTechnologyParametersTest {

    @Test
    public void testRestClientCarrierTechnologyParametersBadList() {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setConfigurationFilePath("src/test/resources/prodcons/RESTClientWithHTTPHeaderBadList.json");
        arguments.setRelativeFileRoot(".");

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (ParameterException pe) {
            assertTrue(pe.getMessage().contains("HTTP header array entry is null\n    parameter"));
            assertTrue(pe.getMessage().trim().endsWith("HTTP header array entry is null"));
        }
    }

    @Test
    public void testRestClientCarrierTechnologyParametersNotKvPairs() {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setConfigurationFilePath("src/test/resources/prodcons/RESTClientWithHTTPHeaderNotKvPairs.json");
        arguments.setRelativeFileRoot(".");

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (ParameterException pe) {
            assertTrue(pe.getMessage()
                            .contains("HTTP header array entries must have one key and one value: [aaa, bbb, ccc]"));
            assertTrue(pe.getMessage().trim()
                            .endsWith("HTTP header array entries must have one key and one value: [aaa]"));
        }
    }

    @Test
    public void testRestClientCarrierTechnologyParametersNulls() {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setConfigurationFilePath("src/test/resources/prodcons/RESTClientWithHTTPHeaderNulls.json");
        arguments.setRelativeFileRoot(".");

        try {
            new ApexParameterHandler().getParameters(arguments);
            fail("test should throw an exception here");
        } catch (ParameterException pe) {
            assertTrue(pe.getMessage().contains("HTTP header key is null or blank: [null, bbb]"));
            assertTrue(pe.getMessage().trim().endsWith("HTTP header value is null or blank: [ccc, null]"));
        }
    }

    @Test
    public void testRestClientCarrierTechnologyParametersOk() {
        ApexCommandLineArguments arguments = new ApexCommandLineArguments();
        arguments.setConfigurationFilePath("src/test/resources/prodcons/RESTClientWithHTTPHeaderOK.json");
        arguments.setRelativeFileRoot(".");

        try {
            ApexParameters parameters = new ApexParameterHandler().getParameters(arguments);

            RestClientCarrierTechnologyParameters rrctp0 = (RestClientCarrierTechnologyParameters) parameters
                            .getEventInputParameters().get("RestClientConsumer0").getCarrierTechnologyParameters();
            assertEquals(0, rrctp0.getHttpHeaders().length);

            RestClientCarrierTechnologyParameters rrctp1 = (RestClientCarrierTechnologyParameters) parameters
                            .getEventInputParameters().get("RestClientConsumer1").getCarrierTechnologyParameters();
            assertEquals(3, rrctp1.getHttpHeaders().length);
            assertEquals("bbb", rrctp1.getHttpHeadersAsMultivaluedMap().get("aaa").get(0));
            assertEquals("ddd", rrctp1.getHttpHeadersAsMultivaluedMap().get("ccc").get(0));
            assertEquals("fff", rrctp1.getHttpHeadersAsMultivaluedMap().get("eee").get(0));
            
            rrctp1.setHttpHeaders(null);
            assertEquals(null, rrctp1.getHttpHeadersAsMultivaluedMap());
        } catch (ParameterException pe) {
            fail("test should not throw an exception");
        }
    }

    @Test
    public void testGettersAndSetters() {
        RestClientCarrierTechnologyParameters rrctp = new RestClientCarrierTechnologyParameters();

        rrctp.setUrl("http://some.where");
        assertEquals("http://some.where", rrctp.getUrl());

        String[][] httpHeaders = new String[2][2];
        httpHeaders[0][0] = "aaa";
        httpHeaders[0][1] = "bbb";
        httpHeaders[1][0] = "ccc";
        httpHeaders[1][1] = "ddd";

        rrctp.setHttpHeaders(httpHeaders);
        assertEquals("aaa", rrctp.getHttpHeaders()[0][0]);
        assertEquals("bbb", rrctp.getHttpHeaders()[0][1]);
        assertEquals("ccc", rrctp.getHttpHeaders()[1][0]);
        assertEquals("ddd", rrctp.getHttpHeaders()[1][1]);

        rrctp.setHttpHeaders(null);
        assertFalse(rrctp.checkHttpHeadersSet());

        String[][] httpHeadersZeroLength = new String[0][0];
        rrctp.setHttpHeaders(httpHeadersZeroLength);
        assertFalse(rrctp.checkHttpHeadersSet());

        rrctp.setHttpHeaders(httpHeaders);
        assertTrue(rrctp.checkHttpHeadersSet());

        rrctp.setHttpMethod(RestClientCarrierTechnologyParameters.HttpMethod.DELETE);
        assertEquals(RestClientCarrierTechnologyParameters.HttpMethod.DELETE, rrctp.getHttpMethod());

        assertEquals("RestClientCarrierTechnologyParameters "
                        + "[url=http://some.where, httpMethod=DELETE, httpHeaders=[[aaa, bbb], [ccc, ddd]]]",
                        rrctp.toString());
    }
}
