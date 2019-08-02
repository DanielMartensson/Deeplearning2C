package se.danielmartensson.deeplearning;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
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
	
	public DL4JLayers(ListBuilder listBuilder) {
		this.listBuilder = listBuilder;
	}
	
	/*
	 * Add a dense layer
	 */
	public void addDenseLayer(int inputs, int outputs, Activation activation) {
		listBuilder.layer(new DenseLayer.Builder()
				.nIn(inputs)
				.nOut(outputs)
				.activation(activation)
				.build());
	}
	
	/*
	 * Add a output layer
	 */
	public void addOutputLayer(int inputs, int outputs, Activation activation, LossFunction lossFunction) {
		listBuilder.layer(new OutputLayer.Builder(lossFunction)
				.nIn(inputs)
				.nOut(outputs)
				.activation(activation)
				.build());
	}
	

}
