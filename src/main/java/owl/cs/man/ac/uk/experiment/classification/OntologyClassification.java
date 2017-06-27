package owl.cs.man.ac.uk.experiment.classification;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import owl.cs.man.ac.uk.experiment.ontology.CompareOntologies;

public class OntologyClassification {
	
	public static boolean isCorrect(OWLOntology o, OWLOntology inf,
			OWLOntologyManager man, OWLReasonerFactory rf) {
		System.out.println("Using "+rf.getReasonerName()+" to verify classification");
		OWLOntology correctinf;
		try {
			correctinf = OntologyClassification.getInferredHierarchy(man, rf.createReasoner(o), o);
			//Map<String,String> data = new HashMap<String,String>();
			//CompareOntologies.compareOntologies("o1l", "o2l", "o1", "o2", data, inf, correctinf);
			return CompareOntologies.equalOntologies(inf,correctinf);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static OWLOntology getInferredHierarchy(OWLOntologyManager manager,
			OWLReasoner r,
			OWLOntology o)
			throws OWLOntologyCreationException {
		System.out.println("Getting inferred hierarchy with: "+r.getReasonerName());
		System.out.println("Size asserted: "+o.getLogicalAxioms().size());
		OWLOntologyNormaliser n = new OWLOntologyNormaliser();
		Set<OWLAxiom> ax = new HashSet<OWLAxiom>();
		ax.addAll(n.getNormalisedForm(o,r));
		System.out.println("Size inferred: "+ax.size());
		return manager.createOntology(ax);
	}

	public static OWLOntology getInferredHierarchy(OWLReasoner r, 
			OWLClass top) throws OWLOntologyCreationException{
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLDataFactory df = man.getOWLDataFactory();
    	OWLOntology o = man.createOntology();
		applySubclasses(r, top, man, df, o);
		return o;
	}

	private static void applySubclasses(OWLReasoner r, OWLClass top, OWLOntologyManager man,
			OWLDataFactory df, OWLOntology o) throws OWLOntologyCreationException {
		if ((r.getSubClasses(top, true).getFlattened()).contains(df.getOWLNothing())
				&& (r.getSubClasses(top, true).getFlattened()).size() == 1){
			return;
		}
		else{
			for(OWLClass sub:r.getSubClasses(top, true).getFlattened()){
				man.addAxiom(o, df.getOWLSubClassOfAxiom(sub, top));
				applySubclasses(r, sub, man, df, o);
			}
		}
	}
}
