package se.danielmartensson.deeplearning2c.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class ConfigurationsView {
    
    public View getView() {
        try {
            View view = FXMLLoader.load(ConfigurationsView.class.getResource("configurations.fxml"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
}
