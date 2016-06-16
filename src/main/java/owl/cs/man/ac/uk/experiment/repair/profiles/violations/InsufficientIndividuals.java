package owl.cs.man.ac.uk.experiment.repair.profiles.violations;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolation;
import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolationVisitor;

@SuppressWarnings("javadoc")
public class InsufficientIndividuals extends OWLProfileViolation implements InsufficientOperandsViolation {
    public InsufficientIndividuals(OWLOntology currentOntology, OWLAxiom node) {
        super(currentOntology, node);
    }

    @Override
    public String toString() {
        return toString("Not enough individuals; at least two needed");
    }

    @Override
    public void accept(OWLProfileViolationVisitor visitor) {
        visitor.visit(this);
    }
}
