package se.danielmartensson.tools;

import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;

public class AlertBoxes{

	/*
	 * When stop using JNonlinearControl
	 */
	public void exit() {
		Alert question = new Alert(AlertType.CONFIRMATION);
		question.setTitle("Closing");
		question.setHeaderText("Do you want to close?");
		question.setResizable(false);
		question.setContentText("Press OK to close.");
		Optional<ButtonType> answer = question.showAndWait();
		if(answer.get() == ButtonType.OK) {
			Platform.exit();
		}
		
	}
	
	/*
	 * Information
	 */
	public void information(String title, String header, String content) {
		Alert info = new Alert(AlertType.INFORMATION);
		info.setTitle(title);
		info.setHeaderText(header);
		info.setContentText(content);
		info.showAndWait();
	}
	
	/*
	 * Asking for input
	 */
	public String input(String promt, String title, String header, String content) {
		TextInputDialog dialog = new TextInputDialog(promt);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		Optional<String> result = dialog.showAndWait();
		if(result.get().length() > 0) {
			return result.get();
		}else {
			return null; // No input
		}
	}
	
	/*
	 * Asking for selection
	 */
	public boolean question(String title, String header, String content) {
		Alert question = new Alert(AlertType.CONFIRMATION);
		question.setTitle(title);
		question.setHeaderText(header);
		question.setContentText(content);
		Optional<ButtonType> result = question.showAndWait();
		if(result.get() == ButtonType.OK) {
			return true; // Yes!
		}else {
			return false; // No!
		}
		
	}
	
}
