package se.danielmartensson.deeplearning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import org.deeplearning4j.exception.DL4JInvalidConfigException;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.learning.config.AdaMax;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import javafx.scene.control.Alert.AlertType;
import lombok.Getter;
import lombok.Setter;
import se.danielmartensson.tools.Dialogs;
import se.danielmartensson.tools.FileHandler;

public class DL4JSerializableConfiguration implements Serializable {	
	/*
	 * Default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * List of strings - Change these? Then you need to change the if-statements in method runConfiguration(Builder builder) below
	 */
	private final @Getter String[] updaterList = {"Adam", "Sgd", "AdaMax", "Nesterovs", "AdaDelta"};
	private final @Getter String[] regularizationList = {"L1", "L2"};
	private final @Getter String[] configurationNames = {"Seed", "Optimization algorithm", "Weight init", "Updater", "Learning rate", "Momentum", "Regularization", "Regularization coefficient"};
	private final @Getter String[] layerNames = {"DenseLayer", "LSTM", "OutputLayer", "RnnOutputLayer"};
	
	/*
	 * Global configuration
	 */
	private @Getter @Setter String modelName;
	private @Getter @Setter long seed;
	private @Getter @Setter OptimizationAlgorithm optimizationAlgorithm;
	private @Getter @Setter WeightInit weightInit;
	private @Getter @Setter String updaterName;
	private @Getter @Setter double learningRate;
	private @Getter @Setter double momentum;
	private @Getter @Setter String regularizationName;
	private @Getter @Setter double regularizationCoefficient;
	
	/*
	 * Layer configuration - They have their "setters" from addLayer() method below
	 */
	private @Getter @Setter ArrayList<String> layerList = new ArrayList<String>(); 
	private @Getter @Setter ArrayList<Integer> nInList = new ArrayList<Integer>();
	private @Getter @Setter ArrayList<Integer> nOutList = new ArrayList<Integer>(); 
	private @Getter @Setter ArrayList<Activation> activationList = new ArrayList<Activation>(); 
	private @Getter @Setter ArrayList<LossFunctions.LossFunction> lossFunctionList = new ArrayList<LossFunctions.LossFunction>(); 
	
	/**
	 * This will load the configurations to the private fields
	 * @param filePath Our file path
	 */
	public void loadDeserializable(String filePath) {
		try {
			/*
			 * Read the deserializable file
			 */
			FileHandler fileHandler = new FileHandler();
			File file = fileHandler.loadNewFile(filePath);
			if(file.exists() == false) {
				new Dialogs().alertDialog(AlertType.WARNING, "Missing", "Missing file " + file.getName() + " just re-save and load model by clicking on selected row");
				return;
			}
			FileInputStream fileInputStream = new FileInputStream(file);
	        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
	        DL4JSerializableConfiguration dL4JSerializableConfiguration = (DL4JSerializableConfiguration) objectInputStream.readObject();
	       
	        /*
	         * Load to fields
	         */
	        modelName = dL4JSerializableConfiguration.getModelName();
	        seed = dL4JSerializableConfiguration.getSeed();
	        optimizationAlgorithm = dL4JSerializableConfiguration.getOptimizationAlgorithm();
	        weightInit = dL4JSerializableConfiguration.getWeightInit();
	        updaterName = dL4JSerializableConfiguration.getUpdaterName();
	        learningRate = dL4JSerializableConfiguration.getLearningRate();
	        momentum = dL4JSerializableConfiguration.getMomentum();
	        regularizationName = dL4JSerializableConfiguration.getRegularizationName();
	        regularizationCoefficient = dL4JSerializableConfiguration.getRegularizationCoefficient();
	        layerList = dL4JSerializableConfiguration.getLayerList();
	        nInList = dL4JSerializableConfiguration.getNInList();
	        nOutList = dL4JSerializableConfiguration.getNOutList();
	        activationList = dL4JSerializableConfiguration.getActivationList();
	        lossFunctionList = dL4JSerializableConfiguration.getLossFunctionList();
	        /*
	         * Add more here...
	         */
	        
	        /*
	         * Close
	         */
	        objectInputStream.close();
	        fileInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			new Dialogs().exception("Cannot read ser file:\n" + filePath, e);
		}
	}
	
	/**
	 * Save serializable file
	 * @param filePath Our file path
	 */
	public void saveSerializable(String filePath) {
		try {
			/*
			 * Create file
			 */
			FileHandler fileHandler = new FileHandler();
			fileHandler.createNewFile(filePath, true);
			File file = fileHandler.loadNewFile(filePath);
			
			/*
			 * Write to file and close file
			 */
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(this); // All fields
			objectOutputStream.close();
			fileOutputStream.close();
		} catch (IOException e) {
			new Dialogs().exception("Cannot write ser file:\n" + filePath, e);
		}
	}
	
	/**
	 * Run the configuration by insert new configuration
	 * @param builder 
	 * @return ListBuilder
	 */
	public ListBuilder runConfiguration(Builder builder) {
		/*
		 * Basic initials
		 */
		builder.seed(seed);
	    builder.weightInit(weightInit);
	    builder.setOptimizationAlgo(optimizationAlgorithm);
		
		/*
		 * Add the updater by checking of string updaterName contains in updaterList
		 */
		if(updaterName.equals(updaterList[0]) == true) { // Sgd
			builder.updater(new Sgd(learningRate));
		}else if(updaterName.equals(updaterList[1]) == true) { // Adam
			builder.updater(new Adam(learningRate));
		}else if(updaterName.equals(updaterList[2]) == true) { // Nesterovs
			builder.updater(new Nesterovs(learningRate, momentum));
		}else if(updaterName.equals(updaterList[3]) == true) { // AdaDelta
			builder.updater(new AdaDelta());
		}else if(updaterName.equals(updaterList[4]) == true) { // AdaMax
			builder.updater(new AdaMax(learningRate));
		}
		/*
         * Add more here...
         */
		
		/*
		 * Add regularization and its coefficient by checking if regularizationName contains inside regularizationList
		 */
		if(regularizationName.equals(regularizationList[0]) == true) // "L1"
			builder.l1(regularizationCoefficient);
		else
			builder.l2(regularizationCoefficient);
		
		/*
		 * Layer configuration
		 */
		ListBuilder listBuilder = new ListBuilder(builder);
		try {
			for(int i = 0; i < layerList.size(); i++) {
				if(layerList.get(i).equals(layerNames[0]) == true) { // DenseLayer
					listBuilder.layer(new DenseLayer.Builder()
							.nIn(nInList.get(i))
							.nOut(nOutList.get(i))
							.activation(activationList.get(i))
							.build());
				}else if(layerList.get(i).equals(layerNames[1]) == true) { // LSTM
					listBuilder.layer(new LSTM.Builder()
							.nIn(nInList.get(i))
							.nOut(nOutList.get(i))
							.activation(activationList.get(i))
							.build());
				}else if(layerList.get(i).equals(layerNames[2]) == true) { // OutputLayer
					listBuilder.layer(new OutputLayer.Builder(lossFunctionList.get(i))
							.nIn(nInList.get(i))
							.nOut(nOutList.get(i))
							.activation(activationList.get(i))
							.build());
				}else if(layerList.get(i).equals(layerNames[3]) == true) { // RnnOutputLayer
					listBuilder.layer(new RnnOutputLayer.Builder(lossFunctionList.get(i))
							.nIn(nInList.get(i))
							.nOut(nOutList.get(i))
							.activation(activationList.get(i))
							.build());
				}
			}
		}catch(DL4JInvalidConfigException e) {
			new Dialogs().exception("Layer configuration error", e);
		}
			/*
	         * Add more here...
	         */
		
		/*
		 * Return our configuration
		 */
		return listBuilder; 
	}
	
	/**
	 * Set a new layer
	 * @param layerName "DenseLayer", "LSTM", "OutputLayer"
	 * @param inputs Input to the layer
	 * @param outputs Output from the layer
	 * @param activation Activation function
	 * @param lossfunction Loss function
	 */
	public void addLayer(String layerName, int inputs, int outputs, Activation activation, LossFunctions.LossFunction lossfunction) {
		layerList.add(layerName);
		nInList.add(inputs);
		nOutList.add(outputs);
		activationList.add(activation);
		lossFunctionList.add(lossfunction);
	}
	
	/**
	 * Clear the layers
	 */
	public void clearLayer() {
		layerList.clear();
		nInList.clear();
		nOutList.clear();
		activationList.clear();
		lossFunctionList.clear();
	}
}
