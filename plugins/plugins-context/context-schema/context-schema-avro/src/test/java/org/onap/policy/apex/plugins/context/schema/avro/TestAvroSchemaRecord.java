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

package org.onap.policy.apex.plugins.context.schema.avro;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.avro.generic.GenericRecord;
import org.junit.Before;
import org.junit.Test;
import org.onap.policy.apex.context.SchemaHelper;
import org.onap.policy.apex.context.impl.schema.SchemaHelperFactory;
import org.onap.policy.apex.context.parameters.SchemaParameters;
import org.onap.policy.apex.model.basicmodel.concepts.AxArtifactKey;
import org.onap.policy.apex.model.basicmodel.concepts.AxKey;
import org.onap.policy.apex.model.basicmodel.service.ModelService;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchema;
import org.onap.policy.apex.model.contextmodel.concepts.AxContextSchemas;
import org.onap.policy.apex.model.utilities.TextFileUtils;

/**
 * @author Liam Fallon (liam.fallon@ericsson.com)
 * @version
 */
public class TestAvroSchemaRecord {
    private final AxKey testKey = new AxArtifactKey("AvroTest", "0.0.1");
    private AxContextSchemas schemas;
    private String recordSchema;
    private String recordSchemaVPN;
    private String recordSchemaVPNReuse;
    private String recordSchemaInvalidFields;

    @Before
    public void initTest() throws IOException {
        schemas = new AxContextSchemas(new AxArtifactKey("AvroSchemas", "0.0.1"));
        ModelService.registerModel(AxContextSchemas.class, schemas);
        new SchemaParameters().getSchemaHelperParameterMap().put("Avro", new AvroSchemaHelperParameters());
        recordSchema = TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExample.avsc");
        recordSchemaVPN = TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExampleVPN.avsc");
        recordSchemaVPNReuse = TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExampleVPNReuse.avsc");
        recordSchemaInvalidFields =
                TextFileUtils.getTextFileAsString("src/test/resources/avsc/RecordExampleInvalidFields.avsc");
    }

    @Test
    public void testRecordInit() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "Avro", recordSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        final GenericRecord newRecordEmpty = (GenericRecord) schemaHelper.createNewInstance();
        assertEquals(null, newRecordEmpty.get("passwordHash"));

        final String inString = TextFileUtils.getTextFileAsString("src/test/resources/data/RecordExampleFull.json");
        final GenericRecord newRecordFull = (GenericRecord) schemaHelper.createNewInstance(inString);
        assertEquals("gobbledygook", newRecordFull.get("passwordHash").toString());
    }

    @Test
    public void testRecordUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "Avro", recordSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleNull.json");
        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleFull.json");
    }

    @Test
    public void testRecordUnmarshalMarshalInvalid() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "Avro", recordSchemaInvalidFields);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleInvalidFields.json");
    }

    @Test
    public void testVPNRecordUnmarshalMarshal() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "Avro", recordSchemaVPN);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        final SchemaHelper schemaHelper = new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());

        testUnmarshalMarshal(schemaHelper, "src/test/resources/data/RecordExampleVPNFull.json");
    }

    @Test
    public void testVPNRecordReuse() throws IOException {
        final AxContextSchema avroSchema =
                new AxContextSchema(new AxArtifactKey("AvroRecord", "0.0.1"), "Avro", recordSchemaVPNReuse);
        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);

        schemas.getSchemasMap().put(avroSchema.getKey(), avroSchema);
        new SchemaHelperFactory().createSchemaHelper(testKey, avroSchema.getKey());
    }

    private void testUnmarshalMarshal(final SchemaHelper schemaHelper, final String fileName) throws IOException {
        final String inString = TextFileUtils.getTextFileAsString(fileName);
        final GenericRecord decodedObject = (GenericRecord) schemaHelper.unmarshal(inString);
        final String outString = schemaHelper.marshal2String(decodedObject);
        assertEquals(inString.replaceAll("\\s+", ""), outString.replaceAll("\\s+", ""));
    }
}