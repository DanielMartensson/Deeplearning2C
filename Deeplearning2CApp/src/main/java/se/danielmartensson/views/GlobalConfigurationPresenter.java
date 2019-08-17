package se.danielmartensson.views;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.DropdownButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.stage.Screen;
import se.danielmartensson.deeplearning.DL4JGlobalConfig;

public class GlobalConfigurationPresenter {

	@FXML
    private View globalconfig;

    @FXML
    private ColumnConstraints gridPane0;

    @FXML
    private ColumnConstraints gridPane1;

    @FXML
    private Label learningRateLabel;
    
    @FXML
    private Label momentumLabel;

    @FXML
    private DropdownButton seedDropDown;

    @FXML
    private DropdownButton optimizationAlgorithmDropDown;

    @FXML
    private DropdownButton updaterDropDown;

    @FXML
    private DropdownButton learningRateDropDown;
    
    @FXML
    private DropdownButton momentumDropDown;

    @FXML
    private DropdownButton regularizationDropDown;

    @FXML
    private DropdownButton l1L2CoefficientDropDown;

    @FXML
    private DropdownButton weightInitializerDropDown;

	private DL4JGlobalConfig dL4JGlobalConfig;

    public void initialize() {
    	globalconfig.setShowTransitionFactory(BounceInRightTransition::new);
        globalconfig.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppBar appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> MobileApplication.getInstance().getDrawer().open()));
                appBar.setTitleText("Global Configuration");
                
                /*
        		 * When we slide into this page - Do a search and select correct drop down button
        		 */
                Platform.runLater(() -> {
	        		searchItem(seedDropDown, DL4JGlobalConfig.seed);
	        		searchItem(optimizationAlgorithmDropDown, DL4JGlobalConfig.optimizationAlgorithm);
	        		searchItem(updaterDropDown, DL4JGlobalConfig.updater);
	        		searchItem(learningRateDropDown, DL4JGlobalConfig.learningRate);
	        		searchItem(momentumDropDown, DL4JGlobalConfig.momentum);
	        		searchItem(regularizationDropDown, DL4JGlobalConfig.regularization);
	        		searchItem(l1L2CoefficientDropDown, DL4JGlobalConfig.coefficient);
	        		searchItem(weightInitializerDropDown, DL4JGlobalConfig.weight);
                });
            }else {
            	/*
            	 * When we going back and leave the page
            	 */
            	String seed = seedDropDown.getSelectedItem().getText();
            	String optimizationAlgorithm = optimizationAlgorithmDropDown.getSelectedItem().getText();
            	String updater = updaterDropDown.getSelectedItem().getText();
            	String learningRate = learningRateDropDown.getSelectedItem().getText();
            	String momentum = momentumDropDown.getSelectedItem().getText();
            	String regularization = regularizationDropDown.getSelectedItem().getText();
            	String coefficient = l1L2CoefficientDropDown.getSelectedItem().getText();
            	String weightInit = weightInitializerDropDown.getSelectedItem().getText();
            	
            	/*
            	 * Save to object
            	 */
            	dL4JGlobalConfig.setSeed(seed);
            	dL4JGlobalConfig.setOptimizationAlgorithm(optimizationAlgorithm);
            	dL4JGlobalConfig.setUpdater(updater, learningRate, momentum);
            	dL4JGlobalConfig.setRegularization(regularization, coefficient);
            	dL4JGlobalConfig.setWeightInit(weightInit);
            }
        });
        
        /*
		 * Change the components to correct size - Only need once!
		 */
		double heightScreen = Screen.getPrimary().getBounds().getHeight();
		double widthScreen = Screen.getPrimary().getBounds().getWidth();
		globalconfig.setPrefSize(widthScreen, heightScreen);
		gridPane0.setPrefWidth(widthScreen*0.1);
		gridPane1.setPrefWidth(widthScreen*0.9);
        
        /*
		 * Connect to our DL4J classes and its functionality 
		 */
		Platform.runLater(() -> {
			ApplicationContext context = new FileSystemXmlApplicationContext("/src/main/resources/se/danielmartensson/beans/DL4JBeans.xml");
			dL4JGlobalConfig = context.getBean("dL4JGlobalConfig", DL4JGlobalConfig.class);
			((FileSystemXmlApplicationContext) context).close();
		});
		
		/*
		 * Just load the drop down buttons
		 */
		loadDropDownButtons();

		/*
		 * Listener for updater drop down button
		 */
		updaterDropDown.selectedItemProperty().addListener(e -> {
			String updateName = updaterDropDown.getSelectedItem().getText();
			if(updateName.equals(("AdaDelta")) == true || updateName.equals(("NoOp")) == true){
				learningRateLabel.setText("-");
				momentumLabel.setText("-");
				learningRateDropDown.setDisable(true);
				momentumDropDown.setDisable(true);
			}else if(updateName.equals(("Nesterovs")) == true){
				learningRateLabel.setText("Learning rate:");
				momentumLabel.setText("Momentum:");
				learningRateDropDown.setDisable(false);
				momentumDropDown.setDisable(false);
			}else {
				learningRateLabel.setText("Learning rate:");
				momentumLabel.setText("-");
				learningRateDropDown.setDisable(false);
				momentumDropDown.setDisable(true);
			}
		});
    }
    
    /**
     * Search and select
     * @param dropDownButton Our drop down button object
     * @param selected Our selected value from the object
     */
    private void searchItem(DropdownButton dropDownButton, String selected) {
    	System.out.println("Selecting: " + selected);
    	for(int i = 0; i < dropDownButton.getItems().size(); i++)
    		if(dropDownButton.getItems().get(i).getText().equals(selected) == true) {
    			dropDownButton.setSelectedItem(dropDownButton.getItems().get(i)); // Match!
    			System.out.println("Selected = " + dropDownButton.getItems().get(i).getText());
    			break;
    		}
	}

	/**
	 * Load the drop down buttons
	 * Remember that you need to change the following methods if you change this method:
	 * 1. createModel(String name) in NeuralNetworkPresenter.java
	 * 2. newRow(String text) in CSVHandler.java
	 * 3. All methods inside DL4JGlobalConfig.java
	 */
    private void loadDropDownButtons() {
    	String[] seeds = {"100", "200", "300", "400", "500", "600", "700", "800", "900", "1000"};
    	String[] optimizationAlgorithms = {"STOCHASTIC_GRADIENT_DESCENT", "CONJUGATE_GRADIENT", "LINE_GRADIENT_DESCENT", "LBFGS"};
    	String[] updaters = {"AdaDelta", "AdaGrad", "Adam", "AdaMax", "AMSGrad", "Nadam", "Nesterovs", "RmsProp", "NoOp", "Sgd"};
    	String[] learningRates = {"0.01", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0"};
    	String[] momentums = {"0.01", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0"};
    	String[] regularizations = {"L1", "L2"};
    	String[] l1L2Coefficients = {"1e-10", "1e-9", "1e-8", "1e-7", "1e-6", "1e-5", "1e-4", "1e-3", "1e-2", "1e-1", "1e-0"};
    	String[] weightInits = {"XAVIER", "ONES", "ZERO", "NORMAL", "RELU", "UNIFORM"};
    	
    	/*
    	 * Load these string arrays above into drop down buttons
    	 */
    	for(int i = 0; i < seeds.length; i++) seedDropDown.getItems().add(new MenuItem(seeds[i]));
    	for(int i = 0; i < optimizationAlgorithms.length; i++) optimizationAlgorithmDropDown.getItems().add(new MenuItem(optimizationAlgorithms[i]));
    	for(int i = 0; i < updaters.length; i++) updaterDropDown.getItems().add(new MenuItem(updaters[i]));
    	for(int i = 0; i < learningRates.length; i++) learningRateDropDown.getItems().add(new MenuItem(learningRates[i]));
    	for(int i = 0; i < momentums.length; i++) momentumDropDown.getItems().add(new MenuItem(momentums[i]));
    	for(int i = 0; i < regularizations.length; i++) regularizationDropDown.getItems().add(new MenuItem(regularizations[i]));
    	for(int i = 0; i < l1L2Coefficients.length; i++) l1L2CoefficientDropDown.getItems().add(new MenuItem(l1L2Coefficients[i]));
    	for(int i = 0; i < weightInits.length; i++) weightInitializerDropDown.getItems().add(new MenuItem(weightInits[i]));
    }
}
