package owl.cs.man.ac.uk.experiment.repair.run;

import java.io.File;

import owl.cs.man.ac.uk.experiment.experiment.Experiment;
import owl.cs.man.ac.uk.experiment.experiment.ExperimentRunner;

public class OntologyRepairExperimentRunner extends ExperimentRunner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ExperimentRunner runner = new OntologyRepairExperimentRunner();
		Experiment experiment = runner.configureExperiment(args);
		runner.runExperiment(experiment);
	}

	@Override
	protected Experiment prepare(String[] args) {
		if (args.length != 5) {
			throw new RuntimeException(
					"You need exactly six parameters: path to ontology, path to csv, path to output dir, overwrite flag, timeout");
		}

		String ontology_path = args[0];
		String csv_path = args[1];
		String out_path = args[2];
		boolean overwrite = args[3].equals("o");
		String timeout = args[4];

		int reasoner_timeout = Integer.valueOf(timeout);
		setProcessTimeout(5000 + (reasoner_timeout));

		setCSVFile(new File(csv_path));
		setOntologyFile(new File(ontology_path));
		
		File outputDir = new File(out_path);
		if(!outputDir.isDirectory()) {
			throw new RuntimeException(
					"Output directory does not exist! "+outputDir);
		}

		return new OntologyRepairExperiment(getOntologyFile(), getCSVFile(),outputDir, overwrite);
	}

}
