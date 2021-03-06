/*
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

package org.onap.policy.apex.model.basicmodel.concepts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.Test;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxValidationResult;

public class AxReferenceKeyTest {

    @Test
    public void testAxReferenceKey() {
        assertNotNull(new AxReferenceKey());
        assertNotNull(new AxReferenceKey(new AxArtifactKey()));
        assertNotNull(new AxReferenceKey(new AxArtifactKey(), "LocalName"));
        assertNotNull(new AxReferenceKey(new AxReferenceKey()));
        assertNotNull(new AxReferenceKey(new AxReferenceKey(), "LocalName"));
        assertNotNull(new AxReferenceKey(new AxArtifactKey(), "ParentLocalName", "LocalName"));
        assertNotNull(new AxReferenceKey("ParentKeyName", "0.0.1", "LocalName"));
        assertNotNull(new AxReferenceKey("ParentKeyName", "0.0.1", "ParentLocalName", "LocalName"));
        assertNotNull(new AxReferenceKey("ParentKeyName:0.0.1:ParentLocalName:LocalName"));
        assertEquals(AxReferenceKey.getNullKey().getKey(), AxReferenceKey.getNullKey());
        assertEquals("NULL:0.0.0:NULL:NULL", AxReferenceKey.getNullKey().getId());

        AxReferenceKey testReferenceKey = new AxReferenceKey();
        testReferenceKey.setParentArtifactKey(new AxArtifactKey("PN", "0.0.1"));
        assertEquals("PN:0.0.1", testReferenceKey.getParentArtifactKey().getId());

        testReferenceKey.setParentReferenceKey(new AxReferenceKey("PN", "0.0.1", "LN"));
        assertEquals("PN:0.0.1:NULL:LN", testReferenceKey.getParentReferenceKey().getId());

        testReferenceKey.setParentKeyName("NPKN");
        assertEquals("NPKN", testReferenceKey.getParentKeyName());

        testReferenceKey.setParentKeyVersion("0.0.1");
        assertEquals("0.0.1", testReferenceKey.getParentKeyVersion());

        testReferenceKey.setParentLocalName("NPKLN");
        assertEquals("NPKLN", testReferenceKey.getParentLocalName());

        testReferenceKey.setLocalName("NLN");
        assertEquals("NLN", testReferenceKey.getLocalName());

        assertFalse(testReferenceKey.isCompatible(AxArtifactKey.getNullKey()));
        assertFalse(testReferenceKey.isCompatible(AxReferenceKey.getNullKey()));
        assertTrue(testReferenceKey.isCompatible(testReferenceKey));

        assertEquals(AxKey.Compatibility.DIFFERENT, testReferenceKey.getCompatibility(AxArtifactKey.getNullKey()));
        assertEquals(AxKey.Compatibility.DIFFERENT, testReferenceKey.getCompatibility(AxReferenceKey.getNullKey()));
        assertEquals(AxKey.Compatibility.IDENTICAL, testReferenceKey.getCompatibility(testReferenceKey));

        AxValidationResult result = new AxValidationResult();
        result = testReferenceKey.validate(result);
        assertEquals(AxValidationResult.ValidationResult.VALID, result.getValidationResult());

        testReferenceKey.clean();

        AxReferenceKey clonedReferenceKey = new AxReferenceKey(testReferenceKey);
        assertEquals("AxReferenceKey:(parentKeyName=NPKN,parentKeyVersion=0.0.1,parentLocalName=NPKLN,localName=NLN)",
            clonedReferenceKey.toString());

        assertFalse(testReferenceKey.hashCode() == 0);

        assertTrue(testReferenceKey.equals(testReferenceKey));
        assertTrue(testReferenceKey.equals(clonedReferenceKey));
        assertFalse(testReferenceKey.equals((Object)"Hello"));
        assertFalse(testReferenceKey.equals(new AxReferenceKey("PKN", "0.0.2", "PLN", "LN")));
        assertFalse(testReferenceKey.equals(new AxReferenceKey("NPKN", "0.0.2", "PLN", "LN")));
        assertFalse(testReferenceKey.equals(new AxReferenceKey("NPKN", "0.0.1", "PLN", "LN")));
        assertFalse(testReferenceKey.equals(new AxReferenceKey("NPKN", "0.0.1", "NPLN", "LN")));
        assertTrue(testReferenceKey.equals(new AxReferenceKey("NPKN", "0.0.1", "NPKLN", "NLN")));

        assertEquals(0, testReferenceKey.compareTo(testReferenceKey));
        assertEquals(0, testReferenceKey.compareTo(clonedReferenceKey));
        assertNotEquals(0, testReferenceKey.compareTo(new AxArtifactKey()));
        assertNotEquals(0, testReferenceKey.compareTo(new AxReferenceKey("PKN", "0.0.2", "PLN", "LN")));
        assertNotEquals(0, testReferenceKey.compareTo(new AxReferenceKey("NPKN", "0.0.2", "PLN", "LN")));
        assertNotEquals(0, testReferenceKey.compareTo(new AxReferenceKey("NPKN", "0.0.1", "PLN", "LN")));
        assertNotEquals(0, testReferenceKey.compareTo(new AxReferenceKey("NPKN", "0.0.1", "NPLN", "LN")));
        assertEquals(0, testReferenceKey.compareTo(new AxReferenceKey("NPKN", "0.0.1", "NPKLN", "NLN")));

        assertNotNull(testReferenceKey.getKeys());

        try {
            testReferenceKey.equals(null);
            fail("test should throw an exception here");
        } catch (Exception iae) {
            assertEquals("comparison object may not be null", iae.getMessage());
        }

        try {
            testReferenceKey.copyTo(null);
            fail("test should throw an exception here");
        } catch (Exception iae) {
            assertEquals("target may not be null", iae.getMessage());
        }

        try {
            testReferenceKey.copyTo(new AxArtifactKey("Key", "0.0.1"));
            fail("test should throw an exception here");
        } catch (Exception iae) {
            assertEquals("org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey is not an instance of "
                + "org.onap.policy.apex.model.basicmodel.concepts.AxReferenceKey", iae.getMessage());
        }

        AxReferenceKey targetRefKey = new AxReferenceKey();
        assertEquals(testReferenceKey, testReferenceKey.copyTo(targetRefKey));
    }

    @Test
    public void testValidation() {
        AxReferenceKey testReferenceKey = new AxReferenceKey();
        testReferenceKey.setParentArtifactKey(new AxArtifactKey("PN", "0.0.1"));
        assertEquals("PN:0.0.1", testReferenceKey.getParentArtifactKey().getId());

        try {
            Field parentNameField = testReferenceKey.getClass().getDeclaredField("parentKeyName");
            parentNameField.setAccessible(true);
            parentNameField.set(testReferenceKey, "Parent Name");
            AxValidationResult validationResult = new AxValidationResult();
            testReferenceKey.validate(validationResult);
            parentNameField.set(testReferenceKey, "ParentName");
            parentNameField.setAccessible(false);
            assertEquals(
                "parentKeyName invalid-parameter parentKeyName with value Parent Name "
                    + "does not match regular expression [A-Za-z0-9\\-_\\.]+",
                validationResult.getMessageList().get(0).getMessage());
        } catch (Exception validationException) {
            fail("test should not throw an exception");
        }

        try {
            Field parentVersionField = testReferenceKey.getClass().getDeclaredField("parentKeyVersion");
            parentVersionField.setAccessible(true);
            parentVersionField.set(testReferenceKey, "Parent Version");
            AxValidationResult validationResult = new AxValidationResult();
            testReferenceKey.validate(validationResult);
            parentVersionField.set(testReferenceKey, "0.0.1");
            parentVersionField.setAccessible(false);
            assertEquals(
                "parentKeyVersion invalid-parameter parentKeyVersion with value Parent Version "
                    + "does not match regular expression [A-Za-z0-9.]+",
                validationResult.getMessageList().get(0).getMessage());
        } catch (Exception validationException) {
            fail("test should not throw an exception");
        }

        try {
            Field parentLocalNameField = testReferenceKey.getClass().getDeclaredField("parentLocalName");
            parentLocalNameField.setAccessible(true);
            parentLocalNameField.set(testReferenceKey, "Parent Local Name");
            AxValidationResult validationResult = new AxValidationResult();
            testReferenceKey.validate(validationResult);
            parentLocalNameField.set(testReferenceKey, "ParentLocalName");
            parentLocalNameField.setAccessible(false);
            assertEquals(
                "parentLocalName invalid-parameter parentLocalName with value "
                    + "Parent Local Name does not match regular expression [A-Za-z0-9\\-_\\.]+|^$",
                validationResult.getMessageList().get(0).getMessage());
        } catch (Exception validationException) {
            fail("test should not throw an exception");
        }

        try {
            Field localNameField = testReferenceKey.getClass().getDeclaredField("localName");
            localNameField.setAccessible(true);
            localNameField.set(testReferenceKey, "Local Name");
            AxValidationResult validationResult = new AxValidationResult();
            testReferenceKey.validate(validationResult);
            localNameField.set(testReferenceKey, "LocalName");
            localNameField.setAccessible(false);
            assertEquals(
                "localName invalid-parameter localName with value Local Name "
                    + "does not match regular expression [A-Za-z0-9\\-_\\.]+|^$",
                validationResult.getMessageList().get(0).getMessage());
        } catch (Exception validationException) {
            fail("test should not throw an exception");
        }
    }
}
