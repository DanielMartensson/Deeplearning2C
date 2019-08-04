package se.danielmartensson.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class Load_Data {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableView<?> tableView;

    @FXML
    void Load_CSV_Eval_Data(ActionEvent event) {
    	
    }

    @FXML
    void Load_CSV_Train_Data(ActionEvent event) {

    }

    @FXML
    void Select_Input_Columns(ActionEvent event) {

    }

    @FXML
    void Select_Output_Columns(ActionEvent event) {

    }

    @FXML
    void Set_Delimiter_For_CSV(ActionEvent event) {

    }

    @FXML
    void initialize() {
        assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'load_data.fxml'.";

    }
    
}
