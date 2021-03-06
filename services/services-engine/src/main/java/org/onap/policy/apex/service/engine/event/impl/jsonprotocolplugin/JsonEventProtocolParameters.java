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

package org.onap.policy.apex.service.engine.event.impl.jsonprotocolplugin;

import org.onap.policy.apex.service.parameters.eventprotocol.EventProtocolTextCharDelimitedParameters;

// @formatter:off
/**
 * Event protocol parameters for JSON as an event protocol.
 *
 * <p>The parameters for this plugin are:
 * <ol>
 * <li>nameAlias: The field in a JSON event to use as an alias for the event name. This parameter is
 * optional.
 * <li>versionAlias: The field in a JSON event to use as an alias for the event version. This
 * parameter is optional.
 * <li>nameSpaceAlias: The field in a JSON event to use as an alias for the event name space. This
 * parameter is optional.
 * <li>sourceAlias: The field in a JSON event to use as an alias for the event source. This
 * parameter is optional.
 * <li>targetAlias: The field in a JSON event to use as an alias for the event target. This
 * parameter is optional.
 * <li>pojoField: The event is received and sent as a single POJO using the event field
 * definition in this field name in the schema, there must be one and only one field in the
 * event definition, the event has a single parameter whose type is the Pojo. This parameter is optional.
 * </ol>
 * 
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
//@formatter:on
public class JsonEventProtocolParameters extends EventProtocolTextCharDelimitedParameters {
    /** The label of this event protocol. */
    public static final String JSON_EVENT_PROTOCOL_LABEL = "JSON";

    // Constants for text block delimiters
    private static final char JSON_TEXT_BLOCK_START_DELIMITER = '{';
    private static final char JSON_TEXT_BLOCK_END_DELIMITER = '}';

    // Aliases for Apex event header fields
    // @formatter:off
    private String nameAlias      = null;
    private String versionAlias   = null;
    private String nameSpaceAlias = null;
    private String sourceAlias    = null;
    private String targetAlias    = null;
    // @formatter:on

    // Flag indicating POJO decoding and encoding and parameter indicating the name of the Pojo field
    private String pojoField = null;

    /**
     * Constructor to create a JSON event protocol parameter instance and register the instance with the parameter
     * service.
     */
    public JsonEventProtocolParameters() {
        this(JSON_EVENT_PROTOCOL_LABEL);
    }

    /**
     * Constructor to create an event protocol parameters instance with the name of a sub class of this class.
     * @param eventProtocolLabel the name of the event protocol for this plugin
     */
    public JsonEventProtocolParameters(final String eventProtocolLabel) {
        super();

        // Set the event protocol properties for the JSON event protocol
        this.setLabel(eventProtocolLabel);

        // Set the starting and ending delimiters for text blocks of JSON events
        this.setStartChar(JSON_TEXT_BLOCK_START_DELIMITER);
        this.setEndChar(JSON_TEXT_BLOCK_END_DELIMITER);

        // Set the event protocol plugin class
        this.setEventProtocolPluginClass(Apex2JsonEventConverter.class.getName());
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getName() {
        return this.getLabel();
    }

    /**
     * Gets the name alias.
     *
     * @return the name alias
     */
    public String getNameAlias() {
        return nameAlias;
    }

    /**
     * Gets the version alias.
     *
     * @return the version alias
     */
    public String getVersionAlias() {
        return versionAlias;
    }

    /**
     * Gets the name space alias.
     *
     * @return the name space alias
     */
    public String getNameSpaceAlias() {
        return nameSpaceAlias;
    }

    /**
     * Gets the source alias.
     *
     * @return the source alias
     */
    public String getSourceAlias() {
        return sourceAlias;
    }

    /**
     * Gets the target alias.
     *
     * @return the target alias
     */
    public String getTargetAlias() {
        return targetAlias;
    }

    /**
     * Return the name of the POJO field to use for POJO decoding and encoding.
     * 
     * @return the name of the POJO field
     */
    public String getPojoField() {
        return pojoField;
    }

    /**
     * Sets the name alias.
     *
     * @param nameAlias the new name alias
     */
    public void setNameAlias(String nameAlias) {
        this.nameAlias = nameAlias;
    }

    /**
     * Sets the version alias.
     *
     * @param versionAlias the new version alias
     */
    public void setVersionAlias(String versionAlias) {
        this.versionAlias = versionAlias;
    }

    /**
     * Sets the name space alias.
     *
     * @param nameSpaceAlias the new name space alias
     */
    public void setNameSpaceAlias(String nameSpaceAlias) {
        this.nameSpaceAlias = nameSpaceAlias;
    }

    /**
     * Sets the source alias.
     *
     * @param sourceAlias the new source alias
     */
    public void setSourceAlias(String sourceAlias) {
        this.sourceAlias = sourceAlias;
    }

    /**
     * Sets the target alias.
     *
     * @param targetAlias the new target alias
     */
    public void setTargetAlias(String targetAlias) {
        this.targetAlias = targetAlias;
    }

    /**
     * Sets the POJO field that name for POJO decoding and encoding.
     * 
     * @param pojoField The name of the POJO field to use on the event
     */
    public void setPojoField(final String pojoField) {
        this.pojoField = pojoField;
    }
}
