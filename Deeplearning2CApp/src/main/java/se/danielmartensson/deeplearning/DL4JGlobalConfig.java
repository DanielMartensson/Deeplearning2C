package se.danielmartensson.deeplearning;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.Builder;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.learning.config.AMSGrad;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.learning.config.AdaMax;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.NoOp;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.learning.config.Sgd;

import lombok.Getter;
import lombok.Setter;


/**
 * This class will handle the global configurations inside the neural network 
 */
public class DL4JGlobalConfig {
	private @Getter @Setter Builder globalConfig;
	public static String updater;
	public static String learningRate;
	public static String momentum;
	public static String optimizationAlgorithm;
	public static String seed;
	public static String regularization;
	public static String coefficient;
	public static String weight;
	
	/**
	 * Constructor
	 * @param globalConfig Global configuration object
	 */
	public DL4JGlobalConfig(Builder globalConfig) {
		this.globalConfig = globalConfig;
	}

	/**
	 * Set this to a random number to improved repeatability 
	 * @param seed Set seed to a random number as string
	 */
	public void setSeed(String seed) {
		DL4JGlobalConfig.seed = seed;
		globalConfig.setSeed(Long.parseLong(seed));
	}
	
	/**
	 * Set the optimization algorithm
	 * @param optimizationAlgorithm Optimization algorithm enum as string
	 */
	public void setOptimizationAlgorithm(String optimizationAlgorithm) {
		DL4JGlobalConfig.optimizationAlgorithm = optimizationAlgorithm;
		if(optimizationAlgorithm.equals("STOCHASTIC_GRADIENT_DESCENT") == true)
			globalConfig.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
		else if(optimizationAlgorithm.equals("CONJUGATE_GRADIENT") == true)
			globalConfig.optimizationAlgo(OptimizationAlgorithm.CONJUGATE_GRADIENT);
		else if(optimizationAlgorithm.equals("LBFGS") == true)
			globalConfig.optimizationAlgo(OptimizationAlgorithm.LBFGS);
		else if(optimizationAlgorithm.equals("LINE_GRADIENT_DESCENT") == true)
			globalConfig.optimizationAlgo(OptimizationAlgorithm.LINE_GRADIENT_DESCENT);
		else
			globalConfig.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);
	}
	
	/**
	 * Set the updater and the learning rate
	 * @param updater Name of the updater as string
	 * @param learningRate Learning rate in form of a string
	 * @param momentum String of momentum
	 */
	public void setUpdater(String updater, String learningRate, String momentum) {
		DL4JGlobalConfig.updater = updater;
		DL4JGlobalConfig.learningRate = learningRate;
		DL4JGlobalConfig.momentum = momentum;
		if(updater.equals("Sgd") == true)
			globalConfig.updater(new Sgd(Double.parseDouble(learningRate)));
		else if(updater.equals("AdaDelta") == true)
			globalConfig.updater(new AdaDelta());
		else if(updater.equals("AdaGrad") == true)
			globalConfig.updater(new AdaGrad(Double.parseDouble(learningRate)));
		else if(updater.equals("Adam") == true)
			globalConfig.updater(new Adam(Double.parseDouble(learningRate)));
		else if(updater.equals("AdaMax") == true)
			globalConfig.updater(new AdaMax(Double.parseDouble(learningRate)));
		else if(updater.equals("AMSGrad") == true)
			globalConfig.updater(new AMSGrad(Double.parseDouble(learningRate)));
		else if(updater.equals("Nadam") == true)
			globalConfig.updater(new Nadam(Double.parseDouble(learningRate)));
		else if(updater.equals("RmsProp") == true)
			globalConfig.updater(new RmsProp(Double.parseDouble(learningRate)));
		else if(updater.equals("NoOp") == true)
			globalConfig.updater(new NoOp());
		else if(updater.equals("Nesterovs") == true)
			globalConfig.updater(new Nesterovs(Double.parseDouble(learningRate), Double.parseDouble(momentum)));
		else
			globalConfig.updater(new AdaDelta());
	}
	
	/**
	 * Set the regularization and its coffeficient
	 * @param regularization L1 or L2 as strings 
	 * @param coefficient Tuning parameter as string
	 */
	public void setRegularization(String regularization, String coefficient) {
		DL4JGlobalConfig.regularization = regularization;
		DL4JGlobalConfig.coefficient = coefficient;
		if(regularization.equals("L1"))
			globalConfig.l1(Double.parseDouble(coefficient));
		else
			globalConfig.l2(Double.parseDouble(coefficient));
	}
	
	/**
	 * Set the weight init
	 * @param weight Set the weight name
	 */
	public void setWeightInit(String weight) {
		DL4JGlobalConfig.weight = weight;
		if(weight.equals("XAVIER") == true)
			globalConfig.weightInit(WeightInit.XAVIER);
		else if(weight.equals("NORMAL") == true)
			globalConfig.weightInit(WeightInit.NORMAL);
		else if(weight.equals("ONES") == true)
			globalConfig.weightInit(WeightInit.ONES);
		else if(weight.equals("RELU") == true)
			globalConfig.weightInit(WeightInit.RELU);
		else if(weight.equals("ZERO") == true)
			globalConfig.weightInit(WeightInit.ZERO);
		else if(weight.equals("UNIFORM") == true)
			globalConfig.weightInit(WeightInit.UNIFORM);
		else
			globalConfig.weightInit(WeightInit.XAVIER);
	}
}
