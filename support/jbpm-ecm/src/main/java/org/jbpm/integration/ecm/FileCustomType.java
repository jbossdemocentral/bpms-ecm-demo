/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.integration.ecm;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.jbpm.formModeler.core.fieldTypes.CustomFieldType;
import org.jbpm.formModeler.service.bb.mvc.components.URLMarkupGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Template;

/**
 * Sample implementation of a File input that can be used in forms to upload files to a storage system
 * and save the File path inside a String property. Two implementation of the storage are available
 * <ul>
 * 	<li>filesystem - default (stores files in /tmp - default location can be changed by setting system property org.jbpm.ecm.fs.location)</li>
 * 	<li>opencmis - integration with existing CMIS compatible services over ATOM</li>
 * </ul>
 * Implementation can be selected by setting system property org.jbpm.ecm.storage.type to one of the implementation
 * ids
 *
 * This Custom Type must be used only on String properties
 */
public class FileCustomType implements CustomFieldType {

    private static final Logger log = LoggerFactory.getLogger(FileCustomType.class);
    private static final String stoarageImpl = System.getProperty("org.jbpm.ecm.storage.type", "filesystem");

    @Inject
    private Instance<FileStorageService> fileStorageServiceInjected;
      
    private FileStorageService fileStorageService;

    @Inject
    private URLMarkupGenerator urlMarkupGenerator;
    
    @PostConstruct
    public void setup() {
    	
    	for (FileStorageService service : fileStorageServiceInjected) {
    		if (service.getId().equals(stoarageImpl)) {
    			fileStorageService = service;
    			break;
    		}
    	}
    }


    public String getDescription(Locale locale) {
        
        return "File Input";
    }

    @SuppressWarnings("rawtypes")
	public Object getValue(Map requestParameters, Map requestFiles, String fieldName, 
    		String namespace, Object previousValue, boolean required, boolean readonly, String... params) {
        String id = namespace + "_file_" + fieldName;

        String oldPath = (String) previousValue;

        // Expecting a delete parameter, if we receive that the current file will be deleted from the system
        String[] deleteParam = (String[]) requestParameters.get(id + "_delete");
        boolean delete = !StringUtils.isEmpty(oldPath) && (deleteParam != null && deleteParam.length > 0 && deleteParam[0] != null && Boolean.valueOf(deleteParam[0]).booleanValue());

        // if there is an uploaded file for that field we will delete the previous one (if existed) and will return the uploaded file path.
        File file = (File) requestFiles.get(id);
        if (file != null) {
            if (!StringUtils.isEmpty(oldPath)) { 
            	String[] idElements = oldPath.split("@");
            	fileStorageService.deleteFile(idElements[1]);
            }
            return fileStorageService.saveFile(fieldName, file);
        }

        // If we receive the delete parameter or we are uploading a new file the current file will be deleted
        if (delete) {
        	String[] idElements = oldPath.split("@");
        	fileStorageService.deleteFile(idElements[1]);
        }

        return previousValue;
    }

    public String getShowHTML(Object value, String fieldName, String namespace, boolean required, 
    		boolean readonly, String... params) {
        return renderField(fieldName, (String) value, namespace, false);
    }

    public String getInputHTML(Object value, String fieldName, String namespace, boolean required, 
    		boolean readonly, String... params) {
        return renderField(fieldName, (String) value, namespace, true && !readonly);
    }

    public String renderField(String fieldName, String path, String namespace, boolean showInput) {

        String str = null;        
        try {
            
            Map<String, Object> context = new HashMap<String, Object>();

            // if there is a file in the specified path, the input will show a link to download it.
            context.put("inputId", namespace + "_file_" + fieldName);
            if (path != null) {
            	// path is equipped with file name to avoid additional file retrieval from storage
            	String[] idElements = path.split("@");
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("content", Base64.encodeBase64String(path.getBytes()));

                String downloadLink = urlMarkupGenerator.getMarkup("ecmfdch", "download", params);

                context.put("showLink", Boolean.TRUE);
                context.put("downloadLink", downloadLink);
                context.put("fileName", idElements[0]);
            } else {
                context.put("showLink", Boolean.FALSE);
            }
            // If the field is readonly or we are just showing the field value we will hide the input file.
            context.put("showInput", showInput);

            InputStream src = this.getClass().getResourceAsStream("input.ftl");
            freemarker.template.Configuration cfg = new freemarker.template.Configuration();
            BeansWrapper defaultInstance = new BeansWrapper();
            defaultInstance.setSimpleMapWrapper(true);
            cfg.setObjectWrapper(defaultInstance);
            cfg.setTemplateUpdateDelay(0);
            Template temp = new Template(fieldName, new InputStreamReader(src), cfg);
            StringWriter out = new StringWriter();
            temp.process(context, out);
            out.flush();
            str = out.getBuffer().toString();
        } catch (Exception e) {
            log.warn("Failed to process template for field '{0}': {1}", fieldName, e);
        }
        return str;
    }

}
