JBoss BPM Suite Document Integration Demo 
=========================================

Project to automate the installation of this product with ECM (document integration with BPM) demo project. Demo is a telco story of
customer working with process to activate her mobile service by downloading a service contract document, signing it (update), and
uploading the results back into the process.

Quickstart
----------

1. [Download and unzip.](https://github.com/eschabell/bpms-ecm-demo/archive/master.zip)

2. Add product installer to installs directory.

3. Run 'init.sh' or 'init.bat' file, product will auto start.

4. Login to http://localhost:8080/business-central  (u:erics / p:bpmsuite1!)

5. Enjoy the BPM demo with document integration.


Running demo
------------
Two options to start BPM Suite server:

   1. Document management on local filesystem, found in /tmp/{timestamp-directories}/mobile-service-agreement.txt

   2. Document management via remote ECM system interface based on open standard CMIS through a [hosted Alfresco service](http://tinyurl.com/cmis-demo),
      login: admin/admin and you can see the document uploaded, view contents, and remember to remove it when done.

Sample mobile service agreement (unsigned) is located in support/mobile-service-agreement.txt, use this as initial upload document, 
then download and sign by adding your name, save it locally, and then upload it via the task form.


Supporting Articles
-------------------
None yet.


Released versions
-----------------

See the tagged releases for the following versions of the product:

- v0.1 - JBoss BPM Suite 6.0.2 installer used, with supporting document integration installed. 

![Process](https://github.com/eschabell/bpms-ecm-demo/blob/master/support/mobile-activation-process.png?raw=true)
