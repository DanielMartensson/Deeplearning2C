package se.danielmartensson.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.writer.CsvWriter;


/**
 * The reason why we are using FastCSV and not SQLite, is due to memory use.
 * @author Daniel Mårtensson
 *
 */
public class CSVHandler {
	private Dialogs dialogs = new Dialogs();
	private CsvReader csvReader;
	private CsvWriter csvWriter;;
	private File file;
	private String delimiter;
	
	/**
	 * Constructor 
	 * @param fileHandler File handler object
	 * @param filePath Path to our file
	 * @param delimiter Separator "," or ";" etc.
	 * @param headers String that contains name of columns with delimiter as separator
	 */
	public CSVHandler(FileHandler fileHandler, String filePath, String delimiter, String headers) {
        file = fileHandler.loadFile(filePath);
        this.delimiter = delimiter;
        csvWriter = new CsvWriter();
        csvReader = new CsvReader();
        csvReader.setFieldSeparator(delimiter.charAt(0));
        csvWriter.setFieldSeparator(delimiter.charAt(0));
        
        /*
         * Check if file has 0 rows = empty
         */
        if(getTotalRows() == 0)
        	newHeader(headers); // Write our header if we don't have one
        csvReader.setContainsHeader(true);
	}
	
	/**
	 * Return a complete row
	 * @param row Row number that we want to return
	 * @return
	 */
	public List<String> getRow(int row) {
		try {
			CsvContainer csvContainer = csvReader.read(file, StandardCharsets.UTF_8);
			return csvContainer.getRow(row).getFields();
		}catch(IOException | NullPointerException e) {
			dialogs.exception("Cannot get rows. Return List<String> = null", e);
			return null;
		}
	}
	
	/**
	 * Replace a whole row
	 * @param row row number
	 * @param text text with delimiter separator
	 */
	public void replaceRow(int row, String text) {
		try {
			/*
			 * Replace all items in a row
			 */
			CsvContainer csvContainer = csvReader.read(file, StandardCharsets.UTF_8);
			Collection<String[]> data = new ArrayList<>();
			data.add((String[]) csvContainer.getHeader().toArray()); // Add header
			for(int i = 0; i < csvContainer.getRowCount(); i++) 
				if(i == row) 
					data.add(text.split(String.valueOf(delimiter))); // Add the replaced data
				else
					data.add((String[]) csvContainer.getRow(i).getFields().toArray()); // Add everything else
			csvWriter.write(file, StandardCharsets.UTF_8, data); // Auto close
		} catch (IOException | NullPointerException e) {
			dialogs.exception("Cannot replace row", e);
		}
	}
	
	/**
	 * Search for a cell value in a g
	 * @param cellValue The cell in form of a string
	 * @param header Name of the column
	 * @return boolean
	 */
	public boolean exist(String cellValue, String header) {
		try {
			CsvContainer csvContainer = csvReader.read(file, StandardCharsets.UTF_8);
			if(csvContainer == null)
				return false; // Nothing has been added, except the header
			for(int i = 0; i < csvContainer.getRowCount(); i++)
				if(cellValue.equals(csvContainer.getRow(i).getField(header)) == true)
					return true; // Yes
			return false; // Nope
		} catch (IOException | NullPointerException e) {
			dialogs.exception("Cannot check existens. Returning false", e);
			return false;
		}
	}
	
	/**
	 * Find on which row cellValue is on a header
	 * @param cellValue
	 * @param header
	 * @return int
	 */
	public int findRow(String cellValue, String header) {
		try {
			CsvContainer csvContainer = csvReader.read(file, StandardCharsets.UTF_8);
			for(int i = 0; i < csvContainer.getRowCount(); i++)
				if(cellValue.equals(csvContainer.getRow(i).getField(header)) == true)
					return i; // Yes
			return 0; // Nope
		} catch (IOException | NullPointerException e) {
			dialogs.exception("Cannot find row index. Returning 0", e);
			return 0;
		}
	}
	
	/**
	 * Delete the whole row at least if we got a row
	 * @param row row number 
	 */
	public void deleteRow(int row)  {
		try {
			/*
			 * Remove a selected row
			 */
			CsvContainer csvContainer = csvReader.read(file, StandardCharsets.UTF_8);
			Collection<String[]> data = new ArrayList<>();
			data.add((String[]) csvContainer.getHeader().toArray()); // Add header
			for(int i = 0; i < csvContainer.getRowCount(); i++) 
				if(i != row) 
					data.add((String[]) csvContainer.getRow(i).getFields().toArray()); // Add everything, except on selected row
			csvWriter.write(file, StandardCharsets.UTF_8, data); // Auto close
			
		} catch (IOException | NullPointerException e) {
			dialogs.exception("Cannot delete row", e);
		}
	}

	/**
	 * Write a new header to the CSV file if the file is empty.
	 * @param rowText Enter the string
	 */
	private void newHeader(String rowText) {
		try {
			Collection<String[]> data = new ArrayList<>();
			data.add(rowText.split(delimiter)); // Add the header data
			csvWriter.write(file, StandardCharsets.UTF_8, data); // Auto close
		} catch (IOException | NullPointerException e) {
			dialogs.exception("Cannot write now row", e);
		}
	}
	
	/**
	 * Create a new row
	 * @param rowText
	 */
	public void newRow(String rowText) {
		try {
			CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8);
			Collection<String[]> data = new ArrayList<>();
			CsvRow csvRow = csvParser.nextRow(); // Need to call this to get the header
			data.add((String[]) csvParser.getHeader().toArray()); // Add header
			if(csvRow != null)
				data.add((String[]) csvRow.getFields().toArray()); // Add the row under the header
			while((csvRow = csvParser.nextRow()) != null) {
				data.add((String[]) csvRow.getFields().toArray()); // Add existing lines to data
			}
			data.add(rowText.split(delimiter)); // Add the new line to data with a new line
			csvWriter.write(file, StandardCharsets.UTF_8, data); // Auto close
		} catch (IOException e) {
			dialogs.exception("Cannot add new rows", e);
		}
	}

	/**
	 * Return total rows
	 * @return int total rows
	 */
	public int getTotalRows() {
		try {
			CsvContainer csvContainer = csvReader.read(file, StandardCharsets.UTF_8);
			if(csvContainer == null)
				return 0; // Null means no rows here
			return csvContainer.getRowCount();
		} catch (IOException e) {
			dialogs.exception("Cannot find total rows. Returning 0", e);
			return 0;
		}
	}

	/**
	 * Return total columns, in this case, it's on row index 0
	 * @return int total columns
	 */
	public int getTotalColumns() {
		try {
			CsvContainer csvContainer = csvReader.read(file, StandardCharsets.UTF_8);
			if(csvContainer == null)
				return 0; // Null means no rows here
			return csvContainer.getRow(0).getFieldCount();
		} catch (IOException e) {
			dialogs.exception("Cannot find total columns. Returning 0.", e);
			return 0;
		}
	}
}