package se.danielmartensson.views;

import com.gluonhq.charm.glisten.mvc.View;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class ModelsView {

    public View getView() {
        try {
            View view = FXMLLoader.load(ModelsView.class.getResource("models.fxml"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }
}
