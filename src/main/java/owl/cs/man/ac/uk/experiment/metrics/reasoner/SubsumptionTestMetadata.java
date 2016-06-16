package owl.cs.man.ac.uk.experiment.metrics.reasoner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import owl.cs.man.ac.uk.experiment.metrics.reasoner.ReasonerMetrics.STType;

public class SubsumptionTestMetadata {

	final SubsumptionTest first;
	Set<SubsumptionTest> tests = new HashSet<SubsumptionTest>();
	long subtest = 0;
	long subtest_pos = 0;
	long subtest_neg = 0;
	long subtest_easy = 0;
	long subtest_hard = 0;
	long subtest_poseasy = 0;
	long subtest_poshard = 0;
	long subtest_negeasy = 0;
	long subtest_neghard = 0;

	SubsumptionTestMetadata(SubsumptionTest st) {
		tests.add(st);
		first = st;
	}

	public boolean addTest(SubsumptionTest st) {
		if (!(st.subClass.equals(first.subClass)
				&& !st.superClass.equals(first.superClass))) {
			return false;
		}
		// SOMEHOW HERE THE SUBTESTS ARE DUPLICATED :P
		if(!tests.contains(st)) {
			tests.add(st);
		}
		return true;
	}

	public void calculateStats() {
		for (SubsumptionTest st : tests) {
			subtest++;
			if (st.type.equals(STType.SATTEST)) {
				subtest_hard++;
				if (st.pos) {
					subtest_pos++;
					subtest_poshard++;
				} else {						
					subtest_neg++;
					subtest_neghard++;
				}
			} else {
				subtest_easy++;
				if (st.pos) {
					subtest_pos++;
					subtest_poseasy++;
				} else {
					subtest_neg++;
					subtest_negeasy++;
				}
			}
		}
	}
	
	public List<Map<String,String>> getAllSubTestData() {
		List<Map<String,String>> stdata = new ArrayList<Map<String,String>>();
		for(SubsumptionTest st:tests) {
			stdata.add(st.getData());
		}
		return stdata;
	}

	public long getSubsumptionTestCount() {

		return subtest;
	}

	public long getSubsumptionTestHardCount() {

		return subtest_hard;
	}

	public long getSubsumptionTestEasyCount() {

		return subtest_easy;
	}

	public long getSubsumptionTestNegEasyCount() {

		return subtest_negeasy;
	}

	public long getSubsumptionTestNegHardCount() {

		return subtest_neghard;
	}

	public long getSubsumptionTestPosCount() {

		return subtest_pos;
	}

	public long getSubsumptionTestNegCount() {

		return subtest_neg;
	}

	public long getSubsumptionTestPosEasyCount() {

		return subtest_poseasy;
	}

	public long getSubsumptionTestPosHardCount() {

		return subtest_poshard;
	}

	public Set<SubsumptionTest> getTests() {
		return tests;
	}

}
