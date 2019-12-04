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

package org.onap.policy.apex.context.test.concepts;

import java.io.Serializable;

/**
 * The Class TestContextItem004.
 */
public class TestContextFloatItem implements Serializable {
    private static final long serialVersionUID = -3359180576903272400L;

    private static final int HASH_PRIME_1 = 31;

    private float floatValue = 0;

    /**
     * The Constructor.
     */
    public TestContextFloatItem() {
        // Default constructor
    }

    /**
     * The Constructor.
     *
     * @param floatValue the float value
     */
    public TestContextFloatItem(final Float floatValue) {
        this.floatValue = floatValue;
    }

    /**
     * Gets the float value.
     *
     * @return the float value
     */
    public float getFloatValue() {
        return floatValue;
    }

    /**
     * Sets the float value.
     *
     * @param floatValue the float value
     */
    public void setFloatValue(final float floatValue) {
        this.floatValue = floatValue;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final int prime = HASH_PRIME_1;
        int result = 1;
        result = prime * result + Float.floatToIntBits(floatValue);
        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestContextFloatItem other = (TestContextFloatItem) obj;
        return Float.floatToIntBits(floatValue) == Float.floatToIntBits(other.floatValue);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        return "TestContextItem004 [floatValue=" + floatValue + "]";
    }
}
