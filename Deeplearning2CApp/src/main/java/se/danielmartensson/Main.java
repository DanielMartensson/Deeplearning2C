package se.danielmartensson;

import se.danielmartensson.views.NeuralNetworksView;
import se.danielmartensson.views.TrainEvalGenerateView;
import se.danielmartensson.views.GlobalConfigurationView;
import se.danielmartensson.views.LayersView;
import se.danielmartensson.views.LoadDataView;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends MobileApplication {

    public static final String NEURALNETWORKS_VIEW = HOME_VIEW;
    public static final String LOADDATA_VIEW = "LoadData View";
    public static final String GLOBALCONFIGURATION_VIEW = "GlobalConfiguration View";
    public static final String LAYERS_VIEW = "Layers View";
    public static final String TRAINEVALGENERATE_VIEW = "TrainEvalGenerate View";
    
    @Override
    public void init() {
        addViewFactory(NEURALNETWORKS_VIEW, () -> new NeuralNetworksView().getView());
        addViewFactory(LOADDATA_VIEW, () -> new LoadDataView().getView());
        addViewFactory(GLOBALCONFIGURATION_VIEW, () -> new GlobalConfigurationView().getView());
        addViewFactory(LAYERS_VIEW, () -> new LayersView().getView());
        addViewFactory(TRAINEVALGENERATE_VIEW, () -> new TrainEvalGenerateView().getView());
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
