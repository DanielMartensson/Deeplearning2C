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
import se.danielmartensson.deeplearning.DL4JData;

public class LoadDataPresenter {

    @FXML
    private View loaddata;
    
    private DL4JData dL4JData;

    public void initialize() {
        loaddata.setShowTransitionFactory(BounceInRightTransition::new);
        
        FloatingActionButton fab = new FloatingActionButton(MaterialDesignIcon.INFO.text,
                e -> System.out.println("Info"));
        fab.showOn(loaddata);
        
        loaddata.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
                        MobileApplication.getInstance().getDrawer().open()));
                appBar.setTitleText("Load Data");
                
                
                appBar.getActionItems().add(MaterialDesignIcon.ATTACH_FILE.button(e -> 
                        System.out.println("Favorite")));
                
                appBar.getActionItems().add(MaterialDesignIcon.COLLECTIONS.button(e -> 
                System.out.println("Favorite")));
            }
        });
        
        /*
    	 * Connect to our DL4J classes and its functionality
    	 */
        Platform.runLater(() -> {
			ApplicationContext context = new FileSystemXmlApplicationContext("/src/main/resources/se/danielmartensson/beans/DL4JBeans.xml");
	    	dL4JData = context.getBean("dL4JData", DL4JData.class);
	    	((FileSystemXmlApplicationContext) context).close();
        });
    }
}
