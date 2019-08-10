package se.danielmartensson.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.StorageService;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Screen;
import se.danielmartensson.deeplearning.DL4JData;
import se.danielmartensson.deeplearning.DL4JGlobalConfig;
import se.danielmartensson.deeplearning.DL4JLayers;
import se.danielmartensson.deeplearning.DL4JModel;
import se.danielmartensson.tools.Dialogs;

public class NeuralNetworksPresenter {

    @FXML
    private View neuralnetworks;
    
    @FXML
    private TableView<String> networkTable;

    private DL4JData dL4JData;
	private DL4JGlobalConfig dL4JGlobalConfig;
	private DL4JLayers dL4JLayers;
	private DL4JModel dL4JModel;

	private Dialogs dialogs;

	

    public void initialize() {
    	
    	
    	
        neuralnetworks.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
               
            	/*
            	 * Title
            	 */
            	AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> MobileApplication.getInstance().getDrawer().open()));
                appBar.setTitleText("Neural Networks");
                
                /*
                 * Set the correct size on tableView
                 */
                //double widthBar = appBar.widthProperty().get();
                double heightBar = appBar.heightProperty().get();
                double heightScreen = Screen.getPrimary().getBounds().getHeight();
                double widthScreen = Screen.getPrimary().getBounds().getWidth();
                networkTable.setPrefSize(widthScreen, heightScreen - heightBar);
                
                /*
                 * Load all the files
                 */
     
                	  
                
                
                /*
                 * Methods
                 */
                appBar.getActionItems().add(MaterialDesignIcon.CREATE.button(e -> {
                	// Open a pop-up dialog and ask for save location
                	
                	String t = dialogs.input("Create!?", "Enter a file name?");
                	System.out.println(t);
                }));
                
                appBar.getActionItems().add(MaterialDesignIcon.FOLDER_OPEN.button(e -> {
        	    	
                	try{
                		ApplicationContext context = new ClassPathXmlApplicationContext(this.getClass().getResource("DL4JBeans1.xml").getPath());
                	}catch(NullPointerException ex) {
                		dialogs.exception("File Not found\nCannot read file!", ex);
                	}
        	    	
                	
                }));
                
                appBar.getActionItems().add(MaterialDesignIcon.SAVE.button(e -> {
                	
                }));
                
                appBar.getActionItems().add(MaterialDesignIcon.DELETE.button(e -> {
                	
                }));
            }
        });
        
        /*
    	 * Connect to our DL4J classes and its functionality
    	 */
        Platform.runLater(() -> {
	    	ApplicationContext context = new ClassPathXmlApplicationContext(this.getClass().getResource("DL4JBeans.xml").getPath());
	    	dL4JData = context.getBean("dL4JData", DL4JData.class);
	    	dL4JGlobalConfig = context.getBean("dL4JGlobalConfig", DL4JGlobalConfig.class);
	    	dL4JLayers = context.getBean("dL4JDataLayers", DL4JLayers.class);
	    	dL4JModel = context.getBean("dL4JModel", DL4JModel.class);
	    	((ClassPathXmlApplicationContext) context).close();
        });
        
        /*
         * Tools
         */
        dialogs = new Dialogs();
        
        
    }
        
}
