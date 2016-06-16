package owl.cs.man.ac.uk.experiment.ontology;

import java.io.File;
import java.io.FilenameFilter;

public class OntologyFileNameFilter implements FilenameFilter {

	public boolean accept(File file, String s) {
		return (s.matches(".*[\\.](owl|rdf|xml|obo)$"));
	}

}
