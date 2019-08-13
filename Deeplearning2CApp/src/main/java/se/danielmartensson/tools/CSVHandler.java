package se.danielmartensson.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.control.Alert.AlertType;

public class CSVHandler {

	/*
	 * Fields
	 */
	private ArrayList<ArrayList<String>> arrayList;
	private Dialogs dialogs;
	private File file;
	private String delimiter;
	protected FileHandler fileHandler;

	/**
	 * Constructor that will open a csv file
	 * @param fileHandler File handler object
	 * @param filePath Path to the file
	 * @param delimiter Separator
	 */
	public CSVHandler(FileHandler fileHandler, String filePath, String delimiter) {
        file = fileHandler.loadFile(filePath);
		arrayList = new ArrayList<>();
		dialogs = new Dialogs();
		this.delimiter = delimiter;
		updateCSVHandler();
	}

	/**
	 * Update the CSVHandler object
	 */
	private void updateCSVHandler() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			arrayList.clear();
			ArrayList<String> rowList;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(delimiter);
				rowList = new ArrayList<String>();
				for(int i = 0; i < values.length; i++) {
					rowList.add(values[i]);
				}
				arrayList.add(rowList); // Add row after row 
			}
			br.close();
		} catch (IOException e) {
			dialogs.exception("Cannot open CSV file:\n" + file.getAbsolutePath(), e);
		}
	}

	/**
	 * Get a single cell
	 * 
	 * @param row    Row index
	 * @param column Column index
	 * @return String
	 */
	public String getCell(int row, int column) {
		return arrayList.get(row).get(column);
	}

	/**
	 * Change a single cell in both CSV file and CSVHandler object
	 * 
	 * @param row    Row index
	 * @param column Column index
	 * @param value
	 */
	public void setCell(int row, int column, String value) {
		arrayList.get(row).add(column, value); // Update
		copyToCSV();
	}
	
	
	/**
	 * Search if the name already exist
	 * @param value Our search value
	 * @return boolean
	 */
	public boolean exist(String name) {
		for (int i = 0; i < getTotalRows(); i++) {
			if(name.equals(arrayList.get(i).get(0))){
				return true; // Exist
			}
		}
		return false; // Not exist
	}
	
	/**
	 * Find on which row number the name is
	 * @param name String of the name
	 * @return int
	 */
	public int findRow(String name) {
		int rowNumber = 0;
		for (int i = 0; i < getTotalRows(); i++) {
			if(name.equals(arrayList.get(i).get(0))){
				rowNumber = i;
				break;
			}
		}
		return rowNumber;
	}
	
	/**
	 * Delete the whole row
	 * @param row
	 */
	public void deleteRow(int row) {
		arrayList.remove(row);
		copyToCSV();
	}

	/**
	 * This will copy the whole CSVHandler object to the CSV file
	 */
	private void copyToCSV() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
			String allText = "";
			for (int i = 0; i < getTotalRows(); i++) {
				for (int j = 0; j < getTotalColumns(); j++) {
					allText += arrayList.get(i).get(j) + delimiter; // Collect all text
				}
				allText += "\n"; // New line
			}
			bw.write(allText);
			bw.close();
		} catch (IOException e) {
			dialogs.exception("Cannot open CSV file:\n" + file.getAbsolutePath(), e);
		}
	}

	/**
	 * Write a new row to the CSV file
	 * 
	 * @param rowText Enter the new row text. Don't forget the delimiter too!
	 */
	public void newRow(String rowText) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			bw.write(rowText + "\n");
			bw.close();
			updateCSVHandler();
		} catch (IOException e) {
			dialogs.exception("Cannot open CSV file:\n" + file.getAbsolutePath(), e);
		}

	}

	/**
	 * Return total rows
	 * 
	 * @return int
	 */
	public int getTotalRows() {
		return arrayList.size();
	}

	/**
	 * Return total columns, in this case, it's on row index 0
	 * 
	 * @return int
	 */
	public int getTotalColumns() {

		if (arrayList.get(0) == null) {
			return 0;
		} else {
			return arrayList.get(0).size();
		}
	}
}
