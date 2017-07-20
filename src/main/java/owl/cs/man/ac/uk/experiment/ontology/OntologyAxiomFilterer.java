package owl.cs.man.ac.uk.experiment.ontology;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;

import owl.cs.man.ac.uk.experiment.util.ExperimentUtilities;

public class OntologyAxiomFilterer {
	
	public static OWLOntology getTBox(File ontology, OWLOntologyManager man)
			throws OWLOntologyCreationException {
		return getTBox(ontology, man, new HashSet<AxiomType<?>>());
	}
	
	public static OWLOntology stripRules(OWLOntology o, OWLOntologyManager man, String ontologyname)
			throws OWLOntologyCreationException {
		Set<OWLAxiom> filteredAxioms = new HashSet<OWLAxiom>();
		for(OWLAxiom ax : o.getLogicalAxioms()) {
			if(ax.getAxiomType().toString().contains("Rule")) {
				System.out.println(ax.getAxiomType());
			}
			else {
				filteredAxioms.add(ax);
			}
		}
		OWLOntology o_out = man.createOntology(filteredAxioms,IRI.create("http://owl.cs.manchester.ac.uk/ontologies/"+System.currentTimeMillis()+ontologyname));
		return o_out;
	}

	public static OWLOntology getTBox(File ontology, OWLOntologyManager man,
			Set<AxiomType<?>> strippedTypes) throws OWLOntologyCreationException {
		OWLOntology o = man.loadOntologyFromOntologyDocument(ontology);
		return getTBox(ontology.getName(), man, strippedTypes, o);
	}

	public static OWLOntology getTBox(String ontologyname, OWLOntologyManager man,
			Set<AxiomType<?>> strippedTypes, OWLOntology o)
			throws OWLOntologyCreationException {
		Set<OWLAxiom> filteredAxioms = new HashSet<OWLAxiom>();
		Set<AxiomType<?>> tboxaxiomtypes = new HashSet<AxiomType<?>>(AxiomType.TBoxAxiomTypes);
		tboxaxiomtypes.addAll(AxiomType.RBoxAxiomTypes);
		for(OWLAxiom ax : ExperimentUtilities.getLogicalAxioms(o, Imports.INCLUDED, false,tboxaxiomtypes)) {
			if(tboxaxiomtypes.contains(ax.getAxiomType())) {
				filteredAxioms.add(ax);
			}
			else {
				strippedTypes.add(ax.getAxiomType());
				System.out.println("Removing: "+ax.getAxiomType());
			}
		}
		OWLOntology o_wo_abox = man.createOntology(filteredAxioms,IRI.create("http://owl.cs.manchester.ac.uk/ontologies/tbox/"+System.currentTimeMillis()+ontologyname));
		return o_wo_abox;
	}

}
