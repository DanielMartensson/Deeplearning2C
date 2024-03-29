package se.danielmartensson.deeplearning2c.deeplearning;


import java.util.concurrent.atomic.AtomicBoolean;

import org.deeplearning4j.exception.DL4JException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.ProgressBar;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;


public class DL4JThread extends Thread{
	private MultiLayerNetwork multiLayerNetwork;
	ProgressBar progressBar;
	private AtomicBoolean continueLoop;
	private int epochs;
	private DataSetIterator trainDataSetIterator;
	private TextArea textArea;
	private int progressBarPosition;
	private String endMessage;
	private String[] textWall = new String[31]; // 31 elements
	private AppBar appBar;
	private Button startButton;
	
	/**
	 * Constructor for model fitting
	 * @param multiLayerNetwork
	 * @param trainDataSetIterator
	 * @param textArea
	 * @param progressBar
	 * @param continueLoop
	 * @param epochs
	 * @param startButton 
	 * @param appBar 
	 */
	public DL4JThread(MultiLayerNetwork multiLayerNetwork, DataSetIterator trainDataSetIterator, TextArea textArea, ProgressBar progressBar, AtomicBoolean continueLoop, int epochs, AppBar appBar, Button startButton) {
		this.multiLayerNetwork = multiLayerNetwork;
		this.trainDataSetIterator = trainDataSetIterator;
		this.textArea = textArea;
		this.progressBar = progressBar;
		this.continueLoop = continueLoop;
		this.epochs = epochs;
		this.appBar = appBar;
		this.startButton = startButton;
	}

	@Override
	public void run() {
		// Initial counters
		progressBarPosition = 1;
		int rowCounter = 0;
		endMessage = "\nStatus: Training done...";
		
		// While loop for training
		while(progressBarPosition < epochs+1) {
			// If we press stop or let the for loop do its full iterations
			if(continueLoop.get() == false)
				break; 
			
			// Train
			try {
				multiLayerNetwork.fit(trainDataSetIterator);
			}catch(DL4JException | IllegalStateException e) {
				Platform.runLater(() -> textArea.setText(e.getMessage())); 
				endMessage = "\nStatus: Training fail...";
				break;
			}
			
			// Delay is needed, else we might lose some icons inside the appBar
			try {
				Thread.sleep(20);   
			} catch (Exception e) {
				e.getStackTrace();
			}
			
			// Print the score out 
			if(rowCounter >= textWall.length) {
				// Get the score and add it to the last element
				String rowLine = "Epoch = " + progressBarPosition + " Score = " + multiLayerNetwork.score();
				for(int j = 0; j < textWall.length-1; j++) 
					textWall[j] = textWall[j+1]; // Shift
				textWall[textWall.length-1] = rowLine; // Last
				Platform.runLater(() -> textArea.setText(String.join("\n", textWall))); 
			}else {
				String rowLine = "Epoch = " + progressBarPosition + " Score = " + multiLayerNetwork.score();
				textWall[rowCounter] = rowLine;
				Platform.runLater(() -> textArea.appendText(rowLine + "\n")); 
				rowCounter++;
			}	
			
			// Our progress bar
			Platform.runLater(() -> progressBar.setProgress(Double.valueOf(progressBarPosition)/Double.valueOf(epochs)));
			
			// Break statement 
			if(progressBarPosition >= epochs)
				break;
			else
				progressBarPosition++; // I use a while-loop due to the Platform.RunLater cannot take variable i as a local variable.
		}
		
		// Print the status message
		Platform.runLater(() -> textArea.appendText(endMessage));
		
		// Change the icon to start button
		Platform.runLater(() -> {
			appBar.getActionItems().remove(0);
			appBar.getActionItems().add(0, startButton);
		});
	}
}
