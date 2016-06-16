package owl.cs.man.ac.uk.experiment.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import owl.cs.man.ac.uk.experiment.ontology.MetricsLabels;

public class ExperimentReasoner {
	final private String reasonerid;
	final private String reasonername;
	private ExperimentReasoner consistencydelegate = null;
	final private SubsumptionTestContext context;
	final private Map<String, String> metadata = new HashMap<String, String>();
	final private List<ExperimentSubsumptionTest> tests = new ArrayList<ExperimentSubsumptionTest>();
	final private Map<String, ExperimentReasoner> delegates = new HashMap<String, ExperimentReasoner>();
	final private Map<String, ExperimentReasoner> otherdelegates = new HashMap<String, ExperimentReasoner>();
	final private Map<String, Map<String, String>> invalid_subtests = new HashMap<String, Map<String, String>>();

	public ExperimentReasoner(Map<String, String> data) {
		this.reasonerid = data.get(MetricsLabels.REASONER_ID);
		this.reasonername = data.get(MetricsLabels.PARAM_REASONER);
		this.metadata.putAll(data);
		this.context = new SubsumptionTestContext(reasonername, reasonerid,
				data.get(MetricsLabels.FILENAME),
				data.get(MetricsLabels.EXPERIMENT_ID));
	}

	public Map<String, ExperimentReasoner> getDelegates() {
		return delegates;
	}

	public Map<String, String> getMetaData() {
		metadata.put(MetricsLabels.DELEGATE_REASONER_CT, delegates.size() + "");
		metadata.put(MetricsLabels.EL_DELEGATE_REASONER_CT,
				otherdelegates.size() + "");
		metadata.put(MetricsLabels.OWL_DELEGATE_REASONER_CT, delegates.size()
				+ "");
		return metadata;
	}

	public ExperimentReasoner getDelegate(String reasonerid_del) {
		return delegates.get(reasonerid_del);
	}

	public String getReasonerid() {
		return reasonerid;
	}

	public String getReasonername() {
		return reasonername;
	}

	public void addMetadata(Map<String, String> rec) {
		getMetaData().putAll(rec);
	}

	public void addSubtestMetaData(String reasonerid, Map<String, String> rec) {
		if (getReasonerid().equals(reasonerid)) {
			ExperimentSubsumptionTest t = new ExperimentSubsumptionTest(
					context, rec);
			ensureTestUniqueness(t);
			getSubsumptionTests().add(t);
		} else if (delegates.containsKey(reasonerid)) {
			delegates.get(reasonerid).addSubtestMetaData(reasonerid, rec);
		} else {
			invalid_subtests.put(reasonerid, rec);
			AnalysisUtils.p("Unkown delegate for Subtestdata in " + toString()
					+ ": " + rec);
		}
	}

	public void ensureTestUniqueness(ExperimentSubsumptionTest t) {
		for(ExperimentSubsumptionTest st:getSubsumptionTests()) {
			if(t.getId().equals(st.getId())) {
				System.out.println("###START###");
				System.out.println(t);
				System.out.println(st);
				AnalysisUtils.p("###END###");
				throw new IllegalArgumentException("Duplicate TEST!");
			}
		}
	}

	public Map<String, List<ExperimentSubsumptionTest>> getSubsumptionTestsAsMap() {
		//TODO Make recursive
		Map<String, List<ExperimentSubsumptionTest>> subtests = new HashMap<String, List<ExperimentSubsumptionTest>>();
		for (String rid : getDelegates().keySet()) {
			ExperimentReasoner r = getDelegates().get(rid);
			subtests.put(r.getReasonerid(), r.getSubsumptionTests());
		}
		if (!getSubsumptionTests().isEmpty()) {
			subtests.put(getReasonerid(), getSubsumptionTests());
		}
		return subtests;
	}

	public List<ExperimentSubsumptionTest> getSubsumptionTests() {
		return tests;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Name: " + reasonername + ", ");
		if (delegates.size() > 0) {
			sb.append("Delegates: " + delegates.size() + ", ");
		} else {
			sb.append("Monolithic Reasoner" + ", ");
		}
		return sb.toString();
	}

	/*
	 * private Integer getNumberOfSubTests() { if(numtests==null){ numtests = 0;
	 * Map<String, List<ExperimentSubsumptionTest>> s =
	 * getSubsumptionTestsAsMap(); for (String r : s.keySet()) { numtests +=
	 * s.get(r).size(); } return numtests; }
	 */
	public void addDelegate(ExperimentReasoner delegate) {
		delegates.put(delegate.getReasonerid(), delegate);
	}

	public SubsumptionTestContext getContext() {
		return context;
	}

	public void addOtherDelegate(ExperimentReasoner delegate) {
		otherdelegates.put(delegate.getReasonerid(), delegate);
	}

	public void addConsistencyDelegate(ExperimentReasoner delegate) {
		if (consistencydelegate == null) {
			consistencydelegate = delegate;
		} else {
			AnalysisUtils.p("CONSISTENCY DELEGATE ALREADY SET!");
		}
	}

	public Map<String, ExperimentReasoner> getOtherDelegates() {
		return otherdelegates;
	}

	public ExperimentReasoner getConsistencyDelegate() {
		return consistencydelegate;
	}

	public boolean idConductedSubtest() {
		return (getSubsumptionTests().size() > 0);
	}
}
