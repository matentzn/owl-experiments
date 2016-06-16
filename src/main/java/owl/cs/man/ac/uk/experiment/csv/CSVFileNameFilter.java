package owl.cs.man.ac.uk.experiment.csv;

import java.io.File;
import java.io.FilenameFilter;

public class CSVFileNameFilter implements FilenameFilter {

	public boolean accept(File file, String s) {
		return (s.matches(".*[\\.](txt|csv)$"));
	}

}
