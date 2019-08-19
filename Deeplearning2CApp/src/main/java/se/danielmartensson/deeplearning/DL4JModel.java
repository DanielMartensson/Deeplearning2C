package se.danielmartensson.deeplearning;

import java.io.File;
import java.io.IOException;

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
import se.danielmartensson.tools.Dialogs;
import se.danielmartensson.tools.FileHandler;

public class DL4JModel {
	
	/*
	 * Tools
	 */
	private Dialogs dialogs = new Dialogs();
	private FileHandler fileHandler = new FileHandler();
	
	/*
	 * DL4J
	 */
	private @Getter DL4JSerializableConfiguration dL4JSerializableConfiguration = new DL4JSerializableConfiguration();
	private MultiLayerNetwork multiLayerNetwork;
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
		/*
		 * Create a json global configuration
		 */
		dL4JSerializableConfiguration.setSeed(100);
		dL4JSerializableConfiguration.setOptimizationAlgorithm(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
		dL4JSerializableConfiguration.setWeightInit(WeightInit.XAVIER);
		dL4JSerializableConfiguration.setUpdaterName("Sgd");
		dL4JSerializableConfiguration.setLearningRate(0.01);
		dL4JSerializableConfiguration.setMomentum(0.01);
		dL4JSerializableConfiguration.setRegularizationName("L1");
		dL4JSerializableConfiguration.setRegularizationCoefficient(Math.pow(10, 0));
		
		/*
		 * Layer configuration
		 */
		dL4JSerializableConfiguration.clearLayer();
		dL4JSerializableConfiguration.addLayer("DenseLayer", 4, 3, Activation.TANH, null);
		dL4JSerializableConfiguration.addLayer("LSTM", 3, 3, Activation.RELU, null);
		dL4JSerializableConfiguration.addLayer("OutputLayer", 3, 3, Activation.SOFTMAX, LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD);
		
		/*
		 * Build the configuration by first create the builder for global configuration.
		 * Then create the layers and build everything to a multilayer configuration
		 */
		builder = new NeuralNetConfiguration.Builder();
		listBuilder = dL4JSerializableConfiguration.runConfiguration(builder);
		multiLayerConfiguration = listBuilder.build();
		
		/*
		 * Create the network model from multilayer configuration
		 */
		multiLayerNetwork = new MultiLayerNetwork(multiLayerConfiguration);
		
		/*
		 * Start model
		 */
		multiLayerNetwork.init();

	    /*
	     * Save the model - Initial .zip file need to exist first!
	     * Clear layers
	     */   
	    saveModel(filePath);
	    dL4JSerializableConfiguration.clearLayer();
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
		} catch (IOException e) {
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
		} catch (IOException e) {
			dialogs.exception("Cannot load model:\n" + filePath, e);
		}
	}
}
