package se.danielmartensson.views;

import java.util.ArrayList;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.DropdownButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import se.danielmartensson.deeplearning.DL4JSerializableConfiguration;
import se.danielmartensson.deeplearning.DL4JModel;
import se.danielmartensson.tools.Dialogs;
import se.danielmartensson.tools.SimpleDependencyInjection;
import se.danielmartensson.views.containers.GlobalConfigurationTable;
import se.danielmartensson.views.containers.LayerConfigurationTable;

public class ConfigurationsPresenter {

    @FXML
    private View configurations;
    
    @FXML
    private TableView<GlobalConfigurationTable> globalTableView;

    @FXML
    private TableColumn<GlobalConfigurationTable, String> columnConfiguration;
    
    @FXML
    private TableColumn<GlobalConfigurationTable, DropdownButton> columnValue;
    
    @FXML
    private TableView<LayerConfigurationTable> layerTableView;
    
    @FXML
    private TableColumn<LayerConfigurationTable, DropdownButton> layerColumn;

    @FXML
    private TableColumn<LayerConfigurationTable, DropdownButton> inputColumn;

    @FXML
    private TableColumn<LayerConfigurationTable, DropdownButton> outputColumn;

    @FXML
    private TableColumn<LayerConfigurationTable, DropdownButton> activationColumn;

    @FXML
    private TableColumn<LayerConfigurationTable, DropdownButton> lossFunctionColumn;
    
    @FXML
    private Tab globalConfigurationTab;
    
    @FXML
    private Tab layerConfigurationTab;
        
	/*
	 * Tools
	 */
	private Dialogs dialogs = new Dialogs();
	
	/*
	 * AppBar
	 */
	private AppBar appBar;
	
	/*
     * Injected from DI
     */
    private DL4JModel dL4JModel;
    DL4JSerializableConfiguration dL4JSerializableConfiguration;
    
    /*
     * Regular fields for the view
     */
    private ObservableList<GlobalConfigurationTable> listForGlobalTable;
	private ObservableList<LayerConfigurationTable> listForLayerTable;

    public void initialize() {
    	/*
		 * Dependency injection
		 */
    	dL4JModel = SimpleDependencyInjection.getDL4JModel();
		
    	/*
    	 * Slide smooth in and out
    	 */
        configurations.setShowTransitionFactory(BounceInRightTransition::new);
        
        /*
    	 * Listener for leaving and enter the page
    	 */
        configurations.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
            	/*
                 * Enter the page
                 */
                appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> MobileApplication.getInstance().getDrawer().open()));
                appBar.setTitleText("Configurations");
                
                /*
                 * Reload the table when we slide in
                 */
                if(reloadTable() == false) {
                	dialogs.alertDialog(AlertType.INFORMATION, "Nothing", "No model where selected");
                	listForGlobalTable.clear(); // If we have add a layer, just delete it
                	listForLayerTable.clear();
                }else {
                	reloadView(); // That too!
                }
                
                /*
                 * If we enter with Layer Configuration tab selected
                 */
                if(layerConfigurationTab.isSelected() == true)
                	addPlusMinus();
                else
                	removePlusMinus();
                
            }else {
            	/*
            	 * Leaving the page - Save all configurations and layers
            	 */
            	saveConfigurationsLayers();
            }
        });
        
        /*        
         * Declare for the global table         
         */
        columnConfiguration.setCellValueFactory(new PropertyValueFactory<GlobalConfigurationTable, String>("configuration"));
        columnValue.setCellValueFactory(new PropertyValueFactory<GlobalConfigurationTable, DropdownButton>("value"));
        listForGlobalTable = FXCollections.observableArrayList();
        globalTableView.setItems(listForGlobalTable);
        
        /*
         * Listener for tabs
         */
        globalConfigurationTab.selectedProperty().addListener(e -> removePlusMinus());
        layerConfigurationTab.selectedProperty().addListener(e -> addPlusMinus());
        
        /*
         * Declare for the view
         */
        layerColumn.setCellValueFactory(new PropertyValueFactory<LayerConfigurationTable, DropdownButton>("layerType"));
        inputColumn.setCellValueFactory(new PropertyValueFactory<LayerConfigurationTable, DropdownButton>("nIn"));
        outputColumn.setCellValueFactory(new PropertyValueFactory<LayerConfigurationTable, DropdownButton>("nOut"));
        activationColumn.setCellValueFactory(new PropertyValueFactory<LayerConfigurationTable, DropdownButton>("activationType"));
        lossFunctionColumn.setCellValueFactory(new PropertyValueFactory<LayerConfigurationTable, DropdownButton>("lossFunctionType"));
        listForLayerTable = FXCollections.observableArrayList();
        layerTableView.setItems(listForLayerTable);
        
        /*
    	 * Get the configuration and layers
    	 */
        dL4JSerializableConfiguration = dL4JModel.getDL4JSerializableConfiguration();
        
    }

    /**
     * Load the table for the layers
     */
    private void reloadView() {
    	/*
    	 * Clear layer table first
    	 */
    	listForLayerTable.clear();
    	
    	/*
    	 * Create a expanded panels from these 
    	 */
    	String[] layerNames = dL4JSerializableConfiguration.getLayerNames();
    	ArrayList<String> layerList = dL4JSerializableConfiguration.getLayerList();
        ArrayList<Integer> nInList = dL4JSerializableConfiguration.getNInList();
        ArrayList<Integer> nOutList = dL4JSerializableConfiguration.getNOutList();
        ArrayList<Activation> activationList = dL4JSerializableConfiguration.getActivationList();
        ArrayList<LossFunctions.LossFunction> lossFunctionList = dL4JSerializableConfiguration.getLossFunctionList();
    	
        /*
         * Loop all layers
         */
    	for(int i = 0; i < layerList.size(); i++) {
            /*
             * Add all drop downs buttons
             */
           	DropdownButton layerType = new DropdownButton();
            layerType.getItems().add(new MenuItem(layerList.get(i))); // Add the selected item
            for(String layerName : layerNames) // Loop all layer names such as DenseLayer, OutputLayer, LSTM etc.
            	if(layerList.get(i).equals(layerName) == false) // Check if we have already insert the first selected?
            		layerType.getItems().add(new MenuItem(layerName)); // Nope! Add!
           	/*
           	 * Do the same for the rest
           	 */
            DropdownButton nIn = new DropdownButton();
           	nIn.getItems().add(new MenuItem(nInList.get(i).toString()));
           	for(int k = 1; k <= 1000; k++)
          		if(nInList.get(i) != k)
           			nIn.getItems().add(new MenuItem(String.valueOf(k)));
           			
        	DropdownButton nOut = new DropdownButton();
         	nOut.getItems().add(new MenuItem(nOutList.get(i).toString()));
           	for(int k = 1; k <= 1000; k++)
           		if(nOutList.get(i) != k)
           			nOut.getItems().add(new MenuItem(String.valueOf(k)));
            		
           	DropdownButton activationType = new DropdownButton();
           	activationType.getItems().add(new MenuItem(activationList.get(i).name()));
           	for(Activation activation : Activation.values())
           		if(activationList.get(i).name().equals(activation.name()) == false)
           			activationType.getItems().add(new MenuItem(activation.name()));
            		
           	DropdownButton lossFunctionType = new DropdownButton();
           	lossFunctionType.getItems().add(new MenuItem(lossFunctionList.get(i).name()));
           	for(LossFunctions.LossFunction lossFunction : LossFunctions.LossFunction.values())
           		if(lossFunctionList.get(i).equals(lossFunction) == false)
           			lossFunctionType.getItems().add(new MenuItem(lossFunction.name()));
           	/*
	         * Add more here...
	         */
           	
           	/*
           	 * Add a row now to our table
             */
            listForLayerTable.addAll(new LayerConfigurationTable(layerType, nIn, nOut, activationType, lossFunctionType));
           	
    	}
	}

	/**
     * This method will get the dL4JSerializableConfiguration object and load its getters
     * from the tableView object. From tableView object, we will get its drop down buttons
     * and from these drop down buttons, we will get what we have selected
     */
	private void saveConfigurationsLayers() {
    	/*
    	 * Just check if we have any selected row numbers
    	 */
    	if(dL4JSerializableConfiguration.getModelName() == null)
    		return; // No model selected - so we don't need to save anything!
    	
    	/*
    	 * Follows linear with dropDownButtonList in method reloadTable()
    	 */
    	long seed = Long.parseLong(listForGlobalTable.get(0).getValue().getSelectedItem().getText());
    	OptimizationAlgorithm optimizationAlgorithm = OptimizationAlgorithm.valueOf(listForGlobalTable.get(1).getValue().getSelectedItem().getText());
    	WeightInit weightInit = WeightInit.valueOf(listForGlobalTable.get(2).getValue().getSelectedItem().getText());
    	String updaterName = listForGlobalTable.get(3).getValue().getSelectedItem().getText();
    	double learningRate = Double.valueOf(listForGlobalTable.get(4).getValue().getSelectedItem().getText());
    	double momentum = Double.valueOf(listForGlobalTable.get(5).getValue().getSelectedItem().getText());
    	String regularizationName = listForGlobalTable.get(6).getValue().getSelectedItem().getText();
    	double regularizationCoefficient = Double.valueOf(listForGlobalTable.get(7).getValue().getSelectedItem().getText());

    	/*
    	 * Set the global configurations now
    	 */
    	dL4JSerializableConfiguration.setSeed(seed);
    	dL4JSerializableConfiguration.setOptimizationAlgorithm(optimizationAlgorithm);
    	dL4JSerializableConfiguration.setWeightInit(weightInit);
    	dL4JSerializableConfiguration.setUpdaterName(updaterName);
    	dL4JSerializableConfiguration.setLearningRate(learningRate);
    	dL4JSerializableConfiguration.setMomentum(momentum);
    	dL4JSerializableConfiguration.setRegularizationName(regularizationName);
    	dL4JSerializableConfiguration.setRegularizationCoefficient(regularizationCoefficient);
    	/*
         * Add more here...
         */
    	
    	/*
    	 * Set the layers now. Begin to declare some arrays where we should later send back
    	 */
    	ArrayList<String> layerList = new ArrayList<String>(); 
    	ArrayList<Integer> nInList = new ArrayList<Integer>();
    	ArrayList<Integer> nOutList = new ArrayList<Integer>(); 
    	ArrayList<Activation> activationList = new ArrayList<Activation>(); 
    	ArrayList<LossFunctions.LossFunction> lossFunctionList = new ArrayList<LossFunctions.LossFunction>(); 
    	
    	/*
    	 * Loop thru all rows that are inside the table
    	 */
    	for(int i = 0; i < listForLayerTable.size(); i++) {
    		/*
    		 * Get the drop down buttons
    		 */
    		LayerConfigurationTable layerConfigurationTable =  listForLayerTable.get(i);
    		String layer = layerConfigurationTable.getLayerType().getSelectedItem().getText();
    		String nIn = layerConfigurationTable.getNIn().getSelectedItem().getText();
    		String nOut = layerConfigurationTable.getNOut().getSelectedItem().getText();
    		String activation = layerConfigurationTable.getActivationType().getSelectedItem().getText();
    		String lossFunction = layerConfigurationTable.getLossFunctionType().getSelectedItem().getText();
    		
    		/*
    		 * Get all the times and place them into the array lists
    		 */
    		layerList.add(layer);
    		nInList.add(Integer.parseInt(nIn));
    		nOutList.add(Integer.parseInt(nOut));
    		activationList.add(Activation.valueOf(activation));
    		lossFunctionList.add(LossFunctions.LossFunction.valueOf(lossFunction));
    	}
    	
    	/*
    	 * Now we have filled our array lists. Send them back
    	 */
    	dL4JSerializableConfiguration.setLayerList(layerList);
    	dL4JSerializableConfiguration.setNInList(nInList);
    	dL4JSerializableConfiguration.setNOutList(nOutList);
    	dL4JSerializableConfiguration.setActivationList(activationList);
    	dL4JSerializableConfiguration.setLossFunctionList(lossFunctionList);
    	/*
         * Add more here...
         */
	}

	/**
     * Reload the table and get the final string arrays from DL4JJasonConfiguration class
     * and also we use the enums from internal DL4J classes such as WeightInit and OptimizationAlgorithm etc.
     */
    private boolean reloadTable()  {
    	listForGlobalTable.clear();
    	/*
    	 * Just check if we have any selected row numbers
    	 */
    	if(dL4JSerializableConfiguration.getModelName() == null)
    		return false; // No model selected
  
        /*
    	 * Load some arrays from DL4JSerializableConfiguration class
    	 */
    	String[] updaterList = dL4JSerializableConfiguration.getUpdaterList();
    	String[] regularizationList = dL4JSerializableConfiguration.getRegularizationList();
    	String[] configurationNames = dL4JSerializableConfiguration.getConfigurationNames();
    	
    	/*
    	 * Create drop down buttons that we should insert to the table
    	 */
    	DropdownButton updaterDropdown = new DropdownButton();
    	DropdownButton regularizationNameDropdown = new DropdownButton();
    	DropdownButton weightInitDropdown = new DropdownButton();
    	DropdownButton optimizationAlgorithmDropdown = new DropdownButton();
    	DropdownButton seedDropdown = new DropdownButton();
    	DropdownButton regularizationCoefficientDropdown = new DropdownButton();
    	DropdownButton learningRateDropdown = new DropdownButton();
    	DropdownButton momentumDropdown = new DropdownButton();
    	
    	/*
    	 * Add the menu items with string labels into drop down buttons
    	 * 1. Add the selected value from the fields inside dL4JSerializableConfiguration object
    	 * 2. Loop thru the arrays from the dL4JSerializableConfiguration and some DL4J classes
    	 * 3. Check if we are in the same index as the fields and insert field if we are not on the same index
    	 */
    	updaterDropdown.getItems().add(new MenuItem(dL4JSerializableConfiguration.getUpdaterName())); // Add field value
    	for(String updater : updaterList)  // Loop thru the list
    		if(updater.equals(dL4JSerializableConfiguration.getUpdaterName()) == false) // Check if that is the selected field that we just added first?
    			updaterDropdown.getItems().add(new MenuItem(updater)); // Nope it is not, add this value now
    	
		regularizationNameDropdown.getItems().add(new MenuItem(dL4JSerializableConfiguration.getRegularizationName()));
    	for(String regularizationName : regularizationList) 
    		if(regularizationName.equals(dL4JSerializableConfiguration.getRegularizationName()) == false)
    			regularizationNameDropdown.getItems().add(new MenuItem(regularizationName));
    	
		weightInitDropdown.getItems().add(new MenuItem(dL4JSerializableConfiguration.getWeightInit().toString()));
    	for(int i = 0; i < WeightInit.values().length; i++)
    		if((String.valueOf(WeightInit.values()[i].toString())).equals(String.valueOf(dL4JSerializableConfiguration.getWeightInit().toString())) == false)
    			weightInitDropdown.getItems().add(new MenuItem(String.valueOf(WeightInit.values()[i].toString())));
    	
		optimizationAlgorithmDropdown.getItems().add(new MenuItem(dL4JSerializableConfiguration.getOptimizationAlgorithm().toString()));
    	for(int i = 0; i < OptimizationAlgorithm.values().length; i++)
    		if((OptimizationAlgorithm.values()[i].toString()).equals(dL4JSerializableConfiguration.getOptimizationAlgorithm().toString()) == false)
    			optimizationAlgorithmDropdown.getItems().add(new MenuItem(OptimizationAlgorithm.values()[i].toString()));

		seedDropdown.getItems().add(new MenuItem(String.valueOf(dL4JSerializableConfiguration.getSeed())));
		regularizationCoefficientDropdown.getItems().add(new MenuItem(String.valueOf(dL4JSerializableConfiguration.getRegularizationCoefficient())));
		learningRateDropdown.getItems().add(new MenuItem(String.valueOf(dL4JSerializableConfiguration.getLearningRate())));
		momentumDropdown.getItems().add(new MenuItem(String.valueOf(dL4JSerializableConfiguration.getMomentum())));
		for(int i = 0; i < 50; i++) {
			if(Long.valueOf(100 + i*100).equals(dL4JSerializableConfiguration.getSeed()) == false)
				seedDropdown.getItems().add(new MenuItem(String.valueOf(100 + i*100)));
			
			if((Double.valueOf(Math.pow(10, -i))).equals(dL4JSerializableConfiguration.getRegularizationCoefficient()) == false)
				regularizationCoefficientDropdown.getItems().add(new MenuItem(String.valueOf(Math.pow(10, -i))));		
    	}
		for(int i = 0; i < 500; i++) {
			if((Double.valueOf(0.0001 + 0.002*i)).equals(dL4JSerializableConfiguration.getLearningRate()) == false)
				learningRateDropdown.getItems().add(new MenuItem(String.valueOf(0.0001 + 0.002*i)));
			
			if((Double.valueOf(0.0001 + 0.002*i)).equals(dL4JSerializableConfiguration.getMomentum()) == false)
	    		momentumDropdown.getItems().add(new MenuItem(String.valueOf(0.0001 + 0.002*i)));			
    	}
    	
    	/*
    	 * Collect all to an array list
    	 */
    	ArrayList<DropdownButton> dropDownButtonList = new ArrayList<DropdownButton>();
    	dropDownButtonList.add(seedDropdown);
    	dropDownButtonList.add(optimizationAlgorithmDropdown);
    	dropDownButtonList.add(weightInitDropdown);
    	dropDownButtonList.add(updaterDropdown);
    	dropDownButtonList.add(learningRateDropdown);
    	dropDownButtonList.add(momentumDropdown);
    	dropDownButtonList.add(regularizationNameDropdown);
    	dropDownButtonList.add(regularizationCoefficientDropdown);
    	/*
         * Add more here...
         */
    	
    	/*
    	 * Create table now
    	 */
    	for(int i = 0; i < configurationNames.length; i++)
    		listForGlobalTable.add(new GlobalConfigurationTable(configurationNames[i], dropDownButtonList.get(i)));
    	
    	/*
    	 * Done! Success!
    	 */
    	return true;
	}

	/**
     * Add the + and - sign when we go to layer tab
     */
	private void addPlusMinus() {
        appBar.getActionItems().add(MaterialDesignIcon.ADD.button(e ->  addLayer()));
        appBar.getActionItems().add(MaterialDesignIcon.REMOVE.button(e -> removeLayer()));
	}

	/**
	 * Remove the + and - sign when we go to global tab
	 */
	private void removePlusMinus() {
		appBar.getActionItems().clear();
	}

	/**
	 * Remove a layer
	 */
	private void removeLayer() {
		if(listForLayerTable.size() > 1) // We need at least have one layer
			listForLayerTable.remove(listForLayerTable.size() -1);
	}

	/**
	 * Add a layer - Pressing the + button in the appBar
	 */
	private void addLayer() {
	    /*
         * Add all drop downs buttons
         */
       	DropdownButton layerType = new DropdownButton();
       	String[] layerNames = dL4JSerializableConfiguration.getLayerNames();
        for(String layerName : layerNames) 
        		layerType.getItems().add(new MenuItem(layerName));
       	/*
       	 * Do the same for the rest
       	 */
        DropdownButton nIn = new DropdownButton();
       	for(int k = 1; k <= 100; k++)
       		nIn.getItems().add(new MenuItem(String.valueOf(k)));
       			
    	DropdownButton nOut = new DropdownButton();
       	for(int k = 1; k <= 100; k++)
       		nOut.getItems().add(new MenuItem(String.valueOf(k)));
        		
       	DropdownButton activationType = new DropdownButton();
       	for(Activation activation : Activation.values())
       		activationType.getItems().add(new MenuItem(activation.name()));
        		
       	DropdownButton lossFunctionType = new DropdownButton();
       	for(LossFunctions.LossFunction lossFunction : LossFunctions.LossFunction.values())
       		lossFunctionType.getItems().add(new MenuItem(lossFunction.name()));
       	/*
         * Add more here...
         */
      		
       	/*
       	 * Add a new row now to our table
         */
        listForLayerTable.addAll(new LayerConfigurationTable(layerType, nIn, nOut, activationType, lossFunctionType));
	}
}
