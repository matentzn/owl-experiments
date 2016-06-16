package owl.cs.man.ac.uk.experiment.dataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import owl.cs.man.ac.uk.experiment.csv.CSVUtilities;
import owl.cs.man.ac.uk.experiment.file.OntologyFileExtensions;
import owl.cs.man.ac.uk.experiment.metrics.StaticMetrics;
import owl.cs.man.ac.uk.experiment.util.ExperimentUtilities;

public class OntologySerialiser {

	/**
	 * @param args
	 * @throws OWLOntologyStorageException
	 * @throws FileNotFoundException
	 */

	private static Map<String, String> data = new HashMap<String, String>();
	private static int logicalaxcount_original = 0;
	public final static IRI SOURCE_ONTOLOGY_ANNOTATIONPROP = IRI
			.create("http://owl.cs.manchester.ac.uk/ontology#sourceOntology");

	public static void main(String[] args) throws FileNotFoundException,
			OWLOntologyStorageException {
		if (args.length != 5) {
			throw new IllegalArgumentException(
					"You need exactly five parameters (ontologyfile, target directory, targetformat (owlxml,rdfxml,functional),merged (mn,my),overwriteflag");
		}

		String ontology_filename = args[0];
		String targetdirpath = args[1];
		String targetformat = args[2];
		String merged = args[3];
		boolean overwrite = args[4].trim().equals("true");

		File targetdir = new File(targetdirpath);
		File exportdir = new File(targetdir, "exported");
		File csv = new File(targetdir, "export_metadata.csv");
		File csvfailed = new File(targetdir, "export_metadata_failed.csv");
		File unsuccessfulldir = new File(targetdir, "unsuccessful");

		targetdir.mkdir();
		exportdir.mkdir();
		unsuccessfulldir.mkdir();

		File ontology = new File(ontology_filename);

		File exported_file = new File(exportdir, createOntologyName(
				ontology.getName(), targetformat));

		data.putAll(ExperimentUtilities.getDefaultExperimentData(
				"OntologySerialiser", ontology));

		boolean continueprocess = true;

		if (!overwrite) {
			System.out.println(exported_file);

			if (exported_file.exists()) {
				System.out
						.println("Already a file with that name in target directory!");
				continueprocess = false;
			}
		}

		if (continueprocess) {
			try {
				File exported_ontology = serialiseOntology(targetformat,
						merged, ontology, exportdir);

				OWLOntologyManager man = OWLManager.createOWLOntologyManager();
				OWLOntology o_exported = man
						.loadOntologyFromOntologyDocument(exported_ontology);

				StaticMetrics sm = new StaticMetrics(o_exported);
				int logaxcount_exported = sm.getLogicalAxiomCount(true);
				data.putAll(sm.getEssentialMetrics());
				data.putAll(ExperimentUtilities.getDefaultExperimentData(
						"OntologySerialiser", exported_ontology));
				data.put("original_filename", ontology.getName());
				data.put(
						"same_axcount",
						""
								+ ((logaxcount_exported - logicalaxcount_original) == 0));
				CSVUtilities.writeCSVData(csv, data, true);

			} catch (Exception e) {
				e.printStackTrace();
				data.putAll(ExperimentUtilities.getDefaultFailureData(e));
				CSVUtilities.writeCSVData(csvfailed, data, true);
				try {
					FileUtils.copyFileToDirectory(ontology, unsuccessfulldir);
					if (exported_file.exists()) {
						FileUtils.deleteQuietly(exported_file);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public static File serialiseOntology(String targetformat, String merged,
			File ontology, File targetdir) throws FileNotFoundException,
			OWLOntologyStorageException, OWLOntologyCreationException {

		if (!ontology.exists()) {
			throw new IllegalArgumentException("ontology " + ontology
					+ " does not exist!");
		}
		if (!targetdir.exists()) {
			throw new IllegalArgumentException("target dir " + targetdir
					+ "does not exist");
		}

		System.out.println("Exporting " + ontology.getName() + " to "
				+ targetformat + "...");

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		OWLOntology o = manager.loadOntologyFromOntologyDocument(ontology);
		StaticMetrics sm = new StaticMetrics(o);
		logicalaxcount_original = sm.getLogicalAxiomCount(true);
		/*
		 * System.out.println(o.getSignature(false).size());
		 * System.out.println(o.getSignature(true).size()); Set<OWLEntity> cl =
		 * o.getSignature(true);
		 */
		if (merged.equals("my")) {
			OWLOntology mergedOntology = OntologySerialiser.mergeImports(o,
					"merged_" + ontology.getName());
			OWLOntology export = manager.createOntology(ExperimentUtilities
					.stripAnnotations(mergedOntology.getAxioms()));
			/*
			 * System.out.println(mergedOntology.getSignature(false).size());
			 * System.out.println(mergedOntology.getSignature(true).size());
			 * Set<OWLEntity> cl2 = mergedOntology.getSignature(true);
			 * cl.removeAll(cl2); System.out.println(cl);
			 */
			return exportOntology(targetformat, ontology, targetdir, manager,
					export,"");
		} else {
			OWLOntology export = manager.createOntology(ExperimentUtilities
					.stripAnnotations(o.getAxioms()));
			return exportOntology(targetformat, ontology, targetdir, manager,
					export,"");
		}
	}

	public static File exportOntology(String targetformat, File ontology,
			File targetdir, OWLOntologyManager manager,
			OWLOntology mergedOntology, String ontologyname) throws OWLOntologyCreationException,
			FileNotFoundException, OWLOntologyStorageException {
		
		String filename = ontologyname;
		
		if(ontologyname.isEmpty()) {
			filename = createOntologyName(ontology.getName(), targetformat);
		}

		if (targetformat.equals("owlxml")) {
			return saveOWLXML(targetdir, mergedOntology, filename, manager);
		} else if (targetformat.equals("rdfxml")) {
			return saveRDFXML(targetdir, mergedOntology, filename, manager);
		} else if (targetformat.equals("functional")) {
			return saveFunctional(targetdir, mergedOntology, filename, manager);
		} else {
			throw new IllegalArgumentException("format " + targetformat
					+ " not supported!");
		}
	}

	private static String createOntologyName(String name, String targetformat) {
		return name + "_" + targetformat
				+ OntologyFileExtensions.get(targetformat);
	}

	public static File saveFunctional(File targetDir, OWLOntology ontology,
			String name, OWLOntologyManager manager)
			throws OWLOntologyCreationException, FileNotFoundException,
			OWLOntologyStorageException {
		OWLOntologyFormat format = new OWLFunctionalSyntaxOntologyFormat();
		return save(targetDir, ontology, manager, format, name); // +OntologyFileExtensions.FUNCTIONAL
	}

	public static File saveRDFXML(File targetDir, OWLOntology ontology,
			String name, OWLOntologyManager manager)
			throws OWLOntologyCreationException, FileNotFoundException,
			OWLOntologyStorageException {
		OWLOntologyFormat format = new RDFXMLOntologyFormat();
		return save(targetDir, ontology, manager, format, name); // +OntologyFileExtensions.RDFXML
	}

	public static File saveOWLXML(File targetDir, OWLOntology ontology,
			String name, OWLOntologyManager manager)
			throws OWLOntologyCreationException, FileNotFoundException,
			OWLOntologyStorageException {
		OWLOntologyFormat format = new OWLXMLOntologyFormat();
		return save(targetDir, ontology, manager, format, name); // +OntologyFileExtensions.OWLXML
	}

	private static File save(File targetDir, OWLOntology ontology,
			OWLOntologyManager manager, OWLOntologyFormat format, String name)
			throws OWLOntologyCreationException, FileNotFoundException,
			OWLOntologyStorageException {
		if (!targetDir.exists()) {
			System.out
					.println("Target directory for serialisation does not exist, replacing...");
			targetDir.mkdirs();
		}
		File file = new File(targetDir, name);
		OutputStream os = new FileOutputStream(file);
		manager.saveOntology(ontology, format, os);
		return file;
	}

	public static void mergeOntologies(Set<OWLOntology> ontologies, String id,
			OWLOntologyManager mergedManager, OWLOntology mergedOntology) {
		OWLDataFactory df = mergedManager.getOWLDataFactory();
		OWLAnnotationProperty sourceOntologyProperty = df
				.getOWLAnnotationProperty(SOURCE_ONTOLOGY_ANNOTATIONPROP);
		int ct = 0;
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		
		for (OWLOntology ont : ontologies) {
			for(OWLAxiom ax:ont.getLogicalAxioms()) {
				axioms.add(ax.getAxiomWithoutAnnotations());
			}
			/*IRI ontIRI = ont.getOntologyID().getOntologyIRI();
			if (ontIRI == null) {
				ontIRI = IRI.create(id + "-import" + ct);
				ct++;
			}
			OWLAnnotation annotation = df
					.getOWLAnnotation(sourceOntologyProperty,
							df.getOWLLiteral(ontIRI.toString()));
			for (OWLAxiom ax : ont.getLogicalAxioms()) {
				OWLAxiom annotatedAxiom = ax.getAnnotatedAxiom(Collections
						.singleton(annotation));
				mergedManager.addAxiom(mergedOntology, annotatedAxiom);
			}*/
		}
		mergedManager.addAxioms(mergedOntology, axioms);
	}

	public static OWLOntology mergeImports(OWLOntology ontology, String id)
			throws OWLOntologyCreationException {
		OWLOntologyManager mergedManager = OWLManager
				.createOWLOntologyManager();
		OWLOntology mergedOntology = mergedManager.createOntology(ontology
				.getOntologyID());	
		mergeOntologies(ontology.getImportsClosure(), id, mergedManager,
				mergedOntology);
		return mergedOntology;
	}

}
