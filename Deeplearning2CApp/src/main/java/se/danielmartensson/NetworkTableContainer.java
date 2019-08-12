package se.danielmartensson;


import javafx.beans.property.SimpleStringProperty;


public class NetworkTableContainer {


	/*
	 * Column names
	 */
	private SimpleStringProperty name;
	private SimpleStringProperty network;
	private SimpleStringProperty trained;
	private SimpleStringProperty accuracy;
	
	/**
	 * Constructor 
	 * @param name Name
	 * @param network RNN or MLP
	 * @param trained Date string
	 * @param accuracy Number string
	 */
	public NetworkTableContainer(String name, String network, String trained, String accuracy) {
		super();
		this.name = new SimpleStringProperty(name);
		this.network = new SimpleStringProperty(network);
		this.trained = new SimpleStringProperty(trained);
		this.accuracy = new SimpleStringProperty(accuracy);
	}

	public String getName() {
		return name.get();
	}

	public void setName(SimpleStringProperty name) {
		this.name = name;
	}

	public String getNetwork() {
		return network.get();
	}

	public void setNetwork(SimpleStringProperty network) {
		this.network = network;
	}

	public String getTrained() {
		return trained.get();
	}

	public void setTrained(SimpleStringProperty trained) {
		this.trained = trained;
	}

	public String getAccuracy() {
		return accuracy.get();
	}

	public void setAccuracy(SimpleStringProperty accuracy) {
		this.accuracy = accuracy;
	}

	

}
