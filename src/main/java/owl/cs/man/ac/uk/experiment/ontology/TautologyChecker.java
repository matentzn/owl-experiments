package owl.cs.man.ac.uk.experiment.ontology;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class TautologyChecker {

	public static boolean isTautology(OWLAxiom ax) {
		OWLOntologyManager emptyman = OWLManager.createOWLOntologyManager();
		OWLOntology emptyo;
		try {
			emptyo = emptyman.createOntology();
			OWLReasonerFactory fac = new Reasoner.ReasonerFactory();
			OWLReasoner reasoner = fac.createReasoner(emptyo);
			if (reasoner.isEntailed(ax)) {
				return true;
			}
			else {
				return false;
			}
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
