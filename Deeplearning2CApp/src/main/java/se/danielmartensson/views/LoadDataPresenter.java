package se.danielmartensson.views;

import java.io.File;
import java.io.IOException;

import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CardPane;
import com.gluonhq.charm.glisten.control.DropdownButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.stage.Screen;
import se.danielmartensson.deeplearning.DL4JData;
import se.danielmartensson.tools.CSVHandler;
import se.danielmartensson.tools.Dialogs;
import se.danielmartensson.tools.FileHandler;

public class LoadDataPresenter {

	@FXML
    private View loaddata;

    @FXML
    private DropdownButton trainDataDropDown;

    @FXML
    private DropdownButton evalDataDropDown;

    @FXML
    private DropdownButton dataTypeDropDown;

    @FXML
    private DropdownButton delimiterDropDown;

    @FXML
    private DropdownButton batchSizeDropDown;

    @FXML
    private DropdownButton labelIndexFromDropDown;

    @FXML
    private DropdownButton labelIndexToDropDown;

    @FXML
    private DropdownButton numPossibleLabelsDropDown;

    @FXML
    private DropdownButton datanormalizerDropDown;

    @FXML
    private Label labelIndexFromLabel;

    @FXML
    private Label labelIndexToLabel;

    @FXML
    private Label possibleLabelsLabel;
    
    @FXML
    private CardPane<?> leftCardPane;
    
    @FXML
    private CardPane<?> rightCardPane;
    
    @FXML
    private Button executeButton;
    
    @FXML
    private ColumnConstraints gridPane0;

    @FXML
    private ColumnConstraints gridPane1;

	private DL4JData dL4JData;

	private FileHandler fileHandler;
	
	private final String pathCSVFolder = "/Deeplearning2CStorage/CSV/";
	private final String fileExtension = ".csv";

	private MenuItem dataTypeMenuItem;

	private MenuItem delimiterMenuItem;

	private MenuItem trainDataMenuItem;

	private MenuItem evalDataMenuItem;

	private MenuItem batchSizeMenuItem;

	private MenuItem labelIndexFromMenuItem;

	private MenuItem labelIndexToMenuItem;

	private MenuItem numPossibleLabelsMenuItem;

	private MenuItem datanormalizerMenuItem;

	private CSVHandler cSVHandler;

	private Dialogs dialogs;

	public void initialize() {
		loaddata.setShowTransitionFactory(BounceInRightTransition::new);
		fileHandler = new FileHandler();
		dialogs = new Dialogs();

		loaddata.showingProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				AppBar appBar = MobileApplication.getInstance().getAppBar();
				appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> MobileApplication.getInstance().getDrawer().open()));
				appBar.setTitleText("Load Data");
				
			}
	
			/*
			 * Load all CSV files once when we slide into this page
			 */
			File files[] = fileHandler.scanFolder(fileExtension, pathCSVFolder);
			trainDataDropDown.getItems().clear();
			evalDataDropDown.getItems().clear();
			for(File file : files) {
				trainDataDropDown.getItems().addAll(new MenuItem(file.getName()));
				evalDataDropDown.getItems().addAll(new MenuItem(file.getName()));
			}
			
			/*
			 * Select the menu items we had been saving before, when we slide into this page
			 */
			insertSavings(trainDataMenuItem, trainDataDropDown);
			insertSavings(evalDataMenuItem, evalDataDropDown);
			insertSavings(delimiterMenuItem, delimiterDropDown);
			insertSavings(dataTypeMenuItem, dataTypeDropDown);
			insertSavings(batchSizeMenuItem, batchSizeDropDown);
			insertSavings(labelIndexFromMenuItem, labelIndexFromDropDown);
			insertSavings(labelIndexToMenuItem, labelIndexToDropDown);
			insertSavings(numPossibleLabelsMenuItem, numPossibleLabelsDropDown);
			insertSavings(datanormalizerMenuItem, datanormalizerDropDown);
		});
		
		/*
		 * Change the components to correct size - Only need once!
		 */
		double heightScreen = Screen.getPrimary().getBounds().getHeight();
		double widthScreen = Screen.getPrimary().getBounds().getWidth();
		loaddata.setPrefSize(widthScreen, heightScreen);
		gridPane0.setPrefWidth(widthScreen*0.1);
		gridPane1.setPrefWidth(widthScreen*0.9);
		executeButton.setPrefWidth(widthScreen*0.9); // Long button


		/*
		 * Connect to our DL4J classes and its functionality
		 */
		Platform.runLater(() -> {
			ApplicationContext context = new FileSystemXmlApplicationContext("/src/main/resources/se/danielmartensson/beans/DL4JBeans.xml");
			dL4JData = context.getBean("dL4JData", DL4JData.class); // We only need this object from Spring
			((FileSystemXmlApplicationContext) context).close();
		});
		
		/*
		 * Listener for data type 
		 */
		dataTypeDropDown.selectedItemProperty().addListener(e->{
			dataTypeMenuItem = dataTypeDropDown.getSelectedItem(); // Save
			hideLabels();
		});
		
		/*
		 * Listener for delimiter
		 */
		delimiterDropDown.selectedItemProperty().addListener(e->{
			delimiterMenuItem = delimiterDropDown.getSelectedItem(); // Save
			reloadBatchLabels();
		});
		
		/*
		 * Listener for train data 
		 */
		trainDataDropDown.selectedItemProperty().addListener(e->{
			trainDataMenuItem = trainDataDropDown.getSelectedItem(); // Save
			reloadBatchLabels();
		});
		
		/*
		 * Listener for eval data
		 */
		evalDataDropDown.selectedItemProperty().addListener(e->{
			evalDataMenuItem = evalDataDropDown.getSelectedItem(); // Save
		});
		
		/*
		 * Listener for batch size
		 */
		batchSizeDropDown.selectedItemProperty().addListener(e->{
			batchSizeMenuItem = batchSizeDropDown.getSelectedItem(); // Save
		});
		
		/*
		 * Listener for label index from
		 */
		labelIndexFromDropDown.selectedItemProperty().addListener(e->{
			labelIndexFromMenuItem = labelIndexFromDropDown.getSelectedItem(); // Save
		});
		
		/*
		 * Listener for label index to
		 */
		labelIndexToDropDown.selectedItemProperty().addListener(e->{
			labelIndexToMenuItem = labelIndexToDropDown.getSelectedItem(); // Save
		});
		
		/*
		 * Listener for possible labels
		 */
		numPossibleLabelsDropDown.selectedItemProperty().addListener(e->{
			numPossibleLabelsMenuItem = numPossibleLabelsDropDown.getSelectedItem(); // Save
		});
		
		/*
		 * Listener for data normalization
		 */
		datanormalizerDropDown.selectedItemProperty().addListener(e->{
			datanormalizerMenuItem = datanormalizerDropDown.getSelectedItem(); // Save
		});
		
		/*
		 * Set static values - Never going to change if DL4J not change
		 */
		dataTypeDropDown.getItems().addAll(new MenuItem("Regression"), new MenuItem("Classification"));
		delimiterDropDown.getItems().addAll(new MenuItem(";"), new MenuItem(","));
		datanormalizerDropDown.getItems().addAll(new MenuItem("NormalizerStandardize"), new MenuItem("None"));
		
	}

	/**
	 * This saves a lot of lines.
	 * The purpose with this method is to insert the menuItem again if we go back to this page.
	 */
	private void insertSavings(MenuItem menuItem, DropdownButton dropDown) {
		if(menuItem != null) 
			dropDown.setSelectedItem(menuItem);
	}

	/**
	 * This method will hide possible labels or label index to depending if we
	 * select regression or classification
	 */
	private void hideLabels() {
		if(dataTypeMenuItem.getText().equals("Regression") == true) {
			numPossibleLabelsDropDown.setDisable(true); // Using regression, we don't need possible labels 
			labelIndexToDropDown.setDisable(false);
			labelIndexFromLabel.setText("Label index from:");
			labelIndexToLabel.setText("Label index to:");
			possibleLabelsLabel.setText("-");
		}else {
			numPossibleLabelsDropDown.setDisable(false);
			labelIndexToDropDown.setDisable(true); // Using classification, we don't need label index to
			labelIndexFromLabel.setText("Label index:");
			labelIndexToLabel.setText("-");
			possibleLabelsLabel.setText("Possible labels:");
		}
	}

	/**
	 * Reload the batch size and labels if we change the train data or delimiter
	 */
	private void reloadBatchLabels() {
		/*
		 * Get rows and columns of selected training data
		 */
		if(trainDataMenuItem != null && delimiterMenuItem != null) {
			String trainDatafileName = trainDataMenuItem.getText();
			String delimiter = delimiterMenuItem.getText();
			cSVHandler = new CSVHandler(fileHandler, pathCSVFolder + trainDatafileName, delimiter);
			int rows = cSVHandler.getTotalRows();
			int columns = cSVHandler.getTotalColumns();
			
			/*
			 * Label index from size and label index to can be as long as total columns in the CSV file
			 */
			labelIndexFromDropDown.getItems().clear();
			labelIndexToDropDown.getItems().clear();
			for(int i = 0; i < columns; i++) {
				labelIndexFromDropDown.getItems().add(new MenuItem(String.valueOf(i)));
				labelIndexToDropDown.getItems().add(new MenuItem(String.valueOf(i)));
			}
			
			/*
			 * Batch size and possible labels can be as long as total rows in the CSV file
			 */
			batchSizeDropDown.getItems().clear();
			numPossibleLabelsDropDown.getItems().clear();
			for(int i = 0; i < rows; i++) {
				batchSizeDropDown.getItems().add(new MenuItem(String.valueOf(i)));
				numPossibleLabelsDropDown.getItems().add(new MenuItem(String.valueOf(i)));
			}
		}
		
	}

	/*
	 * Finally we create our data!
	 */
	@FXML
    void execute(ActionEvent event) {
		/*
		 * Is everything OK?
		 * delimiterMenuItem, fileHandler, datanormalizerMenuItem is always non-null
		 */
		if(fileHandler != null && delimiterMenuItem != null && batchSizeMenuItem != null && labelIndexFromMenuItem != null && labelIndexToMenuItem != null && numPossibleLabelsMenuItem != null && dataTypeMenuItem != null && trainDataMenuItem != null && evalDataMenuItem != null) {
			/*
			 * Get all data now and load it
			 */
			String trainDatafileName = trainDataMenuItem.getText();
			String evalDatafileName = evalDataMenuItem.getText();
			File trainDataFile = fileHandler.loadFile(pathCSVFolder + trainDatafileName);
			File evalDataFile = fileHandler.loadFile(pathCSVFolder + evalDatafileName);
			char delimiter = delimiterMenuItem.getText().charAt(0);
			int batchSize = Integer.parseInt(batchSizeMenuItem.getText());
			int labelIndexFrom = Integer.parseInt(labelIndexFromMenuItem.getText());
			int labelIndexTo = Integer.parseInt(labelIndexToMenuItem.getText());
			int numPossibleLabels = Integer.parseInt(numPossibleLabelsMenuItem.getText());
			boolean regression = false;
			if(dataTypeMenuItem.getText().equals("Regression") == true) {
				regression = true; // Regression data
			}else {
				regression = false; // Classification data
			}
			try {
				/*
				 * Load train and eval data
				 */
				dL4JData.loadTrainData(trainDataFile, delimiter, batchSize, labelIndexFrom, labelIndexTo, numPossibleLabels, regression);
				dL4JData.loadEvalData(evalDataFile, delimiter, batchSize, labelIndexFrom, labelIndexTo, numPossibleLabels, regression);
			
				/*
				 * Normalize our data if needed
				 */
				if(datanormalizerMenuItem.getText().equals("NormalizerStandardize") == true) {
					dL4JData.normalization(new NormalizerStandardize());
				}
				
				/*
				 * Print success!
				 */
				dialogs.alertDialog(AlertType.INFORMATION, "Success", "Data created to the model!");
			} catch (IOException | IndexOutOfBoundsException | InterruptedException | NumberFormatException | IllegalStateException e) {
				dialogs.exception("Could not load data to model!", e);
			}

		}else {
			dialogs.alertDialog(AlertType.INFORMATION, "Missing input", "Do you have forgot something?");
		}
    }


}
