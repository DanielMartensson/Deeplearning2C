package se.danielmartensson.deeplearning2c.deeplearning;

import java.io.File;
import java.io.IOException;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.DataSetIteratorSplitter;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import lombok.Getter;
import lombok.Setter;

public class DL4JData {
	private @Getter DataSetIterator trainDataSetIterator;
	private @Getter DataSetIterator evalDataSetIterator;
	private @Getter DataSetIterator dataSetIterator;
	private @Setter @Getter boolean regression;

	/**
	 * Load the data into the model
	 * 
	 * @param fileCSV           File location
	 * @param delimiter         Separator ";", "," or other
	 * @param batchSize         How many rows we should have in each data set
	 * @param labelIndexFrom    Indexing of start where our first output begin
	 * @param labelIndexTo      Indexing of start where our last output ends
	 * @param numPossibleLabels How many labels does the data have
	 * @param regression        If regression data is used, then labelIndexTo does not matter
	 * @param maxScalar 		The minimum number we want
	 * @param minScalar 		The maximum number we want
	 */
	public void loadData(File fileCSV, char delimiter, int batchSize, int labelIndexFrom, int labelIndexTo, int numPossibleLabels, boolean regression, int minScalar, int maxScalar) throws IOException, InterruptedException {
		dataSetIterator = readCSVDataset(fileCSV, delimiter, batchSize, labelIndexFrom, labelIndexTo, numPossibleLabels, regression);
		
		// Normalize the iterators
		NormalizerMinMaxScaler normalizerMinMaxScaler = new NormalizerMinMaxScaler(minScalar, maxScalar);
		normalizerMinMaxScaler.fit(dataSetIterator);
		
		// Calculate totalBatches = amount of data sets X batch size for each data set
		long totalBatches = 0;
		while(dataSetIterator.hasNext() == true) {
			dataSetIterator.next();
			totalBatches++;
		}
		dataSetIterator.reset();
		totalBatches = totalBatches*dataSetIterator.batch();
		
		// Split the data into 50% train and 50% eval
		DataSetIteratorSplitter dataSetIteratorSplitter = new DataSetIteratorSplitter(dataSetIterator, totalBatches, 0.5);
		trainDataSetIterator = dataSetIteratorSplitter.getTestIterator();
		evalDataSetIterator = dataSetIteratorSplitter.getTestIterator();
	}

	/**
	 * Create the data set iterator object
	 * 
	 * @param fileCSV           File location
	 * @param delimiter         Separator ";", "," or other
	 * @param batchSize         How many rows we should have in each data set
	 * @param labelIndexFrom    Indexing of start where our first output begin
	 * @param labelIndexTo      Indexing of start where our last output ends
	 * @param numPossibleLabels How many labels does the data have
	 * @param regression        If regression data is used, then numPossibleLabels does not matter
	 * @return DataSetIterator
	 */
	private DataSetIterator readCSVDataset(File fileCSV, char delimiter, int batchSize, int labelIndexFrom, int labelIndexTo, int numPossibleLabels, boolean regression) throws IOException, InterruptedException {
		RecordReader recordReader = new CSVRecordReader(delimiter);
		recordReader.initialize(new FileSplit(fileCSV));
		if (regression == true)
			return new RecordReaderDataSetIterator(recordReader, batchSize, labelIndexFrom, labelIndexTo, regression);
		else {
			return new RecordReaderDataSetIterator(recordReader, batchSize, labelIndexFrom, numPossibleLabels);
		}
	}
}
