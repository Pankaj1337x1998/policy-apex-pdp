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

package org.onap.policy.apex.service.engine.main;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexException;
import org.onap.policy.apex.model.basicmodel.concepts.ApexRuntimeException;
import org.onap.policy.common.utils.resources.ResourceUtils;
import org.onap.policy.common.utils.validation.ParameterValidationUtils;

/**
 * This class reads and handles command line parameters for the Apex main program.
 *
 * @author Liam Fallon (liam.fallon@ericsson.com)
 */
public class ApexCommandLineArguments {
    // A system property holding the root directory for relative paths in the configuration file
    public static final String RELATIVE_FILE_ROOT = "APEX_RELATIVE_FILE_ROOT";

    // Recurring string constants
    private static final String FILE_PREAMBLE = " file \"";
    private static final int HELP_LINE_LENGTH = 120;

    // Apache Commons CLI options
    private final Options options;

    // The command line options
    private String modelFilePath = null;
    private String configurationFilePath = null;
    private String relativeFileRoot = null;

    /**
     * Construct the options for the CLI editor.
     */
    public ApexCommandLineArguments() {
        //@formatter:off
        options = new Options();
        options.addOption(Option.builder("h")
                .longOpt("help")
                .desc("outputs the usage of this command")
                .required(false)
                .type(Boolean.class)
                .build());
        options.addOption(Option.builder("v")
                        .longOpt("version")
                        .desc("outputs the version of Apex")
                        .required(false)
                        .type(Boolean.class)
                        .build());
        options.addOption(Option.builder("c")
                        .longOpt("config-file")
                        .desc("the full path to the configuration file to use, "
                                        + "the configuration file must be a Json file "
                                        + "containing the Apex configuration parameters")
                        .hasArg()
                        .argName("CONFIG_FILE")
                        .required(false)
                        .type(String.class)
                        .build());
        options.addOption(Option.builder("rfr")
                        .longOpt("relative-file-root")
                        .desc("the root file path for relative file paths specified in the Apex configuration file, "
                                        + "defaults to the current directory from where Apex is executed")
                        .hasArg()
                        .argName(RELATIVE_FILE_ROOT)
                        .required(false)
                        .type(String.class)
                        .build());
        options.addOption(Option.builder("m").longOpt("model-file")
                .desc("the full path to the model file to use, if set it overrides the model file set in the "
                        + "configuration file").hasArg().argName("MODEL_FILE")
                .required(false)
                .type(String.class).build());
        //@formatter:on
    }

    /**
     * Construct the options for the CLI editor and parse in the given arguments.
     *
     * @param args The command line arguments
     */
    public ApexCommandLineArguments(final String[] args) {
        // Set up the options with the default constructor
        this();

        // Parse the arguments
        try {
            parse(args);
        } catch (final ApexException e) {
            throw new ApexRuntimeException("parse error on Apex parameters", e);
        }
    }

    /**
     * Parse the command line options.
     *
     * @param args The command line arguments
     * @return a string with a message for help and version, or null if there is no message
     * @throws ApexException on command argument errors
     */
    public String parse(final String[] args) throws ApexException {
        // Clear all our arguments
        setConfigurationFilePath(null);
        setModelFilePath(null);

        CommandLine commandLine = null;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (final ParseException e) {
            throw new ApexException("invalid command line arguments specified : " + e.getMessage());
        }

        // Arguments left over after Commons CLI does its stuff
        final String[] remainingArgs = commandLine.getArgs();

        if (remainingArgs.length > 0 && commandLine.hasOption('c') || remainingArgs.length > 1) {
            throw new ApexException("too many command line arguments specified : " + Arrays.toString(args));
        }

        if (remainingArgs.length == 1) {
            configurationFilePath = remainingArgs[0];
        }

        if (commandLine.hasOption('h')) {
            return help(ApexMain.class.getName());
        }

        if (commandLine.hasOption('v')) {
            return version();
        }

        if (commandLine.hasOption('c')) {
            setConfigurationFilePath(commandLine.getOptionValue('c'));
        }

        if (commandLine.hasOption("rfr")) {
            setRelativeFileRoot(commandLine.getOptionValue("rfr"));
        } else {
            setRelativeFileRoot(null);
        }

        if (commandLine.hasOption('m')) {
            setModelFilePath(commandLine.getOptionValue('m'));
        }

        return null;
    }

    /**
     * Validate the command line options.
     *
     * @throws ApexException on command argument validation errors
     */
    public void validate() throws ApexException {
        validateReadableFile("Apex configuration", configurationFilePath);

        if (checkSetModelFilePath()) {
            validateReadableFile("Apex model", modelFilePath);
        }

        validateRelativeFileRoot();
    }

    /**
     * Print version information for Apex.
     * 
     * @return the version string
     */
    public String version() {
        return ResourceUtils.getResourceAsString("version.txt");
    }

    /**
     * Print help information for Apex.
     *
     * @param mainClassName the main class name
     * @return the help string
     */
    public String help(final String mainClassName) {
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter stringPrintWriter = new PrintWriter(stringWriter);

        new HelpFormatter().printHelp(stringPrintWriter, HELP_LINE_LENGTH, mainClassName + " [options...]", "options",
                        options, 0, 0, "");

        return stringWriter.toString();
    }

    /**
     * Gets the model file path.
     *
     * @return the model file path
     */
    public String getModelFilePath() {
        return ResourceUtils.getFilePath4Resource(modelFilePath);
    }

    /**
     * Sets the model file path.
     *
     * @param modelFilePath the model file path
     */
    public void setModelFilePath(final String modelFilePath) {
        this.modelFilePath = modelFilePath;
    }

    /**
     * Check set model file path.
     *
     * @return true, if check set model file path
     */
    public boolean checkSetModelFilePath() {
        return modelFilePath != null && modelFilePath.length() > 0;
    }

    /**
     * Gets the configuration file path.
     *
     * @return the configuration file path
     */
    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    /**
     * Gets the root file path for relative file paths in the configuration file.
     *
     * @return the root file path
     */
    public String getRelativeFileRoot() {
        return relativeFileRoot;
    }

    /**
     * Gets the full expanded configuration file path.
     *
     * @return the configuration file path
     */
    public String getFullConfigurationFilePath() {
        return ResourceUtils.getFilePath4Resource(getConfigurationFilePath());
    }

    /**
     * Sets the configuration file path.
     *
     * @param configurationFilePath the configuration file path
     */
    public void setConfigurationFilePath(final String configurationFilePath) {
        this.configurationFilePath = configurationFilePath;

    }

    /**
     * Sets the root file path for relative file paths in the configuration file.
     *
     * @param relativeFileRoot the configuration file path
     */
    public void setRelativeFileRoot(final String relativeFileRoot) {
        String relativeFileRootValue = relativeFileRoot;

        if (!ParameterValidationUtils.validateStringParameter(relativeFileRoot)) {
            relativeFileRootValue = System.getProperty(RELATIVE_FILE_ROOT);
        }

        if (!ParameterValidationUtils.validateStringParameter(relativeFileRootValue)) {
            relativeFileRootValue = System.getProperty("user.dir");
        }
        else if (!(new File(relativeFileRootValue).isAbsolute())) {
            relativeFileRootValue = System.getProperty("user.dir") + File.separator + relativeFileRootValue;
        }

        this.relativeFileRoot = relativeFileRootValue;
        System.setProperty(RELATIVE_FILE_ROOT, relativeFileRootValue);
    }

    /**
     * Check set configuration file path.
     *
     * @return true, if check set configuration file path
     */
    public boolean checkSetConfigurationFilePath() {
        return configurationFilePath != null && configurationFilePath.length() > 0;
    }

    /**
     * Validate readable file.
     *
     * @param fileTag the file tag
     * @param fileName the file name
     * @throws ApexException the apex exception
     */
    private void validateReadableFile(final String fileTag, final String fileName) throws ApexException {
        if (fileName == null || fileName.length() == 0) {
            throw new ApexException(fileTag + " file was not specified as an argument");
        }

        // The file name can refer to a resource on the local file system or on the class path
        final URL fileUrl = ResourceUtils.getUrl4Resource(fileName);
        if (fileUrl == null) {
            throw new ApexException(fileTag + FILE_PREAMBLE + fileName + "\" does not exist");
        }

        final File theFile = new File(fileUrl.getPath());
        if (!theFile.exists()) {
            throw new ApexException(fileTag + FILE_PREAMBLE + fileName + "\" does not exist");
        }
        if (!theFile.isFile()) {
            throw new ApexException(fileTag + FILE_PREAMBLE + fileName + "\" is not a normal file");
        }
        if (!theFile.canRead()) {
            throw new ApexException(fileTag + FILE_PREAMBLE + fileName + "\" is ureadable");
        }
    }

    /**
     * Validate the relative file root.
     */
    private void validateRelativeFileRoot() throws ApexException {
        File relativeFileRootPath = new File(relativeFileRoot);
        if (!relativeFileRootPath.isDirectory()) {
            throw new ApexException(
                            "relative file root \"" + relativeFileRoot + "\" does not exist or is not a directory");
        }

        if (!relativeFileRootPath.canWrite()) {
            throw new ApexException(
                            "relative file root \"" + relativeFileRoot + "\" does not exist or is not a directory");
        }
    }

}
