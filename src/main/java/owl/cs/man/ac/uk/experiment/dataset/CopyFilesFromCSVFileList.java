package owl.cs.man.ac.uk.experiment.dataset;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class CopyFilesFromCSVFileList {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		
		if (args.length != 3) {
			throw new IllegalArgumentException(
					"You need exactly 3 parameters ("
							+ "A file with only filenames, "
							+ "A source directory" +
							"A target directory");
		}
		
		String filelist_path = args[0];//
		String corpus_dir_path = args[1]; //
		String target_dir_path = args[2]; //
		
	
		
		File filelist = new File(filelist_path);
		File targetdir = new File(target_dir_path);
		File corpus = new File(corpus_dir_path);
		
		
		
		if(!targetdir.exists()) {
			throw new IllegalArgumentException("target DIR must exist!");
		}
		
		List<String> files = FileUtils.readLines(filelist);
		
		System.out.println(files.size());
		
		for(String filename:files) {
			File file =  new File(corpus,filename);
			System.out.print("Copying "+file.getName()+"... ");
			if(file.exists()) {
				System.out.print("exists... ");
				File destination = new File(targetdir,filename);
				if(!destination.exists()) {
					FileUtils.copyFile(file, destination);
					System.out.print("copied... ");
				}
				else {
					System.out.print("was already copied... ");
				}
			}
			else {
				System.err.println("... not exist!");
			}
			System.out.println("done... ");
		}
	}
}
