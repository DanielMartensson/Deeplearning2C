package se.danielmartensson.tools;

import java.io.File;
import java.io.IOException;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.StorageService;



public class FileHandler {
	private File localRoot;
	private Dialogs dialogs = new Dialogs();

	/**
	 * Constructor - Open connection to root catalog
	 * Local root e.g /root/Documents folder
	 */
	public FileHandler(){
		localRoot = Services.get(StorageService.class).flatMap(s -> s.getPublicStorage("Documents")).orElseThrow(() -> new RuntimeException("Error retrieving private storage"));	
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
			System.out.println("Create the new file");
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
			System.out.println("Creating...");
			file.createNewFile();
		} catch (IOException e) {
			dialogs.exception("Cannot create the file at:\n" + file.getAbsolutePath(), e);
		}
	}
	
	/**
	 * Load a file
	 * @param filePath Sting path to our file 
	 * @return File
	 */
	public File loadFile(String filePath) {
		File file = new File(localRoot + filePath);
		System.out.println(file.getAbsolutePath());
		if(file.exists() == false) {
			System.out.println("Yes...file " + file.getPath() + " exist");
			return createNewFile(filePath); // Not exist, we create one instead
		}else if(file.canRead() == false) {
			dialogs.exception("This file cannot be readed:\n" + filePath, new IOException());
			return null;
		}else if(file.canWrite() == false) {
			dialogs.exception("This file cannot be written to:\n" + filePath, new IOException());
			return null;
		}else {
			System.out.println("return file");
			return file;
		}
	}
	
	/**
	 * Scan a folder and list all files
	 * @param fileExtension File extension such as .csv, .txt or .png etc.
	 * @param pathCSVFolder Our string path to the folder
	 * @return File[]
	 */
	public File[] scanFolder(String fileExtension, String pathCSVFolder) {
		File folder = new File(localRoot + pathCSVFolder);
		if(folder.exists() == false) 
			folder.mkdirs(); // Create one
		
		File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(fileExtension));
		if (files != null) 
			return files;
		else 
			return null;
	}
}
