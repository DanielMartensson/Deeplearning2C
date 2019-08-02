package se.danielmartensson.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;

public class Network_Configuration {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Spinner<?> batchSize;

    @FXML
    private Spinner<?> seed;

    @FXML
    private Spinner<?> learningRate;

    @FXML
    private Spinner<?> epochs;

    @FXML
    private Spinner<?> hiddenNodes;

    @FXML
    void initialize() {
        assert batchSize != null : "fx:id=\"batchSize\" was not injected: check your FXML file 'network_configuration.fxml'.";
        assert seed != null : "fx:id=\"seed\" was not injected: check your FXML file 'network_configuration.fxml'.";
        assert learningRate != null : "fx:id=\"learningRate\" was not injected: check your FXML file 'network_configuration.fxml'.";
        assert epochs != null : "fx:id=\"epochs\" was not injected: check your FXML file 'network_configuration.fxml'.";
        assert hiddenNodes != null : "fx:id=\"hiddenNodes\" was not injected: check your FXML file 'network_configuration.fxml'.";

    }
}
