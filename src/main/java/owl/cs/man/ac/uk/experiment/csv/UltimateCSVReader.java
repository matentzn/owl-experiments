package owl.cs.man.ac.uk.experiment.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;


public class UltimateCSVReader {


	private List<Map<String, String>> records = new ArrayList<Map<String, String>>();
	private String[] columns;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public UltimateCSVReader(File file) throws IOException {
		initialise(file, true);
	}
	
	public UltimateCSVReader(File file, boolean hascolumns) throws IOException {
		initialise(file, hascolumns);
	}

	private void initialise(File file, boolean hascolumns)
			throws FileNotFoundException, IOException {
		FileReader freader = new FileReader(file);
		CSVReader reader = new CSVReader(freader,CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, '\0');
		if(hascolumns) {
			columns = reader.readNext();
		}
		String[] nextLine;
		
		while ((nextLine = reader.readNext()) != null) {
			Map<String,String> record = new HashMap<String, String>();
			for(int i=0;i<nextLine.length;i++) {
				if(i>=columns.length) {
					record.put(i+"", nextLine[i]);
				}
				else {
					record.put(columns[i], nextLine[i]);
				}
				
			}
			records.add(record);
		}
	}

	public List<Map<String, String>> getRecords() {
		return this.records;
	}
	
	public List<Map<String, String>> getRecord(String s) {
		return this.getRecord(s);
	}

	public List<String> getColumns() {
		return Arrays.asList(columns);
	}

}
