package se.danielmartensson.tools;

import java.io.File;
import java.io.IOException;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.StorageService;

public class FileHandler {
	
	/*
	 * Fields
	 */
	private File localRoot;
	private Dialogs dialogs;

	/**
	 * Constructor - Open connection to root catalog
	 */
	public FileHandler(){
		/*
		 * Local root e.g /root/.gluon folder
		 */
		localRoot = Services.get(StorageService.class)
	            .flatMap(s -> s.getPublicStorage(""))
	            .orElseThrow(() -> new RuntimeException("Error retrieving private storage"));	
		System.out.println(localRoot.getAbsolutePath());
		/*
		 * Dialogs
		 */
		dialogs = new Dialogs();
	}
	
	/**
	 * Create a new file
	 * @param filePath Sting path to our file
	 * @return File
	 */
	public File createNewFile(String filePath) {
		File file = new File(localRoot + filePath);
		file.getParentFile().mkdirs(); // Create folders
		if(file.exists() == true) {
			boolean overwrite = dialogs.question("File already exist.", "Should we overwrite?");
			if(overwrite == true) {
				createTheFile(file);
				return file;
			}
		}else {
			createTheFile(file);
			return file;
		}
		return null; // We pressed cancel to overwrite
	}
	
	/**
	 * This will create the file
	 * @param file Our file
	 */
	private void createTheFile(File file) {
		try {
			file.createNewFile();
		} catch (IOException e) {
			dialogs.exception("Cannot create the file at:\n" + file.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Load a file
	 * @param filePath Sting path to our file 
	 * @return File
	 * 
	 */
	public File loadFile(String filePath) {
		File file = new File(localRoot + filePath);
		if(file.exists() == false) {
			return createNewFile(filePath); // Not exist, we create one instead
		}else if(file.canRead() == false) {
			dialogs.exception("This file cannot be readed:\n" + filePath, new IOException());
			return null;
		}else if(file.canWrite() == false) {
			dialogs.exception("This file cannot be written to:\n" + filePath, new IOException());
			return null;
		}else {
			return file;
		}
	}

}
