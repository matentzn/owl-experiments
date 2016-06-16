package owl.cs.man.ac.uk.experiment.ontology;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OntologyFiletypePattern {
	public static final Pattern ONTOLOGYFILETYPEPATTERN = Pattern
			.compile(".*(\\.(owl|ttl|owl2|rdf|rdfs|obo|owx|owl\\.xml|owl2\\.xml|owl\\.txt|owl2\\.txt|owl\\.zip|owl2\\.zip|rdf\\.xml|rdf\\.txt|rdf\\.zip|rdf\\.tar\\.gz|owl\\.tar\\.gz|owl2\\.tar\\.gz|owl\\.bz2|owl2\\.bz2|rdf\\.bz2))$");
	public static final Pattern ONTOLOGYFILETYPEPATTERN_EXCLUDING_ARCHIVE = Pattern
			.compile(".*(\\.(owl|ttl|owl2|rdf|rdfs|obo|owx|owl\\.xml|owl2\\.xml|owl\\.txt|owl2\\.txt|rdf\\.xml|rdf\\.txt))$");

	public static boolean potentialFileType(String filename, boolean inclArchive) {
		if(inclArchive) {
			Matcher matcher = ONTOLOGYFILETYPEPATTERN.matcher(filename);
			if (matcher.find()) {
				return true;
			}
		}
		else {
			Matcher matcher = ONTOLOGYFILETYPEPATTERN_EXCLUDING_ARCHIVE.matcher(filename);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}
}
