package se.danielmartensson.views;
import com.gluonhq.charm.glisten.animation.BounceInRightTransition;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.ProgressBar;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.deeplearning4j.exception.DL4JException;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Screen;
import se.danielmartensson.deeplearning.DL4JModel;
import se.danielmartensson.deeplearning.DL4JThread;
import se.danielmartensson.tools.Dialogs;
import se.danielmartensson.tools.FileHandler;
import se.danielmartensson.tools.SimpleDependencyInjection;

public class TrainEvalGeneratePresenter {

    @FXML
    private View view;

    @FXML
    private TextArea textArea;
    
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private RowConstraints gridPane0;

    @FXML
    private RowConstraints gridPane1;

    /*
     * Fields
     */
	private AppBar appBar;
	private Dialogs dialogs = new Dialogs();
	FileHandler fileHandler = new FileHandler();
	private DL4JModel dL4JModel;
	private AtomicBoolean continueLoop = new AtomicBoolean();
	private final String cPath = "/Deeplearning2CStorage/C/";
	
    @FXML
    void initialize() {
    	/*
		 * Dependency injection
		 */
    	dL4JModel = SimpleDependencyInjection.getDL4JModel();
    	
    	/*
    	 * Slide smooth in and out
    	 */
    	view.setShowTransitionFactory(BounceInRightTransition::new);
        
        /*
    	 * Listener for leaving and enter the page
    	 */
    	view.showingProperty().addListener((obs, oldValue, newValue) -> {
    		if (newValue) {
            	/*
                 * Enter the page
                 */
                appBar = MobileApplication.getInstance().getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> MobileApplication.getInstance().getDrawer().open()));
                appBar.setTitleText("Train Eval Generate");
                
                /*
                 * Listeners for appBar
                 */
                appBar.getActionItems().add(MaterialDesignIcon.BUILD.button(e -> trainModel()));
        		appBar.getActionItems().add(MaterialDesignIcon.BUSINESS.button(e -> evaluateModel()));
        		appBar.getActionItems().add(MaterialDesignIcon.COPYRIGHT.button(e -> generateCCode()));
        		
        		/*
        		 * Generate a model from the configuration when we slide in...if we have selected a model
        		 */
        		if(dL4JModel.getDL4JSerializableConfiguration().getModelName() == null)
        			dialogs.alertDialog(AlertType.INFORMATION, "Model", "No model where selected");
        		else
        			dL4JModel.generateModel();
        		
        		if(dL4JModel.getDL4JData().getTrainDataSetIterator() == null)
        			dialogs.alertDialog(AlertType.INFORMATION, "Data", "No training data has been selected");
    		}
    	});
    	
    	/*
		 * Change the progress bar to correct size
		 */
		double heightScreen = Screen.getPrimary().getBounds().getHeight();
		double widthScreen = Screen.getPrimary().getBounds().getWidth();
		view.setPrefSize(widthScreen, heightScreen);
		gridPane0.setPrefHeight(widthScreen*0.55);
		gridPane1.setPrefHeight(heightScreen*0.45);
		progressBar.setPrefSize(widthScreen, heightScreen*0.45);
		
    }

    /**
     * This method will generate C-code
     */
	private void generateCCode() {
		/*
		 * Do a quick check!
		 */
		if(dL4JModel.getDL4JSerializableConfiguration().getModelName() == null)
			return;
		
		/*
		 * Get local date
		 */
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime localDateTime = LocalDateTime.now();  
		String creationDate = dateTimeFormatter.format(localDateTime);
		
		/*
		 * Get table
		 */
		Map<String, INDArray> weights = dL4JModel.getMultiLayerNetwork().paramTable();
		Set<String> weightsName = weights.keySet();
		
		/*
		 * Write header file
		 */
		String modelName = dL4JModel.getDL4JSerializableConfiguration().getModelName();
		String comment = "/*\n" + 
				" * " + modelName + ".h\n" + 
				" *\n" + 
				" *  Created on: " + creationDate + "\n" + 
				" *  	Generated by: Deeplearning2C\n" +
				" *     		Author: Daniel Mårtensson\n" + 
				" */\n\n";
		String ifndef = "#ifndef " + modelName.toUpperCase() + "_H_\n";
		String define = "#define " + modelName.toUpperCase() + "_H_\n\n";
		String function = "void " + modelName + "(float* input, float* output);\n\n";
		String endif = "#endif /* " + modelName.toUpperCase() + "_H_ */";

		/*
		 * Write .h file now
		 */
		String absolutPath = cPath + modelName + ".h";
		fileHandler.writeTextTo(absolutPath, comment + ifndef + define + function + endif);
		
		/*
		 * Write .c file now
		 */
		String include = "#include " + modelName + ".h\n\n";
		String functionStart = "void " + modelName + "(float* input, float* output){\n";
		String arrays = "";
		for(String weightName : weightsName) {
			INDArray selectedWeight = weights.get(weightName);
			int totalRows = selectedWeight.rows();
			int totalColumns = selectedWeight.columns();
			arrays += "\tconst float " + new StringBuilder(weightName).reverse().toString() + "["+totalRows+"]["+totalColumns+"]=";
			String firstPart = selectedWeight.toString().replace("[", "{"); // Just replace [ to { and ] to } and we got a C-array!
			String secondPart = firstPart.replace("]", "}");
			arrays += secondPart.replace("\n", "\n\t\t\t\t\t\t   "); // This like make the matrices symmetrical
			arrays += ";\n\n";
		}
		String functionEnd = "}";
		absolutPath = cPath + modelName + ".c";
		fileHandler.writeTextTo(absolutPath, comment + include + functionStart + arrays + functionEnd);

		
		
		
	}

	/**
	 * This method will evaluate the model. 
	 * From the data iteration set, we select a random data set and evaluate it
	 */
	private void evaluateModel() {
		/*
		 * Do a quick check!
		 */
		if(dL4JModel.getDL4JSerializableConfiguration().getModelName() == null)
			return;
		if(dL4JModel.getDL4JData().getTrainDataSetIterator() == null)
			return;
		
		/*
		 * Clear first the text box and the progressBar
		 */
		textArea.clear();
		progressBar.setProgress(0);
		
		/*
		 * Reset our main data for both resetting train and eval objects of data iteration sets
		 */
		dL4JModel.getDL4JData().getDataSetIterator().reset();
		
		/*
		 * Get the data set from a random batch inside data set iteration
		 */
		DataSetIterator dataTrainSetIterator = dL4JModel.getDL4JData().getTrainDataSetIterator();
		DataSet dataTrainSet = dataTrainSetIterator.next();
		
		/*
		 * Get model and its evaluation
		 */
		MultiLayerNetwork multiLayerNetwork = dL4JModel.getMultiLayerNetwork();
		RegressionEvaluation regressionEvaluation;
		Evaluation evaluation;
		
		/*
		 * Do we have regression or classification data?
		 */
		boolean regression = dL4JModel.getDL4JData().isRegression();
		if(regression == true) {
			/*
			 * Do a test for regression and print it out
			 */
			regressionEvaluation = multiLayerNetwork.evaluateRegression(dataTrainSetIterator);
			INDArray output = multiLayerNetwork.output(dataTrainSet.getFeatures());
			regressionEvaluation.eval(dataTrainSet.getLabels(), output);
			String status = regressionEvaluation.stats();
			textArea.setText(status);
		}else {
			/*
			 * Do a test for classification and print it out 
			 */
			evaluation = multiLayerNetwork.evaluate(dataTrainSetIterator);
			INDArray output = multiLayerNetwork.output(dataTrainSet.getFeatures());
			evaluation.eval(dataTrainSet.getLabels(), output);
			String status = evaluation.stats();
			textArea.setText(status);
		}
	}

	/**
	 * Train our model!
	 */
	private void trainModel() {
		/*
		 * Do a quick check!
		 */
		if(dL4JModel.getDL4JSerializableConfiguration().getModelName() == null)
			return;
		if(dL4JModel.getDL4JData().getTrainDataSetIterator() == null)
			return;
		
		/*
		 * Reset our main data for both resetting train and eval objects of data iteration sets
		 */
		dL4JModel.getDL4JData().getDataSetIterator().reset();
		
		/*
		 * Ask for a epoch number! Minimum is 0
		 */
		int epochs = 0;
		try {
			epochs = Integer.parseInt(dialogs.input("Epochs", "Enter (int)epochs for training?"));
			if(epochs < 0) {
				dialogs.alertDialog(AlertType.INFORMATION, "Zero", "You cannot have 0 epochs!");
				return;
			}
		}catch(NumberFormatException e) {
			dialogs.exception("Cannot convert that text into an integer number!", e);
			return;
		}
		
		/*
		 * Create our thread object and start it. When we start it, then the BUILD icon changes to STOP icon
		 */
		continueLoop.set(true);
		textArea.clear();
		progressBar.setProgress(0);
		DL4JThread dL4JThread = new DL4JThread(dL4JModel.getMultiLayerNetwork(), dL4JModel.getDL4JData().getTrainDataSetIterator(), textArea, progressBar, continueLoop, epochs);
		appBar.getActionItems().remove(0);
		appBar.getActionItems().add(0, MaterialDesignIcon.STOP.button(e -> stopTrain()));
		try {
			dL4JThread.start();
		}catch(DL4JException | IllegalStateException | NullPointerException e) {
			dialogs.exception("Cannot train model", e);
		}
		
	}

	/**
	 * This method will stop the thread and then change back to BUILD icon 
	 */
	private void stopTrain() {
		continueLoop.set(false);
		appBar.getActionItems().remove(0);
		appBar.getActionItems().add(0, MaterialDesignIcon.BUILD.button(e -> trainModel()));
	}

}
