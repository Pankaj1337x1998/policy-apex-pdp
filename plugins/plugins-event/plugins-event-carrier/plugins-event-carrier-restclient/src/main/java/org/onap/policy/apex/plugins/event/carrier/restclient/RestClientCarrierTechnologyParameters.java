/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
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

package org.onap.policy.apex.plugins.event.carrier.restclient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.onap.policy.apex.service.parameters.carriertechnology.RestPluginCarrierTechnologyParameters;
import org.onap.policy.common.parameters.GroupValidationResult;
import org.onap.policy.common.parameters.ValidationStatus;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @formatter:off
/**
 * Apex parameters for REST as an event carrier technology with Apex as a REST client.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>url: The URL that the Apex Rest client will connect to over REST for event reception or event sending. This
 * parameter is mandatory.
 * <li>httpMethod: The HTTP method to use when sending events over REST, legal values are POST (default) and PUT. When
 * receiving events, the REST client plugin always uses the HTTP GET method.
 * <li>httpHeaders, the HTTP headers to send on REST requests, optional parameter, defaults to none.
 * <li>httpCodeFilter: a regular expression filter for returned HTTP codes, if the returned HTTP code passes this
 * filter, then the request is assumed to have succeeded by the plugin, optional, defaults to allowing 2xx codes
 * through, that is a regular expression of "[2][0-9][0-9]"
 * </ol>
 *
 * @author Joss Armstrong (joss.armstrong@ericsson.com)
 */
//@formatter:on
@Setter
@Getter
public class RestClientCarrierTechnologyParameters extends RestPluginCarrierTechnologyParameters {
    // Get a reference to the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientCarrierTechnologyParameters.class);
    /**
     * Constructor to create a REST carrier technology parameters instance and register the instance with the parameter
     * service.
     */
    public RestClientCarrierTechnologyParameters() {
        super();
        CARRIER_TECHNOLOGY_LABEL = "RESTCLIENT";
        EVENT_PRODUCER_PLUGIN_CLASS = ApexRestClientProducer.class.getName();
        EVENT_CONSUMER_PLUGIN_CLASS = ApexRestClientConsumer.class.getName();
        // Set the carrier technology properties for the web socket carrier technology
        this.setLabel(CARRIER_TECHNOLOGY_LABEL);
        this.setEventProducerPluginClass(EVENT_PRODUCER_PLUGIN_CLASS);
        this.setEventConsumerPluginClass(EVENT_CONSUMER_PLUGIN_CLASS);
    }


}
