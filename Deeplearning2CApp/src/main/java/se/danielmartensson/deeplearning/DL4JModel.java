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

import se.danielmartensson.tools.Dialogs;

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
	private Dialogs dialogs;
	
	/*
	 * Logger
	 */
	private static Logger log = LoggerFactory.getLogger(DL4JModel.class);
	
	public DL4JModel(ListBuilder listBuilder, DL4JData dL4JData) {
		this.listBuilder = listBuilder;
		this.dataTrainSetIterator = dL4JData.getDataTrainSetIterator();
		this.dataEvalSetIterator = dL4JData.getDataEvalSetIterator();
		dialogs = new Dialogs();
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
	 * @param boolean
	 */
	public boolean loadModel(File modelPath){
		boolean load = false;
		try {
			multiLayerNetwork = MultiLayerNetwork.load(modelPath, true);
			load = true;
		} catch (IOException e) {
			dialogs.exception("Cannot open model:\n" + modelPath.getPath(), e);
		} 
		return load;
	}
	
	/**
	 * Save the model
	 * @param modelPath File path to the model
	 * @return boolean
	 */
	public boolean saveModel(File modelPath) {
		boolean created = false;
		try {
			multiLayerNetwork.save(modelPath, true);
			created = true;
		} catch (IOException e) {
			dialogs.exception("Cannot save model:\n" + modelPath.getPath(), e);
		} 
		return created;
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
