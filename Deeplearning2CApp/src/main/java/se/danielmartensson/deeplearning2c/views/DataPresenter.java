package se.danielmartensson.deeplearning2c.views;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.DropdownButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Screen;
import se.danielmartensson.deeplearning2c.deeplearning.DL4JModel;
import se.danielmartensson.deeplearning2c.tools.Dialogs;
import se.danielmartensson.deeplearning2c.tools.FileHandler;
import se.danielmartensson.deeplearning2c.tools.SimpleDependencyInjection;
import javafx.scene.control.Alert.AlertType;

public class DataPresenter {

    @FXML
    private View view;

    @FXML
    private Label labelIndexFromLabel;

    @FXML
    private Label labelIndexToLabel;

    @FXML
    private Label possibleLabelsLabel;

    @FXML
    private DropdownButton csvDropdownButton;

    @FXML
    private DropdownButton delimiterDropdownButton;

    @FXML
    private DropdownButton dataTypeDropdownButton;

    @FXML
    private DropdownButton batchSizeDropdownButton;

    @FXML
    private DropdownButton labelIndexFromDropDownButton;

    @FXML
    private DropdownButton labelIndexToDropdownButton;

    @FXML
    private DropdownButton possibleLabelsDropdownButton;

    @FXML
    private Button executeData;
    
    @FXML
    private DropdownButton minScalarDropdownButton;

    @FXML
    private DropdownButton maxScalarDropdownButton;

	private AppBar appBar;
	
	// Tools
    private FileHandler fileHandler = new FileHandler();
    private Dialogs dialogs = new Dialogs();
    private final String dataPath = "/Deeplearning2CStorage/data/";
	
	// Injected as static
    private DL4JModel dL4JModel;

    @FXML
    void initialize() {
    	// Dependency injection
		dL4JModel = SimpleDependencyInjection.getDL4JModel();
    	
    	// Slide smooth in and out
    	view.setShowTransitionFactory(BounceInRightTransition::new);
    	
    	// Listener for leaving and enter the page
    	view.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
            	// Enter the page
                appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> MobileApplication.getInstance().getDrawer().open()));
                appBar.setTitleText("Data");
                appBar.getActionItems().add(MaterialDesignIcon.BUILD.button(e -> executeAction()));
        		appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button(e -> {
        			// Reload data
                    if(reloadData() == false)
            			dialogs.alertDialog(AlertType.INFORMATION, "No data", "You have no CSV files in folder");
        		}));
               
                
            }else {
            	// Leaving the page
            }
        });
    	
    	// Change the grids and view to correct size
		double heightScreen = Screen.getPrimary().getBounds().getHeight();
		double widthScreen = Screen.getPrimary().getBounds().getWidth();
		view.setPrefSize(widthScreen, heightScreen);
		
		// Set some default values for data type drop down button and delimiter drop down button
		dataTypeDropdownButton.getItems().addAll(new MenuItem("Regression"), new MenuItem("Classification"));
		delimiterDropdownButton.getItems().addAll(new MenuItem(","), new MenuItem(";"));
		batchSizeDropdownButton.getItems().add(new MenuItem("0"));
		labelIndexFromDropDownButton.getItems().add(new MenuItem("0"));
		labelIndexToDropdownButton.getItems().add(new MenuItem("0"));
		minScalarDropdownButton.getItems().addAll(new MenuItem("-1"), new MenuItem("0"));
		maxScalarDropdownButton.getItems().addAll(new MenuItem("1"), new MenuItem("0"));
		
		// Listener for labels and drop down buttons
		dataTypeDropdownButton.selectedItemProperty().addListener(e -> {
			if(dataTypeDropdownButton.getSelectedItem().getText().equals("Regression") == true) {
				// Change labels
				labelIndexToLabel.setText("Label index to:");
				labelIndexFromLabel.setText("Label index from:");
				possibleLabelsLabel.setText("");
				
				// Change drop down buttons
				possibleLabelsDropdownButton.setDisable(true);
				labelIndexFromDropDownButton.setDisable(false);
				
			}else {
				// Change labels
				labelIndexFromLabel.setText("Label index:");
				labelIndexToLabel.setText("");
				possibleLabelsLabel.setText("Possible labels:");
				
				// Change drop down buttons
				possibleLabelsDropdownButton.setDisable(false);
				labelIndexToDropdownButton.setDisable(true);
			}
				
		});
		
    }
    
    /**
     * Reload the data drop down button, batch size and labels
     */
    private boolean reloadData() {
		File[] files = fileHandler.scanFolder(".csv", dataPath);
		if(files.length == 0)
			return false;
		csvDropdownButton.getItems().clear();
		for(File file : files)
			csvDropdownButton.getItems().add(new MenuItem(file.getName()));
		
		// Get the dimensions of the CSV data
		String dataFileName = csvDropdownButton.getSelectedItem().getText(); 
		String delimiter = delimiterDropdownButton.getSelectedItem().getText();
		List<List<String>> records = new ArrayList<>();
		File file = fileHandler.loadNewFile(dataPath + dataFileName);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        String[] values = line.split(delimiter);
		        records.add(Arrays.asList(values));
		    }
		    br.close();
		}catch(IOException e) {
			dialogs.exception("Cannot open CSV file: \n" + dataFileName, e);
		}
		
		// Label index from and label index to  can be as long as total columns in the CSV file
		insert(labelIndexFromDropDownButton, labelIndexToDropdownButton, records.get(0).size()); // Column size on row 0
			
		// Batch size and possible labels can be as long as total rows in the CSV file
		insert(batchSizeDropdownButton, possibleLabelsDropdownButton, records.size() + 1); // Row size - We add +1 due to the batch size
		
		// Success!
		return true;
	}
    
    /**
     * Insert new menu items depending after they have been cleared out
     * @param dropDownButton1
     * @param dropDownButton2
     * @param length
     */
	private void insert(DropdownButton dropDownButton1, DropdownButton dropDownButton2, int length) {
		dropDownButton1.getItems().clear();
		dropDownButton2.getItems().clear();
		for(int i = 0; i < length; i++) {
			dropDownButton1.getItems().add(new MenuItem(String.valueOf(i)));
			dropDownButton2.getItems().add(new MenuItem(String.valueOf(i)));
		}
	}

	
	/**
	 * This methods create the data set iteration 
	 */
    public void executeAction() {
		if (csvDropdownButton.getSelectedItem() != null) {
			// Get all data now and load it
			String csvDataFileName = csvDropdownButton.getSelectedItem().getText();
			String dataType = dataTypeDropdownButton.getSelectedItem().getText();
			File dataFile = fileHandler.loadNewFile(dataPath + csvDataFileName);
			char delimiter = delimiterDropdownButton.getSelectedItem().getText().charAt(0);
			int batchSize = Integer.parseInt(batchSizeDropdownButton.getSelectedItem().getText());
			int labelIndexFrom = Integer.parseInt(labelIndexFromDropDownButton.getSelectedItem().getText());
			int labelIndexTo = Integer.parseInt(labelIndexToDropdownButton.getSelectedItem().getText());
			int minScalar = Integer.parseInt(minScalarDropdownButton.getSelectedItem().getText());
			int maxScalar = Integer.parseInt(minScalarDropdownButton.getSelectedItem().getText());
			int numPossibleLabels = Integer.parseInt(possibleLabelsDropdownButton.getSelectedItem().getText());
			boolean regression = false;
			if(dataType.equals("Regression") == true) {
				regression = true; // Regression data
			}else {
				regression = false; // Classification data
			}
			if(labelIndexFrom > labelIndexTo && regression == true) {
				dialogs.alertDialog(AlertType.WARNING, "Label", "Label from cannot be larger than label to");
				return;
			}
			
			// Load train and eval data
			try {
				dL4JModel.getDL4JData().loadData(dataFile, delimiter, batchSize, labelIndexFrom, labelIndexTo, numPossibleLabels, regression, minScalar, maxScalar);
				dL4JModel.getDL4JData().setRegression(regression);
				dialogs.alertDialog(AlertType.INFORMATION, "Success", "Data iteration set created!");
			}catch(IOException |  InterruptedException | RuntimeException e) {
				dialogs.exception("Could not create data iteration set!", e);
			}
		}else {
			dialogs.alertDialog(AlertType.INFORMATION, "No file", "No CSV file where elected");
		}
    }
}