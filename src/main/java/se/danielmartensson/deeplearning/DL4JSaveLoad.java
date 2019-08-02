package se.danielmartensson.deeplearning;

import java.io.File;
import java.io.IOException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import se.danielmartensson.tools.AlertBoxes;

public class DL4JSaveLoad {
	
	private AlertBoxes alertBoxes;
	private File file; // if this is null, that means we have not save it at all
	private FileChooser fileChooser;
	private Stage stage;
	private DL4JModel dL4JModel;

	public DL4JSaveLoad(DL4JModel dL4JModel) {
		alertBoxes = new AlertBoxes();
		stage = new Stage();
		fileChooser = new FileChooser();
		this.dL4JModel = dL4JModel;
	}
	
	/*
	 * Save the model as .zip file
	 */
	public String saveModelAs() throws IOException {
		file = fileChooser.showSaveDialog(stage);
		dL4JModel.getMultiLayerNetwork().save(file, true);
		System.out.println("File save as " + file.getName());
		return file.getName();
	}
	
	/*
	 * Load model
	 */
	public String loadModel() throws IOException {
		file = fileChooser.showOpenDialog(stage);
		if(file != null) {
			MultiLayerNetwork.load(file, true);
			System.out.println("File loaded as " + file.getName());
		}
		return file.getName();
	}
	
	/*
	 * Save the model
	 */
	public void saveModel() throws IOException {
		dL4JModel.getMultiLayerNetwork().save(file, true);
		System.out.println("File " + file.getName() + " saved");
	}
	
	/*
	 * Ask if we want to save the model first
	 */
	public void askIfSaving() throws IOException {
		/*
		 * Ask if we should save first?
		 */
		boolean save = alertBoxes.question("Question!", "Should we save?", "OK or No.");
		if(save == true) {
			 saveModel();
		}
		
	}
	
	/*
	 * When pressning File -> New
	 */
	public String newFile() {
		file = fileChooser.showSaveDialog(stage);
		if(file != null)
			return file.getName();
		else
			return "";
	}

	public File getFile() {
		return file;
	}

}
