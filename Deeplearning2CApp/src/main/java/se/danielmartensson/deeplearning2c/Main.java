package se.danielmartensson.deeplearning2c;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.mvc.SplashView;
import com.gluonhq.charm.glisten.visual.Swatch;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import se.danielmartensson.deeplearning2c.views.ConfigurationsView;
import se.danielmartensson.deeplearning2c.views.DataView;
import se.danielmartensson.deeplearning2c.views.ModelsView;
import se.danielmartensson.deeplearning2c.views.TrainEvalGenerateView;

public class Main extends MobileApplication {

    public static final String MODELS_VIEW = HOME_VIEW;
    public static final String CONFIGURATIONS_VIEW = "Configurations View";
    public static final String DATA_VIEW = "Data View";
    public static final String TRAINEVALGENERATE_VIEW = "Train Eval Generate View";
    
    @Override
    public void init() {
    	// Add a splash view
    	addViewFactory(MobileApplication.SPLASH_VIEW, () -> {
    		Image image = new Image("icon.png");
    		ImageView imageView  = new ImageView(image);
    	     SplashView splashView = new SplashView(imageView);
    	     splashView.setOnShown(e -> {
    	         PauseTransition pause = new PauseTransition(Duration.seconds(3));
    	         pause.setOnFinished(e1 -> splashView.hideSplashView());
    	         pause.play();
    	     }); 
    	     return splashView;
    	 });
    	
        addViewFactory(MODELS_VIEW, () -> new ModelsView().getView());
        addViewFactory(CONFIGURATIONS_VIEW, () -> new ConfigurationsView().getView());
        addViewFactory(DATA_VIEW, () -> new DataView().getView());
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
