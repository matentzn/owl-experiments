package owl.cs.man.ac.uk.experiment.repair.profiles.violations;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolation;
import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolationVisitor;

@SuppressWarnings("javadoc")
public class EmptyOneOfExpression extends OWLProfileViolation implements InsufficientOperandsViolation {
    public EmptyOneOfExpression(OWLOntology currentOntology, OWLAxiom currentAxiom) {
        super(currentOntology, currentAxiom);
    }

    @Override
    public String toString() {
        return toString("Empty OneOf: at least one value needed");
    }

    @Override
    public void accept(OWLProfileViolationVisitor visitor) {
        visitor.visit(this);
    }
}
