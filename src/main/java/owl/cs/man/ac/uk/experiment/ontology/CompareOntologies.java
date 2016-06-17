package owl.cs.man.ac.uk.experiment.ontology;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



//import org.apache.commons.io.FileUtils;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.AutoIRIMapper;

import owl.cs.man.ac.uk.experiment.csv.CSVUtilities;
import owl.cs.man.ac.uk.experiment.dataset.OntologySerialiser;
import owl.cs.man.ac.uk.experiment.util.ExperimentUtilities;

public class CompareOntologies {

	public static void main(String[] args) throws IOException {

		/*
		 * DO NOT CHANGE THE MAIN METHOD
		 */

		if (args.length != 4) {
			throw new IllegalArgumentException(
					"You need exactly 2 parameters ("
							+ "A directory with ontologies, "
							+ "A path to the output CSV.");
		}
		/*
		String ontology1_path = "D:\\owlxml\\files";
		String ontology2_path = "D:\\Dropbox\\PHD\\tools\\corpus\\bpjan\\owlxml";
		String export_path = "D:\\exportcomparison";
		String export_csv_path = "D:\\compare_owlxmlserial.csv";
		*/
		String ontology1_path = args[0];
		String ontology2_path = args[1];
		String export_path = args[2];
		String export_csv_path = args[3];

		File ontology1 = new File(ontology1_path);
		File ontology2 = new File(ontology2_path);
		File exportDir = new File(export_path);

		if (!ontology1.exists()) {
			throw new IOException(ontology1 + " does not exist!");
		}

		if (!ontology2.exists()) {
			throw new IOException(ontology2 + " does not exist!");
		}

		System.out.println("TBOXDIR: " + ontology1);
		
		List<Map<String,String>> exportdata = new ArrayList<Map<String,String>>();
		
		if(ontology1.isFile()) {
			if(ontology2.isFile()) {
				Map<String,String> data = compareOntologies(ontology1, ontology2, "o1", "o2", ontology1.getName(),
						exportDir);
				exportdata.add(data);
			}
		}
		else {
		for (File o1 : ontology1.listFiles(new OntologyFileNameFilter())) {
			System.out.println(o1);
			String filename = o1.getName().replaceAll("_functional.owl", "");
			for (File o2 : ontology2.listFiles(new OntologyFileNameFilter())) {
				if (o2.getName().contains(filename)) {
					Map<String,String> data = compareOntologies(o1, o2, "o1", "o2", o2.getName(),
							exportDir);
					exportdata.add(data);
				}
			}
		}
		}

		CSVUtilities.writeCSVData(new File(export_csv_path), exportdata, true);
		
		/*
		 * 
		 * for(File o1:ontology1.listFiles(new OntologyFileNameFilter())) {
		 * if(o1.getName().contains("m_top_") ||
		 * o1.getName().contains("m_bottom_")) { continue; }
		 * System.out.println(o1); File o2 = o1.getName().contains("m_nested") ?
		 * new File(ontology2,o1.getName().replaceAll("m_nested_", "")) : new
		 * File(ontology2,"m_nested_"+o1.getName()); compareOntologies(o1, o2);
		 * }
		 */

		// compareOntologies(ontology1, ontology2);

	}

	public static Map<String, String> compareOntologies(File ontology1,
			File ontology2, String o1_short, String o2_short, String compid,
			File exportDir) {
		Map<String, String> data = new HashMap<String, String>();
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLOntologyManager man2 = OWLManager.createOWLOntologyManager();
		// ReasonerFactory f = new Reasoner.ReasonerFactory();

		try {
			OWLOntology o1 = null;
			OWLOntology o2 = null;
			try {
				o1 = man.loadOntologyFromOntologyDocument(ontology1);
			}
			catch(UnloadableImportException e) {
				 OWLOntologyIRIMapper autoIRIMapper = new AutoIRIMapper(ontology1.getParentFile(), false);
			     man.addIRIMapper(autoIRIMapper);
			     o1 = man.loadOntologyFromOntologyDocument(ontology1);
			}
			
			try {
				o2 = man2.loadOntologyFromOntologyDocument(ontology2);
			}
			catch(UnloadableImportException e) {
				 OWLOntologyIRIMapper autoIRIMapper = new AutoIRIMapper(ontology2.getParentFile(), false);
			     man2.addIRIMapper(autoIRIMapper);
			     o2 = man2.loadOntologyFromOntologyDocument(ontology2);
			}

			Set<OWLAxiom> difference = compareOntologies(ontology1.getName(),
					ontology2.getName(), o1_short, o2_short, data, o1, o2,false);

			OWLOntologyManager manexp = OWLManager.createOWLOntologyManager();

			OWLOntology o = manexp.createOntology(difference);
			if(!compid.isEmpty()) {
			String expofilename = o1_short + "_" + o2_short + "_" + compid;
			if (!(new File(exportDir, expofilename)).exists()) {
				try {
					OntologySerialiser.saveOWLXML(exportDir, o, expofilename,
							manexp);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
			}
			}

		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		return data;
	}

	public static Set<OWLAxiom> compareOntologies(String o1name, String o2name,
			String o1_short, String o2_short, Map<String, String> data,
			OWLOntology o1, OWLOntology o2,boolean computeTautologies) throws OWLOntologyCreationException {
		Set<OWLAxiom> o1_ax = new HashSet<OWLAxiom>();
		Set<OWLAxiom> o2_ax = new HashSet<OWLAxiom>();
		Set<OWLAxiom> tautologies_o1 = new HashSet<OWLAxiom>();
		Set<OWLAxiom> tautologies_o2 = new HashSet<OWLAxiom>();

		for (OWLAxiom ax : ExperimentUtilities.getLogicalAxioms(o1, true, false, true)) {
			o1_ax.add(ax.getAxiomWithoutAnnotations());
		}
		
		for (OWLAxiom ax : ExperimentUtilities.getLogicalAxioms(o2, true, false, true)) {
			o2_ax.add(ax.getAxiomWithoutAnnotations());
		}
		
		if(computeTautologies) {
			System.out.println("Computing tautologies....");
			tautologies_o1.addAll(getTautologies(o1_ax));
			tautologies_o2.addAll(getTautologies(o2_ax));
			System.out.println("...done");
		}
		else{
			System.out.println("Computing cheap tautologies....");
			tautologies_o1.addAll(getSubClassOfThing(o1_ax));
			tautologies_o2.addAll(getSubClassOfThing(o2_ax));
			System.out.println("...done");
		}

		Set<OWLAxiom> union = new HashSet<OWLAxiom>();
		union.addAll(o1_ax);
		union.addAll(o2_ax);

		Set<OWLAxiom> intersection = new HashSet<OWLAxiom>();
		intersection.addAll(o1_ax);
		intersection.retainAll(o2_ax);

		Set<OWLAxiom> difference = new HashSet<OWLAxiom>();
		difference.addAll(union);
		difference.removeAll(intersection);

		Set<OWLAxiom> o1_wo_o2 = new HashSet<OWLAxiom>();
		o1_wo_o2.addAll(o1_ax);
		o1_wo_o2.removeAll(o2_ax);
		//o1_wo_o2.removeAll(tautologies_o1);

		System.out.println("");
		System.out.println("#################");
		System.out.println("Axioms in " + o1_short + " that are not in "
				+ o2_short);
		for (OWLAxiom ax : o1_wo_o2) {
			System.out.println(ax);
		}
		

		Set<OWLAxiom> o2_wo_o1 = new HashSet<OWLAxiom>();
		o2_wo_o1.addAll(o2_ax);
		o2_wo_o1.removeAll(o1_ax);
		//o2_wo_o1.removeAll(tautologies_o2);

		System.out.println("");
		System.out.println("#################");
		System.out.println("Axioms in " + o2_short + " that are not in "
				+ o1_short);
		for (OWLAxiom ax : o2_wo_o1) {
		 System.out.println(ax);
		}
		//AnalysisUtils.pp("AXIOMS");

		Set<OWLEntity> sigo1 = ExperimentUtilities.getSignature(o1,true);
		Set<OWLEntity> sigo2 = ExperimentUtilities.getSignature(o2,true);
		Set<OWLEntity> entitieso1 = getEntitiesInO1notinO2(sigo1, sigo2,
				o1_short, o2_short);
		Set<OWLEntity> entitieso2 = getEntitiesInO1notinO2(sigo2, sigo1,
				o2_short, o1_short);

		System.out.println("");
		System.out.println("#################");
		System.out.println("Entities in " + o1_short + " that are not in "
				+ o2_short);
		for (OWLEntity e : entitieso1) {
			System.out.println(e);
		}
		System.out.println("");
		System.out.println("#################");
		System.out.println("Entities in " + o2_short + " that are not in "
				+ o1_short);
		for (OWLEntity e : entitieso2) {
			System.out.println(e);
		}

		// Axiomtypes that are not in one but are in the other
		Set<String> axtypeso1 = getAxtypesInO1NotInO2(o1_ax, o2_ax);
		Set<String> axtypeso2 = getAxtypesInO1NotInO2(o2_ax, o1_ax);
		String axtypeso1_s = CSVUtilities.listToCSV(new ArrayList<String>(
				axtypeso1));
		String axtypeso2_s = CSVUtilities.listToCSV(new ArrayList<String>(
				axtypeso2));

		// Axiomtypes of axioms in the difference of o1 and o2
		Set<AxiomType<?>> axtypes_o1_wo_o2 = getAxiomtypes(o1_wo_o2);
		Set<AxiomType<?>> axtypes_o2_wo_o1 = getAxiomtypes(o2_wo_o1);
		String axtypes_o2_wo_o1_s = CSVUtilities
				.listToCSV(new ArrayList<AxiomType<?>>(axtypes_o2_wo_o1));
		String axtypes_o1_wo_o2_s = CSVUtilities
				.listToCSV(new ArrayList<AxiomType<?>>(axtypes_o1_wo_o2));

		Map<String, Map<String, Integer>> axiomtypeanalysis = new HashMap<String, Map<String, Integer>>();

		System.out.println("");

		if (o1_wo_o2.isEmpty()) {
			// if o1 contains o2
			obtainAxiomTypeSignatureMap(difference, sigo1, entitieso2,
					axiomtypeanalysis);
			System.out.println(o1_short + ":old " + o2_short
					+ ":new (signature in difference)");
		} else if (o2_wo_o1.isEmpty()) {
			// if o2 contains o1
			obtainAxiomTypeSignatureMap(difference, sigo2, entitieso1,
					axiomtypeanalysis);
			System.out.println(o2_short + ":old " + o1_short
					+ ":new (signature in difference)");
		}

		for (String axt : axiomtypeanalysis.keySet()) {
			System.out.println("");
			System.out.println(axt);
			StringBuilder sb = new StringBuilder();
			for (String type : axiomtypeanalysis.get(axt).keySet()) {
				System.out.println(type + ": "
						+ axiomtypeanalysis.get(axt).get(type));
				sb.append(type + ":" + axiomtypeanalysis.get(axt).get(type)
						+ " ");
			}
			data.put(o1_short + "_" + o2_short + "_" + axt, sb.toString());
		}

		System.out.println("Comparing:");
		System.out.println(o1_short + ": " + o1name);
		System.out.println(o2_short + ": " + o2name);
		System.out.println("Size " + o1_short + ": " + o1_ax.size());
		System.out.println("Size " + o2_short + ": " + o2_ax.size());
		System.out.println("" + o1_short + " contains " + o2_short + ": "
				+ o2_wo_o1.isEmpty());
		System.out.println("" + o2_short + " contains " + o1_short + ": "
				+ o1_wo_o2.isEmpty());
		System.out.println("" + o2_short + " wo " + o1_short + " size: "
				+ o2_wo_o1.size());
		System.out.println("" + o1_short + " wo " + o2_short + " size: "
				+ o1_wo_o2.size());
		System.out.println("" + o2_short + " sig diff to " + o1_short
				+ " size: " + entitieso2.size());
		System.out.println("" + o1_short + " sig diff to " + o2_short
				+ " size: " + entitieso1.size());
		System.out.println("" + o1_short + " axtypediff " + o2_short + ": "
				+ axtypeso1_s);
		System.out.println("" + o2_short + " axtypediff " + o1_short + ": "
				+ axtypeso2_s);
		System.out.println("" + o1_short + " axtype_in_diff " + o2_short + ": "
				+ axtypes_o1_wo_o2_s);
		System.out.println("" + o2_short + " axtype_in_diff " + o1_short + ": "
				+ axtypes_o2_wo_o1_s);

		// - o1.getClassesInSignature().size() because we dont need the a
		// scl a test
		long subsumptiontesto1 = (o1.getClassesInSignature(true).size() * o1
				.getClassesInSignature().size())
				- o1.getClassesInSignature().size();
		long subsumptiontesto2 = (o2.getClassesInSignature(true).size() * o2
				.getClassesInSignature(true).size())
				- o2.getClassesInSignature(true).size();

		double diff = (double) Math.abs(subsumptiontesto1 - subsumptiontesto2);
		double average = ((double) subsumptiontesto1 + (double) subsumptiontesto2) / 2;
		double percent_difference = (diff / average) * 100;

		boolean equivalent = (o1_wo_o2.isEmpty() && o2_wo_o1.isEmpty());
		
		data.put(o1_short, o1name);
		data.put(o2_short, o2name);
		data.put("log_equivalent", equivalent+"");
		data.put(o1_short + "_size", o1_ax.size() + "");
		data.put(o2_short + "_size", o2_ax.size() + "");
		data.put(o1_short + "_sig", sigo1.size() + "");
		data.put(o2_short + "_sig", sigo2.size() + "");
		data.put(o1_short + "_subtests", subsumptiontesto1 + "");
		data.put(o2_short + "_subtests", subsumptiontesto2 + "");
		data.put(o1_short + "_subtestpercdiff_" + o2_short, percent_difference
				+ "");
		data.put(o1_short + "_subtestdiff_" + o2_short, diff + "");
		data.put(o2_short + ">" + o1_short, o1_wo_o2.isEmpty() + "");
		data.put(o1_short + ">" + o2_short, o2_wo_o1.isEmpty() + "");
		data.put(o2_short + "_wo_" + o1_short, o2_wo_o1.size() + "");
		data.put(o1_short + "_wo_" + o2_short, o1_wo_o2.size() + "");
		data.put(o1_short + "_sigdiff_" + o2_short, entitieso1.size() + "");
		data.put(o2_short + "_sigdiff_" + o1_short, entitieso2.size() + "");
		data.put(o1_short + "_" + o2_short + "_difference", difference.size()
				+ "");
		data.put(o1_short + "_wo_" + o2_short + "_axtype", axtypeso1_s + "");
		data.put(o2_short + "_wo_" + o1_short + "_axtype", axtypeso2_s + "");
		data.put(o1_short + "_" + o2_short + "_axtype_in_diff",
				axtypes_o1_wo_o2_s + "");
		data.put(o2_short + "_" + o1_short + "_axtype_in_diff",
				axtypes_o2_wo_o1_s + "");

		difference.removeAll(tautologies_o1);
		difference.removeAll(tautologies_o2);
		data.put(o1_short + "_" + o2_short + "_difference_wo_tautologies",
				difference.size() + "");
		data.put(o1_short + "_tauto", tautologies_o1.size() + "");
		data.put(o2_short + "_tauto", tautologies_o2.size() + "");
		return difference;
	}

	private static Collection<? extends OWLAxiom> getSubClassOfThing(
			Set<OWLAxiom> axioms) {
		Set<OWLAxiom> tautologies = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : axioms) {
			if(ax instanceof OWLSubClassOfAxiom) {
				OWLSubClassOfAxiom axs = (OWLSubClassOfAxiom)ax;
				if(axs.getSuperClass().isOWLThing()) {
					tautologies.add(ax);
				}
			}
		}
		return tautologies;
	}

	public static Collection<OWLAxiom> getTautologies(Set<OWLAxiom> axioms)
			throws OWLOntologyCreationException {
		OWLOntologyManager emptyman = OWLManager.createOWLOntologyManager();
		OWLOntology emptyo = emptyman.createOntology();
		OWLReasonerFactory fac = new Reasoner.ReasonerFactory();
		OWLReasoner reasoner = fac.createReasoner(emptyo);
		Set<OWLAxiom> tautologies = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : axioms) {

			try {
				if (reasoner.isEntailed(ax)) {
					// System.out.println("Entailed: " + ax);
					tautologies.add(ax);
					/*
					 * try { System.in.read(); } catch (IOException e) { // TODO
					 * Auto-generated catch block e.printStackTrace(); }
					 */

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return tautologies;
	}

	public static void obtainAxiomTypeSignatureMap(Set<OWLAxiom> difference,
			Set<OWLEntity> sigo1, Set<OWLEntity> sigo2,
			Map<String, Map<String, Integer>> axiomtypeanalysis) {
		for (OWLAxiom ax : difference) {
			String axt = ax.getAxiomType().toString();
			if (!axiomtypeanalysis.containsKey(axt)) {
				axiomtypeanalysis.put(axt, new HashMap<String, Integer>());
				axiomtypeanalysis.get(axt).put("pureold", 0);
				axiomtypeanalysis.get(axt).put("purenew", 0);
				axiomtypeanalysis.get(axt).put("mixed", 0);
			}
			Set<OWLEntity> axsig = ExperimentUtilities.getSignature(ax);
			boolean containsold = false;
			boolean containsnew = false;
			for (OWLEntity e : axsig) {
				if (sigo1.contains(e)) {
					containsold = true;
				}
				if (sigo2.contains(e)) {
					containsnew = true;
				}
			}

			String oldsigcode = "";

			if (containsold && containsnew) {
				oldsigcode = "mixed";
			} else if (containsold && !containsnew) {
				oldsigcode = "pureold";
			} else if (!containsold && containsnew) {
				oldsigcode = "purenew";
			} else {
				System.err.println("Axiom does not contain a signature? "
						+ ax.toString());
				continue;
			}

			axiomtypeanalysis.get(axt).put(oldsigcode,
					axiomtypeanalysis.get(axt).get(oldsigcode) + 1);
		}
	}

	private static Set<OWLEntity> getEntitiesInO1notinO2(Set<OWLEntity> o1_sig,
			Set<OWLEntity> o2_sig, String o1_short, String o2_short) {
		Set<OWLEntity> o1_not_o2 = new HashSet<OWLEntity>(o1_sig);
		o1_not_o2.removeAll(o2_sig);

		Set<OWLEntity> axtypes = new HashSet<OWLEntity>();

		for (OWLEntity e : o1_not_o2) {
			axtypes.add(e);
		}
		// System.out.println("DIFFERENCE IN AXIOMTYPES: " + sb);
		return axtypes;
	}

	private static Set<String> getAxtypesInO1NotInO2(Set<OWLAxiom> o1_ax,
			Set<OWLAxiom> o2_ax) {
		Set<AxiomType<?>> o1_axt = getAxiomtypes(o1_ax);
		Set<AxiomType<?>> o2_axt = getAxiomtypes(o2_ax);
		Set<AxiomType<?>> o1_not_o2 = new HashSet<AxiomType<?>>(o1_axt);
		o1_not_o2.removeAll(o2_axt);

		Set<String> axtypes = new HashSet<String>();
		for (AxiomType<?> axt : o1_not_o2) {
			axtypes.add(axt.getName());
		}
		// System.out.println("DIFFERENCE IN AXIOMTYPES: " + sb);
		return axtypes;
	}

	private static Set<AxiomType<?>> getAxiomtypes(Set<OWLAxiom> axioms) {
		Set<AxiomType<?>> axtypes = new HashSet<AxiomType<?>>();
		for (OWLAxiom ax : axioms) {
			axtypes.add(ax.getAxiomType());
		}
		return axtypes;
	}

	public static boolean equalOntologies(OWLOntology o1,
			OWLOntology o2) {
		
		Set<OWLAxiom> o1_ax = new HashSet<OWLAxiom>();
		Set<OWLAxiom> o2_ax = new HashSet<OWLAxiom>();
		Set<OWLAxiom> tautologies_o1 = new HashSet<OWLAxiom>();
		Set<OWLAxiom> tautologies_o2 = new HashSet<OWLAxiom>();
		
		for (OWLAxiom ax : o1.getLogicalAxioms()) {
			o1_ax.add(ax.getAxiomWithoutAnnotations());
		}

		for (OWLAxiom ax : o2.getLogicalAxioms()) {
			o2_ax.add(ax.getAxiomWithoutAnnotations());
		}
		
		
		try {
			tautologies_o1.addAll(getTautologies(o1_ax));
			tautologies_o2.addAll(getTautologies(o2_ax));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<OWLAxiom> o1_wo_o2 = new HashSet<OWLAxiom>();
		o1_wo_o2.addAll(o1_ax);
		o1_wo_o2.removeAll(o2_ax);
		o1_wo_o2.removeAll(tautologies_o1);

		System.out.println("Axioms in o1 that are not in o2: "+o1_wo_o2.size());
		for (OWLAxiom ax : o1_wo_o2) {
			System.out.println(ax);
		}

		Set<OWLAxiom> o2_wo_o1 = new HashSet<OWLAxiom>();
		o2_wo_o1.addAll(o2_ax);
		o2_wo_o1.removeAll(o1_ax);
		o2_wo_o1.removeAll(tautologies_o2);
		
		System.out.println("Axioms in o2 that are not in o1: "+o2_wo_o1.size());
		for (OWLAxiom ax : o2_wo_o1) {
			System.out.println(ax);
		}
		
		return (o2_wo_o1.isEmpty() & o1_wo_o2.isEmpty());
	}

}
