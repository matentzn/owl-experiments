package owl.cs.man.ac.uk.experiment.repair.profiles.violations;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolation;
import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolationVisitor;

/** Punning between properties is not allowed
 * 
 * @author ignazio */
public class IllegalPunning extends OWLProfileViolation {
    private IRI iri;

    /** @param currentOntology
     * @param node
     * @param iri */
    public IllegalPunning(OWLOntology currentOntology, OWLAxiom node, IRI iri) {
        super(currentOntology, node);
        this.iri = iri;
    }

    @Override
    public String toString() {
        return toString("Cannot pun between properties: %s", iri.toQuotedString());
    }

    @Override
    public void accept(OWLProfileViolationVisitor visitor) {
        visitor.visit(this);
    }
}
