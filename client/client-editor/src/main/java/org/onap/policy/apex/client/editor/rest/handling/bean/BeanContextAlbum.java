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

import javax.xml.bind.annotation.XmlType;

/**
 * The ContextAlbum Bean.
 */
@XmlType
public class BeanContextAlbum extends BeanBase {
    private String name = null;
    private String version = null;
    private String scope = null;
    private String uuid = null;
    private String description = null;
    private BeanKeyRef itemSchema = null;
    private boolean writeable;

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
     * Gets the scope.
     *
     * @return the scope
     */
    public String getScope() {
        return scope;
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
     * Gets the item schema.
     *
     * @return the item schema
     */
    public BeanKeyRef getItemSchema() {
        return itemSchema;
    }

    /**
     * Gets the writeable.
     *
     * @return the writeable
     */
    public boolean getWriteable() {
        return writeable;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "ContextAlbum [name=" + name + ", version=" + version + ", scope=" + scope + ", uuid=" + uuid
                + ", description=" + description + ", itemSchema=" + itemSchema + ", writeable=" + writeable + "]";
    }
}
