package owl.cs.man.ac.uk.experiment.experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import owl.cs.man.ac.uk.experiment.classification.OntologyClassification;
import owl.cs.man.ac.uk.experiment.dataset.OntologySerialiser;
import owl.cs.man.ac.uk.experiment.ontology.MetricsLabels;
import owl.cs.man.ac.uk.experiment.util.ExperimentUtilities;
import owl.cs.man.ac.uk.experiment.util.ReasonerUtilities;

public abstract class ReasonerExperiment extends Experiment {

	private File inferred_hierarchy_dir;
	private final String reasonername_parameter;
	private String reasonername;
	private final OWLReasonerFactory rf;
	private final OWLReasonerConfiguration reasonerConfig;

	public ReasonerExperiment(File ontfile, File csvfile,
			File inferred_hierachy, String reasonername, int reasoner_timeout) {
		super(ontfile, csvfile);
		this.reasonername_parameter = reasonername;
		this.reasonername = reasonername;
		if (inferred_hierachy != null) {
			this.inferred_hierarchy_dir = inferred_hierachy;
		}
		rf = ReasonerUtilities.getFactory(reasonername);
		reasonerConfig = ReasonerUtilities.getReasonerConfig(reasonername_parameter,reasoner_timeout);
		
		addResult(MetricsLabels.REASONERNAME, rf.getReasonerName());
		addResult(MetricsLabels.REASONERNAME_CLASS, rf.getClass().getSimpleName());
		addResult(MetricsLabels.REASONER_JAR, ExperimentUtilities.getJARName(rf.getClass()));
		addResult(MetricsLabels.PARAM_REASONER, reasonername_parameter);
		addResult(MetricsLabels.INF_HIER_DIR, inferred_hierarchy_dir + "");
		addResult(MetricsLabels.REASONER_TIMEOUT, reasoner_timeout + "");
	}

	protected OWLReasoner createReasoner(OWLOntology module) {
		if(reasonername.equals("konclude")) {
			
			try {
				URL url = new URL("http://localhost:8080");
				System.err.println("Konclude not implemented at the moment: "+url);
				//TODO
				/*OWLlinkReasonerConfiguration reasonerConfiguration =
						new OWLlinkReasonerConfiguration(url);*/						
				 	return rf.createNonBufferingReasoner(module, reasonerConfig);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return rf.createReasoner(module, reasonerConfig);
	}

	protected OWLReasonerFactory getReasonerFactory() {
		return rf;
	}

	protected String getReasonerName() {
		return reasonername;
	}

	protected String getReasonerParameter() {
		return reasonername_parameter;
	}

	protected OWLReasonerConfiguration getReasonerConfig() {
		return reasonerConfig;
	}

	protected void updateReasonerName(OWLReasoner reasoner) {
		if (reasoner.getReasonerName() != null) {
			reasonername = ReasonerUtilities.getReasonerFullname(reasoner);
			addResult(MetricsLabels.REASONERNAME, reasonername);
			addResult(MetricsLabels.REASONERNAME_CLASS, rf.getClass().getSimpleName());
		}
	}

	protected File getInferredHierachyDir() {
		return inferred_hierarchy_dir;
	}

	protected void exportInferredHierarchy(OWLOntologyManager manager,
			OWLReasoner r, OWLOntology o) {
		try {
			exportInferredHierarchy(manager,"inf_"+reasonername_parameter+"_",r,o);
		} catch (OWLOntologyStorageException e) {
			addResult(MetricsLabels.EXPORT_EXCEPTION,
					"OWLOntologyStorageException");
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			addResult(MetricsLabels.EXPORT_EXCEPTION,
					"OWLOntologyCreationException");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			addResult(MetricsLabels.EXPORT_EXCEPTION, "FileNotFoundException");
			e.printStackTrace();
		}

	}

	protected void exportInferredHierarchy(OWLOntologyManager manager, String prefix, OWLReasoner r, OWLOntology o)
			throws OWLOntologyCreationException, FileNotFoundException,
			OWLOntologyStorageException {
		if (isExportInferredHierarchy()) {
			File infhier = new File(getInferredHierachyDir(), prefix
					+ getOntologyFile().getName());
			OWLOntology inf = OntologyClassification.getInferredHierarchy(manager, r, o);
			OntologySerialiser.saveOWLXML(infhier.getParentFile(), inf,
					infhier.getName(), manager);
		}
	}

	protected boolean isExportInferredHierarchy() {
		if (inferred_hierarchy_dir == null) {
			return false;
		} else if (!inferred_hierarchy_dir.isDirectory()) {
			return false;
		} else {
			return true;
		}
	}

}
