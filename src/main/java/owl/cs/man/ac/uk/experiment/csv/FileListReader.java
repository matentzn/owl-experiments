package owl.cs.man.ac.uk.experiment.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

public class FileListReader {

	private String columnname = "filename";
	private List<String> files = new ArrayList<String>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	
	public FileListReader(File file, boolean hascolumns) throws IOException {
		initialise(file, hascolumns);
	}

	private void initialise(File file, boolean hascolumns)
			throws FileNotFoundException, IOException {
		FileReader freader = new FileReader(file);
		CSVReader reader = new CSVReader(freader);
		if(hascolumns) {
			columnname = reader.readNext()[0];
		}
		String[] nextLine;
		
		while ((nextLine = reader.readNext()) != null) {
			if(nextLine!=null) {
			files.add(nextLine[0]);	
			}
		}
	}

	public List<String> getFiles() {
		return this.files;
	}

	public String getColumnName() {
		return columnname;
	}

}
