package se.danielmartensson.main;

import javafx.application.Application;
import javafx.stage.Stage;
import se.danielmartensson.tools.AlertBoxes;
import se.danielmartensson.tools.GUILoaders;


/**
 * This is Deeplearning2C. The purpose with this Java applications is:
 * 1. Create classification
 * 2. Create regression
 * 3. Create prediction
 * 4. Create detection
 * 
 * This application will use MLP and RNN networks and generate all of them into one .c file
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
		front.setTitle("Deeplearning2C");		
		front.show();
	}


}