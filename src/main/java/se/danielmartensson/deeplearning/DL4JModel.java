package se.danielmartensson.deeplearning;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;

/**
 * This class is will handle the train, evaluation and simulation
 */
public class DL4JModel {

	/*
	 * Objects
	 */
	private ListBuilder listBuilder;
	private MultiLayerNetwork multiLayerNetwork;
	
	public DL4JModel(ListBuilder listBuilder) {
		this.listBuilder = listBuilder;
	}
	
	/*
	 * Build model
	 */
	public void buildModel() {
		multiLayerNetwork = new MultiLayerNetwork(listBuilder.build());
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
	 * Set train data to the model
	 */
	
	
	/*
	 * Train the model
	 */
	public void trainModel() {
		multiLayerNetwork.fit();
	}
	
	protected MultiLayerNetwork getMultiLayerNetwork() {
		return multiLayerNetwork;
	}
}
