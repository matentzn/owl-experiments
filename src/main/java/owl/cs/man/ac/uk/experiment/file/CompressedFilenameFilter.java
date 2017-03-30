package owl.cs.man.ac.uk.experiment.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

public class CompressedFilenameFilter implements FilenameFilter {

	private static final Pattern extensions = Pattern
			.compile(".*(\\.(rar|7z|zip|gz|bz2|tar))$");
	
	public boolean accept(File file, String arg1) {
		if (extensions.matcher(arg1).matches()) {
			return true;
		}
		return false;
	}
}
