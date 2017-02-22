package owl.cs.man.ac.uk.experiment.api;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class TestReasoner {

	public static void main(String[] args) {
		//test1();
		test2();
	}

	private static void test1() {
		OWLOntology o;
		try {
			o = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File("D:\\Dropbox (Personal)\\michael_nico\\bugs\\pellet1.owl"));
			OWLReasoner r = new PelletReasonerFactory().createReasoner(o);
			OWLDataFactory df = o.getOWLOntologyManager().getOWLDataFactory();
		    r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
			
			OWLAxiom ax = df.getOWLSubClassOfAxiom(df.getOWLClass(IRI.create("http://www.semanticweb.org/nico/ontologies/2016/11/untitled-ontology-59#X")), df.getOWLClass(IRI.create("http://www.semanticweb.org/nico/ontologies/2016/11/untitled-ontology-59#A")));
			
			System.out.println(r.isEntailed(ax));
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void test2() {
		OWLOntology o;
		try {
			o = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File("D:\\Dropbox (Personal)\\michael_nico\\bugs\\pellet2.owl"));
			OWLReasoner r = new PelletReasonerFactory().createReasoner(o);
			OWLDataFactory df = o.getOWLOntologyManager().getOWLDataFactory();
		    r.precomputeInferences(InferenceType.CLASS_HIERARCHY);
			
			OWLAxiom ax = df.getOWLSubClassOfAxiom(df.getOWLClass(IRI.create("http://www.semanticweb.org/nico/ontologies/2016/11/untitled-ontology-59#A")), df.getOWLClass(IRI.create("http://www.semanticweb.org/nico/ontologies/2016/11/untitled-ontology-59#B")));
			
			System.out.println(r.isEntailed(ax));
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
