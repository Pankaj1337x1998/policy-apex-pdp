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

package org.onap.policy.apex.client.editor.rest.handling.bean;

import java.util.Map;

import javax.xml.bind.annotation.XmlType;

/**
 * The Policy Bean.
 */
@XmlType
public class BeanPolicy extends BeanBase {
    private String name = null;
    private String version = null;
    private String uuid = null;
    private String description = null;
    private String firstState = null;
    private String template = null;
    private Map<String, BeanState> states = null;

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the uuid.
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the first state.
     *
     * @return the first state
     */
    public String getFirstState() {
        return firstState;
    }

    /**
     * Gets the template.
     *
     * @return the template
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Gets the states.
     *
     * @return the states
     */
    public Map<String, BeanState> getStates() {
        return states;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "Policy [name=" + name + ", version=" + version + ", uuid=" + uuid + ", description=" + description
                + ", firstState=" + firstState + ", template=" + template + ", states=" + states + "]";
    }

}
