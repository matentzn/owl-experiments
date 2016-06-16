package owl.cs.man.ac.uk.experiment.repair.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.Version;

import owl.cs.man.ac.uk.experiment.experiment.Experiment;
import owl.cs.man.ac.uk.experiment.file.FileUtilities;
import owl.cs.man.ac.uk.experiment.metrics.StaticMetrics;
import owl.cs.man.ac.uk.experiment.ontology.MetricsLabels;
import owl.cs.man.ac.uk.experiment.ontology.OntologyUtilities;
import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolation;

public class OntologyRepairExperiment extends Experiment {

	private final boolean overwrite;
	private final File outputDir;
	
	/**
	 * @param args
	 */
	public OntologyRepairExperiment(File ontfile, File csvfile, File outputDir, boolean overwrite) {
		super(ontfile, csvfile);
		this.overwrite = overwrite;
		this.outputDir = outputDir;
	}

	public void process() throws RuntimeException,
			OWLOntologyCreationException, OWLOntologyStorageException, IOException {

		String filename_new = getOntologyFile().getName()
				.replaceAll(".owl", "").replaceAll(".xml", "")
				.replaceAll(".rdf", "").replaceAll(" ", "");

		System.out.println("Initialising..");
		
		File out = new File(outputDir, getOntologyFile().getName().replaceAll(" ",
				""));
		
		if (overwrite) {
			if (out.isDirectory()) {
				try {
					FileUtils.deleteDirectory(out);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			if (out.isDirectory()) {
				System.out.println("Directory " + outputDir
						+ " already exists, no overwrite set, aborting");
				return;
			}
		}
		out.mkdir();
		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o = m.loadOntologyFromOntologyDocument(getOntologyFile());
		
		System.out.println("zipping..");
		Set<OWLProfileViolation> s = OntologyUtilities.repair(o);
		StringBuilder sb = new StringBuilder();
		for(OWLProfileViolation pv:s) {
			sb.append(pv.getClass().getSimpleName()+" ");
		}
		addResult(MetricsLabels.FIXED_VIOLATIONS,sb.toString().trim());
		StaticMetrics sm = new StaticMetrics(o);
		addResult(sm.getEssentialMetrics());
	}

	@Override
	public String getExperimentName() {
		return "OWLZipExperiment";
	}

	@Override
	protected Version getExperimentVersion() {
		return new Version(0, 0, 0, 1);
	}

	private File saveOntology(OWLOntology o, String name, File outputDir)
			throws OWLOntologyStorageException, IOException {
		File out = new File(outputDir, name);
		OutputStream os = new FileOutputStream(out);
		o.getOWLOntologyManager().saveOntology(o, os);
		os.close();
		return out;
	}

	public File zipOntology(OWLOntology o,String ontologyname, File targetDir) throws IOException, OWLOntologyStorageException, OWLOntologyCreationException {
		long startsave = System.currentTimeMillis();
		File ontology = saveOntology(o, "root.owl", targetDir);
		long endsave = System.currentTimeMillis();
		addResult("filesize_ont", FileUtils.sizeOf(ontology)+"");
		long startzip = System.currentTimeMillis();
		List<File> filestozip = new ArrayList<File>();
		filestozip.add(ontology);
		File zip = new File(targetDir,ontologyname+".owl.zip");
		FileUtilities.zip(filestozip,zip);
		long endzip = System.currentTimeMillis();
		addResult("time_save", endsave-startsave+"");
		addResult("time_zip", endzip-startzip+"");
		addResult("filesize_zip", FileUtils.sizeOf(zip)+"");
		return zip;
	}
	
	public OWLOntology unzipOntology(File zip) throws IOException, OWLOntologyCreationException {
		File tmp = new File(zip.getParentFile(),"tmp");
		if(tmp.exists()) {
			FileUtils.deleteQuietly(tmp);
		}
		tmp.mkdir();
		long startunzip = System.currentTimeMillis();
		FileUtilities.unzip(zip, tmp);
		long endunzip = System.currentTimeMillis();
		File ontology = new File(tmp,"root.owl");
		long startload = System.currentTimeMillis();
		OWLOntology o = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(ontology);
		long endload = System.currentTimeMillis();
		addResult("time_unzip",endunzip-startunzip+"");
		addResult("time_load",endload-startload+"");
		return o;
	}

}
