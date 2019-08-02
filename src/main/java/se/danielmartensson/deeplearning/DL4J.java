package se.danielmartensson.deeplearning;

import java.util.Random;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class DL4J {
	
	/*
	 * Deeplearning4J objects
	 */
	private Builder globalConfig;
	private ListBuilder listBuilder;
	
	/*
	 * Deeplearning4J methods in other classes
	 */
	private DL4JGlobalConfig dL4JGlobalConfig;
	private DL4JLayers dL4JLayers;
	private DL4JModel dL4JModel;
	private DL4JSaveLoad dl4JSaveLoad;

	public DL4J(){
		/*
		 * Global configurations and layer holder
		 */
		globalConfig = new NeuralNetConfiguration.Builder(); // Our global configuration for neural network
		listBuilder = new NeuralNetConfiguration.ListBuilder(globalConfig); // This hold layers
		
		/*
		 * Then create objects for our under classes
		 */
		dL4JGlobalConfig = new DL4JGlobalConfig(globalConfig);
		dL4JLayers = new DL4JLayers(listBuilder);
		dL4JModel = new DL4JModel(listBuilder);
		dl4JSaveLoad = new DL4JSaveLoad(dL4JModel);
		
	}
	
	/*
	 * Create an initial neural network model
	 */
	public void initialModel() {
		// Configurations
		dL4JGlobalConfig.setSeed(new Random().nextInt(100) + 300); // Set random number 100-300
		dL4JGlobalConfig.setOptimizationAlgorithm(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT); // Set default optimization algorithm
		dL4JGlobalConfig.setRegularization("L1", 0.5); // L1-Regularization with 0.5
		dL4JGlobalConfig.setUpdater(new Adam()); //  Adam updater
		dL4JGlobalConfig.setWeightInit(WeightInit.XAVIER); // Initial weights
		// Layers
		dL4JLayers.addDenseLayer(5, 10, Activation.RELU); // Input layer
		dL4JLayers.addOutputLayer(10, 5, Activation.RELU, LossFunction.NEGATIVELOGLIKELIHOOD); // Output layer
		// Model
		dL4JModel.buildModel(); // Build model now - No more are required
	}

	public DL4JGlobalConfig getdL4JGlobalConfig() {
		return dL4JGlobalConfig;
	}

	public DL4JLayers getdL4JLayers() {
		return dL4JLayers;
	}

	public DL4JModel getdL4JNetwork() {
		return dL4JModel;
	}

	public DL4JSaveLoad getDl4JSaveLoad() {
		return dl4JSaveLoad;
	}
	
	
}
