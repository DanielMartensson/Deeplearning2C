package se.danielmartensson;

import se.danielmartensson.views.ModelsView;
import se.danielmartensson.views.ConfigurationsView;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends MobileApplication {

    public static final String MODELS_VIEW = HOME_VIEW;
    public static final String CONFIGURATIONS_VIEW = "Configurations View";
    
    @Override
    public void init() {
        addViewFactory(MODELS_VIEW, () -> new ModelsView().getView());
        addViewFactory(CONFIGURATIONS_VIEW, () -> new ConfigurationsView().getView());

        DrawerManager.buildDrawer(this);
    }

    @Override
    public void postInit(Scene scene) {
        Swatch.BLUE.assignTo(scene);

        scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());
        ((Stage) scene.getWindow()).getIcons().add(new Image(Main.class.getResourceAsStream("/icon.png")));
        ((Stage) scene.getWindow()).setTitle("Deeplearning2C");
    }
}
