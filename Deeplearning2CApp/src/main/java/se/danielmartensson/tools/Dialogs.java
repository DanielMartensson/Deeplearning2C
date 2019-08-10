package se.danielmartensson.tools;

import java.util.Optional;

import com.gluonhq.charm.glisten.control.Alert;
import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.control.ExceptionDialog;
import com.gluonhq.charm.glisten.control.TextField;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class Dialogs {
	
	
	/**
	 * Create an exception dialog with intro text and an exception
	 * @param introText Text to show
	 * @param ex Our error
	 */
	public void exception(String introText, Exception ex) {
		ExceptionDialog exceptionDialog = new ExceptionDialog();
		exceptionDialog.setIntroText(introText);
		exceptionDialog.setException(ex);
		exceptionDialog.showAndWait();
	}
	

	/**
	 * Ask a question. Default return is false
	 * @param titleText String title
	 * @param content String question
	 * @return
	 */
	public String input(String titleText, String content) {
		Dialog<String> dialog = new Dialog<String>();
		dialog.setAutoHide(false);
		dialog.setResult("");
		dialog.setTitleText(titleText);
		dialog.setContent(new TextField(content));
		Button button_OK = new Button("OK");
		Button button_CANCLE = new Button("Cancle");
		button_OK.setOnAction(e-> dialog.hide());
		button_CANCLE.setOnAction(e-> dialog.hide());
		dialog.getButtons().addAll(button_OK, button_CANCLE);
		return dialog.showAndWait().get();
	}
	
	/**
	 * Information
	 * @param title String title
	 * @param content String information
	 */
	public void information(String title, String content) {
		Alert info = new Alert(AlertType.INFORMATION);
		info.setAutoHide(false);
		info.setTitleText(title);
		info.setContentText(content);
		info.showAndWait();
	}
	
	
	
	/**
	 * Asking for selection
	 * @param title String title
	 * @param content String question
	 * @return
	 */
	public boolean question(String title, String content) {
		Alert question = new Alert(AlertType.CONFIRMATION);
		question.setAutoHide(false);
		question.setTitleText(title);
		question.setContentText(content);
		question.setContentText(content);
		Optional<ButtonType> result = question.showAndWait();
		if(result.get() == ButtonType.OK) {
			return true; // Yes!
		}else {
			return false; // No!
		}
	}
}
