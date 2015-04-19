#!/bin/bash

# Start BPMS
/opt/jboss/bpms/jboss-eap-6.4/bin/standalone.sh -c standalone.xml -b 0.0.0.0 -bmanagement 0.0.0.0 "$@" > /dev/null 2>&1 &

# Always open shell to enable visibility of filesystem contents
exec /bin/bash