/*-
 * ============LICENSE_START=======================================================
 *  Copyright (C) 2016-2018 Ericsson. All rights reserved.
 *  Modifications Copyright (C) 2019-2020 Nordix Foundation.
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

package org.onap.policy.apex.model.basicmodel.concepts;

import java.util.List;

import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;
import org.onap.policy.common.utils.validation.Assertions;

/**
 * This class records a usage of a key in the system. When the list of keys being used by a concept is built using the
 * getKeys() method of the {@link AxConcept} class, an instance of this class is created for every key occurrence. The
 * list of keys returned by the getKeys() method is a list of {@link AxKeyUse} objects.
 *
 * <p>Validation checks that each key is valid.
 */

public class AxKeyUse extends AxKey {
    private static final long serialVersionUID = 2007147220109881705L;

    private AxKey usedKey;

    /**
     * The Default Constructor creates this concept with a null key.
     */
    public AxKeyUse() {
        this(new AxArtifactKey());
    }

    /**
     * Copy constructor.
     *
     * @param copyConcept the concept to copy from
     */
    public AxKeyUse(final AxKeyUse copyConcept) {
        super(copyConcept);
    }

    /**
     * This constructor creates an instance of this class, and holds a reference to a used key.
     *
     * @param usedKey a used key
     */
    public AxKeyUse(final AxKey usedKey) {
        Assertions.argumentNotNull(usedKey, "usedKey may not be null");
        this.usedKey = usedKey;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxKey getKey() {
        return usedKey;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<AxKey> getKeys() {
        return usedKey.getKeys();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String getId() {
        return usedKey.getId();
    }

    /**
     * Sets the key.
     *
     * @param key the key
     */
    public void setKey(final AxKey key) {
        Assertions.argumentNotNull(key, "usedKey may not be null");
        this.usedKey = key;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxKey.Compatibility getCompatibility(final AxKey otherKey) {
        return usedKey.getCompatibility(otherKey);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean isCompatible(final AxKey otherKey) {
        return usedKey.isCompatible(otherKey);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxValidationResult validate(final AxValidationResult result) {
        if (usedKey.equals(AxArtifactKey.getNullKey())) {
            result.addValidationMessage(new AxValidationMessage(usedKey, this.getClass(), ValidationResult.INVALID,
                    "usedKey is a null key"));
        }
        return usedKey.validate(result);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void clean() {
        usedKey.clean();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(":(");
        builder.append("usedKey=");
        builder.append(usedKey);
        builder.append(")");
        return builder.toString();
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public AxConcept copyTo(final AxConcept target) {
        Assertions.argumentNotNull(target, "target may not be null");

        final Object copyObject = target;
        Assertions.instanceOf(copyObject, AxKeyUse.class);

        final AxKeyUse copy = ((AxKeyUse) copyObject);
        try {
            copy.usedKey = usedKey.getClass().getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            throw new ApexRuntimeException("error copying concept key: " + e.getMessage(), e);
        }
        usedKey.copyTo(copy.usedKey);

        return copy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + usedKey.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("comparison object may not be null");
        }

        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final AxKeyUse other = (AxKeyUse) obj;
        return usedKey.equals(other.usedKey);
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public int compareTo(final AxConcept otherObj) {
        Assertions.argumentNotNull(otherObj, "comparison object may not be null");

        if (this == otherObj) {
            return 0;
        }
        if (getClass() != otherObj.getClass()) {
            return this.hashCode() - otherObj.hashCode();
        }

        final AxKeyUse other = (AxKeyUse) otherObj;

        return usedKey.compareTo(other.usedKey);
    }
}
