package se.danielmartensson.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import se.danielmartensson.tools.Dialogs;
import se.danielmartensson.tools.FileHandler;

public class LoadDataPresenter {

	@FXML
    private View loaddata;

    @FXML
    private ColumnConstraints gridPane0;

    @FXML
    private ColumnConstraints gridPane1;

    @FXML
    private CardPane<?> rightCardPane;

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
    private Button scanButton;

    @FXML
    private Button executeButton;

    @FXML
    private CardPane<?> leftCardPane;

    @FXML
    private Label labelIndexFromLabel;

    @FXML
    private Label labelIndexToLabel;

    @FXML
    private Label possibleLabelsLabel;

	private DL4JData dL4JData;

	private FileHandler fileHandler = new FileHandler();
	
	private final String pathCSVFolder = "/Deeplearning2CStorage/CSV/";
	private final String fileExtension = ".csv";

	private Dialogs dialogs = new Dialogs();

	public void initialize() {
		loaddata.setShowTransitionFactory(BounceInRightTransition::new);

		loaddata.showingProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue) {
				AppBar appBar = MobileApplication.getInstance().getAppBar();
				appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> MobileApplication.getInstance().getDrawer().open()));
				appBar.setTitleText("Load Data");
			}
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
		scanButton.setPrefWidth(widthScreen*0.9); // Long button

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
		dataTypeDropDown.selectedItemProperty().addListener(e -> hideLabels());
		
		/*
		 * Add initial menu items to drop downs
		 */
		dataTypeDropDown.getItems().addAll(new MenuItem("Regression"), new MenuItem("Classification"));
		delimiterDropDown.getItems().addAll(new MenuItem(";"), new MenuItem(","));
		datanormalizerDropDown.getItems().addAll(new MenuItem("NormalizerStandardize"), new MenuItem("None"));
		batchSizeDropDown.getItems().add(new MenuItem("0"));
		labelIndexFromDropDown.getItems().add(new MenuItem("0"));
		labelIndexToDropDown.getItems().add(new MenuItem("0"));
		numPossibleLabelsDropDown.getItems().add(new MenuItem("0"));
		
	}

	/**
	 * This method will hide possible labels or label index to depending if we
	 * select regression or classification
	 */
	private void hideLabels() {
		if(dataTypeDropDown.getSelectedItem().getText().equals("Regression") == true) {
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
	private void reloadBatchAndLabels() {
		/*
		 * Get the data in the csv handler
		 */
		String trainDatafileName = trainDataDropDown.getSelectedItem().getText(); 
		String delimiter = delimiterDropDown.getSelectedItem().getText();
		List<List<String>> records = new ArrayList<>();
		File file = fileHandler.loadFile(pathCSVFolder + trainDatafileName);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		        String[] values = line.split(delimiter);
		        records.add(Arrays.asList(values));
		    }
		    br.close();
		}catch(IOException e) {
			dialogs.exception("Cannot open CSV file: \n" + trainDatafileName, e);
		}
			
		/*
		 * Label index from and label index to  can be as long as total columns in the CSV file
		 */
		insert(labelIndexFromDropDown, labelIndexToDropDown, records.get(0).size()); // Column size on row 0
			
		/*
		 * Batch size and possible labels can be as long as total rows in the CSV file
		 */
		insert(batchSizeDropDown, numPossibleLabelsDropDown, records.size()); // Row size
		
		records.clear();
	}
	
	/**
	 * Insert new menu items depending after they have been cleared out
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
	 * Scan for CSV data
	 * @param event
	 */
	@FXML
    void scan(ActionEvent event) {
		/*
		 * Load all CSV files once when we first time slide into this page
		 */
		File files[] = fileHandler.scanFolder(fileExtension, pathCSVFolder);
		for(File file : files) {
			trainDataDropDown.getItems().addAll(new MenuItem(file.getName()));
			evalDataDropDown.getItems().addAll(new MenuItem(file.getName()));
		}
		
		/*
		 * Warning if we missing data
		 */
		if(files.length == 0)
			dialogs.alertDialog(AlertType.WARNING, "Missing", "You need to add CSV data.");
		else 
			reloadBatchAndLabels();
    }

	/**
	 * Finally we create our data!
	 * @param event
	 */
	@FXML
    void execute(ActionEvent event) {
		if (trainDataDropDown.getSelectedItem() != null && evalDataDropDown.getSelectedItem() != null) {
			/*
			 * Get all data now and load it
			 */
			String trainDatafileName = trainDataDropDown.getSelectedItem().getText();
			String evalDatafileName = evalDataDropDown.getSelectedItem().getText();
			String dataType = dataTypeDropDown.getSelectedItem().getText();
			File trainDataFile = fileHandler.loadFile(pathCSVFolder + trainDatafileName);
			File evalDataFile = fileHandler.loadFile(pathCSVFolder + evalDatafileName);
			char delimiter = delimiterDropDown.getSelectedItem().getText().charAt(0);
			int batchSize = Integer.parseInt(batchSizeDropDown.getSelectedItem().getText());
			int labelIndexFrom = Integer.parseInt(labelIndexFromDropDown.getSelectedItem().getText());
			int labelIndexTo = Integer.parseInt(labelIndexToDropDown.getSelectedItem().getText());
			if(labelIndexFrom > labelIndexTo) {
				dialogs.alertDialog(AlertType.WARNING, "Label", "Lable from cannot be larger than label to");
				return;
			}
			int numPossibleLabels = Integer.parseInt(numPossibleLabelsDropDown.getSelectedItem().getText());
			String dataNormalizer = datanormalizerDropDown.getSelectedItem().getText();
			boolean regression = false;
			if(dataType.equals("Regression") == true) {
				regression = true; // Regression data
			}else {
				regression = false; // Classification data
			}
			
			/*
			 * Load train and eval data
			 */
			try {
				dL4JData.loadTrainData(trainDataFile, delimiter, batchSize, labelIndexFrom, labelIndexTo, numPossibleLabels, regression);
				dL4JData.loadEvalData(evalDataFile, delimiter, batchSize, labelIndexFrom, labelIndexTo, numPossibleLabels, regression);
			
				/*
				 * Normalize our data if needed
				 */
				if(dataNormalizer.equals("NormalizerStandardize") == true) {
					dL4JData.normalization(new NormalizerStandardize());
				}
				
				/*
				 * Print success!
				 */
				dialogs.alertDialog(AlertType.INFORMATION, "Success", "Data created to the model!");
			
			} catch (IOException | IndexOutOfBoundsException | InterruptedException | IllegalStateException | IllegalArgumentException e) {
				dialogs.exception("Could not load data to model!", e);
			}
		}else {
			dialogs.alertDialog(AlertType.INFORMATION, "Missing", "Do you have forgot something?");
		}
    }
}
