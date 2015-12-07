JBoss BPM Suite Document Integration Demo 
=========================================
Project to automate the installation of this product with ECM (document integration with BPM) demo project. Demo is a telco story of
customer working with process to activate her mobile service by downloading a service contract document, signing it (update), and
uploading the results back into the process.

There are two options available to you for using this demo; local and containerized.


Option 1 - Install on your machine
----------------------------------
1. [Download and unzip.](https://github.com/jbossdemocentral/bpms-ecm-demo/archive/master.zip)

2. Add product installer to installs directory.

3. Run 'init.sh' or 'init.bat' file. 'init.bat' must be run with Administrative privileges.

4. You have two options to run demo, using filesystem for document storage or CMIS interface over network:

   ```
   $ ./target/jboss-eap-6.4/bin/standalone.sh
  
   or 

   $ ./target/jboss-eap-6.4/bin/standalone.sh -Dorg.jbpm.ecm.storage.type=opencmis
   ```

5. Login to http://localhost:8080/business-central  (u:erics / p:bpmsuite1!)

   ```
   - build & deploy mobile service activation process

   - start process, submit provided service agreement document from suport/mobile-service-agreement.txt

   - at user task, form presented that allows user to download service agreement, edit & sign agreement, upload signed document to
     complet task.

   - view document stored either on filesystem (/tmp/{date-time-stamp-dir}/mobile-service-agreement.txt) or on CMIS online storage
     at http://tinyurl.com/cmis-demo  (login: admin/admin)
   ```


Option 2 - Generate containerized installation
----------------------------------------------
The following steps can be used to configure and run the demo in a container

1. [Download and unzip.](https://github.com/jbossdemocentral/bpms-ecm-demo/archive/master.zip)

2. Add product installer to installs directory.

3. Copy contents of support/docker directory to the project root.

4. Build demo image

	```
	docker build -t jbossdemocentral/bpms-ecm-demo .
	```
5. You have two options to run demo, using filesystem for document storage or CMIS interface over network:
    
    ```
	docker run -it -p 8080:8080 -p 9990:9990 jbossdemocentral/bpms-ecm-demo
    ```
    or
      
    ```
	docker run -it -p 8080:8080 -p 9990:9990 jbossdemocentral/bpms-ecm-demo -Dorg.jbpm.ecm.storage.type=opencmis
    ```
6. Login to http://&lt;DOCKER_HOST&gt;:8080/business-central  (u:erics / p:bpmsuite1!)

    ```
   - build & deploy mobile service activation process

   - start process, submit provided service agreement document from support/mobile-service-agreement.txt

   - at user task, form presented that allows user to download service agreement, edit & sign agreement, upload signed document to
     complet task.

   - view document stored either on filesystem in container using terminal used to launch container (/tmp/{date-time-stamp-dir}/mobile-service-agreement.txt) or on CMIS online storage
     at http://tinyurl.com/cmis-demo  (login: admin/admin)
     ```

Running demo
------------
Two options to start BPM Suite server:

   1. Document management on local filesystem, found in /tmp/{timestamp-directories}/mobile-service-agreement.txt

   2. Document management via remote ECM system interface based on open standard CMIS through a [hosted Alfresco service](http://tinyurl.com/cmis-demo),
      login: admin/admin and you can see the document uploaded, view contents, and remember to remove it when done.

   3. Claimed tasks that are not competed within a minute will be reassigned automatically back into the group for processing.

Sample mobile service agreement (unsigned) is located in support/mobile-service-agreement.txt, use this as initial upload document, 
then download and sign by adding your name, save it locally, and then upload it via the task form.


Supporting Articles
-------------------
- [7 Steps to Your First Process with JBoss BPM Suite Starter	Kit](http://www.schabell.org/2015/08/7-steps-first-process-jboss-bpmsuite-starter-kit.html)

- [3 shockingly easy ways into JBoss rules, events, planning & BPM](http://www.schabell.org/2015/01/3-shockingly-easy-ways-into-jboss-brms-bpmsuite.html)

- [Jump Start Your Rules, Events, Planning and BPM Today](http://www.schabell.org/2014/12/jump-start-rules-events-planning-bpm-today.html)

- [5 Handy Tips From JBoss BPM Suite For Release 6.0.3](http://www.schabell.org/2014/10/5-handy-tips-from-jboss-bpmsuite-release-603.html)

- [Lightning brings Red Hat JBoss BPM Suite ECM telco CMIS integration demo](http://www.schabell.org/2014/07/lightning-strike-brings-redhat-jboss-bpmsuite-ecm-cmis-demo.html)


Released versions
-----------------
See the tagged releases for the following versions of the product:

- v0.7 - JBoss BPM Suite 6.2.0, JBoss EAP 6.4.4 and supporting document integration installed.

- v0.6 - JBoss BPM Suite 6.1 with supporting document integration installed.

- v0.5 - JBoss BPM Suite 6.0.3 with automated task reassignment.

- v0.4 - JBoss BPM Suite 6.0.3 with optional containerized installation.

- v0.3 - moved to JBoss Demo Central, with updated windows init.bat support.

- v0.2 - JBoss BPM Suite 6.0.3 installer with supporting document integration installed. 

- v0.1 - JBoss BPM Suite 6.0.2 installer used, with supporting document integration installed. 

![Process](https://github.com/jbossdemocentral/bpms-ecm-demo/blob/master/docs/demo-images/mobile-activation-process.png?raw=true)

![BPM Suite](https://github.com/jbossdemocentral/bpms-ecm-demo/blob/master/docs/demo-images/bpmsuite.png?raw=true)
