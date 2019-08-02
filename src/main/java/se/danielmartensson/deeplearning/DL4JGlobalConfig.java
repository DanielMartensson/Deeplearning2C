package se.danielmartensson.deeplearning;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.learning.config.IUpdater;


/**
 * This class will handle the global configurations inside the neural network 
 */
public class DL4JGlobalConfig {
	
	/*
	 * Objects
	 */
	private Builder globalConfig;
	
	public DL4JGlobalConfig(Builder globalConfig) {
		this.globalConfig = globalConfig;
	}

	/*
	 * Set this to a random number to improved repeatability 
	 */
	public void setSeed(long seed) {
		globalConfig.setSeed(seed);
	}
	
	/*
	 * Set the optimization algorithm
	 */
	public void setOptimizationAlgorithm(OptimizationAlgorithm optimizationAlgo) {
		globalConfig.optimizationAlgo(optimizationAlgo);
	}
	
	/*
	 * Set the updater
	 */
	public void setUpdater(IUpdater updater) {
		globalConfig.updater(updater);
	}
	
	/*
	 * Set the regularization and its number
	 */
	public void setRegularization(String LX, double number) {
		if(LX.equals("L1"))
			globalConfig.l1(number);
		else
			globalConfig.l2(number);
	}
	
	/*
	 * Set the weight init
	 */
	public void setWeightInit(WeightInit weightInit) {
		globalConfig.weightInit(weightInit);
	}
	
}
