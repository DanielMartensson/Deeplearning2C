package se.danielmartensson.deeplearning;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

/**
 * This class is made for creations of the layers
 */
public class DL4JLayers {

	/*
	 * Objects
	 */
	private ListBuilder listBuilder;
	
	/**
	 * Constructor
	 * @param listBuilder Our neural network layer holder
	 */
	public DL4JLayers(ListBuilder listBuilder) {
		this.listBuilder = listBuilder;
	}
	
	/**
	 * Add a dense layer
	 * @param inputs How many inputs?
	 * @param outputs How many outputs?
	 * @param activation What type of activation function?
	 */
	public void addDenseLayer(int inputs, int outputs, Activation activation) {
		listBuilder.layer(new DenseLayer.Builder()
				.nIn(inputs)
				.nOut(outputs)
				.activation(activation)
				.build());
	}
	
	/**
	 * Add a LSTM layer
	 * @param inputs How many inputs?
	 * @param outputs How many outputs?
	 * @param activation What type of activation function?
	 */
	public void addLSTMLayer(int inputs, int outputs, Activation activation) {
		listBuilder.layer(new LSTM.Builder()
				.nIn(inputs)
				.nOut(outputs)
				.activation(activation)
				.build());
	}
	
	/**
	 * Add a output layer
	 * @param inputs How many inputs?
	 * @param outputs How many outputs?
	 * @param activation What type of activation function?
	 * @param lossFunction What type of loss function?
	 */
	public void addOutputLayer(int inputs, int outputs, Activation activation, LossFunction lossFunction) {
		listBuilder.layer(new OutputLayer.Builder(lossFunction)
				.nIn(inputs)
				.nOut(outputs)
				.activation(activation)
				.build());
	}
	
	/**
	 * Add a RNN Output layer
	 * @param inputs How many inputs?
	 * @param outputs How many outputs?
	 * @param activation What type of activation function?
	 * @param lossFunction What type of loss function?
	 */
	public void addRnnOutputLayer(int inputs, int outputs, Activation activation, LossFunction lossFunction) {
		listBuilder.layer(new RnnOutputLayer.Builder(lossFunction)
				.nIn(inputs)
				.nOut(outputs)
				.activation(activation)
				.build());
	}

}
