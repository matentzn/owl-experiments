package owl.cs.man.ac.uk.experiment.classification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import owl.cs.man.ac.uk.experiment.util.ReasonerUtilities;

public class OWLOntologyNormaliser {

	private static final String iri = "http://owl.cs.manchester.ac.uk/";
	private String reasonername = "unkown";
	private Properties conf;
	private Set<OWLAxiom> blacklist = new HashSet<OWLAxiom>();;
	private OWLDataFactory df = null;
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/**
	 * constructor
	 * 
	 * @param ontology
	 * @param conf
	 */
	
	public OWLOntologyNormaliser(Properties conf) {
		setConfig(conf);
	}
	
	public OWLOntologyNormaliser() {
		Properties conf = new Properties();
		try {
			conf.load(getClass().getResourceAsStream("default.properties"));
			System.out.println("Configuration: "+conf);
			//AnalysisUtils.pp("");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setConfig(conf);
	}

	public OWLOntologyNormaliser(File file) {
		Properties conf = new Properties();
		try {
			conf.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setConfig(conf);
	}

	/**
	 * sets the config
	 * 
	 * @param conf
	 */
	private void setConfig(Properties conf) {
		this.conf = conf;
	}

	/**
	 * Computes entailments from a given OWL ontology
	 * 
	 * @return a set of {@link org.semanticweb.owlapi.model.OWLAxiom}
	 */
	
	public Set<OWLAxiom> getNormalisedForm(Set<OWLAxiom> ax, OWLReasonerFactory rf) {
		Set<OWLAxiom> normalised = new HashSet<OWLAxiom>();
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		df = man.getOWLDataFactory();
		OWLOntology o;
		try {
			o = man.createOntology(ax,IRI.create(iri + UUID.randomUUID()));
			logger.info("creating reasoner " + rf.getReasonerName());
			OWLReasoner reasoner = rf.createReasoner(o);
			return getNormalisedForm(o, reasoner);
		} catch (OWLOntologyCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		blacklist.clear();
		return normalised;
	}

	public Set<OWLAxiom> getNormalisedForm(OWLOntology o, OWLReasoner reasoner) {
		Set<OWLAxiom> normalised = new HashSet<OWLAxiom>();
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		df = man.getOWLDataFactory();
		if(reasoner.getReasonerName()!=null) {
			reasonername = ReasonerUtilities.getReasonerFullname(reasoner);
		}
		logger.info("done creating reasoner.");
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		if (reasoner.isConsistent()) {
			System.out.println("Consistent!");
			// this gets us the entailmented axioms without annotations
			// if the axiom already exists in the ontology, we have to add
			// its annotations back
			try {
				normalised = computeEntailmentsForConsistentOntology(reasoner,o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			logger.severe("ontology is inconsistent.");
		}
		blacklist.clear();
		return normalised;
	}

	/**
	 * computes entailments the same way as the InferredAxiomGenerator does
	 * 
	 * @param reasoner
	 *            reasoner to use
	 * @return a set of entailments
	 */
	private Set<OWLAxiom> computeEntailmentsForConsistentOntology(
			OWLReasoner reasoner,OWLOntology o) {
		Set<OWLAxiom> result = new HashSet<OWLAxiom>();

		// compute entailments first
		for (OWLClass cl : o.getClassesInSignature()) {
			
			if (conf.getProperty("includeAtomicSubs").equals("true")) {
				addAtomicSubsumptionAxioms(cl, reasoner, result);
			}
			if (conf.getProperty("includeAtomicEquiv").equals("true")) {
				addAtomicEquivalentClassesAxioms(cl, reasoner, result);
			}
			if (conf.getProperty("includeUnsatClasses").equals("true")) {
				addUnsatisfiableClasses(cl, reasoner, result);
			}
		}

		// remove asserted
		if (conf.getProperty("includeAsserted").equals("false")) {
			removeAssertedAxioms(result, o);
		}

		// add or remove nonstrict subs
		if (conf.getProperty("includeNonStrict").equals("false")) {
			// removal just in case the reasoner adds non-strict subsumptions by
			// default. who knows!
			removeNonStrictAtomicSubsumptions(result, reasoner);
		} else {
			// adding something here is ok - will only add direct subsumptions
			// and
			// no asserted because they're already blacklisted (if)
			addNonStrictAtomicSubsumptions(result, reasoner,o);
		}

		boolean includeImported = Boolean.parseBoolean(conf
				.getProperty("includeImported"));
		boolean includeMixed = Boolean.parseBoolean(conf
				.getProperty("includeMixed"));
		boolean includeNative = Boolean.parseBoolean(conf
				.getProperty("includeNative"));
		//
		if (includeNative) {
			if (!includeImported && !includeMixed) {
				// removeNonNativeEntailments(result);
			} else if (!includeImported && includeMixed) {
				// removeImportedEntailments(result);
			}
		}
		return result;
	}

	/**
	 * adds non-strict subsumptions to an entailment set. Not so useful, but
	 * hey, it's in the paper.
	 * 
	 * @param result
	 * @param reasoner
	 */
	private void addNonStrictAtomicSubsumptions(Set<OWLAxiom> result,
			OWLReasoner reasoner,OWLOntology o) {
		for (OWLClass cl : o.getClassesInSignature()) {
			if (reasoner.isSatisfiable(cl)) {
				Set<OWLClass> eqCls = reasoner.getEquivalentClasses(cl)
						.getEntitiesMinus(cl);
				for (OWLClass eq : eqCls) {
					OWLAxiom sc1 = df.getOWLSubClassOfAxiom(cl, eq);
					OWLAxiom sc2 = df.getOWLSubClassOfAxiom(eq, cl);
					if (!blacklist.contains(sc1)) {
						result.add(sc1);
					}
					if (!blacklist.contains(sc2)) {
						result.add(sc2);
					}
				}
			}
		}
	}

	/**
	 * removes all nonstrict subsumptions.
	 * 
	 * @param result
	 * @param reasoner
	 */
	private void removeNonStrictAtomicSubsumptions(Set<OWLAxiom> result,
			OWLReasoner reasoner) {
		Set<OWLAxiom> removals = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : result) {
			if (ax instanceof OWLSubClassOfAxiom) {
				OWLSubClassOfAxiom sc = (OWLSubClassOfAxiom) ax;
				OWLEquivalentClassesAxiom eq = df.getOWLEquivalentClassesAxiom(
						sc.getSubClass(), sc.getSuperClass());
				if (reasoner.isEntailed(eq)
						&& !sc.getSuperClass().isOWLNothing()) {
					removals.add(sc);
					blacklist.add(sc);
				}
			}
		}
		result.removeAll(removals);
	}

	/**
	 * removes all asserted subsumptions
	 * 
	 * @param result
	 */
	private void removeAssertedAxioms(Set<OWLAxiom> result, OWLOntology o) {
		for (OWLClass cl : o.getClassesInSignature()) {
			for (OWLSubClassOfAxiom ax : o.getSubClassAxiomsForSubClass(cl)) {
				result.remove(ax);
				blacklist.add(ax);
			}
		}
	}

	/**
	 * adds all atomic subsumption axioms
	 * 
	 * @param cls
	 *            the owl class to get superclasses for
	 * @param reasoner
	 *            the reasoner to use
	 * @param result
	 *            a set of subclass of axioms
	 */
	private void addAtomicSubsumptionAxioms(OWLClass cls, OWLReasoner reasoner,
			Set<OWLAxiom> result) {
		boolean direct = !Boolean.parseBoolean(conf
				.getProperty("includeIndirect"));
		//KatanaUtils.pause();
		if (reasoner.isSatisfiable(cls)) {
			Set<OWLClass> superClasses = reasoner.getSuperClasses(cls, direct)
					.getFlattened();
			for (OWLClass sup : superClasses) {
				boolean includeTop = Boolean.parseBoolean(conf
						.getProperty("includeTop"));
				OWLAxiom sc = df.getOWLSubClassOfAxiom(cls, sup);
				if (!blacklist.contains(sc)) {
					// if the superclass isn't Top, just add the axiom
					if (!sup.isOWLThing()) {
						result.add(sc);
					} else if (sup.isOWLThing() && includeTop
							&& superClasses.size() == 1) {
						// else, if we only have Top and allow including Top,
						// add it to the list
						result.add(sc);
					}
				}
			}
		}
	}

	/**
	 * adds unsatisfiable classes
	 * 
	 * @param entity
	 * @param reasoner
	 * @param result
	 */
	private void addUnsatisfiableClasses(OWLClass entity, OWLReasoner reasoner,
			Set<OWLAxiom> result) {
		if (!reasoner.isSatisfiable(entity)) {
			OWLAxiom sc = df.getOWLSubClassOfAxiom(entity, df.getOWLNothing());
			if (!blacklist.contains(sc)) {
				result.add(sc);
			}
		}
	}

	/**
	 * adds atomic equivalent classes.
	 * 
	 * @param reasoner
	 * @param result
	 */
	private void addAtomicEquivalentClassesAxioms(OWLClass cls,
			OWLReasoner reasoner, Set<OWLAxiom> result) {
		if (reasoner.isSatisfiable(cls)) {
			Set<OWLClass> eqClasses = reasoner.getEquivalentClasses(cls)
					.getEntitiesMinus(cls);
			for (OWLClass sup : eqClasses) {
				OWLAxiom ax = df.getOWLEquivalentClassesAxiom(cls, sup);
				if (!blacklist.contains(ax)) {
					result.add(ax);
				}
			}
		}
	}

	public String getReasonerName() {
		return reasonername;
	}

}
