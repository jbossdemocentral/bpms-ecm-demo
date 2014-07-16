package org.jbpm.integration.ecm.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.io.IOUtils;
import org.jbpm.integration.ecm.FileStorageService;

@Named("opencmis")
@ApplicationScoped
public class OpenCMISFileStorageServiceImpl implements FileStorageService {

	private String storagePath = System.getProperty("org.jbpm.ecm.fs.location", "/tmp");
	
	public static final String USER = System.getProperty("org.jbpm.ecm.cmis.user", "admin");
	public static final String PASSWORD = System.getProperty("org.jbpm.ecm.cmis.password", "admin");
	public static final String URL = System.getProperty("org.jbpm.ecm.cmis.url", "http://cmis.alfresco.com/cmisatom");
	public static final String REPO = System.getProperty("org.jbpm.temp.ecm.cmis.repo", "288244af-fedb-45e6-8a6e-f5c842241d0f");
	
	private MimetypesFileTypeMap mimeMap = new MimetypesFileTypeMap();
	
	public String saveFile(String fieldName, File file) {
		String type = mimeMap.getContentType(file);
		Session session = getRepositorySession(USER, PASSWORD, URL, REPO);
		Folder parent = (Folder) session.getObjectByPath("/");
		Folder root = parent;
		if (root == null) {
			root = session.getRootFolder();
		}
		// properties 
		// (minimal set: name and object type id)
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		properties.put(PropertyIds.NAME, file.getName());
		// set default type if none is given
		if (type == null) {
			type = "text/plain";
		}
		// content	
		try {
			InputStream input = new FileInputStream(file);
			ContentStream contentStream = new ContentStreamImpl(file.getName(), null, type, input);
	
			// create a major version
			Document newDoc = root.createDocument(properties, contentStream, VersioningState.MAJOR);
			input.close();
			
			return file.getName()+"@"+newDoc.getId();
		
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public File getFile(String id) {
		Session session = getRepositorySession(USER, PASSWORD, URL, REPO);
		
		Document doc = (Document)session.getObject(id);
		if (doc == null) {
			return null;
		}
		File output= null;
		if (doc.getContentStream() != null) {
			ContentStream stream = doc.getContentStream();
			try {
				String temp = UUID.randomUUID().toString();
				output = new File(storagePath + File.separator + temp, stream.getFileName());
				output.getParentFile().mkdirs();
				FileOutputStream out = new FileOutputStream(output);
		
				IOUtils.copy(stream.getStream(), out);
				
				out.close();
				// just a simple clean up
				output.deleteOnExit();
			
			} catch (IOException e) {
				throw new RuntimeException("Cannot read document content", e);
			}
		}
		return output;
	}

	public boolean deleteFile(String id) {
		try {
			Session session = getRepositorySession(USER, PASSWORD, URL, REPO);
			Document document = (Document) session.getObject(id);
			document.delete(false);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteFile(File file) {
		throw new UnsupportedOperationException("Not supported");
	}

	
	// helper methods
	protected Session getRepositorySession(String user, String password, String url, String repository) {
		try {
			SessionFactory factory = SessionFactoryImpl.newInstance();
			Map<String, String> parameter = new HashMap<String, String>();
	
			// user credentials
			parameter.put(SessionParameter.USER, user);
			parameter.put(SessionParameter.PASSWORD, password);
	
			// connection settings
			parameter.put(SessionParameter.ATOMPUB_URL, url);
			parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			parameter.put(SessionParameter.REPOSITORY_ID, repository);
	
			// create session
			Session session = factory.createSession(parameter);
			
			return session;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public String getId() {
		return "opencmis";
	}
}
