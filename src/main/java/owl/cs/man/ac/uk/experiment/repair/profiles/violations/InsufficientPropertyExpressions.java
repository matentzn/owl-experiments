package owl.cs.man.ac.uk.experiment.repair.profiles.violations;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolation;
import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolationVisitor;

@SuppressWarnings("javadoc")
public class InsufficientPropertyExpressions extends OWLProfileViolation implements InsufficientOperandsViolation {
    public InsufficientPropertyExpressions(OWLOntology ontology, OWLAxiom axiom) {
        super(ontology, axiom);
    }

    @Override
    public String toString() {
        return toString("Not enough property expressions; at least two needed");
    }

    @Override
    public void accept(OWLProfileViolationVisitor visitor) {
        visitor.visit(this);
    }
}
