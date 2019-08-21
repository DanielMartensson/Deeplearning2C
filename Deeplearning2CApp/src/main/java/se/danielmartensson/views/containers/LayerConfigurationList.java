package se.danielmartensson.views.containers;

import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import lombok.Getter;
import lombok.Setter;

public class LayerConfigurationList {
	
	private @Getter @Setter String layerType;
	private @Getter @Setter int nIn;
	private @Getter @Setter int nOut;
	private @Getter @Setter String activationType;
	private @Getter @Setter String lossFunctionType;
	
	public LayerConfigurationList(String layerType, int nIn, int nOut, String activationType, String lossFunctionType) {
		this.layerType = layerType;
		this.nIn = nIn;
		this.nOut = nOut;
		this.activationType = activationType;
		this.lossFunctionType = lossFunctionType;
	}
	

	

}
