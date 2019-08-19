package se.danielmartensson.views;

import java.util.ArrayList;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.weights.WeightInit;
import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
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
import se.danielmartensson.tools.StaticDependencyInjection;
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
    private CharmListView<String, String> listView;
    
    @FXML
    private Tab globalConfigurationTab;
    
    @FXML
    private Tab layerConfigurationTab;
        
	/*
	 * Tools
	 */
	private Dialogs dialogs = new Dialogs();
	
	/*
     * Injected from Spring
     */
    private DL4JModel dL4JModel;
    
    /*
     * Regular fields for the view
     */
    private ObservableList<GlobalConfigurationTable> list;
	private AppBar appBar;

    public void initialize() {
    	/*
		 * Dependency injection
		 */
    	dL4JModel = StaticDependencyInjection.contextDL4J.getBean("dL4JModel", DL4JModel.class);
		
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
        list = FXCollections.observableArrayList();
        tableView.setItems(list);
        
        /*
         * Listener for tabs
         */
        globalConfigurationTab.selectedProperty().addListener(e -> removePlusMinus());
        layerConfigurationTab.selectedProperty().addListener(e -> addPlusMinus());
    }

    /**
     * This method will get the dL4JSerializableConfiguration object and load its getters
     * from the tableView object. From tableView object, we will get its drop down buttons
     * and from these drop down buttons, we will get what we have selected
     */
	private void saveConfigurationsLayers() {
		/*
    	 * Get the configuration and layers
    	 */
    	DL4JSerializableConfiguration dL4JSerializableConfiguration = dL4JModel.getDL4JSerializableConfiguration();
    	
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
	}

	/**
     * Reload the table and get the final string arrays from DL4JJasonConfiguration class
     * and also we use the enums from internal DL4J classes such as WeightInit and OptimizationAlgorithm etc.
     */
    private boolean reloadTable()  {
    	list.clear();
    	
    	/*
    	 * Get the configuration and layers
    	 */
    	DL4JSerializableConfiguration dL4JSerializableConfiguration = dL4JModel.getDL4JSerializableConfiguration();
    	
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
    		list.add(new GlobalConfigurationTable(configurationNames[i], dropDownButtonList.get(i)));
    	
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

	private void removeLayer() {

	}

	private void addLayer() {

	}
	
	
}
