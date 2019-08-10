package se.danielmartensson.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class NeuralNetworksView {

    public View getView() {
        try {
            View view = FXMLLoader.load(NeuralNetworksView.class.getResource("neuralnetworks.fxml"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
}
