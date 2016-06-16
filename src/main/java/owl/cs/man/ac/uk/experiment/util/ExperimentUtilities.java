package owl.cs.man.ac.uk.experiment.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.io.FileUtils;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

import owl.cs.man.ac.uk.experiment.ontology.MetricsLabels;
import owl.cs.man.ac.uk.experiment.ontology.OntologyFileNameFilter;
import owl.cs.man.ac.uk.experiment.dataset.OntologySerialiser;
import owl.cs.man.ac.uk.experiment.experiment.Experiment;
import owl.cs.man.ac.uk.experiment.file.FileUtilities;
import owl.cs.man.ac.uk.experiment.metrics.reasoner.ReasonerMetrics.ReasonerMetaData;

public class ExperimentUtilities {

	public static final String MANCHESTEROWLIRI = "http://owl.cs.manchester.ac.uk/";

	private static ArrayList<String> modularreasoners = new ArrayList<String>() {
		{
			add("chainsaw");
			add("katana");
			add("more");
		}
	};

	public static File getReferenceOntologyInDir(File owlfile) {
		List<File> files = getFileRecursive(owlfile);

		if (files.size() == 1) {
			return files.get(0);
		} else if (files.size() > 1) {
			OWLOntologyManager man = OWLManager.createOWLOntologyManager();

			for (File f : files) {
				OWLOntologyIRIMapper autoIRIMapper = new AutoIRIMapper(
						f.getParentFile(), false);
				man.addIRIMapper(autoIRIMapper);
				try {
					man.loadOntologyFromOntologyDocument(f);
				} catch (OWLOntologyCreationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			OWLOntologyManager manmerge = OWLManager.createOWLOntologyManager();
			try {
				OWLOntology o = manmerge.createOntology();
				OntologySerialiser.mergeOntologies(man.getOntologies(),
						owlfile.getName(), manmerge, o);
				File out = new File(owlfile, owlfile.getName() + "merged");
				OntologySerialiser.saveOWLXML(out.getParentFile(), o,
						out.getName(), manmerge);
				return out;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;
	}

	private static List<File> getFileRecursive(File filesin) {
		List<File> files = new ArrayList<File>();
		for (File f : filesin.listFiles(new OntologyFileNameFilter())) {
			if (f.getName().startsWith(".")) {
				continue;
			}
			if (f.isDirectory()) {
				files.addAll(getFileRecursive(f));
			}
			files.add(f);
		}
		return files;
	}

	public static double speedAccordingToAmdahlsLaw(int threads,
			double speedupablefraction) {
		if (threads < 1) {
			throw new IllegalArgumentException(
					"thread has to be a positive number greater 0");
		}
		if (speedupablefraction >= 0 && speedupablefraction <= 1) {
			double speedup = 1 / ((1 - speedupablefraction) + (speedupablefraction / threads));
			return speedup;
		} else {
			throw new IllegalArgumentException(
					"serial fraction does not have the correct range (0-1)");
		}
	}

	public static double speedAccordingToGustafsonsLaw(int threads,
			double serialfraction) {
		if (threads < 1) {
			throw new IllegalArgumentException(
					"thread has to be a positive number greater 0");
		}
		if (serialfraction >= 0 && serialfraction <= 1) {
			double speedup = threads - serialfraction * (threads - 1);
			return speedup;
		} else {
			throw new IllegalArgumentException(
					"serial fraction does not have the correct range (0-1)");
		}
	}

	public static Map<String, String> getDefaultFailureData(Exception e) {
		Map<String, String> csvdata = new HashMap<String, String>();
		String stacktrace = e.getMessage();
		if (stacktrace == null) {
			stacktrace = "no message";
		}
		stacktrace = stacktrace.replaceAll("[^a-zA-Z0-9 \\-\\.\\:\\/\\(]", "");
		if (stacktrace.length() > 801) {
			stacktrace = stacktrace.substring(0, 800) + " [...RESTCUTOFF]";
		}
		csvdata.put(MetricsLabels.JAVA_EXCEPTION_MESSAGE, stacktrace);
		csvdata.put(MetricsLabels.JAVA_EXCEPTION, "" + e.getClass());
		if (e.getCause() != null) {
			csvdata.put(MetricsLabels.JAVA_EXCEPTION_CAUSE, ""
					+ e.getCause().getClass());
		}
		return csvdata;
	}

	public static Map<String, String> getDefaultExperimentData(
			String experimentname, String filename) {
		Map<String, String> data = new HashMap<String, String>();
		data.put(MetricsLabels.EXPERIMENT_ID, experimentname);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		data.put(MetricsLabels.EXPERIMENT_DATE, dateFormat.format(date));
		data.put(MetricsLabels.FILENAME, filename);
		return data;
	}
	
	public static Map<String, String> getDefaultReasonerMetadata(OWLReasoner r) {
		Map<String, String> data = new HashMap<String, String>();
		data.put(MetricsLabels.REASONERNAME_CLASS, r.getClass().getSimpleName());
		data.put(MetricsLabels.REASONERNAME,r.getReasonerName());
		data.put(MetricsLabels.REASONERVERSION,ReasonerUtilities.getReasonerVersion(r));
		return data;
	}

	public static Map<String, String> getDefaultExperimentData(
			String experimentname, File file) {
		Map<String, String> data = new HashMap<String, String>();
		data.putAll(getDefaultExperimentData(experimentname, file.getName()));
		data.putAll(getFileMetrics(file));
		return data;
	}

	public static int getTaxonomyAxiomCount(Collection<OWLAxiom> collection) {
		return getTaxonomyAxioms(collection).size();
	}

	public static Set<OWLAxiom> getTaxonomyAxioms(
			Collection<OWLAxiom> collection) {
		Set<OWLAxiom> taxonomyAxioms = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : collection) {
			if (ax instanceof OWLSubClassOfAxiom) {
				taxonomyAxioms.add(ax);
			} else if (ax instanceof OWLEquivalentClassesAxiom) {
				taxonomyAxioms.add(ax);
			}
		}
		return taxonomyAxioms;
	}

	public static void normaliseOWLNothingAxioms(OWLOntologyManager manager,
			Set<OWLAxiom> axioms) {
		OWLDataFactory df = manager.getOWLDataFactory();
		Set<OWLAxiom> remove = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : axioms) {
			if (ax instanceof OWLEquivalentClassesAxiom) {
				// nested class expressions (a sc b and exists r.c => a, b,
				// exists r.c)
				if (ax.getNestedClassExpressions().contains(df.getOWLNothing())) {
					remove.add(ax);
				}
			}
		}
		axioms.removeAll(remove);
	}

	public static void stripSubClassOfOWLThingAxioms(Set<OWLAxiom> axioms) {
		Set<OWLAxiom> remove = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : axioms) {
			if (ax instanceof OWLSubClassOfAxiom) {
				// nested class expressions (a sc b and exists r.c => a, b,
				// exists r.c)
				if (((OWLSubClassOfAxiom) ax).getSuperClass().isOWLThing()) {
					remove.add(ax);
				}
			}
		}
		axioms.removeAll(remove);
	}

	public static List<InferredAxiomGenerator<? extends OWLAxiom>> getClassificationAxiomGenerators() {
		List<InferredAxiomGenerator<? extends OWLAxiom>> generatorlist = new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>();
		InferredSubClassAxiomGenerator generator = new InferredSubClassAxiomGenerator();
		InferredEquivalentClassAxiomGenerator generatorE = new InferredEquivalentClassAxiomGenerator();
		generatorlist.add(generatorE);
		generatorlist.add(generator);
		return generatorlist;
	}

	public static void exportTaxonomy(Collection<OWLAxiom> collection,
			OWLOntologyManager exportManager, File file)
			throws OWLOntologyCreationException, FileNotFoundException,
			OWLOntologyStorageException {
		Set<OWLAxiom> taxonomyAxioms = ExperimentUtilities
				.getTaxonomyAxioms(collection);
		normaliseOWLNothingAxioms(exportManager, taxonomyAxioms);
		stripSubClassOfOWLThingAxioms(taxonomyAxioms);
		Set<OWLAxiom> axiomsWOAnnotations = stripAnnotationsFromAxioms(taxonomyAxioms);
		OWLOntology export = exportManager.createOntology();
		exportManager.addAxioms(export, axiomsWOAnnotations);
		OntologySerialiser.saveOWLXML(file.getParentFile(), export,
				file.getName(), exportManager);
	}

	public static void exportOntology(OWLOntology module,
			OWLOntologyManager manager, File file)
			throws OWLOntologyCreationException, FileNotFoundException,
			OWLOntologyStorageException {
		Set<OWLAxiom> axiomsWOAnnotations = stripAnnotationsFromAxioms(module
				.getAxioms());
		OWLOntology export = manager.createOntology();
		manager.addAxioms(export, axiomsWOAnnotations);
		OntologySerialiser.saveOWLXML(file.getParentFile(), export,
				file.getName(), manager);
	}

	private static Set<OWLAxiom> stripAnnotationsFromAxioms(Set<OWLAxiom> axioms) {
		Set<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : axioms) {
			newAxioms.add(ax.getAxiomWithoutAnnotations());
		}
		return newAxioms;
	}

	public static Set<OWLAxiom> stripAnnotations(Set<OWLAxiom> axioms) {
		Set<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : axioms) {
			if (ax instanceof OWLAnnotationAssertionAxiom) {
				continue;
			}
			newAxioms.add(ax.getAxiomWithoutAnnotations());
		}
		return newAxioms;
	}

	public static String getAxiomsTSV(Collection<OWLAxiom> axioms) {
		StringBuilder tsv = new StringBuilder();
		for (OWLAxiom axiom : axioms) {
			tsv.append(axiom + " ");
		}
		return tsv.toString();
	}

	public static String getTSV(Collection<OWLEntity> entities) {
		StringBuilder tsv = new StringBuilder();
		if (entities != null) {
			for (OWLEntity e : entities) {
				tsv.append(e + " ");
			}
		}
		return tsv.toString();
	}

	public static double calculateAverage(Collection<Integer> list) {
		Integer sum = 0;
		for (Integer i : list) {
			sum += i;
		}
		BigDecimal bd = new BigDecimal(sum.doubleValue() / list.size());
		return bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static Map<String, List<Map<String, String>>> binByProfile(
			Map<String, Map<String, String>> allFilesByName) {
		Map<String, List<Map<String, String>>> profilebins = new HashMap<String, List<Map<String, String>>>();
		List<Map<String, String>> dl = new ArrayList<Map<String, String>>();
		List<Map<String, String>> el = new ArrayList<Map<String, String>>();
		List<Map<String, String>> ql = new ArrayList<Map<String, String>>();
		List<Map<String, String>> rl = new ArrayList<Map<String, String>>();
		binProfiles(allFilesByName.values(), dl, el, ql, rl);
		profilebins.put("dl", dl);
		profilebins.put("el", el);
		profilebins.put("ql", ql);
		profilebins.put("rl", rl);
		return profilebins;
	}

	private static void binProfiles(Collection<Map<String, String>> collection,
			List<Map<String, String>> dl, List<Map<String, String>> el,
			List<Map<String, String>> ql, List<Map<String, String>> rl) {
		for (Map<String, String> record : collection) {
			boolean puredl = true;
			if (record.get("owl2_el").equals("1")) {
				el.add(record);
				puredl = false;
			}
			if (record.get("owl2_rl").equals("1")) {
				rl.add(record);
				puredl = false;
			}
			if (record.get("owl2_ql").equals("1")) {
				ql.add(record);
				puredl = false;
			}
			if (record.get("owl2_dl").equals("1")) {
				if (puredl) {
					dl.add(record);
				}
			}
		}
	}

	public static Set<OWLEntity> getSignature(OWLOntology sub, boolean imports) {
		Set<OWLEntity> seedSig = new HashSet<OWLEntity>();
		seedSig.addAll(sub.getClassesInSignature(imports));
		seedSig.addAll(sub.getObjectPropertiesInSignature(imports));
		seedSig.addAll(sub.getDataPropertiesInSignature(imports));
		seedSig.addAll(sub.getIndividualsInSignature(imports));
		return seedSig;
	}

	public static Set<OWLEntity> getSignature(OWLAxiom ax) {
		Set<OWLEntity> seedSig = new HashSet<OWLEntity>();
		seedSig.addAll(ax.getClassesInSignature());
		seedSig.addAll(ax.getObjectPropertiesInSignature());
		seedSig.addAll(ax.getDataPropertiesInSignature());
		seedSig.addAll(ax.getIndividualsInSignature());
		return seedSig;
	}

	public static boolean isIntegerParsable(String id) {
		try {
			Integer.parseInt(id);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isLongParsable(String id) {
		try {
			Long.parseLong(id);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isDoubleParsable(String id) {
		try {
			Double d = Double.parseDouble(id);
			if (d.isNaN()) {
				return false;
			}
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isNumeric(String id) {
		return (isIntegerParsable(id) || isLongParsable(id) || isDoubleParsable(id));
	}

	public static Set<OWLAxiom> getLogicalAxioms(OWLOntology o,
			boolean includeImportsClosure, boolean skiprules) {
		Set<AxiomType<?>> types = new HashSet<AxiomType<?>>();
		types.addAll(AxiomType.TBoxAxiomTypes);
		types.addAll(AxiomType.RBoxAxiomTypes);
		types.addAll(AxiomType.ABoxAxiomTypes);
		return getLogicalAxioms(o, includeImportsClosure, skiprules, types);
	}

	public static Set<OWLAxiom> getLogicalAxioms(OWLOntology o,
			boolean includeImportsClosure, boolean skiprules,
			Set<AxiomType<?>> types) {
		return getLogicalAxioms(o, includeImportsClosure, skiprules, false,
				types);
	}

	public static Set<OWLAxiom> getLogicalAxioms(OWLOntology o,
			boolean includeImportsClosure, boolean skiprules,
			boolean stripaxiomanno, Set<AxiomType<?>> types) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : o.getLogicalAxioms()) {
			if (types.contains(ax.getAxiomType())) {
				axioms.add(ax);
			}
		}
		if (includeImportsClosure) {
			for (OWLOntology imp : o.getImports()) {
				for (OWLAxiom ax : imp.getLogicalAxioms()) {
					if (types.contains(ax.getAxiomType())) {
						axioms.add(ax);
					}
				}
			}
		}
		if (skiprules) {
			stripRules(axioms);
		}
		if (stripaxiomanno) {
			axioms = stripAnnotations(axioms);
		}
		return axioms;
	}
	
	public static Set<OWLAxiom> getLogicalAxioms(OWLOntology o,
			boolean includeImportsClosure, boolean skiprules,
			boolean stripaxiomanno) {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		for (OWLAxiom ax : o.getLogicalAxioms()) {
			axioms.add(ax);
			
		}
		if (includeImportsClosure) {
			for (OWLOntology imp : o.getImports()) {
				for (OWLAxiom ax : imp.getLogicalAxioms()) {
					axioms.add(ax);					
				}
			}
		}
		if (skiprules) {
			stripRules(axioms);
		}
		if (stripaxiomanno) {
			axioms = stripAnnotations(axioms);
		}
		return axioms;
	}

	public static void stripRules(Set<OWLAxiom> axioms) {
		Set<OWLAxiom> tmp = new HashSet<OWLAxiom>(axioms);
		axioms.clear();
		for (OWLAxiom ax : tmp) {
			if (!ax.getAxiomType().toString().equals("Rule")) {
				axioms.add(ax);
			}
		}
	}

	public static void stripAxiomsWithNonOWL2Datatypes(Set<OWLAxiom> axioms) {
		Set<OWLAxiom> tmp = new HashSet<OWLAxiom>(axioms);
		axioms.clear();
		for (OWLAxiom ax : tmp) {
			boolean containsnonowldt = false;
			for (OWLDatatype dt : ax.getDatatypesInSignature()) {
				if (!dt.isBuiltIn()) {
					containsnonowldt = true;
					break;
				}
			}
			if (!containsnonowldt) {
				axioms.add(ax);
			}
		}
	}

	public static Map<? extends String, ? extends String> getDefaultExperimentData(
			Experiment experiment) {
		Map<String, String> data = getDefaultExperimentData(
				experiment.getExperimentName(), experiment.getOntologyFile());
		data.put(MetricsLabels.EXPERIMENT_VERSION,
				experiment.getExperimentVersionFormatted());
		data.put(MetricsLabels.RUNID, experiment.getRunid());
		return data;
	}

	public static File getResourcePath(Object o) {
		return new File(o.getClass().getProtectionDomain().getCodeSource()
				.getLocation().getPath());
	}

	public static Map<? extends String, ? extends String> getFileMetrics(
			File file) {
		Map<String, String> data = new HashMap<String, String>();
		data.put(MetricsLabels.FILEPATH, file.getParent());
		data.put(MetricsLabels.FILESIZE, FileUtils.sizeOf(file) + "");
		return data;
	}

	public static boolean isNaturalNumber(String value) {
		try {
			Double d = Double.valueOf(value);
			long l = d.longValue();
			return (l >= 0 && (double) l == d);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static long getLong(Map<String, String> rec, String metric)
			throws NumberFormatException {
		if (rec == null) {
			throw new NumberFormatException("Record is null.");
		}
		if (!rec.containsKey(metric)) {
			// System.out.println(rec);
			throw new NumberFormatException("Record does not contain metric "
					+ metric);
		}
		String sl = rec.get(metric);
		return getLong(sl);

		/*
		 * try { long l = return l; } catch(Exception e) { e.printStackTrace();
		 * System.out.println(metric); System.out.println(sl);
		 * System.out.println(rec); System.exit(0); } return 0;
		 */
	}

	public static long getLong(String sl) {
		Double d = Double.valueOf(sl);
		long s = d.longValue();
		if (d != (double) s) {
			throw new NumberFormatException(sl + " not valid long!");
		}
		return s;
	}

	public static long getLong(String sl, boolean checknotnull) {
		long s = Long.parseLong(sl);
		if (checknotnull) {
			if (s < 1) {
				throw new NumberFormatException(
						"Measurement smaller than 1 nanosecond.");
			}
		}
		return s;
	}

	public static double getSeconds(Long l) {
		return round((l.doubleValue() / (double) 1000000000), 8);
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static String getSpaceSeparatedStringFromCollection(
			Collection<?> collection) {
		StringBuilder sb = new StringBuilder();
		for (Object o : collection) {
			sb.append(o.toString() + " ");
		}
		return sb.toString().trim();
	}

	public static int getInteger(String string) {
		return Integer.parseInt(string);
	}

	public static int getNaturalNumberValue(String metrics_rec) {
		try {
			int i = Integer.parseInt(metrics_rec);
			return i;
		} catch (Exception e) {
			return -1;
		}
	}

	public static int getMax(Collection<? extends Integer> collection) {
		return Collections.max(collection);
	}

	public static String getJARName(Class MyClass) {
		return new File(MyClass.getProtectionDomain().getCodeSource()
				.getLocation().getPath()).getName();
	}

	public static boolean ignoreFile(File ignorelist, String name) {
		try {
			Set<String> files = new HashSet<String>(
					FileUtils.readLines(ignorelist));
			if (files.contains(name)) {
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}

	public static long getLong(Map<String, String> rec, ReasonerMetaData rmd) {
		return getLong(rec, rmd.getName());
	}

	public static Object pickRandom(List<? extends Object> lin) {
		List<Object> l = new ArrayList<Object>(lin);
		Collections.shuffle(lin);
		return l.get(0);
	}

	public static boolean nameInvolvesModularReasoner(String reasonername) {
		String r = reasonername.toLowerCase();
		for (String mr : modularreasoners) {
			if (r.contains(mr)) {
				return true;
			}
		}
		return false;
	}

	public static OWLOntology loadOntologyFromFile(File ontfile,
			OWLOntologyManager manager, boolean autoirimapper)
			throws OWLOntologyCreationException, IOException {
		File owlfile = FileUtilities.isZipFile(ontfile) ? processCompressed(ontfile)
				: ontfile;
		if (autoirimapper) {
			OWLOntologyIRIMapper map = new AutoIRIMapper(
					owlfile.getParentFile(), true);
			manager.addIRIMapper(map);
		}
		return manager.loadOntologyFromOntologyDocument(owlfile);
	}

	private static File processCompressed(File ontfile) throws IOException {
		File tmp = Files.createTempDirectory("tempfiles").toFile();
		File owlfile = FileUtilities.unzipOwlFile(ontfile, ontfile.getName(),
				tmp);
		if (owlfile.isDirectory()) {
			owlfile = ExperimentUtilities.getReferenceOntologyInDir(owlfile);
		}

		return owlfile;
	}
	
	public static Set<AxiomType<?>> getTBoxAxiomTypes(boolean including_rbox) {
		Set<AxiomType<?>> axty = new HashSet<AxiomType<?>>();
		if(including_rbox) {
		axty.addAll(AxiomType.RBoxAxiomTypes);
		}
		axty.addAll(AxiomType.TBoxAxiomTypes);
		return axty;
	}
	
	public static Set<AxiomType<?>> getABoxAxiomTypes() {
		Set<AxiomType<?>> axty = new HashSet<AxiomType<?>>();
		axty.addAll(AxiomType.ABoxAxiomTypes);
		return axty;
	}

	public static Set<OWLAxiom> getTBoxAxioms(Set<OWLAxiom> axioms) {
		Set<OWLAxiom> tbox = new HashSet<OWLAxiom>();
		Set<AxiomType<?>> types = getTBoxAxiomTypes(true);
		for(OWLAxiom ax:axioms) {
			if(types.contains(ax.getAxiomType())) {
				tbox.add(ax);
			}
		}
		return tbox;
	}
	
	public static Set<OWLAxiom> getABoxAxioms(Set<OWLAxiom> axioms) {
		Set<OWLAxiom> abox = new HashSet<OWLAxiom>();
		Set<AxiomType<?>> types = getABoxAxiomTypes();
		for(OWLAxiom ax:axioms) {
			if(types.contains(ax.getAxiomType())) {
				abox.add(ax);
			}
		}
		return abox;
	}

	public static String getMax(Set<String> keySet) {
		List<String> l = new ArrayList<String>(keySet);
		Collections.sort(l);
		return l.get(l.size()-1);
	}

	public static Double getMax(List<Double> all) {
		return Collections.max(all);
	}

	public static Set<String> getRandomSubset(Set<String> cl, int size) {
		List<String> l = new ArrayList<String>(cl);
		if(l.size()<=size) {
			return new HashSet<String>(l);
		}
		else {
			Collections.shuffle(l);
			return new HashSet<String>(l.subList(0, size));
		}
	}

	public static OWLAxiom getInconsistencyEntailment() {
		OWLDataFactory df = OWLManager.getOWLDataFactory();
		return df.getOWLSubClassOfAxiom(df.getOWLThing(), df.getOWLNothing());
	}
	
}
