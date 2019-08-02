package se.danielmartensson.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import se.danielmartensson.deeplearning.DL4J;
import se.danielmartensson.tools.AlertBoxes;
import se.danielmartensson.tools.GUILoaders;

public class Front {
	
	/*
	 * Stages
	 */
	private Stage front_stage;
	private Stage load_data_stage;
	private Stage network_configuration_stage;
	
	private AlertBoxes alertBoxes;
	private DL4J dL4J;

    @FXML
    private ResourceBundle resources;
    
    @FXML
    private MenuItem Save_Model_MenuItem;

    @FXML
    private MenuItem Save_Model_As_MenuItem;

    @FXML
    private URL location;

    @FXML
    private Menu Recent_Model;

    @FXML
    private LineChart<String, Float> lineChart;

    @FXML
    private TextArea textArea;

    @FXML
    void About_JNonlinearControl(ActionEvent event) {
    	alertBoxes.information("About JNonlinearControl", "JNonlinearControl - For Nonlinear Models", "For more information. Visit my GitHub page"); // Show the about dialog
    }

    @FXML
    void Evaluate_Model(ActionEvent event) {
    	
    }

    @FXML
    void Generate_C_Code(ActionEvent event) {

    }

    @FXML
    void Load_Data(ActionEvent event) {
    	load_data_stage.show(); 
    }

    @FXML
    void Network_Model_Configuration(ActionEvent event) {
    	network_configuration_stage.show(); 
    }

    @FXML
    void New_Model(ActionEvent event) throws IOException {  
    	/*
    	 * Have we saved before?
    	 */
    	String fileName = null;
    	if(dL4J.getDl4JSaveLoad().getFile() != null) { // Yes
    		dL4J.getDl4JSaveLoad().askIfSaving(); // First save the current model
    		fileName = dL4J.getDl4JSaveLoad().newFile(); // Save a file as...
    	}else if(dL4J.getDl4JSaveLoad().getFile() == null) { // No
    		fileName = dL4J.getDl4JSaveLoad().newFile(); // Save a file as....
    	}
 
    	/*
    	 * Then create an initial model, save it and then change title
    	 */
    	if(!fileName.equals("")) {
	    	dL4J.initialModel();
	    	dL4J.getDl4JSaveLoad().saveModel();
	    	changeTitle(fileName);
    	}
    	
    }
    
    /*
     * Change the title on the menu bar
     */
    private void changeTitle(String fileName) {
    	front_stage = (Stage) lineChart.getScene().getWindow();
		front_stage.setTitle("JNonlinearControl - " + fileName);
		Save_Model_MenuItem.setDisable(false);
		Save_Model_As_MenuItem.setDisable(false);
    }

    @FXML
    void Open_Model(ActionEvent event) throws IOException {
    	String fileName = dL4J.getDl4JSaveLoad().loadModel();
    	if(!fileName.equals("")) 
    		changeTitle(fileName);
    }

    @FXML
    void Quit_JNonlinearControl(ActionEvent event) {

    }

    @FXML
    void Save_Model(ActionEvent event) throws IOException {
    	dL4J.getDl4JSaveLoad().saveModel();
    }

    @FXML
    void Save_Model_As(ActionEvent event) throws IOException {
    	String fileName = dL4J.getDl4JSaveLoad().saveModelAs();
    	front_stage.setTitle("JNonlinearControl - " + fileName);
    }

    @FXML
    void Simulate_Model(ActionEvent event) {

    }

    @FXML
    void Stop_Process(ActionEvent event) {

    }

    @FXML
    void Train_Model(ActionEvent event) {

    }

    @FXML
    void initialize() throws IOException, InterruptedException {
    	
    	/*
    	 * Load all the stages
    	 */ 
    	
    	load_data_stage = new Stage();
    	network_configuration_stage = new Stage();
    	load_data_stage.setScene(new GUILoaders().getScene("/se/danielmartensson/controller/load_data.fxml"));
    	network_configuration_stage.setScene(new GUILoaders().getScene("/se/danielmartensson/controller/network_configuration.fxml"));
    	load_data_stage.setTitle("Load Measured Data");
    	network_configuration_stage.setTitle("Configure Your Neural Network Model");
    	
    	/*
    	 * Get the alert boxes
    	 */
    	alertBoxes = new AlertBoxes();
    	
    	/*
    	 * Deeplearning4J
    	 */
    	dL4J = new DL4J();
    	
    	/*
    	 * Disable these first
    	 */
    	Save_Model_MenuItem.setDisable(true);
		Save_Model_As_MenuItem.setDisable(true);
    	
        assert Recent_Model != null : "fx:id=\"Recent_Model\" was not injected: check your FXML file 'front.fxml'.";
        assert lineChart != null : "fx:id=\"lineChart\" was not injected: check your FXML file 'front.fxml'.";
        assert textArea != null : "fx:id=\"textArea\" was not injected: check your FXML file 'front.fxml'.";

    }
}
