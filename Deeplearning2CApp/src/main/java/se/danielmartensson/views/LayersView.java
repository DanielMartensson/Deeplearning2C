package se.danielmartensson.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class LayersView {
    
    public View getView() {
        try {
            View view = FXMLLoader.load(LayersView.class.getResource("layers.fxml"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
}
