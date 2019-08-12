package se.danielmartensson.views;

import java.io.File;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Sgd;
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
import javafx.stage.Screen;
import se.danielmartensson.NetworkTableContainer;
import se.danielmartensson.deeplearning.DL4JGlobalConfig;
import se.danielmartensson.deeplearning.DL4JLayers;
import se.danielmartensson.deeplearning.DL4JModel;
import se.danielmartensson.tools.CSVHandler;
import se.danielmartensson.tools.Dialogs;
import se.danielmartensson.tools.FileHandler;

public class NeuralNetworksPresenter {

	/*
	 * Fields
	 */
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
	private DL4JGlobalConfig dL4JGlobalConfig;
	private DL4JLayers dL4JLayers;
	private DL4JModel dL4JModel;
	private Dialogs dialogs;
	private CSVHandler cSVHandler;
	ObservableList<NetworkTableContainer> list;

	private FileHandler fileHandler;

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
				// double widthBar = appBar.widthProperty().get();
				double heightBar = appBar.heightProperty().get();
				double heightScreen = Screen.getPrimary().getBounds().getHeight();
				double widthScreen = Screen.getPrimary().getBounds().getWidth();
				networkTable.setPrefSize(widthScreen, heightScreen - heightBar);

				/*
				 * Load the table if we have loaded the file before
				 */
				if (list != null) {
					reloadTable();
				}

				/*
				 * Methods
				 */
				appBar.getActionItems().add(MaterialDesignIcon.CREATE.button(e -> {
					createNewTableRow();
				}));

				appBar.getActionItems().add(MaterialDesignIcon.SAVE.button(e -> {
					saveModel();
				}));

				appBar.getActionItems().add(MaterialDesignIcon.DELETE.button(e -> {
					deleteModel();
				}));
			}
		});

		/*
		 * Dialogs
		 */
		dialogs = new Dialogs();

		/*
		 * Set the columns in the table view
		 */
		columnName.setCellValueFactory(new PropertyValueFactory<NetworkTableContainer, String>("name"));
		columnNetwork.setCellValueFactory(new PropertyValueFactory<NetworkTableContainer, String>("network"));
		columnTrained.setCellValueFactory(new PropertyValueFactory<NetworkTableContainer, String>("trained"));
		columnAccuracy.setCellValueFactory(new PropertyValueFactory<NetworkTableContainer, String>("accuracy"));

		/*
		 * Listener for table view
		 */
		networkTable.getSelectionModel().selectedItemProperty().addListener(e -> {
			Platform.runLater(() -> useModel());
		});

		/*
		 * Connect to our DL4J classes and its functionality 
		 */
		Platform.runLater(() -> {
			ApplicationContext context = new FileSystemXmlApplicationContext("/src/main/resources/se/danielmartensson/beans/DL4JBeans.xml");
			dL4JGlobalConfig = context.getBean("dL4JGlobalConfig", DL4JGlobalConfig.class);
			dL4JLayers = context.getBean("dL4JLayers", DL4JLayers.class);
			dL4JModel = context.getBean("dL4JModel", DL4JModel.class);
			((FileSystemXmlApplicationContext) context).close();
		});

		/*
		 * For the CSVHandler and FileHandler and also the FXCollections for
		 * networkTable
		 */
		Platform.runLater(() -> {
			ApplicationContext context = new FileSystemXmlApplicationContext("/src/main/resources/se/danielmartensson/beans/FileBeans.xml");
			cSVHandler = context.getBean("cSVHandler", CSVHandler.class);
			fileHandler = context.getBean("fileHandler", FileHandler.class);
			list = FXCollections.observableArrayList();
			networkTable.setItems(list);
			((FileSystemXmlApplicationContext) context).close();
			reloadTable();

			/*
			 * This initial selection in the table at the start up of the app
			 */
			int rows = cSVHandler.getTotalRows();
			if (rows > 0) {
				networkTable.getSelectionModel().select(0); // First row
				useModel();
			}
		});

		/*
		 * If we got some models or a model
		 */

	}

	/**
	 * Select one row at the table view
	 */
	private void useModel() {
		File file = modelSelectionTable();
		if (file != null) {
			if (dL4JModel.loadModel(file) == false) {
				dialogs.alertDialog(AlertType.ERROR, "Loading", "Could not load model!");
			}
		}
	}

	/**
	 * Create a new table row inside the table view
	 */
	private void createNewTableRow() {
		String name = dialogs.input("New", "Enter new name");
		if (name.contains(",") == false && name.equals("") == false) {
			boolean exist = cSVHandler.exist(name);
			if (exist == false) {
				if (createModel(name) == true) {
					cSVHandler.newRow(name + ",-,-,-");
					reloadTable();
					networkTable.getSelectionModel().selectLast();
				} else {
					dialogs.alertDialog(AlertType.ERROR, "Creation", "Could not create model!");
				}
			} else {
				dialogs.alertDialog(AlertType.ERROR, "Exist", "File name already exist.");
			}
		} else {
			if (name.contains(",")) {
				dialogs.alertDialog(AlertType.ERROR, "Cannot use ','", "Select another file name.");
			}
		}
	}

	/**
	 * Create an initial basic model so we can save it
	 * 
	 * @param modelName Name if the model
	 * @return
	 */
	private boolean createModel(String modelName) {
		dL4JGlobalConfig.setSeed(100);
		dL4JGlobalConfig.setRegularization("L1", 0.9);
		dL4JGlobalConfig.setOptimizationAlgorithm(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
		dL4JGlobalConfig.setUpdater(new Sgd(0.3));
		dL4JGlobalConfig.setWeightInit(WeightInit.XAVIER);
		dL4JLayers.addDenseLayer(3, 3, Activation.SOFTMAX);
		dL4JLayers.addOutputLayer(3, 3, Activation.SOFTMAX, LossFunction.NEGATIVELOGLIKELIHOOD);
		dL4JModel.buildModel();
		dL4JModel.initModel();
		String filePath = modelPath + modelName + ".zip";
		File file = fileHandler.createNewFile(filePath);
		return dL4JModel.saveModel(file);
	}

	/**
	 * Save the model
	 */
	private void saveModel() {
		File file = modelSelectionTable();
		if (file != null) {
			if (dialogs.question("Save", "Do you want to save?")) {
				if (dL4JModel.saveModel(file) == false) {
					dialogs.alertDialog(AlertType.ERROR, "Saving", "Could not save model!");
				}
			}
		} else {
			dialogs.alertDialog(AlertType.INFORMATION, "No model", "You have not selected any model.");
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
				 * Delete
				 */
				String modelName = modelSelectionTable().getName().replace(".zip", "");
				cSVHandler.deleteRow(cSVHandler.findRow(modelName));
				int index = networkTable.getSelectionModel().getSelectedIndex();
				file.delete();
				reloadTable();

				/*
				 * This will make so we always using the row below
				 */
				int totalRows = cSVHandler.getTotalRows();
				if (index == totalRows) {
					networkTable.getSelectionModel().select(index);
				} else if (index > totalRows) {
					networkTable.getSelectionModel().select(totalRows);
				} else if (index < totalRows) {
					networkTable.getSelectionModel().select(index);
				}
				useModel();
			}
		} else {
			dialogs.alertDialog(AlertType.INFORMATION, "No model", "You have not selected any model.");
		}
	}

	/**
	 * This method return the model we selected inside the table view
	 * 
	 * @return File
	 */
	private File modelSelectionTable() {
		NetworkTableContainer networkTableContainer = networkTable.getSelectionModel().getSelectedItem();
		if (networkTableContainer != null) {
			String modelName = networkTableContainer.getName();
			String filePath = modelPath + modelName + ".zip";
			return fileHandler.loadFile(filePath);
		} else {
			return null;
		}
	}

	/**
	 * Load the table here
	 * 
	 * @param cSVHandler Our CSV Handler object
	 */
	private void reloadTable() {
		list.clear();
		for (int i = 0; i < cSVHandler.getTotalRows(); i++) {
			String name = cSVHandler.getCell(i, 0);
			String network = cSVHandler.getCell(i, 1);
			String trained = cSVHandler.getCell(i, 2);
			String accuracy = cSVHandler.getCell(i, 3);
			list.add(new NetworkTableContainer(name, network, trained, accuracy));
		}
	}
}
