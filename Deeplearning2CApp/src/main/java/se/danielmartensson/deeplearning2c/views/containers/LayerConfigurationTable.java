package se.danielmartensson.deeplearning2c.views.containers;

import com.gluonhq.charm.glisten.control.DropdownButton;

import lombok.Getter;
import lombok.Setter;

public class LayerConfigurationTable {
	
	private @Getter @Setter DropdownButton layerType;
	private @Getter @Setter DropdownButton nIn;
	private @Getter @Setter DropdownButton nOut;
	private @Getter @Setter DropdownButton activationType;
	private @Getter @Setter DropdownButton lossFunctionType;
	private @Getter @Setter DropdownButton dropOutProbability;
	
	public LayerConfigurationTable(DropdownButton layerType, DropdownButton nIn, DropdownButton nOut, DropdownButton activationType, DropdownButton lossFunctionType, DropdownButton dropOutProbability) {
		this.layerType = layerType;
		this.nIn = nIn;
		this.nOut = nOut;
		this.activationType = activationType;
		this.lossFunctionType = lossFunctionType;
		this.dropOutProbability = dropOutProbability;
	}
}
