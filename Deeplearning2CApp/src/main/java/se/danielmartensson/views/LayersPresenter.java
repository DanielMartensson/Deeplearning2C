package se.danielmartensson.views;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import javafx.application.Platform;
import javafx.fxml.FXML;
import se.danielmartensson.deeplearning.DL4JLayers;

public class LayersPresenter {

    @FXML
    private View layers;

	private DL4JLayers dL4JLayers;


    public void initialize() {
    	layers.setShowTransitionFactory(BounceInRightTransition::new);
        
        FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.INFO.text,
                e -> System.out.println("Info"));
        fab.showOn(layers);
        
        layers.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        MobileApplication.getInstance().getDrawer().open()));
                appBar.setTitleText("Layers");
                
                
                appBar.getActionItems().add(MaterialDesignIcon.ADD.button(e -> 
                        System.out.println("Favorite")));
                
                appBar.getActionItems().add(MaterialDesignIcon.REMOVE.button(e -> 
                System.out.println("Favorite")));
            }
        });
        
        /*
		 * Connect to our DL4J classes and its functionality 
		 */
		Platform.runLater(() -> {
			ApplicationContext context = new FileSystemXmlApplicationContext("/src/main/resources/se/danielmartensson/beans/DL4JBeans.xml");
			dL4JLayers = context.getBean("dL4JLayers", DL4JLayers.class);
			((FileSystemXmlApplicationContext) context).close();
		});
    }
}
