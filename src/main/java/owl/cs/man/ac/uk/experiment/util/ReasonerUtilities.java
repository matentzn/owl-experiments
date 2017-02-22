package owl.cs.man.ac.uk.experiment.util;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
//import org.semanticweb.more.MOReReasonerFactory;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

//import owl.cs.manchester.adreasoner.ADReasonerFactory;
//import uk.ac.manchester.cs.chainsaw.ChainsawReasonerFactory;
import uk.ac.manchester.cs.jfact.JFactFactory;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;

//import eu.trowl.owlapi3.rel.reasoner.dl.RELReasonerFactory;

public class ReasonerUtilities {

	public static String getReasonerFullname(OWLReasoner reasoner) {
		return reasoner.getReasonerName() + " " + getReasonerVersion(reasoner);
	}

	public static String getReasonerVersion(OWLReasoner reasoner) {
		return reasoner.getReasonerVersion().getMajor() + "."
				+ reasoner.getReasonerVersion().getMinor() + "."
				+ reasoner.getReasonerVersion().getPatch() + "."
				+ reasoner.getReasonerVersion().getBuild();
	}

	public static OWLReasonerFactory getFactory(String reasonername) {
		if (reasonername.startsWith("hermit")) {
			return new Reasoner.ReasonerFactory();
		} /*else if (reasonername.startsWith("fact")) {
			return new FaCTPlusPlusReasonerFactory();
		} */else if (reasonername.startsWith("jfact")) {
			return new JFactFactory();
		} else if (reasonername.startsWith("pellet")) {
			return new PelletReasonerFactory();
		} else if (reasonername.startsWith("structural")) {
			return new StructuralReasonerFactory();
		} else if (reasonername.startsWith("elk")) {
			return new ElkReasonerFactory();
		} /*else if (reasonername.startsWith("more")) {
			String[] p = reasonername.split("-");
			if (p.length != 2) {
				throw new IllegalArgumentException(
						"Reasoner name "
								+ reasonername
								+ " starts with more but is not of the right form: more-hermit");
			}
			String delagate = p[1];
			int del = 0;
			if (delagate.equals("hermit")) {
				del = 0;
			} else if (delagate.equals("pellet")) {
				del = 1;
			} else if (delagate.equals("jfact")) {
				del = 2;
			} else {
				throw new IllegalArgumentException(
						"Reasoner name "
								+ reasonername
								+ " starts with more but does not contain a valid delegate: more-hermit/pellet/jfact");
			}
			return new MOReReasonerFactory(del);
		}*/
			
		
		// removed because we want reasoning with more
		/**
		

			return null;

		}
		**/
		/*
		 * else if (reasonername.equalsIgnoreCase("trowl")) {
		 * System.out.println("Using TrOWL DL, non TMS package.."); return new
		 * RELReasonerFactory(); } else if
		 * (reasonername.equalsIgnoreCase("adreasoner")) { return new
		 * ADReasonerFactory();
		 * 
		 * }
		 */
		else {
			throw new IllegalArgumentException("Reasoner name " + reasonername
					+ " illegal. Must be one of pellet, hermit, fact, jfact");
		}
	}

	public static String getReasonerFullname(OWLReasoner r, String alt) {
		if (r.getReasonerName() == null) {
			return alt;
		}
		return getReasonerFullname(r);
	}

	public static OWLReasonerConfiguration getReasonerConfig(String param,
			long reasoner_timeout) {
		return new SimpleConfiguration(reasoner_timeout);
	}

	public static OWLReasonerConfiguration getReasonerConfig(String param) {
		return new SimpleConfiguration();
	}

	public static ModuleType getModuleType(String strategy) {
		if (strategy.contains("-bot-")) {
			return ModuleType.BOT;
		} else if (strategy.contains("-top-")) {
			return ModuleType.TOP;
		} else if (strategy.contains("-star-")) {
			return ModuleType.STAR;
		} else {
			return ModuleType.BOT;
		}
	}
}
