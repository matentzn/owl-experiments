package owl.cs.man.ac.uk.experiment.file;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class RenameFilesInDirectory {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("Takes in a directory path and a regular expression with optional replacement and renames the files in a directory.");
		
		if (args.length != 3) {
			throw new IllegalArgumentException(
					"You need exactly 3 parameters ("
							+ "path to a directory, "
							+ "a regular expression to match what should be renamed" +
							"a replacement string");
		}

		String directory = args[0];		
		File sourceDir = new File(directory);
		if(!sourceDir.exists()) {
			throw new IllegalArgumentException(
					"Source directory does not exist");
		}
		
		String matchRegex = args[1];
		String replace = args[2];
		
		for (File file : sourceDir.listFiles()) {
				String newName = file.getName().replaceAll(matchRegex, replace);
				File renamedFile = new File(sourceDir, newName);
				System.out.println("Rename "+file+" to "+renamedFile);
				FileUtils.moveFile(file, renamedFile);
			}
		}

}
