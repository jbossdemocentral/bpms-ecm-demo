#!/bin/sh 
DEMO="JBoss BPM Suite ECM Integration Demo"
AUTHORS="Maciej Swiderski, Eric D. Schabell"
PROJECT="git@github.com:eschabell/bpms-install-demo.git"
PRODUCT="JBoss BPM Suite"
JBOSS_HOME=./target/jboss-eap-6.1
SERVER_DIR=$JBOSS_HOME/standalone/deployments
SERVER_CONF=$JBOSS_HOME/standalone/configuration/
SERVER_BIN=$JBOSS_HOME/bin
SRC_DIR=./installs
SUPPORT_DIR=./support
PRJ_DIR=./projects
BPMS=jboss-bpms-installer-6.0.2.GA-redhat-5.jar
VERSION=6.0.2

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
echo "##             ${AUTHORS}              ##"
echo "##                                                             ##"   
echo "##  ${PROJECT}             ##"
echo "##                                                             ##"   
echo "#################################################################"
echo

command -v mvn -q >/dev/null 2>&1 || { echo >&2 "Maven is required but not installed yet... aborting."; exit 1; }

# make some checks first before proceeding.	
if [ -r $SRC_DIR/$BPMS ] || [ -L $SRC_DIR/$BPMS ]; then
		echo Product sources are present...
		echo
else
		echo Need to download $BPMS package from the Customer Portal 
		echo and place it in the $SRC_DIR directory to proceed...
		echo
		exit
fi

# Move the old JBoss instance, if it exists, to the OLD position.
if [ -x $JBOSS_HOME ]; then
		echo "  - existing JBoss product install detected..."
		echo
		echo "  - moving existing JBoss product install aside..."
		echo
		rm -rf $JBOSS_HOME.OLD
		mv $JBOSS_HOME $JBOSS_HOME.OLD
fi

# Run installer.
echo Product installer running now...
echo
java -jar $SRC_DIR/$BPMS $SUPPORT_DIR/installation-bpms -variablefile $SUPPORT_DIR/installation-bpms.variables

echo
echo " - setting up standalone.xml configuration adjustments..."
echo
cp $SUPPORT_DIR/standalone.xml $SERVER_CONF

# Add execute permissions to the standalone.sh script.
echo "  - making sure standalone.sh for server is executable..."
echo
chmod u+x $JBOSS_HOME/bin/standalone.sh

# build custom extension for ECM demo.
mvn package -f $PRJ_DIR/brms-file-upload-cmis/pom.xml

echo
echo " - adding libs needed for CMIS interaction and file-uplaoding to business central..."
echo
cp $SUPPORT_DIR/libs/*.jar $SERVER_DIR/business-central.war/WEB-INF/lib
cp $PRJ_DIR/brms-file-upload-cmis/target/brms-file-upload-cmis-1.0.0.jar $SERVER_DIR/business-central.war/WEB-INF/lib

echo
echo "Start the $PRODUT with:"
echo
echo "    $SERVER_BIN/standalone.sh"
echo
echo "Login to business central in browser at:"
echo
echo "    http://localhost:8080/business-central  (u:erics / p:bpmsuite1!)"
echo
echo "$PRODUCT $VERSION $DEMO setup complete."

