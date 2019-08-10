package se.danielmartensson.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class GlobalConfigurationView {
    
    public View getView() {
        try {
            View view = FXMLLoader.load(GlobalConfigurationView.class.getResource("globalconfiguration.fxml"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
}
