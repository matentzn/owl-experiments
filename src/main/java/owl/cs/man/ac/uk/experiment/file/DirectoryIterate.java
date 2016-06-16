package owl.cs.man.ac.uk.experiment.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class DirectoryIterate {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//File sourceDir = new File("D:\\Dropbox\\PHD\\Projects\\Experiments\\corpora\\bioportal10hardest");
		File sourceDir = new File("D:\\Dropbox\\PHD\\Projects\\Experiments\\corpora\\bioportal80startified");
			
		for (File file : sourceDir.listFiles()) {
			if(file.isFile()) {
				List<String> fileLines = FileUtils.readLines(file);
				String lastline = fileLines.get(fileLines.size()-1);
				String linebeforethat = fileLines.get(fileLines.size()-2);
				if(lastline.contains("NULL")) {
					System.out.println(lastline);
					System.out.println(file);
					fileLines.remove(fileLines.size()-1);
					FileUtils.writeLines(file, fileLines);
				}
			}
		}
	}

}
