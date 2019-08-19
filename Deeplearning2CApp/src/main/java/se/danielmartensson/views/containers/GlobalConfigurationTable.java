package se.danielmartensson.views.containers;

import com.gluonhq.charm.glisten.control.DropdownButton;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

public class GlobalConfigurationTable {
	
	/*
	 * Same names as in column names
	 */
	private @Setter StringProperty configuration;
	private @Getter @Setter DropdownButton value;

	public GlobalConfigurationTable(String configuration, DropdownButton value) {
		this.configuration = new SimpleStringProperty(configuration);
		this.value = value;
		
	}

	/**
	 * Special case when returning back configuration
	 * @return String
	 */
	public String getConfiguration() {
		return configuration.get();
	}

}
