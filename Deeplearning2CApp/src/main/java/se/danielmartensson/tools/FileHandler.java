package se.danielmartensson.tools;

import java.io.File;
import java.io.FileNotFoundException;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.StorageService;

public class FileHandler {
	
	/*
	 * Fields
	 */
	private File privateStorage;

	/**
	 * Constructor
	 */
	public FileHandler() {
		try {
			privateStorage = Services.get(StorageService.class)
				      .flatMap(StorageService::getPrivateStorage)
				      .orElseThrow(() -> new FileNotFoundException("Could not access private storage."));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			  
	}

}
