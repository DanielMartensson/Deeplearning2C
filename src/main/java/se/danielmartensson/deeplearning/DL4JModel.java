package se.danielmartensson.deeplearning;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.regression.RegressionEvaluation;

/**
 * This class is will handle the train, evaluation and simulation
 */
public class DL4JModel {

	/*
	 * Objects
	 */
	private ListBuilder listBuilder;
	private MultiLayerNetwork multiLayerNetwork;
	private MultiLayerConfiguration multiLayerConfiguration;
	
	public DL4JModel(ListBuilder listBuilder) {
		this.listBuilder = listBuilder;
	}
	
	/*
	 * Build model
	 */
	public void buildModel() {
		multiLayerConfiguration = listBuilder.build();
		multiLayerNetwork = new MultiLayerNetwork(multiLayerConfiguration);
	}

	/*
	 * Start the model
	 */
	public void initModel() {
		multiLayerNetwork.init();
	}
	
	/*
	 * Set a listener
	 */
	public void setListener(int printIterations) {
		multiLayerNetwork.setListeners(new ScoreIterationListener(printIterations));
	}
	
	/*
	 * Train the model
	 */
	public void trainModel(int iterations) {
		for(int i = 0; i < iterations; i++)
			multiLayerNetwork.fit();
	}
	
	/*
	 * Evaluate the model
	 */
	public void evaluateModel() {
		//RegressionEvaluation regressionEvaluation = multiLayerNetwork.evaluateRegression(myTestData);
	}
	
	protected MultiLayerNetwork getMultiLayerNetwork() {
		return multiLayerNetwork;
	}

	protected MultiLayerConfiguration getMultiLayerConfiguration() {
		return multiLayerConfiguration;
	}
	
}
