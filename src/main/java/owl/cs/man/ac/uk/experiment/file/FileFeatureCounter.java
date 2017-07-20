package owl.cs.man.ac.uk.experiment.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileFeatureCounter {
	
	public static int countLines(File file) throws IOException {
		int fileCounter = 0;
		if(file.isDirectory()) {
			for(File f : file.listFiles()) {
				fileCounter+=countLinesInSingleFile(f);
			}
			return fileCounter;
		} 
		else if (file.exists()) {
				return countLinesInSingleFile(file);
		}
		return 0;
	}

	private static int countLinesInSingleFile(File file)
			throws FileNotFoundException, IOException {
		int fileCounter = 0;
		BufferedReader br = new BufferedReader(new FileReader(file));
		while (br.readLine() != null) {
			fileCounter++;
		}
		br.close();
		return fileCounter;
	}
	
	public static int countUnicodeChars(File file) throws IOException {
		int charCounter = 0;
		if(file.isDirectory()) {
			for(File f : file.listFiles()) {
				charCounter+=countCharsInSingleFile(f);
			}
			return charCounter;
		} 
		else if (file.exists()) {
				return countCharsInSingleFile(file);
		}
		return 0;
	}
	
	private static int countCharsInSingleFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		int counter = 0;
		while ((br.read()) != -1) {
            counter++;
        }
		br.close();
        return counter;
	}

}
