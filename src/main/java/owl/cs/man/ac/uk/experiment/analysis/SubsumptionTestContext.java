package owl.cs.man.ac.uk.experiment.analysis;

import java.util.HashMap;
import java.util.Map;

import owl.cs.man.ac.uk.experiment.ontology.MetricsLabels;

public class SubsumptionTestContext {
	
	final String reasoner;
	final String reasonerid;
	final String ontologyid;
	final String experimentid;
	Map<String,String> metadata = new HashMap<String,String>();
	
	public SubsumptionTestContext(String reasoner,String reasonerid,String ontologyid,String experimentid) {
		this.reasoner=reasoner;
		this.reasonerid=reasonerid;
		this.ontologyid=ontologyid;
		this.experimentid=experimentid;
		metadata.put(MetricsLabels.REASONERNAME,reasoner);
		metadata.put(MetricsLabels.REASONER_ID,reasonerid);
		metadata.put(MetricsLabels.FILENAME,ontologyid);
		metadata.put(MetricsLabels.EXPERIMENT_ID,experimentid);
	}
	
	public Map<String,String> getMetadata() {
		return metadata;
	}

	public String getReasoner() {
		return reasoner;
	}

}
