package se.danielmartensson.tools;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class GUILoaders {
	
	/*
	 * 
	 */
	public Scene getScene(String path) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource(path));
		Scene scene = new Scene(root);
		return scene;
	}
}
