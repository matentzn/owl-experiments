package owl.cs.man.ac.uk.experiment.experiment;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import owl.cs.man.ac.uk.experiment.csv.CSVUtilities;
import owl.cs.man.ac.uk.experiment.util.ExperimentUtilities;
import owl.cs.man.ac.uk.experiment.util.TerribleTimeoutThread;

public abstract class ExperimentRunner {

	File ontologyfile;
	File csvfile;
	long processtimeout = 21600000;

	/**
	 * @param args
	 */

	public Experiment configureExperiment(String[] args) {
		Experiment experiment = prepare(args);
		verifyOntologyFile();
		verifyCSVFile();
		return experiment;
	}

	private void verifyOntologyFile() {
		if (ontologyfile == null) {
			throw new IllegalArgumentException("The ontology file is not set!");
		} else if (!ontologyfile.exists()) {
			throw new IllegalArgumentException(ontologyfile
					+ " is not a valid file!");
		}
	}

	private void verifyCSVFile() {
		if (csvfile == null) {
			throw new IllegalArgumentException("The CSV file is not set!");
		} else if (!csvfile.getParentFile().isDirectory()) {
			throw new IllegalArgumentException(
					"The CSV file is not put in a valid directory!");
		}
	}

	protected abstract Experiment prepare(String[] args);

	/**
	 * @param args
	 */
	public void runExperiment(Experiment experiment) {

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(experiment);

		try {
			Thread thread = new TerribleTimeoutThread(
					(long) (getProcessTimeout() * 1.2));
			thread.start();
			System.out.println(future.get(getProcessTimeout(),
					TimeUnit.MILLISECONDS));
			thread.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, String> failureData = ExperimentUtilities
					.getDefaultFailureData(e);
			if (e.getMessage().contains("TerribleTimeoutThread")) {
				failureData.put("terribletimeout", "1");
			} else {
				failureData.put("terribletimeout", "0");
			}
			failureData.putAll(ExperimentUtilities
					.getDefaultExperimentData(experiment));
			CSVUtilities.appendCSVData(new File(getCSVFile().getParentFile(),
					"failed_" + getCSVFile().getName()), failureData);
			executor.shutdownNow();
			System.exit(9);
			//throw new RuntimeException("Uncomment System Exit.");
		}

		executor.shutdownNow();
		//System.err.println("Uncomment System Exit.");
		System.exit(0);
	}

	protected long getProcessTimeout() {
		return processtimeout;
	}

	public void setProcessTimeout(long timeout) {
		processtimeout = timeout;
	}

	public File getOntologyFile() {
		return ontologyfile;
	}

	public File getCSVFile() {
		return csvfile;
	}

	protected void setCSVFile(File csv) {
		csvfile = csv;
	}

	protected void setOntologyFile(File ontology) {
		ontologyfile = ontology;
	}

}
