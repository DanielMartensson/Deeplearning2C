package se.danielmartensson.views;

import java.io.File;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import lombok.Getter;
import se.danielmartensson.deeplearning.DL4JModel;
import se.danielmartensson.tools.Dialogs;
import se.danielmartensson.tools.FileHandler;
import se.danielmartensson.tools.SimpleDependencyInjection;

/**
 * Model view class.
 * @author Daniel Mårtensson
 *
 */
public class ModelsPresenter {

    @FXML
    private View models;

    @FXML
    private @Getter TableView<String> tableView;
    
    @FXML
    private TableColumn<String, String> columnName;
    
    /*
     * Tools
     */
    private FileHandler fileHandler = new FileHandler();
    private Dialogs dialogs = new Dialogs();
    
    /*
     * Injected as static
     */
    private DL4JModel dL4JModel;
    
    /*
     * For table
     */
    private final String modelPath = "/Deeplearning2CStorage/";
	private ObservableList<String> list;

    public void initialize() {
    	/*
		 * Dependency injection
		 */
		dL4JModel = SimpleDependencyInjection.getDL4JModel();
		
    	/*
    	 * Run this test so we make sure that we can create and delete files
    	 */
    	fileHandler.runCreateDeleteTest(modelPath + "test" + ".zip");
    	
    	/*
    	 * Slide smooth in and out
    	 */
    	models.setShowTransitionFactory(BounceInRightTransition::new);
    	
    	/*
    	 * Listener for leaving and enter the page
    	 */
        models.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
            	/*
                 * Enter the page
                 */
            	AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setTitleText("Models");
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> MobileApplication.getInstance().getDrawer().open()));
        		appBar.getActionItems().add(MaterialDesignIcon.CREATE.button(e -> createModel()));
        		appBar.getActionItems().add(MaterialDesignIcon.SAVE.button(e -> saveModel()));
        		appBar.getActionItems().add(MaterialDesignIcon.DELETE.button(e -> deleteModel()));
   		
                
            }else {
            	/*
            	 * Leaving the page and set the name on the model we have selected
            	 */
            	dL4JModel.getDL4JSerializableConfiguration().setModelName(tableView.getSelectionModel().getSelectedItem());
            	
            }
        });
        	
		/*
		 * ObservableList to table view
		 */
        columnName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
		list = FXCollections.observableArrayList();
		tableView.setItems(list);
		
		/*
		 * Update the table view if we have files and select first row
		 */
		int totalFiles = fileHandler.countFiles(".zip", modelPath);
		if(totalFiles > 0) {
			updateTableView();
			tableView.getSelectionModel().selectFirst();
			String modelName = tableView.getSelectionModel().getSelectedItem();
			dL4JModel.loadModel(modelPath + modelName + ".zip", false);
		}
    }
    
    /**
     * Delete our model and update table
     */
    private void deleteModel() {
    	/*
    	 * Check if we have selected a row
    	 */
    	if(tableView.getSelectionModel().getSelectedItem() == null)
    		return;
    	
    	/*
    	 * Ask a question if we should delete or not
    	 */
    	if(dialogs.question("Delete", "Do you want to delete?") == false)
    		return;
    	
    	/*
    	 * Begin to delete
    	 */
    	String modelName = tableView.getSelectionModel().getSelectedItem();
    	int selectedRow = tableView.getSelectionModel().getSelectedIndex() + 1;
    	int totalFiles = fileHandler.countFiles(".zip", modelPath);
    	fileHandler.deleteFile(modelPath + modelName + ".zip");
    	fileHandler.deleteFile(modelPath + modelName + ".ser");
    	updateTableView();
    	if(selectedRow == totalFiles) // selectedRow can never be larger than totalFiles
    		tableView.getSelectionModel().selectLast();
    	else
    		tableView.getSelectionModel().select(selectedRow-1);
	}

    /**
     * Save the model and its configurations and then load it
     */
	private void saveModel() {
		String modelName = tableView.getSelectionModel().getSelectedItem();
		if(modelName == null)
			return; // No selected model
		if(dialogs.question("Save", "Do you want to save " + modelName + " ?") == true)
			dL4JModel.saveModel(modelPath + modelName + ".zip");
	}

	/**
	 * Create a new model
	 */
	private void createModel() {
		/*
		 * Pop-up dialog that ask for a name
		 */
		String modelName = dialogs.input("New", "Enter new name");
		
		/*
		 * Exceptions
		 */
		if(modelName.equals("test") == true) {
			dialogs.alertDialog(AlertType.INFORMATION, "Cannot use 'test'", "Select another file name."); // Look at initialize() 
			return;
		}
		
		if(modelName.equals("") == true) {
			return;
		}
		
		/*
		 * Create the file and update the table and then select last row
		 */
		fileHandler.createNewFile(modelPath + modelName + ".zip", false);
		updateTableView();
		tableView.getSelectionModel().selectLast();
		
		/*
		 * Insert a real basic model into that .zip file we just created. Then save it.
		 */
		dL4JModel.createBasicModel(modelPath + modelName + ".zip");
	}

	/**
	 * This method will update the table view
	 */
	private void updateTableView() {
		list.clear();
		File[] files = fileHandler.scanFolder(".zip", modelPath);
		if(files != null)
			for(File file : files) 
				list.add(file.getName().replace(".zip", ""));
	}

	/**
	 * When we click on the table view, e.g on a row, then we going to load a model
	 * @param event Mouse event
	 */
	@FXML
    void clickedOnTableView(MouseEvent event) {
		String modelName = tableView.getSelectionModel().getSelectedItem();
		if(modelName == null)
			return; // No selected model
		dL4JModel.loadModel(modelPath + modelName + ".zip", true);
    }
	
	
}