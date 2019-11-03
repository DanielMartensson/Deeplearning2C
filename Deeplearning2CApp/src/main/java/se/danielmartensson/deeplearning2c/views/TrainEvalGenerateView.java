package se.danielmartensson.deeplearning2c.views;

import java.io.IOException;

import com.gluonhq.charm.glisten.mvc.View;

import javafx.fxml.FXMLLoader;

public class TrainEvalGenerateView {

	public View getView() {
        try {
            View view = FXMLLoader.load(ModelsView.class.getResource("traintestgenerate.fxml"));
            return view;
        } catch (IOException e) {
            System.out.println("IOException: " + e);
            return new View();
        }
    }

}
