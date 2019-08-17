package se.danielmartensson.views;

import java.io.File;
import java.util.List;

import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import se.danielmartensson.containers.NetworkTableContainer;
import se.danielmartensson.deeplearning.DL4JGlobalConfig;
import se.danielmartensson.deeplearning.DL4JLayers;
import se.danielmartensson.deeplearning.DL4JModel;
import se.danielmartensson.tools.CSVHandler;
import se.danielmartensson.tools.Dialogs;
import se.danielmartensson.tools.FileHandler;

public class NeuralNetworksPresenter {
	@FXML
	private View neuralnetworks;

	@FXML
	private TableView<NetworkTableContainer> networkTable;

	@FXML
	private TableColumn<NetworkTableContainer, String> columnName;

	@FXML
	private TableColumn<NetworkTableContainer, String> columnNetwork;

	@FXML
	private TableColumn<NetworkTableContainer, String> columnTrained;

	@FXML
	private TableColumn<NetworkTableContainer, String> columnAccuracy;

	private final String modelPath = "/Deeplearning2CStorage/Models/";
	private final String delimiter = ",";
	private DL4JGlobalConfig dL4JGlobalConfig;
	private DL4JLayers dL4JLayers;
	private DL4JModel dL4JModel;
	private Dialogs dialogs = new Dialogs();
	private CSVHandler networkTableCSV;
	ObservableList<NetworkTableContainer> list;
	private FileHandler fileHandler;
	private Integer selectedRowIndex = null;

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
				double heightBar = appBar.heightProperty().get();
				double heightScreen = Screen.getPrimary().getBounds().getHeight();
				double widthScreen = Screen.getPrimary().getBounds().getWidth();
				neuralnetworks.setPrefSize(widthScreen, heightScreen);
				networkTable.setPrefSize(widthScreen, heightScreen - heightBar);

				/*
				 * Load the table if we have loaded the table before
				 */
				if (list != null) {
					reloadTable();
				}

				/*
				 * Methods for the icons inside the appBar
				 */
				appBar.getActionItems().add(MaterialDesignIcon.CREATE.button(e -> createNewTableRow()));
				appBar.getActionItems().add(MaterialDesignIcon.SAVE.button(e -> saveModel()));
				appBar.getActionItems().add(MaterialDesignIcon.DELETE.button(e -> deleteModel()));
				
				/*
				 * When we entry this page - Select the past row index
				 */
				if(selectedRowIndex != null) 
					networkTable.getSelectionModel().select(selectedRowIndex);
				else
					try {
						if(networkTableCSV.getTotalRows() == 0) // Zero rows = selectedRowIndex is null
							selectedRowIndex = 0;
					}catch(NullPointerException e) {
						selectedRowIndex = 0; // networkTableCSV not init yet. Happens only in the beginning of start up
					}
					
			}else {
				/*
				 * When we leave this page - Get the index of selected row
				 */
				if(networkTable.getSelectionModel().getSelectedItem() != null)
					selectedRowIndex = networkTable.getSelectionModel().getSelectedIndex();
				else
					selectedRowIndex = null; // Empty table
			}
				
		});

		/*
		 * Set the columns in the table view
		 */
		columnName.setCellValueFactory(new PropertyValueFactory<NetworkTableContainer, String>("name"));
		columnNetwork.setCellValueFactory(new PropertyValueFactory<NetworkTableContainer, String>("network"));
		columnTrained.setCellValueFactory(new PropertyValueFactory<NetworkTableContainer, String>("trained"));
		columnAccuracy.setCellValueFactory(new PropertyValueFactory<NetworkTableContainer, String>("accuracy"));

		/*
		 * Load DL4J and CSV
		 */
		Platform.runLater(() -> {
			String[] beans = {
					"/src/main/resources/se/danielmartensson/beans/DL4JBeans.xml",
					"/src/main/resources/se/danielmartensson/beans/FileBeans.xml"};
			ApplicationContext context = new FileSystemXmlApplicationContext(beans);
			dL4JGlobalConfig = context.getBean("dL4JGlobalConfig", DL4JGlobalConfig.class);
			dL4JLayers = context.getBean("dL4JLayers", DL4JLayers.class);
			dL4JModel = context.getBean("dL4JModel", DL4JModel.class);
			networkTableCSV = context.getBean("networkTableCSV", CSVHandler.class);
			fileHandler = context.getBean("fileHandler", FileHandler.class);
			list = FXCollections.observableArrayList();
			networkTable.setItems(list);
			((FileSystemXmlApplicationContext) context).close();
			reloadTable();

			/*
			 * This initial selection in the table at the start up of the application
			 */
			if (networkTableCSV.getTotalRows() > 0) {
				/*
				 * We must create a model, save it and then delete it so we can have
				 * access to the file system. Access to the file system is given by
				 * file.createNewFile(); original java method
				 */
				if(createModel("0") == true) {
					reloadTable();
					networkTable.getSelectionModel().selectLast();
					selectedRowIndex = networkTable.getSelectionModel().getSelectedIndex();
					File file = modelSelectionTable();
					deleteNow(file);
				}

				/*
				 * Select first row and use that model
				 */
				networkTable.getSelectionModel().select(0); 
				useModel();
			}
		});
		
	}

	/**
	 * Select one row at the table view
	 */
	private void useModel() {
		System.out.println("useModel");
		System.out.println("useModel + selectedRowIndex = " + selectedRowIndex);
		File file = modelSelectionTable();
		if (file != null) // File exist
			if (dL4JModel.loadModel(file) == true) {
				/*
				 * Load from the CSV
				 */
				String modelName = file.getName().replace(".zip", "");
				int rowNumber = networkTableCSV.findRow(modelName, "Name");
				System.out.println("Set values...");
				List<String> cells = networkTableCSV.getRow(rowNumber);
				String seed = cells.get(4);
				String optimizationAlgorithm = cells.get(5);
				String updater = cells.get(6);
				String learningRate = cells.get(7);
				String momentum = cells.get(8);
				String regularization = cells.get(9);
				String coefficent = cells.get(10);
				String weight = cells.get(11);
				System.out.println("Use model...use algo: " + optimizationAlgorithm);
				dL4JGlobalConfig.setSeed(seed);
				dL4JGlobalConfig.setOptimizationAlgorithm(optimizationAlgorithm);
				dL4JGlobalConfig.setUpdater(updater, learningRate, momentum);
				dL4JGlobalConfig.setRegularization(regularization, coefficent);
				dL4JGlobalConfig.setWeightInit(weight); 
				selectedRowIndex = networkTable.getSelectionModel().getSelectedIndex(); // Save
			}else
				dialogs.alertDialog(AlertType.ERROR, "Loading", "Could not load model!");
	}

	/**
	 * Create a new table row inside the table view
	 */
	private void createNewTableRow() {
		String modelName = dialogs.input("New", "Enter new name");
		if (modelName.contains(delimiter) == false && modelName.equals("") == false && modelName.equals("0") == false) {
			boolean exist = networkTableCSV.exist(modelName, "Name");
			System.out.println("Model exist? :" + exist);
			if (exist == false) {
				if (createModel(modelName) == true) {
					reloadTable();
					networkTable.getSelectionModel().selectLast();
					selectedRowIndex = networkTable.getSelectionModel().getSelectedIndex(); 
				} else {
					dialogs.alertDialog(AlertType.ERROR, "Creation", "Could not create model!");
				}
			} else {
				dialogs.alertDialog(AlertType.ERROR, "Exist", "File name already exist.");
			}
		} else {
			if (modelName.contains(",")) 
				dialogs.alertDialog(AlertType.ERROR, "Cannot use ','", "Select another file name.");
			else if (modelName.contains("0")) 
				dialogs.alertDialog(AlertType.ERROR, "Cannot use '0'", "Select another file name.");
			else
				dialogs.alertDialog(AlertType.INFORMATION, "Missing name", "Select a file name");
		}
	}

	/**
	 * Create an initial basic model so we can save it
	 * Note that createModel saves to the CSV file GlobalConfig.csv
	 * 
	 * @param modelName Name of the model
	 * @return boolean
	 */
	private boolean createModel(String modelName) {
		/*
		 * Global configuration
		 * CSV file follows linear with the GUI globalconfigration.fxml drop down components
		 */
		dL4JGlobalConfig.setSeed("100");
		dL4JGlobalConfig.setOptimizationAlgorithm("STOCHASTIC_GRADIENT_DESCENT");
		dL4JGlobalConfig.setUpdater("Sgd", "0.1", "0.5");
		dL4JGlobalConfig.setRegularization("L1", "1e-04");
		dL4JGlobalConfig.setWeightInit("XAVIER");
		
		/*
		 * Layer configuration
		 */
		dL4JLayers.addDenseLayer(3, 3, Activation.SOFTMAX);
		dL4JLayers.addOutputLayer(3, 3, Activation.SOFTMAX, LossFunction.NEGATIVELOGLIKELIHOOD);
		
		/*
		 * Create model and save it and its global parameters and also the table views
		 * Also don't forget to update loadGlobalConfiguration() in GlobalConfigurationPresenter.java
		 * if you update the globalConfig.CSV.newRow() line below
		 */
		dL4JModel.buildModel();
		dL4JModel.initModel();
		String filePath = modelPath + modelName + ".zip";
		File file = fileHandler.createNewFile(filePath);
		boolean saveSuccess = dL4JModel.saveModel(file);
		if(saveSuccess == true)
			networkTableCSV.newRow(modelName + delimiter + "MLP" + delimiter + "-" + delimiter + "-" + delimiter + DL4JGlobalConfig.seed + delimiter + DL4JGlobalConfig.optimizationAlgorithm + delimiter + DL4JGlobalConfig.updater + delimiter + DL4JGlobalConfig.learningRate + delimiter + DL4JGlobalConfig.momentum + delimiter + DL4JGlobalConfig.regularization + delimiter + DL4JGlobalConfig.coefficient + delimiter + DL4JGlobalConfig.weight); // Save our initial network table configuration
		return saveSuccess;
	}

	/**
	 * Save the model and also the global configuration
	 */
	private void saveModel() {
		File file = modelSelectionTable();
		if (file != null) { 
			System.out.println(file.getPath());
			if (dialogs.question("Save", "Do you want to save?") == true) 
				if (dL4JModel.saveModel(file) == true) {
					/*
					 * Save to the CSV
					 */
					String modelName = file.getName().replace(".zip", "");
					int rowNumber = networkTableCSV.findRow(modelName, "Name");
					List<String> cells = networkTableCSV.getRow(rowNumber); 
					String name = cells.get(0);
					String network = cells.get(1);
					String trained = cells.get(2);
					String accuracy = cells.get(3);
					String rowText = name + delimiter + network + delimiter + trained + delimiter + accuracy + delimiter + DL4JGlobalConfig.seed + delimiter + DL4JGlobalConfig.optimizationAlgorithm + delimiter + DL4JGlobalConfig.updater + delimiter + DL4JGlobalConfig.learningRate + delimiter + DL4JGlobalConfig.momentum + delimiter + DL4JGlobalConfig.regularization + delimiter + DL4JGlobalConfig.coefficient + delimiter + DL4JGlobalConfig.weight; // Save our initial network table configuration
					networkTableCSV.replaceRow(rowNumber, rowText);
				}else {
					dialogs.alertDialog(AlertType.ERROR, "Saving", "Could not save model!");
				}
		}else {
			dialogs.alertDialog(AlertType.INFORMATION, "No model", "You have not selected any model in the table.");
		}
	}

	/**
	 * This method delete our model and also the row inside the table view
	 */
	private void deleteModel() {
		File file = modelSelectionTable();
		if (file != null) {
			if (dialogs.question("Delete", "Do you want to delete?")) {
				/*
				 * Delete model and change the selection of the table
				 */
				deleteNow(file);
				useModel();
			}
		}else {
			dialogs.alertDialog(AlertType.INFORMATION, "No model", "You have not selected any model.");
		}
	}
	
	/**
	 * This method is created because to minimize boiler plate code
	 * We using deleteNow(File file) on two places in this java file
	 * @param file
	 */
	private void deleteNow(File file) {
		String modelName = modelSelectionTable().getName().replace(".zip", "");
		int rowNumber = networkTableCSV.findRow(modelName, "Name");
		int totalRows = networkTableCSV.getTotalRows() - 1;
		networkTableCSV.deleteRow(rowNumber);
		file.delete();
		reloadTable();
		if(selectedRowIndex < totalRows) 
			networkTable.getSelectionModel().select(selectedRowIndex); // Select current row
		else
			networkTable.getSelectionModel().select(selectedRowIndex-1); // Select the last row
		selectedRowIndex = networkTable.getSelectionModel().getSelectedIndex(); 
	}

	/**
	 * This method return the model we selected inside the table view
	 * @return File
	 */
	private File modelSelectionTable() {
		System.out.println("modelSelectionTable");
		NetworkTableContainer networkTableContainer = networkTable.getSelectionModel().getSelectedItem();
		if (networkTableContainer != null) {
			System.out.println("modelSelectionTable model select");
			String modelName = networkTableContainer.getName();
			String filePath = modelPath + modelName + ".zip";
			return fileHandler.loadFile(filePath);
		} else {
			return null;
		}
	}

	/**
	 * Load the table here
	 * @param networkTableCSV Our CSV Handler object
	 */
	private void reloadTable() {
		list.clear();
		for (int i = 0; i < networkTableCSV.getTotalRows(); i++) {
			List<String> cells = networkTableCSV.getRow(i); 
			String name = cells.get(0);
			String network = cells.get(1);
			String trained = cells.get(2);
			String accuracy = cells.get(3);
			list.add(new NetworkTableContainer(name, network, trained, accuracy));
		}
	}
	
	/**
	 * If we click on the table, that means we click on a row, or not, but we will load that row
	 * if the row exist and we clicked on it
	 * I don't want to use a listener for that because it will give me no control over useModel()
	 * @param event
	 */
	@FXML
    void tableClicked(MouseEvent event) {
		selectedRowIndex = networkTable.getSelectionModel().getSelectedIndex();
		useModel();
    }
}