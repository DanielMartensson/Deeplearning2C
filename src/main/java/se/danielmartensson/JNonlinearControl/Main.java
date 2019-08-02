package se.danielmartensson.JNonlinearControl;

import javafx.application.Application;
import javafx.stage.Stage;
import se.danielmartensson.tools.AlertBoxes;
import se.danielmartensson.tools.GUILoaders;


/**
 * JavaFX App
 */
public class Main extends Application {

	/*
	 * Start the start(Stage front)
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage front) throws Exception {
		front.setOnCloseRequest(e->{
			e.consume();
			AlertBoxes alertBoxes = new AlertBoxes();
			alertBoxes.exit();
		});

		front.setScene(new GUILoaders().getScene("/se/danielmartensson/controller/front.fxml"));
		front.setTitle("JNonlinearControl");		
		front.show();
	}


}