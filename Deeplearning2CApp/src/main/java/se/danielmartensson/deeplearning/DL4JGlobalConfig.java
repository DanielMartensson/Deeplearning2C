package se.danielmartensson.deeplearning;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.learning.config.IUpdater;

import lombok.Getter;
import lombok.Setter;


/**
 * This class will handle the global configurations inside the neural network 
 */
public class DL4JGlobalConfig {
	
	/*
	 * Objects
	 */
	private @Getter @Setter Builder globalConfig;
	
	/**
	 * Constructor
	 * @param globalConfig Global configuration object
	 */
	public DL4JGlobalConfig(Builder globalConfig) {
		this.globalConfig = globalConfig;
	}

	/**
	 * Set this to a random number to improved repeatability 
	 * @param seed Set seed to a random number
	 */
	public void setSeed(long seed) {
		globalConfig.setSeed(seed);
	}
	
	/**
	 * Set the optimization algorithm
	 * @param optimizationAlgo Optimization algorithm object
	 */
	public void setOptimizationAlgorithm(OptimizationAlgorithm optimizationAlgo) {
		globalConfig.optimizationAlgo(optimizationAlgo);
	}
	
	/**
	 * Set the updater
	 * @param updater Updater object
	 */
	public void setUpdater(IUpdater updater) {
		globalConfig.updater(updater);
	}
	
	/**
	 * Set the regularization and its number
	 * @param LX L1 or L2
	 * @param number Tuning parameter
	 */
	public void setRegularization(String LX, double number) {
		if(LX.equals("L1"))
			globalConfig.l1(number);
		else
			globalConfig.l2(number);
	}
	
	/**
	 * Set the weight init
	 * @param weightInit Set the weight init object 
	 */
	public void setWeightInit(WeightInit weightInit) {
		globalConfig.weightInit(weightInit);
	}
	
}
