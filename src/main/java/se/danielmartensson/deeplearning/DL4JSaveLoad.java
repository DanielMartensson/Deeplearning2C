package se.danielmartensson.deeplearning;

import java.io.File;
import java.io.IOException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import se.danielmartensson.tools.AlertBoxes;

public class DL4JSaveLoad {
	
	private AlertBoxes alertBoxes;
	private File file; // if this is null, that means we have not save it at all
	private FileChooser fileChooser;
	private Stage stage;
	private DL4JModel dL4JModel;
	private DataSetIterator dataEvalSetIterator;
	private DataSetIterator dataTrainSetIterator;

	/**
	 * Constructor
	 * @param dL4JModel Our Deeplearning4J model
	 */
	public DL4JSaveLoad(DL4JModel dL4JModel) {
		alertBoxes = new AlertBoxes();
		stage = new Stage();
		fileChooser = new FileChooser();
		this.dL4JModel = dL4JModel;
	}
	
	/**
	 * Save the model as...and return its name
	 */
	public String saveModelAs() throws IOException {
		file = fileChooser.showSaveDialog(stage);
		dL4JModel.getMultiLayerNetwork().save(file, true);
		return file.getName();
	}
	
	/**
	 * Load model and return its name
	 */
	public String loadModel() throws IOException {
		file = fileChooser.showOpenDialog(stage);
		if(file != null) {
			MultiLayerNetwork.load(file, true); return file.getName();
		}else
			return "";
	}
	
	/**
	 * Save the model as the current name
	 */
	public void saveModel() throws IOException {
		dL4JModel.getMultiLayerNetwork().save(file, true);
	}
	
	/**
	 * Ask if we want to save the model first
	 */
	public void askIfSaving() throws IOException {
		boolean save = alertBoxes.question("Question!", "Should we save?", "OK or No.");
		if(save == true) 
			 saveModel();
		
	}
	
	/**
	 * When pressing File -> New, we want the file name back
	 */
	public String newFile() {
		file = fileChooser.showSaveDialog(stage);
		if(file != null)
			return file.getName();
		else
			return "";
	}

	/**
	 * Get the file to Front.java for displaying the file name at the menu bar
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Create the data set iterator object
	 * @param fileCSV File location
	 * @param delimiter Separator ";", "," or other
	 * @param batchSize How many rows we should have in each data set
	 * @param labelIndexFrom Indexing of start where our first output begin
	 * @param labelIndexTo Indexing of start where our last output ends
	 * @param regression This is always true
	 */
	private DataSetIterator readCSVDataset(File fileCSV, char delimiter, int batchSize, int labelIndexFrom, int labelIndexTo, boolean regression) throws IOException, InterruptedException{
		RecordReader recordReader = new CSVRecordReader(delimiter);
		recordReader.initialize(new FileSplit(fileCSV));
		return new RecordReaderDataSetIterator(recordReader, batchSize, labelIndexFrom, labelIndexTo, regression);
	}
	
	/** Load the train data into the model
	 * @param fileCSV File location
	 * @param delimiter Separator ";", "," or other
	 * @param batchSize How many rows we should have in each data set
	 * @param labelIndexFrom Indexing of start where our first output begin
	 * @param labelIndexTo Indexing of start where our last output ends
	 */
	public void loadTrainDataRegression(File fileCSV, char delimiter, int batchSize, int labelIndexFrom, int labelIndexTo) throws IOException, InterruptedException {
		boolean regression = true;
		dataTrainSetIterator = readCSVDataset(fileCSV, delimiter, batchSize, labelIndexFrom, labelIndexTo, regression);
	}
	
	/** Load the evaluation data into the model
	 * @param fileCSV File location
	 * @param delimiter Separator ";", "," or other
	 * @param batchSize How many rows we should have in each data set
	 * @param labelIndexFrom Indexing of start where our first output begin
	 * @param labelIndexTo Indexing of start where our last output ends
	 */
	public void loadEvalDataRegression(File fileCSV, char delimiter, int batchSize, int labelIndexFrom, int labelIndexTo) throws IOException, InterruptedException {
		boolean regression = true;
		dataEvalSetIterator = readCSVDataset(fileCSV, delimiter, batchSize, labelIndexFrom, labelIndexTo, regression);
	}
	
	

}
