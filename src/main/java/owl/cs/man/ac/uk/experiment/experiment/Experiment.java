package owl.cs.man.ac.uk.experiment.experiment;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.util.Version;

import owl.cs.man.ac.uk.experiment.analysis.AnalysisUtils;
import owl.cs.man.ac.uk.experiment.csv.CSVUtilities;
import owl.cs.man.ac.uk.experiment.ontology.MetricsLabels;
import owl.cs.man.ac.uk.experiment.util.ExperimentUtilities;

public abstract class Experiment implements Callable<String> {

	private final File csvfile;
	private final File ontfile;
	private final String runid;
	private final Map<String,String> experimentResults = new HashMap<String,String>();
	
	public Experiment(File ontfile, File csvfile) {
		this.ontfile = ontfile;
		this.csvfile = csvfile;
		this.runid = System.nanoTime()+"";
		
		experimentResults.putAll(ExperimentUtilities.getDefaultExperimentData(this));
		experimentResults.put("jar_name",ExperimentUtilities.getResourcePath(OWLManager.createOWLOntologyManager()).getName());
		addResult(MetricsLabels.PARAM_ONTFILE, ontfile.getAbsolutePath());
	}

	/**
	 * @param args
	 */
	
	public void exportResults() {
		CSVUtilities.writeCSVData(getCSVFile(), experimentResults,true);
	}
	
	final protected void exportAdditionalDataToCSV(File file,List<Map<String,String>> data) {
		CSVUtilities.writeCSVData(file, data, false);
	}
	
	protected void writeFailureData(Exception e) {
		experimentResults.putAll(ExperimentUtilities.getDefaultFailureData(e));
		exportResults();
	}
	
	protected File getCSVFile() {
		return csvfile;
	}
	
	protected String getFilename() {
		return ontfile.getName();
	}
	
	public File getOntologyFile() {
		return ontfile;
	}
	
	public String call() throws Exception {
		try {
			this.process();
			recordMemoryUsage();
			System.gc();
			exportResults();
		}
		catch (Exception e) {
			e.printStackTrace();
			writeFailureData(e);
			throw new RuntimeException(e);
		}
		return "finished";
	}

	public void recordMemoryUsage() {
		try {
		    List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
		    List<Long> mem = new ArrayList<Long>();
		    for (MemoryPoolMXBean pool : pools) {
		        MemoryUsage peak = pool.getPeakUsage();
		        mem.add(peak.getUsed());
		    }
		    addResult(MetricsLabels.PEAK_MEMORY_USAGE,AnalysisUtils.getSum(mem)+"");
 
   } catch (Throwable t) {
		    System.err.println("Exception in agent: " + t);
   }
	}
	
	public Map<String,String> getExperimentParameters() {
		return experimentResults;
	}
	
	public String getRunid() {
		return runid;
	}
	
	protected void addResult(String name, String value) {
		value = value == null ? "" : value;
		experimentResults.put(name, value);
	}
	
	protected void addResult(Map<String,String> data) {
		experimentResults.putAll(data);
	}
	
	protected Map<String,String> getResult() {
		return new HashMap<String,String>(experimentResults);
	}
	
	public String getExperimentVersionFormatted() {
		Version v = getExperimentVersion();
		return v.getMajor()+"."+v.getMinor()+"."+v.getPatch()+"."+v.getBuild();
	}

	public String getExperimentName() {
		return this.getClass().getSimpleName();
	}
	
	protected abstract void process() throws Exception;
	protected abstract Version getExperimentVersion();

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Experiment run id: "+runid+" \n");
		sb.append("CSV Path: "+csvfile+" \n");
		sb.append("Ontology Path: "+ontfile+" \n");
		return sb.toString();
	}
	
}
