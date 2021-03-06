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
 * The StateOutput Bean.
 */
@XmlType
public class BeanStateOutput extends BeanBase {

    private BeanKeyRef event = null;
    private String nextState = null;

    /**
     * Gets the event.
     *
     * @return the event
     */
    public BeanKeyRef getEvent() {
        return event;
    }

    /**
     * Gets the next state.
     *
     * @return the next state
     */
    public String getNextState() {
        return nextState;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "StateOutput [event=" + event + ", nextState=" + nextState + "]";
    }

}
