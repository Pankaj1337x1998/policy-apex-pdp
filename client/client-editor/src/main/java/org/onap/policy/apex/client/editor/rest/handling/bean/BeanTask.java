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

import java.util.Arrays;
import java.util.Map;

import javax.xml.bind.annotation.XmlType;

/**
 * The Task Bean.
 */
@XmlType
public class BeanTask extends BeanBase {
    private String name = null;
    private String version = null;
    private String uuid = null;
    private String description = null;
    private BeanLogic taskLogic = null;
    private Map<String, BeanField> inputFields = null;
    private Map<String, BeanField> outputFields = null;
    private Map<String, BeanTaskParameter> parameters = null;
    private BeanKeyRef[] contexts = null;

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
     * Gets the task logic.
     *
     * @return the task logic
     */
    public BeanLogic getTaskLogic() {
        return taskLogic;
    }

    /**
     * Gets the input fields.
     *
     * @return the input fields
     */
    public Map<String, BeanField> getInputFields() {
        return inputFields;
    }

    /**
     * Gets the output fields.
     *
     * @return the output fields
     */
    public Map<String, BeanField> getOutputFields() {
        return outputFields;
    }

    /**
     * Gets the parameters.
     *
     * @return the parameters
     */
    public Map<String, BeanTaskParameter> getParameters() {
        return parameters;
    }

    /**
     * Gets the contexts.
     *
     * @return the contexts
     */
    public BeanKeyRef[] getContexts() {
        return contexts;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "BeanTask [name=" + name + ", version=" + version + ", uuid=" + uuid + ", description=" + description
                + ", taskLogic=" + taskLogic + ", inputFields=" + inputFields + ", outputFields=" + outputFields
                + ", parameters=" + parameters + ", contexts=" + Arrays.toString(contexts) + "]";
    }
}
