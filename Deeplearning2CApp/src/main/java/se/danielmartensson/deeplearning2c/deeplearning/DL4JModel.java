package se.danielmartensson.deeplearning2c.deeplearning;

import java.io.File;
import java.io.IOException;

import org.deeplearning4j.exception.DL4JInvalidConfigException;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import javafx.scene.control.Alert.AlertType;
import lombok.Getter;
import se.danielmartensson.deeplearning2c.tools.Dialogs;
import se.danielmartensson.deeplearning2c.tools.FileHandler;

public class DL4JModel {
	
	// Tools
	private Dialogs dialogs = new Dialogs();
	private FileHandler fileHandler = new FileHandler();
	
	// DL4J
	private @Getter DL4JSerializableConfiguration dL4JSerializableConfiguration = new DL4JSerializableConfiguration();
	private @Getter DL4JData dL4JData = new DL4JData();
	private @Getter MultiLayerNetwork multiLayerNetwork;
	private MultiLayerConfiguration multiLayerConfiguration;
	private ListBuilder listBuilder;
	private Builder builder;

	public DL4JModel() {
		
	}
	
	/**
	 * Create a basic model - We should always create a model after we have create a file
	 * @param filePath Our file path
	 */
	public void createBasicModel(String filePath) { 
		// Create a json global configuration
		dL4JSerializableConfiguration.setSeed(100);
		dL4JSerializableConfiguration.setOptimizationAlgorithm(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
		dL4JSerializableConfiguration.setWeightInit(WeightInit.XAVIER);
		dL4JSerializableConfiguration.setUpdaterName("Sgd");
		dL4JSerializableConfiguration.setLearningRate(0.01);
		dL4JSerializableConfiguration.setMomentum(0.01);
		dL4JSerializableConfiguration.setRegularizationName("L1");
		dL4JSerializableConfiguration.setRegularizationCoefficient(Math.pow(10, 0));
		// Add more here...
		
		// Layer configuration - We need at least ONE layer
		dL4JSerializableConfiguration.clearLayer();
		dL4JSerializableConfiguration.addLayer("DenseLayer", 4, 3, Activation.TANH, LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD, 1.0); // Loss function does not effect this layer
		dL4JSerializableConfiguration.addLayer("DenseLayer", 3, 5, Activation.RELU, LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD, 1.0); // Loss function does not effect this layer
		dL4JSerializableConfiguration.addLayer("DenseLayer", 5, 7, Activation.RELU, LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD, 1.0); // Loss function does not effect this layer
		dL4JSerializableConfiguration.addLayer("OutputLayer", 7, 3, Activation.SOFTMAX, LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD, 1.0); // Loss function effect this layer
		// Add more here...
		
		// Generate model from the configurations
		generateModel();

	    // Save the model - Initial .zip file need to exist first!   
	    saveModel(filePath);
	}
	
	/**
	 * This will generate a model from the configuration inside DL4JSerializableConfiguration class
	 */
	public void generateModel() {
		/*
		 * Build the configuration by first create the builder for global configuration.
		 * Then create the layers and build everything to a multilayer configuration
		 */
		builder = new NeuralNetConfiguration.Builder();
		listBuilder = dL4JSerializableConfiguration.runConfiguration(builder);
		try {
			multiLayerConfiguration = listBuilder.build();
		} catch(IllegalStateException | DL4JInvalidConfigException e) {
			dialogs.alertDialog(AlertType.ERROR, "Cannot create model", e.getMessage());
			dL4JSerializableConfiguration.clearLayer();
			dL4JSerializableConfiguration.setModelName(null);
			return;
		}
		
		// Create the network model from multilayer configuration
		multiLayerNetwork = new MultiLayerNetwork(multiLayerConfiguration);
		
		// Start model - Need to do that so we can save it
		multiLayerNetwork.init();
	}
	
	/**
	 * Save model and serializable
	 * @param filePath Our file path
	 */
	public void saveModel(String filePath) {
		File locationToSave = fileHandler.loadNewFile(filePath);   
	    boolean saveUpdater = true;                                            
	    try {
			multiLayerNetwork.save(locationToSave, saveUpdater);
			dL4JSerializableConfiguration.saveSerializable(filePath.replace(".zip", ".ser"));
		} catch (IOException | IllegalStateException e) {
			dialogs.exception("Cannot save model:\n" + filePath, e);
		}
	}
	
	/**
	 * Load model and deserializable
	 * @param filePath Our file path
	 * @param displaySuccessDialog Set this to true if you want a success message, false if you don't want to see it.
	 */
	public void loadModel(String filePath, boolean displaySuccessDialog) {
		File locationToLoad = fileHandler.loadNewFile(filePath); 
		boolean saveUpdater = true;
		try {
			multiLayerNetwork = MultiLayerNetwork.load(locationToLoad, saveUpdater);
			dL4JSerializableConfiguration.loadDeserializable(filePath.replace(".zip", ".ser")); 
			if(displaySuccessDialog == true)
				dialogs.alertDialog(AlertType.INFORMATION, "Success", "Model loaded");
		} catch (IOException | IllegalStateException e) {
			dialogs.exception("Cannot load model:\n" + filePath, e);
		}
	}
}
