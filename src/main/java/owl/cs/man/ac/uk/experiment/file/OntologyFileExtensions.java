package owl.cs.man.ac.uk.experiment.file;

public class OntologyFileExtensions {

	public static final String OWLXML = ".xml.owl";
	public static final String RDFXML = ".xml.rdf";
	public static final String FUNCTIONAL = ".owl";
	
	public static String get(String name) {
		if(name.equals("owlxml")) {
			return OWLXML;
		} 
		else if(name.equals("rdfxml")) {
			return RDFXML;
		}
		else if(name.equals("functional")) {
			return FUNCTIONAL;
		}
		else {
			throw new IllegalArgumentException(name+" is not a valid format!");
		}
	}
	
}
