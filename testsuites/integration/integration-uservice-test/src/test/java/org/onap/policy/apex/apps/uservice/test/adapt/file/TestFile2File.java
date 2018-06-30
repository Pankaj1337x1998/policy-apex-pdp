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

package org.onap.policy.apex.apps.uservice.test.adapt.file;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.onap.policy.apex.core.infrastructure.messaging.MessagingException;
import org.onap.policy.apex.core.infrastructure.threading.ThreadUtilities;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.utilities.TextFileUtils;
import org.onap.policy.apex.service.engine.main.ApexMain;

public class TestFile2File {

    @Test
    public void testJsonFileEvents() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/File2FileJsonEvent.json"};

        testFileEvents(args, "src/test/resources/events/EventsOut.json", 48956);
    }

    @Test
    public void testXMLFileEvents() throws MessagingException, ApexException, IOException {
        final String[] args = {"src/test/resources/prodcons/File2FileXmlEvent.json"};

        testFileEvents(args, "src/test/resources/events/EventsOut.xmlfile", 106739);
    }

    private void testFileEvents(final String[] args, final String outFilePath, final long expectedFileSize)
            throws MessagingException, ApexException, IOException {
        final ApexMain apexMain = new ApexMain(args);

        final File outFile = new File(outFilePath);

        while (!outFile.exists()) {
            ThreadUtilities.sleep(500);
        }

        // Wait for the file to be filled
        long outFileSize = 0;
        while (true) {
            final String fileString = TextFileUtils.getTextFileAsString(outFilePath).replaceAll("\\s+", "");
            outFileSize = fileString.length();
            if (outFileSize > 0 && outFileSize >= expectedFileSize) {
                break;
            }
            ThreadUtilities.sleep(500);
        }

        apexMain.shutdown();
        outFile.delete();
        assertEquals(outFileSize, expectedFileSize);
    }
}


