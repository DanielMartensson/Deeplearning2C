package se.danielmartensson.deeplearning;

import java.io.File;
import java.io.IOException;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private DataSetIterator dataTrainSetIterator;
	private DataSetIterator dataEvalSetIterator;
	
	/*
	 * Logger
	 */
	private static Logger log = LoggerFactory.getLogger(DL4JModel.class);
	
	public DL4JModel(ListBuilder listBuilder, DL4JData dL4JData) {
		this.listBuilder = listBuilder;
		this.dataTrainSetIterator = dL4JData.getDataTrainSetIterator();
		this.dataEvalSetIterator = dL4JData.getDataEvalSetIterator();
	}
	
	/**
	 * Build the model
	 */
	public void buildModel() {
		multiLayerConfiguration = listBuilder.build();
		multiLayerNetwork = new MultiLayerNetwork(multiLayerConfiguration);
	}

	/**
	 * Start the model
	 */
	public void initModel() {
		multiLayerNetwork.init();
	}
	
	/**
	 * Set a listener
	 * @param printIterations How many iterations?
	 */
	public void setListener(int printIterations) {
		multiLayerNetwork.setListeners(new ScoreIterationListener(printIterations));
	}
	
	/**
	 * Train the model
	 * @param numEpochs How many iterations?
	 */
	public void trainModel(int numEpochs) {
		for(int i = 0; i < numEpochs; i++) {
			dataTrainSetIterator.reset();
			multiLayerNetwork.fit(dataTrainSetIterator);
		}
	}
	
	/**
	 * Evaluate the model from classification data
	 */
	public void evaluateClassificationModel() {
		Evaluation evaluation =  multiLayerNetwork.evaluate(dataEvalSetIterator);
		log.info(evaluation.stats());
	}
	
	/**
	 * Evaluate the model from regression data
	 */
	public void evaluateRegressionModel() {
		RegressionEvaluation regressionEvaluation = multiLayerNetwork.evaluateRegression(dataEvalSetIterator);
		log.info(regressionEvaluation.stats());
	}
	
	/**
	 * Load model and return its name
	 * @param modelPath File path to the model
	 * @throws IOException 
	 */
	public void loadModel(File modelPath) throws IOException{
		multiLayerNetwork = MultiLayerNetwork.load(modelPath, true);
	}
	
	/**
	 * Save the model
	 * @param modelPath File path to the model
	 * @throws IOException 
	 */
	public void saveModel(File modelPath) throws IOException {
		multiLayerNetwork.save(modelPath, true); 
	}
	
	/**
	 * Rename the model
	 * @param newModelPath New file path to the model
	 * @param modelPath File path to the model
	 * @throws IOException 
	 */
	public void renameModel(File newModelPath, File modelPath) throws IOException {
		if(modelPath.exists()) {
			modelPath.delete();
		}
		saveModel(newModelPath);
	}
	
}
