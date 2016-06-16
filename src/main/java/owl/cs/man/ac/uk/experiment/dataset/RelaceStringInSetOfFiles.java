package owl.cs.man.ac.uk.experiment.dataset;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class RelaceStringInSetOfFiles {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			throw new IllegalArgumentException(
					"You need exactly 2 parameters ("
							+ "A directory with all the files, "
							+ "A string with the replacement matching regex, "
							+ "A string with the replacement.");
		}

		String dir_path = args[0];
		String regex = args[1];
		String replacement = args[2];

		System.out.println(regex);

		File dir = new File(dir_path);

		for (File file : dir.listFiles()) {
			try {
				List<String> lines = FileUtils.readLines(file);
				List<String> newLines = FileUtils.readLines(file);
				for (String line : lines) {
					newLines.add(line.replaceAll(regex, replacement));
				}
				FileUtils.writeLines(file, newLines);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
