/*
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

package org.onap.policy.apex.model.basicmodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationMessage;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult.ValidationResult;

public class ValidationTest {

    @Test
    public void test() {
        AxValidationResult result = new AxValidationResult();
        AxReferenceKey refKey = new AxReferenceKey("PK", "0.0.1", "PLN", "LN");
        result = refKey.validate(result);

        assertNotNull(result);
        assertTrue(result.isOk());
        assertTrue(result.isValid());
        assertEquals(AxValidationResult.ValidationResult.VALID, result.getValidationResult());
        assertNotNull(result.getMessageList());

        AxValidationMessage vmess0 = new AxValidationMessage(AxArtifactKey.getNullKey(), AxArtifactKey.class,
                        ValidationResult.VALID, "Some message");
        result.addValidationMessage(vmess0);

        assertTrue(result.isOk());
        assertTrue(result.isValid());
        assertEquals(AxValidationResult.ValidationResult.VALID, result.getValidationResult());
        assertNotNull(result.getMessageList());
        assertNotNull("hello", result.toString());

        AxValidationMessage vmess1 = new AxValidationMessage(AxArtifactKey.getNullKey(), AxArtifactKey.class,
                        ValidationResult.OBSERVATION, "Some message");
        result.addValidationMessage(vmess1);

        assertTrue(result.isOk());
        assertTrue(result.isValid());
        assertEquals(AxValidationResult.ValidationResult.OBSERVATION, result.getValidationResult());
        assertNotNull(result.getMessageList());
        assertNotNull("hello", result.toString());

        AxValidationMessage vmess2 = new AxValidationMessage(AxArtifactKey.getNullKey(), AxArtifactKey.class,
                        ValidationResult.WARNING, "Some message");
        result.addValidationMessage(vmess2);

        assertFalse(result.isOk());
        assertTrue(result.isValid());
        assertEquals(AxValidationResult.ValidationResult.WARNING, result.getValidationResult());
        assertNotNull(result.getMessageList());
        assertNotNull("hello", result.toString());

        AxValidationMessage vmess3 = new AxValidationMessage(AxArtifactKey.getNullKey(), AxArtifactKey.class,
                        ValidationResult.INVALID, "Some message");
        result.addValidationMessage(vmess3);

        assertFalse(result.isOk());
        assertFalse(result.isValid());
        assertEquals(AxValidationResult.ValidationResult.INVALID, result.getValidationResult());
        assertNotNull(result.getMessageList());
        assertNotNull("hello", result.toString());

        assertEquals(AxValidationResult.ValidationResult.INVALID, result.getMessageList().get(3).getValidationResult());
        assertEquals("Some message", result.getMessageList().get(3).getMessage());
        assertEquals(AxArtifactKey.class.getName(), result.getMessageList().get(3).getObservedClass());
        assertEquals(AxArtifactKey.getNullKey(), result.getMessageList().get(3).getObservedKey());
    }
}
