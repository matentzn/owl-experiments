package owl.cs.man.ac.uk.experiment.ontology;

import java.util.*;

import org.semanticweb.owlapi.model.*;

public class OntologyCycleDetector {

	/*
	 * Adapted from Dmitry Tsarkov
	 */

	/*
	 * Sound, but maybe incomplete (may miss some cycles).
	 */
	public static boolean containsCycle(OWLOntology ontology, boolean includeImports) {
		final Set<OWLClass> Classes = ontology.getClassesInSignature(includeImports);
		System.out.println("DeCycler Started with " + Classes.size()
				+ " classes");
		for (OWLClass Class : Classes) {
			Queue<OWLClass> Queue = new LinkedList<OWLClass>();
			Set<OWLClass> Visited = new HashSet<OWLClass>();
			Queue.add(Class);
			// System.out.println("DeCycler.processOntology() Class " + Class);
			while (Queue.peek() != null) {
				OWLClass current = Queue.poll();
				if (current == Class && !Queue.isEmpty()) {
					System.out.println("DeCycler.processOntology(): bugs");
				}
				// no need to check the same entry more than once
				if (Visited.contains(current))
					continue;
				Visited.add(current);

				Set<OWLAxiom> Defs = getReferencingAxioms(ontology, current, includeImports);
				Set<OWLClassExpression> ces = getDefinition(current, Defs);

				// no definition for a class -- fine!
				OWLClassExpression C = ontology.getOWLOntologyManager()
						.getOWLDataFactory().getOWLObjectIntersectionOf(ces);

				// check the signature of that axiom
				Set<OWLClass> AxSig = C.getClassesInSignature();

				if (AxSig.contains(Class)) {
					return true;
				} else {
					AxSig.remove(current);
					Queue.addAll(AxSig);
				}

			}
		}
		return false;
	}

	public static Set<OWLAxiom> getReferencingAxioms(OWLOntology ontology,
			OWLClass current, boolean includeImports) {
		Set<OWLAxiom> refs = new HashSet<OWLAxiom>();
		if(includeImports) {
			for(OWLOntology o:ontology.getImportsClosure()) {
				refs.addAll(o.getReferencingAxioms(current));
			}
		}
		else {
			refs.addAll(ontology.getReferencingAxioms(current));
		}
		return refs;
	}

	private static Set<OWLClassExpression> getDescription(OWLClass current,
			OWLAxiom ax) {
		Set<OWLClassExpression> ces = new HashSet<OWLClassExpression>();
		if (ax.isOfType(AxiomType.SUBCLASS_OF)) {
			OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom) ax;
			return Collections.singleton(axiom.getSuperClass());
		} else if (ax.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
			OWLEquivalentClassesAxiom axiom = (OWLEquivalentClassesAxiom) ax;
			ces.addAll(axiom.getClassExpressions());
			ces.remove(current);
		}
		return ces;
	}

	private static Set<OWLClassExpression> getDefinition(OWLClass current,
			Set<OWLAxiom> defs) {
		Set<OWLClassExpression> sce = new HashSet<OWLClassExpression>();
		for (OWLAxiom ax : defs) {
			if (ax.isOfType(AxiomType.SUBCLASS_OF)) {
				OWLSubClassOfAxiom axiom = (OWLSubClassOfAxiom) ax;
				if (current.equals(axiom.getSubClass())) {
					sce.addAll(getDescription(current, ax));
				}
			} else if (ax.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
				OWLEquivalentClassesAxiom axiom = (OWLEquivalentClassesAxiom) ax;
				for (OWLClassExpression cl : axiom.getClassExpressions()) {
					if (!cl.isAnonymous() && current.equals(cl.asOWLClass())) {
						sce.addAll(getDescription(current, ax));
					}
				}
			}
		}
		return sce;
	}

}
