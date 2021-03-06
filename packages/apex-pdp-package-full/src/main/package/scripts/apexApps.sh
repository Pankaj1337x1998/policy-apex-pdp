#!/usr/bin/env bash

#-------------------------------------------------------------------------------
# ============LICENSE_START=======================================================
#  Copyright (C) 2016-2018 Ericsson. All rights reserved.
#  Modifications Copyright (C) 2019-2020 Nordix Foundation.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0
# ============LICENSE_END=========================================================
#-------------------------------------------------------------------------------
##
## Script to run APEX Applications, call with '-h' for help
## - requires BASH with associative arrays, bash of at least version 4
## - for BASH examples with arrays see for instance: http://www.artificialworlds.net/blog/2012/10/17/bash-associative-array-examples/
## - adding a new app means to add a command to APEX_APP_MAP and a description to APEX_APP_DESCR_MAP using same/unique key
##
## @package    org.onap.policy.apex
## @author     Sven van der Meer <sven.van.der.meer@ericsson.com>
## @version    v2.0.0

##
## DO NOT CHANGE CODE BELOW, unless you know what you are doing
##

if [ -z $APEX_HOME ]
then
    APEX_HOME="/opt/app/policy/apex-pdp"
fi

if [ ! -d $APEX_HOME ]
then
    echo
    echo 'Apex directory "'$APEX_HOME'" not set or not a directory'
    echo "Please set environment for 'APEX_HOME'"
    exit
fi

## Environment variables for HTTPS
KEYSTORE="${APEX_HOME}/etc/ssl/policy-keystore"
KEYSTORE_PASSWD="Pol1cy_0nap"
TRUSTSTORE="${APEX_HOME}/etc/ssl/policy-truststore"
TRUSTSTORE_PASSWD="Pol1cy_0nap"

## HTTPS parameters
HTTPS_PARAMETERS="-Djavax.net.ssl.keyStore=${KEYSTORE} -Djavax.net.ssl.keyStorePassword=${KEYSTORE_PASSWD} -Djavax.net.ssl.trustStore=${TRUSTSTORE} -Djavax.net.ssl.trustStorePassword=${TRUSTSTORE_PASSWD}"

## script name for output
MOD_SCRIPT_NAME=`basename $0`

## check BASH version, we need >=4 for associative arrays
if [ "${BASH_VERSION:0:1}" -lt 4 ] ; then
    echo
    echo "$MOD_SCRIPT_NAME: requires bash 4 or higher for associative arrays"
    echo
    exit
fi

## config for CP apps
_config="${HTTPS_PARAMETERS} -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -Dhazelcast.config=$APEX_HOME/etc/hazelcast.xml -Dhazelcast.mancenter.enabled=false"

## jmx test config
_jmxconfig="-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9911 -Dcom.sun.management.jmxremote.local.only=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"

## Maven/APEX version
_version=`cat $APEX_HOME/etc/app-version.txt`

## system to get CygWin paths
system=`uname -s | cut -c1-6`
cpsep=":"
if [ "$system" == "CYGWIN" ] ; then
    APEX_HOME=`cygpath -m ${APEX_HOME}`
    cpsep=";"
fi

## CP for CP apps
CLASSPATH="$APEX_HOME/etc${cpsep}$APEX_HOME/etc/hazelcast${cpsep}$APEX_HOME/etc/infinispan${cpsep}$APEX_HOME/lib/*"

## array of applications with name=command
declare -A APEX_APP_MAP
APEX_APP_MAP["ws-console"]="java -jar $APEX_HOME/lib/applications/simple-wsclient-$_version-jar-with-dependencies.jar -c"
APEX_APP_MAP["ws-echo"]="java -jar $APEX_HOME/lib/applications/simple-wsclient-$_version-jar-with-dependencies.jar"
APEX_APP_MAP["tpl-event-json"]="java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.tools.model.generator.model2event.Model2EventMain"
APEX_APP_MAP["model-2-cli"]="java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.tools.model.generator.model2cli.Model2ClMain"
APEX_APP_MAP["cli-editor"]="java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.auth.clieditor.ApexCommandLineEditorMain"
APEX_APP_MAP["cli-tosca-editor"]="java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.auth.clieditor.tosca.ApexCliToscaEditorMain"
APEX_APP_MAP["engine"]="java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.service.engine.main.ApexMain"
APEX_APP_MAP["full-client"]="java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -jar $APEX_HOME/lib/applications/apex-client-full-$_version-full.jar"
APEX_APP_MAP["event-gen"]="java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.testsuites.performance.benchmark.eventgenerator.EventGenerator"
APEX_APP_MAP["onappf"]="java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config org.onap.policy.apex.services.onappf.ApexStarterMain"
APEX_APP_MAP["jmx-test"]="java -Dlogback.configurationFile=$APEX_HOME/etc/logback.xml -cp ${CLASSPATH} $_config $_jmxconfig org.onap.policy.apex.service.engine.main.ApexMain"

## array of applications with name=description
declare -A APEX_APP_DESCR_MAP
APEX_APP_DESCR_MAP["ws-console"]="a simple console sending events to APEX, connect to APEX consumer port"
APEX_APP_DESCR_MAP["ws-echo"]="a simple echo client printing events received from APEX, connect to APEX producer port"
APEX_APP_DESCR_MAP["tpl-event-json"]="provides JSON templates for events generated from a policy model"
APEX_APP_DESCR_MAP["model-2-cli"]="generates CLI Editor Commands from a policy model"
APEX_APP_DESCR_MAP["cli-editor"]="runs the APEX CLI Editor"
APEX_APP_DESCR_MAP["cli-tosca-editor"]="runs the APEX CLI Tosca Editor"
APEX_APP_DESCR_MAP["engine"]="starts the APEX engine"
APEX_APP_DESCR_MAP["full-client"]="starts the full APEX client (rest editor, deployment, monitoring) in a simple webserver"
APEX_APP_DESCR_MAP["event-generator"]="starts the event generator in a simple webserver for performance testing"
APEX_APP_DESCR_MAP["onappf"]="starts the ApexStarter which handles the Apex Engine based on instructions from PAP"
APEX_APP_DESCR_MAP["jmx-test"]="starts the APEX engine with creating jmx connection configuration"

##
## Help screen and exit condition (i.e. too few arguments)
##
Help()
{
    echo ""
    echo "$MOD_SCRIPT_NAME - runs APEX applications"
    echo ""
    echo "       Usage:  $MOD_SCRIPT_NAME [options] | [<application> [<application options>]]"
    echo ""
    echo "       Options"
    echo "         -d <app>    - describes an application"
    echo "         -l          - lists all applications supported by this script"
    echo "         -h          - this help screen"
    echo ""
    echo ""
    exit 255;
}
if [ $# -eq 0 ]; then
    Help
fi


##
## read command line, cannot do as while here due to 2-view CLI
##
if [ "$1" == "-l" ]; then
    echo "$MOD_SCRIPT_NAME: supported applications:"
    echo " --> ${!APEX_APP_MAP[@]}"
    echo ""
    exit 0
fi
if [ "$1" == "-d" ]; then
    if [ -z "$2" ]; then
        echo "$MOD_SCRIPT_NAME: no application given to describe, supported applications:"
        echo " --> ${!APEX_APP_MAP[@]}"
        echo ""
        exit 0;
    else
        _cmd=${APEX_APP_DESCR_MAP[$2]}
        if [ -z "$_cmd" ]; then
            echo "$MOD_SCRIPT_NAME: unknown application '$2'"
            echo ""
            exit 0;
        fi
        echo "$MOD_SCRIPT_NAME: application '$2'"
        echo " --> $_cmd"
        echo ""
        exit 0;
    fi
fi
if [ "$1" == "-h" ]; then
    Help
    exit 0
fi


_app=$1
shift
_cmd=${APEX_APP_MAP[$_app]}
if [ -z "$_cmd" ]; then
    echo "$MOD_SCRIPT_NAME: application '$_app' not supported"
    exit 1
fi
_cmd="$_cmd $*"
## echo "$MOD_SCRIPT_NAME: running application '$_app' with command '$_cmd'"
exec $_cmd

