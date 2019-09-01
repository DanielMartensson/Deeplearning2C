package se.danielmartensson.views;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

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
    private final String modelPath = "/Deeplearning2CStorage/model/";
    private final String cPath = "/Deeplearning2CStorage/cgeneration/";
	private ObservableList<String> list;

    public void initialize() {
    	/*
		 * Dependency injection
		 */
		dL4JModel = SimpleDependencyInjection.getDL4JModel();
		
    	/*
    	 * Run this test so we make sure that we can create and delete files
    	 */
    	fileHandler.runCreateDeleteTest(modelPath + "test/test" + ".zip");
    	
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
		int totalFiles = fileHandler.scanFolderNames(modelPath).length;
		if(totalFiles > 0) {
			updateTableView();
			tableView.getSelectionModel().selectFirst();
			String modelName = tableView.getSelectionModel().getSelectedItem();
			dL4JModel.loadModel(modelPath + modelName + "/" + modelName + ".zip", false);
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
    	int totalFiles = fileHandler.scanFolderNames(modelPath).length;
    	fileHandler.deleteFolder(modelPath + modelName + "/");
    	fileHandler.deleteFolder(cPath + modelName + "/BLAS/");
    	fileHandler.deleteFolder(cPath + modelName + "/");
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
			dL4JModel.saveModel(modelPath + modelName + "/" + modelName + ".zip");
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
			dialogs.alertDialog(AlertType.INFORMATION, "Empty name", "Select another file name."); 
			return;
		}
		
		if(modelName.matches("[a-zA-Z0-9]*") == false) {
			dialogs.alertDialog(AlertType.INFORMATION, "Only numbers and letters", "Select another file name."); 
			return; // Contains more than letters and numbers? Return then.
		}
		
		if(Character.isDigit(modelName.charAt(0)) == true) {
			dialogs.alertDialog(AlertType.INFORMATION, "Model name cannot start with a number", "Select another file name.");
			return; // Model name have a number at start. Not valid for C-code function names.
		}
		/*
		 * Create the file and update the table and then select last row
		 */
		fileHandler.createNewFile(modelPath + modelName + "/" + modelName + ".zip", false);
		updateTableView();
		tableView.getSelectionModel().selectLast();
		
		/*
		 * Insert a real basic model into that .zip file we just created. Then save it.
		 */
		dL4JModel.createBasicModel(modelPath + modelName + "/" + modelName + ".zip");
		
		/*
		 * Create new empty C-files
		 */
		fileHandler.createNewFile(cPath + modelName + "/" + modelName + ".c", true);
		fileHandler.createNewFile(cPath + modelName + "/" + modelName + ".h", true);
		
		/*
		 * Copy these files from blas folder by using input stream
		 */
		String[] blasFileNames = {"activation.c", "f2c.h", "functions.h", "lsame.c", "sgemv_.c", "xerbla_.c"};
		for(String blasFileName : blasFileNames) {
			InputStream inputStream = this.getClass().getResourceAsStream("blas/" + blasFileName);
			String destinationPath = cPath + modelName + "/BLAS/" + blasFileName;
			fileHandler.createNewFile(destinationPath, true);
			File file = fileHandler.loadNewFile(destinationPath);
			try {
				FileUtils.copyInputStreamToFile(inputStream, file);
			} catch (IOException e) {
				dialogs.exception("Cannot move BLAS-files from resource folder", e);
			}
		}
	}


	/**
	 * This method will update the table view
	 */
	private void updateTableView() {
		list.clear();
		File[] files = fileHandler.scanFolderNames(modelPath);
		if(files != null)
			for(File file : files)
				list.add(file.getName());
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
		dL4JModel.loadModel(modelPath + modelName + "/" + modelName + ".zip", true);
    }
	
	
}