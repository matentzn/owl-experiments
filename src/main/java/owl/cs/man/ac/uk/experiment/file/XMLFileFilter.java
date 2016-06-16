package owl.cs.man.ac.uk.experiment.file;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class XMLFileFilter extends FileFilter {

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return false;
		}

		String s = f.getName();

		return s.endsWith(".xml") || s.endsWith(".XML");
	}

	public String getDescription() {
		return "xml";
	}
}
