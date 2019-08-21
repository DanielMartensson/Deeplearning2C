package se.danielmartensson.views;

import java.util.ArrayList;
import java.util.Arrays;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.control.DropdownButton;
import com.gluonhq.charm.glisten.control.ExpansionPanel;
import com.gluonhq.charm.glisten.control.ExpansionPanel.CollapsedPanel;
import com.gluonhq.charm.glisten.control.ExpansionPanel.ExpandedPanel;
import com.gluonhq.charm.glisten.control.ExpansionPanelContainer;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.Getter;
import se.danielmartensson.deeplearning.DL4JSerializableConfiguration;
import se.danielmartensson.deeplearning.DL4JModel;
import se.danielmartensson.tools.Dialogs;
import se.danielmartensson.tools.SimpleDependencyInjection;
import se.danielmartensson.views.containers.GlobalConfigurationTable;

public class ConfigurationsPresenter {

    @FXML
    private View configurations;
    
    @FXML
    private TableView<GlobalConfigurationTable> tableView;

    @FXML
    private TableColumn<GlobalConfigurationTable, String> columnConfiguration;

    @FXML
    private TableColumn<GlobalConfigurationTable, DropdownButton> columnValue;

    @FXML
    private CharmListView<ExpansionPanelContainer, String> listView;
    
    @FXML
    private Tab globalConfigurationTab;
    
    @FXML
    private Tab layerConfigurationTab;
        
	/*
	 * Tools
	 */
	private Dialogs dialogs = new Dialogs();
	
	/*
     * Injected from DI
     */
    private DL4JModel dL4JModel;
    DL4JSerializableConfiguration dL4JSerializableConfiguration;
    
    /*
     * Regular fields for the view
     */
    private ObservableList<GlobalConfigurationTable> listForTable;
	private AppBar appBar;
	private ObservableList<ExpansionPanelContainer> listForView;

	private ExpansionPanelContainer expansionPanelContainer;

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
                if(reloadTable() == false)
                	dialogs.alertDialog(AlertType.INFORMATION, "Nothing", "No model where selected");
                else
                	reloadView(); // That too!
                
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
         * Declare for the table         
         */
        columnConfiguration.setCellValueFactory(new PropertyValueFactory<GlobalConfigurationTable, String>("configuration"));
        columnValue.setCellValueFactory(new PropertyValueFactory<GlobalConfigurationTable, DropdownButton>("value"));
        listForTable = FXCollections.observableArrayList();
        tableView.setItems(listForTable);
        
        /*
         * Listener for tabs
         */
        globalConfigurationTab.selectedProperty().addListener(e -> removePlusMinus());
        layerConfigurationTab.selectedProperty().addListener(e -> addPlusMinus());
        
        /*
         * Declare for the view
         */
        listForView = FXCollections.observableArrayList();
        expansionPanelContainer = new ExpansionPanelContainer();
        listForView.add(expansionPanelContainer);
        listView.setItems(listForView);
        
        /*
    	 * Get the configuration and layers
    	 */
        dL4JSerializableConfiguration = dL4JModel.getDL4JSerializableConfiguration();
        
    }

    /**
     * Load the view for the layers
     */
    private void reloadView() {
    	/*
    	 * Clear view first
    	 */
    	expansionPanelContainer.getItems().clear();
    	
    	/*
    	 * Create a expanded panels from these 
    	 */
    	String[] dropDownTypes =  dL4JSerializableConfiguration.getDropdownTypes(); 
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
        	 * New expansions panel and add it to the expansions panel container
        	 */
            ExpansionPanel expansionPanel = new ExpansionPanel();
            expansionPanelContainer.getItems().add(expansionPanel);
            
            /*
             * Create an collapsed panel and first set a label with the name of the layer index e.g. layer 0, layer 1 etc and the layer type
             */
            CollapsedPanel collapsedPanel = new CollapsedPanel();
            collapsedPanel.getTitleNodes().add(0, new Label("Layer " + expansionPanelContainer.getItems().size())); // Layer index
           	collapsedPanel.getTitleNodes().add(1, new Label(layerList.get(i))); // Layer type
            expansionPanel.setCollapsedContent(collapsedPanel); // Set collapsed panel into expansion panel
            
            /*
             * Create an expanded panel and begin to set a grid pane into it
             */
            ExpandedPanel expandedPanel = new ExpandedPanel();
            GridPane gridPane = new GridPane();
            expandedPanel.setContent(gridPane);
            expansionPanel.setExpandedContent(expandedPanel);
            
            /*
             * Add all drop downs and labels
             */
            for(int j = 0; j < dropDownTypes.length; j++) {
            	/*
            	 * Create the  element label and set its position inside the grid
            	 */
            	Label elementLabel = new Label(dropDownTypes[j]);
            	gridPane.add(elementLabel, 0, j); // To the left
            	
           		/*
               	 * Create the drop down button and add the elements and set its position inside the grid
               	 */
            	DropdownButton elementDropdown = new DropdownButton();
            	if(dropDownTypes[j].equals(dropDownTypes[0])) { // Layer types
            		elementDropdown.getItems().add(new MenuItem(layerList.get(i)));
            		for(String layerName : layerNames)
            			if(layerList.get(i).equals(layerName) == false)
            				elementDropdown.getItems().add(new MenuItem(layerName));
            		
            	}else if(dropDownTypes[j].equals(dropDownTypes[1])) { // Inputs
            		elementDropdown.getItems().add(new MenuItem(nInList.get(i).toString()));
           			for(int k = 1; k <= 100; k++)
           				if(nInList.get(i) != k)
           					elementDropdown.getItems().add(new MenuItem(String.valueOf(k)));
           			
            	}else if(dropDownTypes[j].equals(dropDownTypes[2])) { // Outputs
            		elementDropdown.getItems().add(new MenuItem(nOutList.get(i).toString()));
            		for(int k = 1; k <= 100; k++)
            			if(nOutList.get(i) != k)
            				elementDropdown.getItems().add(new MenuItem(String.valueOf(k)));
            		
            	}else if(dropDownTypes[j].equals(dropDownTypes[3])) { // Activations
            		elementDropdown.getItems().add(new MenuItem(activationList.get(i).name()));
            		for(Activation activation : Activation.values())
            			if(activationList.get(i).name().equals(activation.name()) == false)
            				elementDropdown.getItems().add(new MenuItem(activation.name()));
            		
            	}else if(dropDownTypes[j].equals(dropDownTypes[4])) { // Loss functions
            		if(lossFunctionList.get(i) != null) { // Loss functions is used for output layers, there fore loss function can be null for other layers in configuration
            			elementDropdown.getItems().add(new MenuItem(lossFunctionList.get(i).name()));
            			for(LossFunctions.LossFunction lossFunction : LossFunctions.LossFunction.values())
            				if(lossFunctionList.get(i).equals(lossFunction) == false)
            					elementDropdown.getItems().add(new MenuItem(lossFunction.name()));
            		}else {
            			elementDropdown.getItems().add(new MenuItem("null"));
            			for(LossFunctions.LossFunction lossFunction : LossFunctions.LossFunction.values())
            				elementDropdown.getItems().add(new MenuItem(lossFunction.name()));
            		}
           		}		
           		gridPane.add(elementDropdown, 1, j); // To the middle
           	}
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
    	long seed = Long.parseLong(tableView.getItems().get(0).getValue().getSelectedItem().getText());
    	OptimizationAlgorithm optimizationAlgorithm = OptimizationAlgorithm.valueOf(tableView.getItems().get(1).getValue().getSelectedItem().getText());
    	WeightInit weightInit = WeightInit.valueOf(tableView.getItems().get(2).getValue().getSelectedItem().getText());
    	String updaterName = tableView.getItems().get(3).getValue().getSelectedItem().getText();
    	double learningRate = Double.valueOf(tableView.getItems().get(4).getValue().getSelectedItem().getText());
    	double momentum = Double.valueOf(tableView.getItems().get(5).getValue().getSelectedItem().getText());
    	String regularizationName = tableView.getItems().get(6).getValue().getSelectedItem().getText();
    	double regularizationCoefficient = Double.valueOf(tableView.getItems().get(7).getValue().getSelectedItem().getText());

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
    	 * Set the layers now. Begin to declare some arrays where we should later send back
    	 */
		String[] dropDownTypes =  dL4JSerializableConfiguration.getDropdownTypes(); // We use this for a for-loop and if-statement only! Not sending back
    	ArrayList<String> layerList = new ArrayList<String>(); 
    	ArrayList<Integer> nInList = new ArrayList<Integer>();
    	ArrayList<Integer> nOutList = new ArrayList<Integer>(); 
    	ArrayList<Activation> activationList = new ArrayList<Activation>(); 
    	ArrayList<LossFunctions.LossFunction> lossFunctionList = new ArrayList<LossFunctions.LossFunction>(); 
    	
    	/*
    	 * Get the container for the expension panel
    	 */
    	ExpansionPanelContainer expansionPanelContainer = listForView.get(0); // We only have one container
    	
    	/*
    	 * Loop thru all expansion panels that are inside the container
    	 */
    	for(int i = 0; i < expansionPanelContainer.getItems().size(); i++) {
    		/*
    		 * Get the expansion panel and get the content, e.g the grid pane, from it
    		 */
    		ExpansionPanel expansionPanel = expansionPanelContainer.getItems().get(i);
    		ExpandedPanel expandedPanel = (ExpandedPanel) expansionPanel.getExpandedContent();
    		GridPane gridPane = (GridPane) expandedPanel.getContent(); // Cast Node to GridPane
    		
    		/*
    		 * Loop thru the grid pane column 1 and row j to get the drop down button.
    		 * From the drop down button, select
    		 */
    		for(int j = 0; j < dropDownTypes.length; j++) {
    			DropdownButton dropdownButton = (DropdownButton) getNodeFromGridPane(gridPane, 1, j);
    			
    			/*
    			 * This will prevent that we get an exception later down
    			 */
    			String selectedValue;
    			try {
    				selectedValue = dropdownButton.getSelectedItem().getText();
        		}catch(NullPointerException e) {
        			selectedValue = "null";
        		}
    			System.out.println("Selected value for " + dropDownTypes[j] + " in layer " + i + " is value = " + selectedValue);
    			 
        		
        		/*
        		 * Select what kind of drop down button we have select
        		 */
        		if(dropDownTypes[j].equals(dropDownTypes[0]) == true) {
        			layerList.add(selectedValue);
        		}else if(dropDownTypes[j].equals(dropDownTypes[1]) == true) {
        			nInList.add(Integer.parseInt(selectedValue));
        		}else if(dropDownTypes[j].equals(dropDownTypes[2]) == true) {
        			nOutList.add(Integer.parseInt(selectedValue));
        		}else if(dropDownTypes[j].equals(dropDownTypes[3]) == true) {
        			activationList.add(Activation.valueOf(selectedValue));
        		}else if(dropDownTypes[j].equals(dropDownTypes[4]) == true) {
        			if(selectedValue.equals("null") == false)
        				lossFunctionList.add(LossFunctions.LossFunction.valueOf(selectedValue));
        			else
        				lossFunctionList.add(null);

        		}
    		}
    	}
    	
    	/*
    	 * Now we have filled our array lists. Send them back
    	 */
    	dL4JSerializableConfiguration.setLayerList(layerList);
    	dL4JSerializableConfiguration.setNInList(nInList);
    	dL4JSerializableConfiguration.setNOutList(nOutList);
    	dL4JSerializableConfiguration.setActivationList(activationList);
    	dL4JSerializableConfiguration.setLossFunctionList(lossFunctionList);
	}
	
	/**
	 * This will get the node from a grid pane
	 * @param gridPane Grid pane object
	 * @param col Column index
	 * @param row Row index
	 * @return Node
	 */
	private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
	    for (Node node : gridPane.getChildren()) {
	        if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
	            return node;
	        }
	    }
	    return null;
	}

	/**
     * Reload the table and get the final string arrays from DL4JJasonConfiguration class
     * and also we use the enums from internal DL4J classes such as WeightInit and OptimizationAlgorithm etc.
     */
    private boolean reloadTable()  {
    	listForTable.clear();
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
			
			if((Double.valueOf(0.001 + 0.02*i)).equals(dL4JSerializableConfiguration.getLearningRate()) == false)
				learningRateDropdown.getItems().add(new MenuItem(String.valueOf(0.001 + 0.02*i)));
			
			if((Double.valueOf(0.001 + 0.02*i)).equals(dL4JSerializableConfiguration.getMomentum()) == false)
	    		momentumDropdown.getItems().add(new MenuItem(String.valueOf(0.001 + 0.02*i)));			
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
    	 * Create table now
    	 */
    	for(int i = 0; i < configurationNames.length; i++)
    		listForTable.add(new GlobalConfigurationTable(configurationNames[i], dropDownButtonList.get(i)));
    	
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
		if(expansionPanelContainer.getItems().size() > 1) // We need at least have one layer
			expansionPanelContainer.getItems().remove(expansionPanelContainer.getItems().size() -1);
	}

	/**
	 * Add a layer
	 */
	private void addLayer() {
		/*
    	 * Create a expanded panels from these 
    	 */
		String[] dropDownTypes =  dL4JSerializableConfiguration.getDropdownTypes(); 

    	/*
    	 * New expansions panel and add it to the expansions panel container
    	 */
        ExpansionPanel expansionPanel = new ExpansionPanel();
        expansionPanelContainer.getItems().add(expansionPanel);
        	
       	/*
         * Create an collapsed panel and first set a label with the name of the layer index e.g. layer 0, layer 1 etc and the layer type
         */
        CollapsedPanel collapsedPanel = new CollapsedPanel();
        collapsedPanel.getTitleNodes().add(0, new Label("Layer " + expansionPanelContainer.getItems().size())); // Layer index
       	collapsedPanel.getTitleNodes().add(1, new Label("DenseLayer")); // Layer type
        expansionPanel.setCollapsedContent(collapsedPanel); // Set collapsed panel into expansion panel
    		
    	/*
         * Create an expanded panel and begin to set a grid pane into it
         */
        ExpandedPanel expandedPanel = new ExpandedPanel();
        GridPane gridPane = new GridPane();
        expandedPanel.setContent(gridPane);
        expansionPanel.setExpandedContent(expandedPanel);
        	
       	/*
         * Add all drop downs and labels
         */
        for(int j = 0; j < dropDownTypes.length; j++) {
        	/*
        	 * Create the  element label and set its position inside the grid
        	 */
        	Label elementLabel = new Label(dropDownTypes[j]);
        	gridPane.add(elementLabel, 0, j); // To the left
        	
       		/*
           	 * Create the drop down button and add the elements and set its position inside the grid
           	 */
        	DropdownButton elementDropdown = new DropdownButton();
        	if(dropDownTypes[j].equals(dropDownTypes[0])) { // Layer types
           		elementDropdown.getItems().addAll(new MenuItem("DenseLayer"), new MenuItem("LSTM"), new MenuItem("OutputLayer"));
        	}else if(dropDownTypes[j].equals(dropDownTypes[1])) { // Inputs
       			for(int i = 1; i <= 100; i++)
           			elementDropdown.getItems().add(new MenuItem(String.valueOf(i)));
        	}else if(dropDownTypes[j].equals(dropDownTypes[2])) { // Outputs
        		for(int i = 1; i <= 100; i++)
           			elementDropdown.getItems().add(new MenuItem(String.valueOf(i)));
        	}else if(dropDownTypes[j].equals(dropDownTypes[3])) { // Activations
        		for(Activation activation : Activation.values())
           			elementDropdown.getItems().add(new MenuItem(activation.name()));
        	}else if(dropDownTypes[j].equals(dropDownTypes[4])) { // Loss functions
        		for(LossFunctions.LossFunction lossFunction : LossFunctions.LossFunction.values())
       				elementDropdown.getItems().add(new MenuItem(lossFunction.name()));
       		}		
       		gridPane.add(elementDropdown, 1, j); // To the middle
       	}
	}
}
