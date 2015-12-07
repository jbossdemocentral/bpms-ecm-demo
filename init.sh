#!/bin/sh 
DEMO="JBoss BPM Suite ECM Integration Demo"
AUTHORS="Maciej Swiderski, Andrew Block, Eric D. Schabell"
PROJECT="git@github.com:jbossdemocentral/bpms-install-demo.git"
PRODUCT="JBoss BPM Suite"
JBOSS_HOME=./target/jboss-eap-6.4
SERVER_DIR=$JBOSS_HOME/standalone/deployments
SERVER_CONF=$JBOSS_HOME/standalone/configuration/
SERVER_BIN=$JBOSS_HOME/bin
SRC_DIR=./installs
SUPPORT_DIR=./support
PRJ_DIR=./projects
BPMS=jboss-bpmsuite-6.2.0.GA-installer.jar
EAP=jboss-eap-6.4.0-installer.jar
EAP_PATCH=jboss-eap-6.4.4-patch.zip
VERSION=6.2

# wipe screen.
clear 

echo
echo "#################################################################"
echo "##                                                             ##"   
echo "##  Setting up the ${DEMO}        ##"
echo "##                                                             ##"   
echo "##                                                             ##"   
echo "##     ####  ####   #   #      ### #   # ##### ##### #####     ##"
echo "##     #   # #   # # # # #    #    #   #   #     #   #         ##"
echo "##     ####  ####  #  #  #     ##  #   #   #     #   ###       ##"
echo "##     #   # #     #     #       # #   #   #     #   #         ##"
echo "##     ####  #     #     #    ###  ##### #####   #   #####     ##"
echo "##                                                             ##"   
echo "##                                                             ##"   
echo "##  brought to you by,                                         ##"   
echo "##           ${AUTHORS}  ##"
echo "##                                                             ##"   
echo "##  ${PROJECT}      ##"
echo "##                                                             ##"   
echo "#################################################################"
echo

command -v mvn -q >/dev/null 2>&1 || { echo >&2 "Maven is required but not installed yet... aborting."; exit 1; }

# make some checks first before proceeding.	
if [ -r $SRC_DIR/$EAP ] || [ -L $SRC_DIR/$EAP ]; then
	echo Product sources are present...
	echo
else
	echo Need to download $EAP package from the Customer Portal 
	echo and place it in the $SRC_DIR directory to proceed...
	echo
	exit
fi

if [ -r $SRC_DIR/$EAP_PATCH ] || [ -L $SRC_DIR/$EAP_PATCH ]; then
	echo Product patches are present...
	echo
else
	echo Need to download $EAP_PATCH package from the Customer Portal 
	echo and place it in the $SRC_DIR directory to proceed...
	echo
	exit
fi

if [ -r $SRC_DIR/$BPMS ] || [ -L $SRC_DIR/$BPMS ]; then
		echo Product sources are present...
		echo
else
		echo Need to download $BPMS package from the Customer Portal 
		echo and place it in the $SRC_DIR directory to proceed...
		echo
		exit
fi

# Remove old JBoss instance, if it exists.
if [ -x $JBOSS_HOME ]; then
		echo "  - existing JBoss product install detected and removed..."
		echo
		rm -rf ./target
fi

# Run installer.
echo "JBoss EAP installer running now..."
echo
java -jar $SRC_DIR/$EAP $SUPPORT_DIR/installation-eap -variablefile $SUPPORT_DIR/installation-eap.variables

if [ $? -ne 0 ]; then
	echo
	echo Error occurred during JBoss EAP installation!
	exit
fi

echo
echo "Applying JBoss EAP 6.4.4 patch now..."
echo
$JBOSS_HOME/bin/jboss-cli.sh --command="patch apply $SRC_DIR/$EAP_PATCH"

if [ $? -ne 0 ]; then
	echo Error occurred during JBoss EAP patch installation!
	exit
fi

echo
echo JBoss BPM Suite installer running now...
echo
java -jar $SRC_DIR/$BPMS $SUPPORT_DIR/installation-bpms -variablefile $SUPPORT_DIR/installation-bpms.variables

if [ $? -ne 0 ]; then
	echo Error occurred during $PRODUCT installation!
	exit
fi

echo
echo "  - setting up demo projects..."
echo
cp -r $SUPPORT_DIR/bpm-suite-demo-niogit $SERVER_BIN/.niogit

echo
echo " - setting up user roles with application-roles.properties ..."
echo
cp $SUPPORT_DIR/application-roles.properties $SERVER_CONF

echo
echo " - setting up standalone.xml configuration adjustments..."
echo
cp $SUPPORT_DIR/standalone.xml $SERVER_CONF

# Add execute permissions to the standalone.sh script.
echo "  - making sure standalone.sh for server is executable..."
echo
chmod u+x $JBOSS_HOME/bin/standalone.sh

echo "  - setup email task notification users..."
echo
cp $SUPPORT_DIR/userinfo.properties $SERVER_DIR/business-central.war/WEB-INF/classes/

# build custom extension for ECM demo.
mvn package -f $PRJ_DIR/brms-file-upload-cmis/pom.xml

echo
echo " - adding file-uplaoding to business central..."
cp $PRJ_DIR/brms-file-upload-cmis/target/brms-file-upload-cmis-1.0.0.jar $SERVER_DIR/business-central.war/WEB-INF/lib

# Optional: uncomment this to install mock data for BPM Suite.
#
#echo - setting up mock bpm dashboard data...
#cp $SUPPORT_DIR/1000_jbpm_demo_h2.sql $SERVER_DIR/dashbuilder.war/WEB-INF/etc/sql
#echo

echo
echo "Start $PRODUCT in one of two ways depending on usage:"
echo
echo "   1. Document stored on local filesystem in /tmp/{timestamp-directories}/mobile-serivce-agreement.txt"
echo
echo "               $ $SERVER_BIN/standalone.sh"
echo
echo "   2. Document is uploaded to and from Alfresco online CMIS service, start with a property:"
echo
echo "               $ $SERVER_BIN/standalone.sh -Dorg.jbpm.ecm.storage.type=opencmis"
echo
echo "The uploaded documents will be available in browser:"
echo
echo "               http://tinyurl.com/cmis-demo  (login: admin/admin)"
echo
echo "Login into business central at:"
echo
echo "    http://localhost:8080/business-central  (u:erics / p:bpmsuite1!)"
echo
echo "$PRODUCT $VERSION $DEMO setup complete."

