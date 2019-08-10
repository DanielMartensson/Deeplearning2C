package se.danielmartensson.deeplearning;

import java.io.File;
import java.io.IOException;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;

import lombok.Getter;


public class DL4JData {
	

	private @Getter DataSetIterator dataEvalSetIterator;
	private @Getter DataSetIterator dataTrainSetIterator;

	/**
	 * Constructor
	 */
	public DL4JData() {
		
	}
	
	/** Load the train data into the model
	 * @param fileCSV File location
	 * @param delimiter Separator ";", "," or other
	 * @param batchSize How many rows we should have in each data set
	 * @param labelIndexFrom Indexing of start where our first output begin
	 * @param labelIndexTo Indexing of start where our last output ends
	 * @param numPossibleLabels How many labels does the data have
	 * @param regression If regression data is used, then labelIndexTo does not matter
	 */
	public void loadTrainData(File fileCSV, char delimiter, int batchSize, int labelIndexFrom, int labelIndexTo, int numPossibleLabels, boolean regression) throws IOException, InterruptedException {
		dataTrainSetIterator = readCSVDataset(fileCSV, delimiter, batchSize, labelIndexFrom, labelIndexTo, numPossibleLabels, regression);
	}
	
	/** Load the evaluation data into the model
	 * @param fileCSV File location
	 * @param delimiter Separator ";", "," or other
	 * @param batchSize How many rows we should have in each data set
	 * @param labelIndexFrom Indexing of start where our first output begin
	 * @param labelIndexTo Indexing of start where our last output ends
	 * @param numPossibleLabels How many labels does the data have
	 * @param regression If regression data is used, then labelIndexTo does not matter
	 */
	public void loadEvalDataRegression(File fileCSV, char delimiter, int batchSize, int labelIndexFrom, int labelIndexTo, int numPossibleLabels, boolean regression) throws IOException, InterruptedException {
		dataEvalSetIterator = readCSVDataset(fileCSV, delimiter, batchSize, labelIndexFrom, labelIndexTo, numPossibleLabels, regression);
	}
	
	/**
	 * Create the data set iterator object
	 * @param fileCSV File location
	 * @param delimiter Separator ";", "," or other
	 * @param batchSize How many rows we should have in each data set
	 * @param labelIndexFrom Indexing of start where our first output begin
	 * @param labelIndexTo Indexing of start where our last output ends
	 * @param numPossibleLabels How many labels does the data have
	 * @param regression If regression data is used
	 */
	private DataSetIterator readCSVDataset(File fileCSV, char delimiter, int batchSize, int labelIndexFrom, int labelIndexTo, int numPossibleLabels, boolean regression) throws IOException, InterruptedException{
		RecordReader recordReader = new CSVRecordReader(delimiter);
		recordReader.initialize(new FileSplit(fileCSV));
		if(regression == true)
			return new RecordReaderDataSetIterator(recordReader, batchSize, labelIndexFrom, labelIndexTo, regression);
		else {
			return new RecordReaderDataSetIterator(recordReader, batchSize, labelIndexFrom, numPossibleLabels);
		}
	}
	
	/**
	 * Normalize data from the CSV file
	 * @param normalizer Normalizer interface. Can be either AbstractDataSetNormalizer or NormalizerStandardize object as argument
	 */
	public void normalization(DataNormalization normalizer) {
		fitTransformation(normalizer, dataEvalSetIterator);
		fitTransformation(normalizer, dataTrainSetIterator);
	}
	
	/**
	 * Fit dataSetIterator
	 * Transform each dataset in the dataset iterator object
	 * @param normalizer Normalizer object
	 * @param dataSetIterator Dataset iterator object
	 */
	private void fitTransformation(DataNormalization normalizer, DataSetIterator dataSetIterator) {
		normalizer.fit(dataSetIterator);
		while(dataSetIterator.hasNext()) {
			normalizer.transform(dataSetIterator.next());
		}
		dataSetIterator.reset();
	}

}
