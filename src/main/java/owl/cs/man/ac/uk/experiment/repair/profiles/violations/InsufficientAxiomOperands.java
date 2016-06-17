package owl.cs.man.ac.uk.experiment.repair.profiles.violations;

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataIntersectionOf;
import org.semanticweb.owlapi.model.OWLDataUnionOf;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import owl.cs.man.ac.uk.experiment.repair.profiles.OWLObjectChanger;
import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolation;
import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolationVisitor;

@SuppressWarnings("javadoc")
public class InsufficientAxiomOperands extends OWLProfileViolation implements InsufficientOperandsViolation {
    private final OWLObject expression;

    public InsufficientAxiomOperands(OWLOntology currentOntology, OWLAxiom node, OWLObject c) {
        super(currentOntology, node);
        expression = c;
    }

    @Override
    public void accept(OWLProfileViolationVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return toString("Not enough operands; at least two needed: %s", expression);
    }
    
    @Override
    public List<OWLOntologyChange> repair() {
    	List<OWLOntologyChange> repairs = new ArrayList<OWLOntologyChange>();
    	
    	if(expression instanceof OWLAxiom) {
    		return super.repair();
    	}
    	
        return repairs;
    }
}