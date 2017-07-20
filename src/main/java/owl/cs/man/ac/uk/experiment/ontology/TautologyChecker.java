package owl.cs.man.ac.uk.experiment.ontology;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
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
	
	public static Set<OWLAxiom> getTautologies(Set<OWLAxiom> unionSet) throws OWLOntologyCreationException {
		Set<OWLAxiom> delSet = new HashSet<OWLAxiom>();
		OWLReasoner r = new Reasoner.ReasonerFactory().createReasoner(OWLManager.createOWLOntologyManager().createOntology());
		for(OWLAxiom ax:unionSet)
		{
			if(r.isEntailed(ax))
			{
				delSet.add(ax);
			}
			
		}
		return delSet;
		
		
	}
	
	// Filtering of axioms - remove declarations and tautologies. At the moment
		// tautologies are
		// just defined w.r.t classification. Later versions will require something
		// more complex.
		public static Set<OWLAxiom> getTautologySyntactic(Set<OWLAxiom> cleanSet) {
			Set<OWLAxiom> filter = new HashSet<OWLAxiom>();
			for (OWLAxiom ax : cleanSet) {
				if (ax.isOfType(AxiomType.DECLARATION)) {
					filter.add(ax);
				} else {
					// Sanity check
					if (ax.isOfType(AxiomType.SUBCLASS_OF)) {
						if (isTautology((OWLSubClassOfAxiom) ax)) {
							filter.add(ax);
						}
					}
				}
			}
			cleanSet.removeAll(filter);
			return cleanSet;
		}
		
		private static Boolean isTautology(OWLSubClassOfAxiom ax) {
			return ax.getSubClass().isOWLNothing() || ax.getSuperClass().isOWLThing();
		}
}
