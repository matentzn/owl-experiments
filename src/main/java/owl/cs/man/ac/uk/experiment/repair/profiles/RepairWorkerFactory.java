package owl.cs.man.ac.uk.experiment.repair.profiles;

import java.io.FileNotFoundException;
import java.util.Collections;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

import owl.cs.man.ac.uk.experiment.repair.profiles.violations.InsufficientOperandsViolation;

public class RepairWorkerFactory {

	public static void main(String[] args) {
		OWLDataFactory df = OWLManager.getOWLDataFactory();

		OWLClass owlClassA = df.getOWLClass(IRI.create("urn:A"));
		OWLClass owlClassB = df.getOWLThing();//df.getOWLClass(IRI.create("urn:B"));
		OWLClass owlClass = df.getOWLClass(IRI.create("urn:test"));
		OWLClassExpression ex1 = df.getOWLObjectUnionOf(owlClassB);
		OWLClassExpression ex2 = df.getOWLObjectIntersectionOf(ex1);
		OWLClassExpression ex = df.getOWLObjectUnionOf(ex2, ex1);

		OWLAxiom ax = df.getOWLSubClassOfAxiom(owlClassA, ex);

		System.out.println("EX: " + ax);
		OWLObjectChanger oc = getInsufficientOperandsWorker(df);

		System.out.println(ax.accept(oc));

	}

	public static OWLObjectChanger getWorker(OWLDataFactory df,
			OWLProfileViolation v) {
		if (v instanceof InsufficientOperandsViolation) {
			return getInsufficientOperandsWorker(df);
		}

		return null;
	}

	public static OWLObjectChanger getInsufficientOperandsWorker(
			OWLDataFactory df) {
		return new OWLObjectChanger(df) {

			@Override
			public OWLClassExpression visit(OWLObjectUnionOf ce) {
				if (ce.getOperands().size() == 0) {
					return df.getOWLNothing();
				}
				if (ce.getOperands().size() == 1) {
					OWLClassExpression ex = ce.getOperands().iterator().next();
					if (ex.isOWLNothing()) {
						return df.getOWLNothing();
					} else {
						// if nothing check
						return df.getOWLObjectUnionOf(duplicate(ex),
								df.getOWLNothing());
					}
				}
				return super.visit(ce);
			}

			@Override
			public OWLClassExpression visit(OWLObjectIntersectionOf ce) {
				if (ce.getOperands().size() == 0) {
					return df.getOWLThing();
				}
				if (ce.getOperands().size() == 1) {
					OWLClassExpression ex = ce.getOperands().iterator().next();
					if (ex.isOWLThing()) {
						return df.getOWLThing();
					} else {
						return df.getOWLObjectIntersectionOf(duplicate(ex),
								df.getOWLThing());
					}
				}
				return super.visit(ce);
			}

			/*
			 * @Override public OWLObject visit(OWLDataUnionOf ce) { if
			 * (ce.getOperands().size() == 0) { return null; } if
			 * (ce.getOperands().size() == 1) { return
			 * ce.getOperands().iterator().next(); } return super.visit(ce); }
			 * 
			 * @Override public OWLObject visit(OWLDataIntersectionOf ce) { if
			 * (ce.getOperands().size() == 0) { return null; } if
			 * (ce.getOperands().size() == 1) { return
			 * ce.getOperands().iterator().next(); } return super.visit(ce); }
			 */

		};
	}
}
