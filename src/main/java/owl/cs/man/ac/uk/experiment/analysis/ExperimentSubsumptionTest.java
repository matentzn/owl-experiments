package owl.cs.man.ac.uk.experiment.analysis;

import java.util.HashMap;
import java.util.Map;

import owl.cs.man.ac.uk.experiment.metrics.reasoner.ReasonerMetrics.ReasonerMetaData;
import owl.cs.man.ac.uk.experiment.util.ExperimentUtilities;

public class ExperimentSubsumptionTest {
	
	final SubsumptionTestContext c;
	final String superClass;
	final String subClass;
	final String id;
	long start;
	long end;
	public boolean pos;
	Map<String,String> metadata = new HashMap<String,String>();
	
	public ExperimentSubsumptionTest(SubsumptionTestContext c, String superClass,String subClass,long start,long end,boolean pos) {
		this.superClass = superClass;
		this.subClass = subClass;
		this.start = start;
		this.end = end;
		this.id = subClass+superClass;
		this.pos = pos;
		this.c = c;
	}

	public ExperimentSubsumptionTest(SubsumptionTestContext c, Map<String, String> subrec) {
		this.superClass = subrec.get(ReasonerMetaData.SUPERCLASS.getName());
		this.subClass = subrec.get(ReasonerMetaData.SUBCLASS.getName());
		this.start = ExperimentUtilities.getLong(subrec.get(ReasonerMetaData.SATTEST_START.getName()));
		this.end = ExperimentUtilities.getLong(subrec.get(ReasonerMetaData.SATTEST_END.getName()));
		this.id = subClass+superClass;
		this.c = c;
		this.pos = subrec.get(ReasonerMetaData.SATTEST_END.getName()).equals("1");
		metadata.putAll(subrec);
	}

	public Map<String, String> getMetaData() {
		return metadata;
	}
	
	public String getSuperClass() {
		return superClass;
	}

	public String getSubClass() {
		return subClass;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}
	
	public String getId() {
		return id;
	}

	public long getDuration() {
		return (getEnd()-getStart());
	}

	public SubsumptionTestContext getContext() {
		return c;
	}
	
	public String toString() {
		return getId()+" "+getDuration()+" "+pos+" "+getContext().reasonerid;
	}
	
}
