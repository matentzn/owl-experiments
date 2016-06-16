package owl.cs.man.ac.uk.experiment.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import owl.cs.man.ac.uk.experiment.ontology.MetricsLabels;
import owl.cs.man.ac.uk.experiment.util.ExperimentUtilities;
import owl.cs.man.ac.uk.experiment.metrics.reasoner.ReasonerMetrics.ReasonerMetaData;

public class ExperimentRun {

	final ExperimentOntology ontology;
	final double SIMILARITY_THRESHOLD = 0.05;
	final String runid;
	final ExperimentReasoner reasoner;
	List<ExperimentReasoner> reasoners = new ArrayList<ExperimentReasoner>();
	final Map<String, String> metadata = new HashMap<String, String>();
	Map<String, Map<String, String>> invalid_delegates = new HashMap<String, Map<String, String>>();

	public ExperimentRun(String runid, ExperimentReasoner r,
			Map<String, String> data) {
		if (!ExperimentRun.isPrimaryReasoner(data)) {
			throw new IllegalArgumentException("ExperimentReasoner " + r
					+ " is not a valid primary reasoner!");
		}
		this.ontology = new ExperimentOntology(data.get(MetricsLabels.FILENAME));
		this.runid = runid;
		this.reasoner = r;
		metadata.putAll(data);
	}

	/*
	 * Compute Results
	 */

	public void computeResults() {
		AnalysisUtils.p("#############################");
		AnalysisUtils.p("Computing results: " + toString());

		computeOWLAPIResults();
		/*
		 * if (isMonolithicReasonerRun()) { processMonolitic(getReasoner()); }
		 * else
		 */
		if (isModularReasonerRun()) {
			processModularReasoner(getReasoner(),getMetaData());
		} else {
			throw new IllegalArgumentException(
					"Neiter Monolithic nor Modular, corrupted record?");
		}
	}

	private boolean isModularReasonerRun() {
		int delegates = getReasoner().getDelegates().size();
		delegates += getReasoner().getOtherDelegates().size();
		return (delegates > 0);
	}

	private void computeOWLAPIResults() {
		// AnalysisUtils.p(getReasoner().getMetaData());
		// AnalysisUtils.pause();
		long owlapiload = ExperimentUtilities.getLong(getReasoner()
				.getMetaData(), MetricsLabels.OWLAPILOAD_TS);
		long reasonercreate = ExperimentUtilities.getLong(getReasoner()
				.getMetaData(), MetricsLabels.CREATEREASONER_TS);
		long classification = ExperimentUtilities.getLong(getReasoner()
				.getMetaData(), MetricsLabels.CLASSIFICATION_TS);
		long dispose = ExperimentUtilities.getLong(getReasoner().getMetaData(),
				MetricsLabels.DISPOSE_TS);
		long finish = ExperimentUtilities.getLong(getReasoner().getMetaData(),
				MetricsLabels.FINISH_TS);

		addResult("B1" + MetricsLabels.ONTOLOGYLOAD_TIME,
				(reasonercreate - owlapiload) + "");
		addResult("B2" + MetricsLabels.CREATEREASONER_TIME,
				(classification - reasonercreate) + "");
		addResult("B3" + MetricsLabels.CLASSIFICATION_TIME,
				(dispose - classification) + "");
		addResult("B4" + MetricsLabels.DISPOSEREASONER_TIME, (finish - dispose)
				+ "");
	}

	private static void processMonolitic(ExperimentReasoner r) {
		
		
		long pp = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.PP_TS.getName());
		if(r.getReasonername().contains("fact")&&!r.getReasonername().contains("jfact")) {
			if(r.getMetaData().containsKey(ReasonerMetaData.FACT_CC_TS)) {
				String facts = r.getMetaData().get(ReasonerMetaData.FACT_CC_TS);
				try {
					long l = Long.parseLong(facts);
					if(l>0) {
						long fact_sync = l - pp;
						long cc = ExperimentUtilities.getLong(r.getMetaData(),
								ReasonerMetaData.CC_TS.getName());
						long pco = ExperimentUtilities.getLong(r.getMetaData(),
								ReasonerMetaData.PRP_TS.getName());
						long t = ExperimentUtilities.getLong(r.getMetaData(),
								ReasonerMetaData.ST_TS.getName());
						long pop = ExperimentUtilities.getLong(r.getMetaData(),
								ReasonerMetaData.POP_TS.getName());
						r.getMetaData().put(ReasonerMetaData.CC_TS.getName(), cc-fact_sync+"");
						r.getMetaData().put(ReasonerMetaData.PRP_TS.getName(), pco-fact_sync+"");
						r.getMetaData().put(ReasonerMetaData.ST_TS.getName(), t-fact_sync+"");
						r.getMetaData().put(ReasonerMetaData.POP_TS.getName(), pop-fact_sync+"");
						
						for (ExperimentSubsumptionTest st : r.getSubsumptionTests()) {
							st.start = st.start - fact_sync;
							st.end = st.end - fact_sync;
						}
					}
				}
				catch(Exception e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		long cc = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.CC_TS.getName());
		long pco = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.PRP_TS.getName());
		long t = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.ST_TS.getName());
		long pop = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.POP_TS.getName());
		long popfin = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.POPFIN_TS.getName());
		
		
		r.getMetaData().put("A1" + MetricsLabels.REASONING_PREPROCESSING_TIME,
				(cc - pp) + "");

		r.getMetaData().put("A2" + MetricsLabels.REASONING_CONSISTENCY_TIME,
				(pco - cc) + "");
		long pcolength = (t - pco);
		r.getMetaData().put(
				"A3" + MetricsLabels.REASONING_PREREASONINGOPTIMISATION_TIME,
				(pcolength) + "");
		long traversallength = (pop - t);

		r.getMetaData().put("A4" + MetricsLabels.REASONING_TRAVERSALPHASE_TIME,
				(traversallength) + "");
		r.getMetaData().put("A5" + MetricsLabels.REASONING_POSTROCESSING_TIME,
				(popfin - pop) + "");

		long totalduration_st = 0;
		long totalduration_pco = 0;
		long totalduration_other = 0;
		int SAT_CT_TOTAL = 0; // Number of redundant tests (vs itself)
		int SAT_CT_TOTAL_VERYHARD = 0; // >100 sec
		int SAT_CT_TOTAL_HARD = 0; // > 10 sec
		int SAT_CT_TOTAL_MEDIUMHARD = 0; // > 1 sec
		int SAT_CT_TOTAL_MEDIUM = 0; // > 100 ms
		int SAT_CT_TOTAL_MEDIUMEASY = 0; // > 100 ms
		int SAT_CT_TOTAL_EASY = 0; // > 10 ms
		int SAT_CT_TOTAL_VERYEASY = 0; // > 1 ms
		int SAT_CT_TOTAL_TRIVIAL = 0; // 100000 ns

		long TT_SAT_ALL = 0;

		for (ExperimentSubsumptionTest st : r.getSubsumptionTests()) {
			SAT_CT_TOTAL++;
			if (st.getDuration() > 100000000000l) {
				SAT_CT_TOTAL_VERYHARD++;
			} else if (st.getDuration() > 10000000000l) {
				SAT_CT_TOTAL_HARD++;
			} else if (st.getDuration() > 1000000000l) {
				SAT_CT_TOTAL_MEDIUMHARD++;
			} else if (st.getDuration() > 100000000l) {
				SAT_CT_TOTAL_MEDIUM++;
			} else if (st.getDuration() > 10000000l) {
				SAT_CT_TOTAL_MEDIUMEASY++;
			} else if (st.getDuration() > 1000000l) {
				SAT_CT_TOTAL_EASY++;
			} else if (st.getDuration() > 100000l) {
				SAT_CT_TOTAL_VERYEASY++;
			} else {
				SAT_CT_TOTAL_TRIVIAL++;
			}
			TT_SAT_ALL += st.getDuration();

			if (AnalysisUtils.isInsideRange(st.getStart(), st.getEnd(), t, pop)) {
				totalduration_st += st.getDuration();
			} else if (AnalysisUtils.isInsideRange(st.getStart(), st.getEnd(),
					pco, t)) {
				totalduration_pco += st.getDuration();
			} else {
				totalduration_other += st.getDuration();
			}
		}

		r.getMetaData().put("sat_ct_total", SAT_CT_TOTAL + "");
		r.getMetaData().put("sat_ct_total_01_veryhard",
				SAT_CT_TOTAL_VERYHARD + "");
		r.getMetaData().put("sat_ct_total_02_hard", SAT_CT_TOTAL_HARD + "");
		r.getMetaData().put("sat_ct_total_03_mediumhard",
				SAT_CT_TOTAL_MEDIUMHARD + "");
		r.getMetaData().put("sat_ct_total_04_medium", SAT_CT_TOTAL_MEDIUM + "");
		r.getMetaData().put("sat_ct_total_05_mediumeasy",
				SAT_CT_TOTAL_MEDIUMEASY + "");
		r.getMetaData().put("sat_ct_total_06_easy", SAT_CT_TOTAL_EASY + "");
		r.getMetaData().put("sat_ct_total_07_veryeasy",
				SAT_CT_TOTAL_VERYEASY + "");
		r.getMetaData().put("sat_ct_total_08_trivial",
				SAT_CT_TOTAL_TRIVIAL + "");
		r.getMetaData().put("tt_sat_all", TT_SAT_ALL + "");

		printSubtestComputation("traversal", traversallength, totalduration_st);

		if (totalduration_pco > 0) {
			printSubtestComputation("pco", pcolength, totalduration_pco);
			AnalysisUtils.pause();
		}
		if (totalduration_other > 0) {
			AnalysisUtils.p("OTHER");
			AnalysisUtils.p(totalduration_other);
			AnalysisUtils.pause();
		}

		r.getMetaData().put(
				"rat_tt_sat_st",
				ExperimentUtilities.round(
						((double) totalduration_st / (double) traversallength),
						3)
						+ "");
	}

	private static void processModularReasoner(ExperimentReasoner r, Map<String, String> data) {

		/*
		 * Extract base phase data
		 */

		long pp_mod = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.PP_TS);
		long modcl_mod = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.MODCL_TS);
		long dec = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.DEC_TS);
		long dec_fin = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.DECEND_TS);
		long cc = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.CC_TS);
		long cc_fin = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.CCFIN_TS);
		long pop_mod = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.POP_TS);
		long popfin_mod = ExperimentUtilities.getLong(r.getMetaData(),
				ReasonerMetaData.POPFIN_TS);

		long pp_duration = modcl_mod - pp_mod;
		long modcl_duration = pop_mod - modcl_mod;
		long pop_duration = popfin_mod - pop_mod;
		long dec_duration = dec_fin - dec;
		long cc_duration = cc_fin - cc;

		data.put("M1" + MetricsLabels.REASONING_PREPROCESSING_TIME,
				(pp_duration) + "");

		data.put("M2" + MetricsLabels.REASONING_MODULARREASONING_TIME,
				(modcl_duration) + "");

		data.put("M3" + MetricsLabels.REASONING_POSTROCESSING_TIME,
				(pop_duration) + "");

		data.put("M91" + MetricsLabels.REASONING_DECOMPOSITION_TIME,
				(dec_duration) + "");
		data.put("M92" + MetricsLabels.REASONING_CONSISTENCY_TIME,
				(cc_duration) + "");

		long cltime_all = (popfin_mod - pp_mod);
		long cltime_reasoning = (modcl_duration);

		data.put("cltime_all", cltime_all + "");
		data.put("cltime_reasoning", cltime_reasoning + "");

		// boolean consistencycheck = extractConsistencyCheckData();

		/*
		 * Extract Subsumption test data
		 */

		Map<String, List<ExperimentSubsumptionTest>> subtestdata_indexedbyreasoner = r
				.getSubsumptionTestsAsMap();
		
		for(String k:r.getDelegates().keySet()) {
			AnalysisUtils.p("R1: "+k);
			AnalysisUtils.p("R: "+r.getReasonerid());
			Set<String> ids = new HashSet<String>();
			List<ExperimentSubsumptionTest> s = r.getDelegate(k).getSubsumptionTests();
			for(ExperimentSubsumptionTest st:s){
				if(ids.contains(st.getId())) {
					AnalysisUtils.p(st.getMetaData().get("reasonerid"));
					AnalysisUtils.p("DDDANGER" + st.getId());
					throw new IllegalArgumentException("Duplicate ST!!");
				}
				ids.add(st.getId());
			}
		}

		Map<String, List<ExperimentSubsumptionTest>> subtest_record = new HashMap<String, List<ExperimentSubsumptionTest>>();

		long totalduration_st_modcl = 0; // SUM all STs during module
											// classification (should be
											// all)
		long totalduration_st_pp = 0; // SUM all STs during preprocessing
		long totalduration_st_pop = 0; // SUM all STs in other phases

		long tt_del_pp = 0; // SUM all delegate PP times
		long tt_del_cc = 0; // SUM all delegate CC times
		long tt_del_prp = 0; // SUM all delegate PRP times
		long tt_del_st = 0; // SUM all delegate ST times
		long tt_del_pop = 0; // SUM all delegate POP times

		int SAT_CT_TOTAL = 0; // Number of redundant tests (vs itself)
		int SAT_CT_TOTAL_HARD = 0; // Number of redundant tests (vs itself)
		/*
		 * long first_cc_del = 999999999999999999l; long first_ccfin_del =
		 * 999999999999999999l;
		 */

		for (String reasonerid_del : subtestdata_indexedbyreasoner.keySet()) {
			ExperimentReasoner delegate = r.getDelegate(reasonerid_del);
			Map<String, String> del_rec = delegate.getMetaData();
			// String reasonername_del =
			// del_rec.get(MetricsLabels.REASONERNAME);
			// String filename_del = del_rec.get(MetricsLabels.FILENAME);

			long pp_del = ExperimentUtilities.getLong(del_rec,
					ReasonerMetaData.PP_TS);
			long cc_del = ExperimentUtilities.getLong(del_rec,
					ReasonerMetaData.CC_TS);
			long prp_del = ExperimentUtilities.getLong(del_rec,
					ReasonerMetaData.PRP_TS);
			/*
			 * if (!consistencycheck) { if (cc_del < first_cc_del) {
			 * first_cc_del = cc_del; first_ccfin_del = prp_del; } }
			 */
			long t_del = ExperimentUtilities.getLong(del_rec,
					ReasonerMetaData.ST_TS);
			long pop_del = ExperimentUtilities.getLong(del_rec,
					ReasonerMetaData.POP_TS);
			long popfin_del = ExperimentUtilities.getLong(del_rec,
					ReasonerMetaData.POPFIN_TS);

			tt_del_pp += (cc_del - pp_del);
			tt_del_cc += (prp_del - cc_del);
			tt_del_prp += (t_del - prp_del);
			tt_del_st += (pop_del - t_del);
			tt_del_pop += (popfin_del - pop_del);

			for (ExperimentSubsumptionTest st : subtestdata_indexedbyreasoner
					.get(reasonerid_del)) {
				long starttime = st.getStart();
				long endtime = st.getEnd();
				long testduration = st.getDuration();

				SAT_CT_TOTAL++;
				if (testduration > 1000000000) {
					SAT_CT_TOTAL_HARD++;
				}

				if (AnalysisUtils.isInsideRange(starttime, endtime, modcl_mod,
						pop_mod)) {
					totalduration_st_modcl += testduration;
				} else if (AnalysisUtils.isInsideRange(starttime, endtime,
						pp_mod, modcl_mod)) {
					totalduration_st_pp += testduration;
				} else if (AnalysisUtils.isInsideRange(starttime, endtime,
						pop_mod, popfin_mod)) {
					totalduration_st_pop += testduration;
				} else {
					AnalysisUtils.e(st + " OUTSIDE RANGE!");
					AnalysisUtils.pause();
				}
				if (!subtest_record.containsKey(st.getId())) {
					subtest_record.put(st.getId(),
							new ArrayList<ExperimentSubsumptionTest>());
				}
				subtest_record.get(st.getId()).add(st);
			} // FORALL SUBTESTS END
		} // FORALL REASONERS END

		long TT_SAT_UNIQUE_WC = 0; // Total time SAT tests - No redundancy WC
		long TT_SAT_UNIQUE_BC = 0; // Total time SAT tests - No redundancy
		long TT_SAT_UNIQUE_FIRST = 0; // Total time SAT tests - No redundancy,
										// taking the first test

		long TT_SAT_RED_WC = 0; // Total time added because of Redundancy WC
		long TT_SAT_RED_BC = 0; // Total time added because of Redundancy BC
		long TT_SAT_RED_FIRST = 0; // Total time SAT tests - No redundancy,
									// taking the first test

		long TT_SAT_ALL = 0;

		final int SAT_CT_UNIQUE = subtest_record.size();
		final int SAT_CT_RED = SAT_CT_TOTAL - SAT_CT_UNIQUE; // Number of
																// redundant

		// EXTRACT ST METRICS
		for (String subest : subtest_record.keySet()) {
			List<ExperimentSubsumptionTest> tests = subtest_record.get(subest);
			List<Long> testtimes = new ArrayList<Long>();
			for (ExperimentSubsumptionTest st : tests) {
				testtimes.add(st.getDuration());
			}
			long fastest = AnalysisUtils.getMin(testtimes);
			long slowest = AnalysisUtils.getMax(testtimes);
			long first = getFirstTestTime(tests);

			TT_SAT_UNIQUE_BC += fastest;
			TT_SAT_UNIQUE_WC += slowest;
			TT_SAT_UNIQUE_FIRST += first;
			long sum = AnalysisUtils.getSum(testtimes);
			TT_SAT_RED_BC += (sum - fastest);
			TT_SAT_RED_WC += (sum - slowest);
			TT_SAT_RED_FIRST += (sum - first);
			TT_SAT_ALL += sum;
		}
		// compare to monolithic..

		data.put("cltime_subtests", totalduration_st_modcl + "");
		data.put("tt_sat_red_wc", TT_SAT_RED_WC + "");
		data.put("tt_sat_red_bc", TT_SAT_RED_BC + "");
		data.put("tt_sat_red_first", TT_SAT_RED_FIRST + "");
		data.put("tt_sat_unique_bc", TT_SAT_UNIQUE_BC + "");
		data.put("tt_sat_unique_wc", TT_SAT_UNIQUE_WC + "");
		data.put("tt_sat_unique_first", TT_SAT_UNIQUE_FIRST + "");
		data.put("tt_sat_all", TT_SAT_ALL + "");

		/*
		 * Process Computation Results
		 */

		printSubtestComputation("traversal", modcl_duration,
				totalduration_st_modcl);

		if (totalduration_st_pp > 0) {
			printSubtestComputation("pco", pp_duration, totalduration_st_pp);
			AnalysisUtils.pause();
		}
		if (totalduration_st_pop > 0) {
			printSubtestComputation("pop", pop_duration, totalduration_st_pop);
			AnalysisUtils.pause();
		}

		data.put("sat_ct_unique", SAT_CT_UNIQUE + "");
		data.put("sat_ct_red", SAT_CT_RED + "");
		data.put("sat_ct_total", SAT_CT_TOTAL + "");
		data.put("sat_ct_total_hard", SAT_CT_TOTAL_HARD + "");

		data.put("tt_del_pp", tt_del_pp + "");
		data.put("tt_del_cc", tt_del_cc + "");
		data.put("tt_del_prp", tt_del_prp + "");
		data.put("tt_del_st", tt_del_st + "");
		data.put("tt_del_pop", tt_del_pop + "");
		
		data.put("rat_tt_sat_st", ExperimentUtilities.round(
				((double) TT_SAT_ALL / (double) modcl_duration), 3) + "");

		data.put(
				"rat_dec_pp",
				ExperimentUtilities.round(
						((double) dec_duration / (double) pp_duration), 3) + "");
		data.put(
				"rat_dec_cltime_all",
				ExperimentUtilities.round(
						((double) dec_duration / (double) cltime_all), 3) + "");
		data.put(
				"rat_pp_cltime_all",
				ExperimentUtilities.round(
						((double) pp_duration / (double) cltime_all), 3) + "");
		data.put(
				"rat_del_cc_cltime_all",
				ExperimentUtilities.round(
						((double) tt_del_cc / (double) cltime_all), 3) + "");

		
		int del_reasoner_conducting_sat_ct = 0;
		long delegate_worst_cl_time = 0;
		Map<String, ExperimentReasoner> del = r.getDelegates();
		
		for (String s : del.keySet()) {
			ExperimentReasoner delr = del.get(s);
			long pp_ts = ExperimentUtilities.getLong(delr.getMetaData(),
					ReasonerMetaData.PP_TS);
			long popfin_ts = ExperimentUtilities.getLong(delr.getMetaData(),
					ReasonerMetaData.POPFIN_TS);
			long duration = popfin_ts - pp_ts;
			if (duration > delegate_worst_cl_time) {
				// AnalysisUtils.p("worse!"+delegate_worst_cl_time+" "+duration);
				// AnalysisUtils.pause();
				delegate_worst_cl_time = duration;
			}
			if (delr.idConductedSubtest()) {
				del_reasoner_conducting_sat_ct++;
			}
			processMonolitic(delr);
		}

		data.put("delreasoner_conductedsat_ct", del_reasoner_conducting_sat_ct
				+ "");
		data.put("delreasoner_worst_cltime", delegate_worst_cl_time + "");
		double serialfraction = 1 - (modcl_duration / cltime_all);
		/*
		data.put(
				"amsdahl_simulation_alldel",
				ExperimentUtilities.speedAccordingToAmdahlsLaw(del.size(),
						1 - serialfraction) + "");
		data.put(
				"amsdahl_simulation_16",
				ExperimentUtilities.speedAccordingToAmdahlsLaw(16,
						1 - serialfraction) + "");
		data.put(
				"amsdahl_simulation_8",
				ExperimentUtilities.speedAccordingToAmdahlsLaw(8,
						1 - serialfraction) + "");
		data.put(
				"gustafsons_simulation_alldel",
				ExperimentUtilities.speedAccordingToGustafsonsLaw(del.size(),
						serialfraction) + "");
		data.put(
				"gustafsons_simulation_16",
				ExperimentUtilities.speedAccordingToGustafsonsLaw(16,
						serialfraction) + "");
		data.put(
				"gustafsons_simulation_8",
				ExperimentUtilities.speedAccordingToGustafsonsLaw(8,
						serialfraction) + "");
			*/
	}

	private static long getFirstTestTime(List<ExperimentSubsumptionTest> tests) {
		ExperimentSubsumptionTest first = null;
		for (ExperimentSubsumptionTest e : tests) {
			if (first == null) {
				first = e;
			} else {
				// if the current test happened earlier then current "first"
				if (first.getStart() > e.getStart()) {
					first = e;
				}
			}
		}
		return first.getDuration();
	}

	/*public Map<String, List<ExperimentSubsumptionTest>> getSatTestMapForMonolithicReasoner(
			ExperimentRun er) {
		Map<String, List<ExperimentSubsumptionTest>> mono_sat = new HashMap<String, List<ExperimentSubsumptionTest>>();
		if (er != null) {
			Map<String, List<ExperimentSubsumptionTest>> s = er.getReasoner()
					.getSubsumptionTestsAsMap();
			for (String reasoner : s.keySet()) {
				for (ExperimentSubsumptionTest st : s.get(reasoner)) {
					if (!mono_sat.containsKey(st.getId())) {
						mono_sat.put(st.getId(),
								new ArrayList<ExperimentSubsumptionTest>());
					}
					mono_sat.get(st.getId()).add(st);
				}
			}
		}
		return mono_sat;
	}*/

	/*
	 * Getters and Setters
	 */

	public ExperimentOntology getOntology() {
		return ontology;
	}

	public String getRunid() {
		return runid;
	}

	public ExperimentReasoner getReasoner() {
		return reasoner;
	}

	public Map<String, String> getMetaData() {
		return metadata;
	}

	private void addResult(String key, Object value) {
		getMetaData().put(key, value.toString());
	}

	public void addReasonerMetadata(String reasonerid, Map<String, String> rec) {
		if (getReasoner().getReasonerid().equals(reasonerid)) {
			getReasoner().addMetadata(rec);
		} else if (getReasoner().getDelegates().containsKey(reasonerid)) {
			getReasoner().getDelegate(reasonerid).addMetadata(rec);
		} else {
			if (validDelegate(rec)) {
				rec.put(ReasonerMetaData.DELEGATETYPE.getName(),
						"katana-owl-delegate");
				ExperimentReasoner delegate = new ExperimentReasoner(rec);
				delegate.addMetadata(rec);
				getReasoner().addDelegate(delegate);
				// AnalysisUtils.pause();

			} else if (consistencyDelegate(rec)) {
				ExperimentReasoner delegate = new ExperimentReasoner(rec);
				delegate.addMetadata(rec);
				getReasoner().addConsistencyDelegate(delegate);
			} else if (elDelegate(rec)) {
				ExperimentReasoner delegate = new ExperimentReasoner(rec);
				delegate.addMetadata(rec);
				getReasoner().addOtherDelegate(delegate);
			} else {
				invalid_delegates.put(reasonerid, rec);
				AnalysisUtils.e("XInvalid delegate record for " + toString()
						+ ": " + rec + " rid:" + reasonerid);
				// AnalysisUtils.pause();
			}
		}
	}

	private boolean elDelegate(Map<String, String> rec) {
		if (rec.containsKey("MODCL_TS")) {
			if (!rec.get("MODCL_TS").isEmpty()) {
				return false;
			}
		}
		if (rec.containsKey("CC_TS")) {
			if (!rec.get("CC_TS").isEmpty()) {
				return false;
			}
		}
		if (rec.containsKey("delreasoner_cl")) {
			if (rec.get("delreasoner_cl").trim().equals("ElkReasoner")) {
				// AnalysisUtils.p("ELK!");
				rec.put(ReasonerMetaData.DELEGATETYPE.getName(), "katana-el");
				return true;
			}
		}
		return false;
	}

	private boolean consistencyDelegate(Map<String, String> rec) {
		if (rec.containsKey(ReasonerMetaData.DELEGATETYPE.getName())) {
			if (rec.get(ReasonerMetaData.DELEGATETYPE.getName()).equals(
					"katana-consistency")) {
				return true;
			}
		}
		return false;
	}

	public void addSubtestMetaData(String reasonerid, Map<String, String> rec) {
		getReasoner().addSubtestMetaData(reasonerid, rec);
	}

	/*
	 * Record validity
	 */

	public static boolean isPrimaryReasoner(Map<String, String> rec) {
		if (rec.containsKey(ReasonerMetaData.DELEGATEREASONERCLASS.getName())) {
			if (!rec.get(ReasonerMetaData.DELEGATEREASONERCLASS.getName())
					.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private boolean validDelegate(Map<String, String> del_rec) {
		// AnalysisUtils.p(del_rec.get("delreasoner_cl"));
		// AnalysisUtils.pause();
		if (isDelegateRecord(del_rec)) {
			// AnalysisUtils.p("ISDELREC");
			try {
				if (del_rec.get(ReasonerMetaData.PP_TS.getName()).isEmpty()
						|| del_rec.get(ReasonerMetaData.CC_TS.getName())
								.isEmpty()
						|| del_rec.get(ReasonerMetaData.PRP_TS.getName())
								.isEmpty()
						|| del_rec.get(ReasonerMetaData.ST_TS.getName())
								.isEmpty()
						|| del_rec.get(ReasonerMetaData.POP_TS.getName())
								.isEmpty()
						|| del_rec.get(ReasonerMetaData.POPFIN_TS.getName())
								.isEmpty()) {
					/*
					 * AnalysisUtils.p("IN: " + del_rec);
					 * AnalysisUtils.p(del_rec.get("PP_TS"));
					 * AnalysisUtils.p(del_rec.get("CC_TS"));
					 * AnalysisUtils.p(del_rec.get("PRP_TS"));
					 * AnalysisUtils.p(del_rec.get("ST_TS"));
					 * AnalysisUtils.p(del_rec.get("POP_TS"));
					 * AnalysisUtils.p(del_rec.get("POPFIN_TS"));
					 * AnalysisUtils.p(del_rec.get("delreasoner_cl"));
					 * AnalysisUtils.pause();
					 */
					return false;
				} else {
					// AnalysisUtils.p("TRUE");
					// AnalysisUtils.pause();
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean isDelegateRecord(Map<String, String> rec) {
		boolean delegate = (rec
				.containsKey(ReasonerMetaData.DELEGATEREASONERCLASS.getName()) && !(rec
				.get(ReasonerMetaData.DELEGATEREASONERCLASS.getName())
				.isEmpty()));
		return delegate;
	}

	/*
	 * Utility
	 */

	public static void printSubtestComputation(String phase, long traversallength,
			long totalduration_st) {
		AnalysisUtils.p(phase);
		AnalysisUtils.p("Total SUM tests, sec: " + (totalduration_st));
		AnalysisUtils.p("Total Traversaltime, sec: " + (traversallength));
		AnalysisUtils.p("Difference, sec: "
				+ (traversallength - totalduration_st));
		double rel = ExperimentUtilities.round(
				((double) totalduration_st / (double) traversallength), 2);
		AnalysisUtils.p("                              [REL: " + rel + "]");
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Run: " + getRunid() + ", ");
		sb.append("Ontology: " + getOntology() + ", ");
		sb.append("Reasoner: " + getReasoner() + ", ");
		return sb.toString();
	}

	public Map<String, Map<String, String>> getInvalidDelegateRecords() {
		return invalid_delegates;
	}
	/*
	 * private void compare(ExperimentRun mod,ExperimentRun er,Map<String,
	 * List<ExperimentSubsumptionTest>> subtest_record) { //ExperimentRun er =
	 * getRandomMonolithicRun(related);
	 * 
	 * if (er != null) { // COMPARED TO MON long SAT_CT_EASIER_WC = 0; // Number
	 * of easier SAT tests WC compared long SAT_CT_EASIER_BC = 0; // Number of
	 * easier SAT tests BC compared long SAT_CT_EASIER_FIRST = 0; long
	 * SAT_CT_EASIER_OVERALL = 0; long SAT_CT_SAME_WC = 0; // Number of easier
	 * SAT tests WC compared long SAT_CT_SAME_BC = 0; // Number of easier SAT
	 * tests BC compared long SAT_CT_SAME_FIRST = 0; long SAT_CT_SAME_OVERALL =
	 * 0; long SAT_CT_HARDER_WC = 0; // Number of easier SAT tests WC long
	 * SAT_CT_HARDER_BC = 0; // Number of easier SAT tests BC long
	 * SAT_CT_HARDER_FIRST = 0; long SAT_CT_HARDER_OVERALL = 0; long
	 * SAT_CT_AVOIDED_MONO = 0; // Number tests avoided compared to // random
	 * monolithic (same as // delegate) long SAT_CT_ADDED_MONO = 0; // Number
	 * tests added compared to random // monolithic (same as delegate)
	 * 
	 * //AnalysisUtils.p("TERROR"); //AnalysisUtils.p(er.r.getMetaData());
	 * //AnalysisUtils.pause(); long pp_mon =
	 * ExperimentUtilities.getLong(er.getReasoner() .getMetaData(),
	 * ReasonerMetaData.PP_TS.getName()); long cc_mon =
	 * ExperimentUtilities.getLong(er.getReasoner() .getMetaData(),
	 * ReasonerMetaData.CC_TS.getName()); long pco_mon =
	 * ExperimentUtilities.getLong(er.getReasoner() .getMetaData(),
	 * ReasonerMetaData.PRP_TS.getName()); long t_mon =
	 * ExperimentUtilities.getLong(er.getReasoner() .getMetaData(),
	 * ReasonerMetaData.ST_TS.getName()); long pop_mon =
	 * ExperimentUtilities.getLong(er.getReasoner() .getMetaData(),
	 * ReasonerMetaData.POP_TS.getName()); long popfin_mon =
	 * ExperimentUtilities.getLong(er.getReasoner() .getMetaData(),
	 * ReasonerMetaData.POPFIN_TS.getName());
	 * 
	 * long cmp_mon_cltime_all = (popfin_mon - pp_mon); long
	 * cmp_mon_cltime_reasoning = (pop_mon - cc_mon); long cmp_mon_cltime_st =
	 * (pop_mon - t_mon); addResult("cmp_mon_cltime_all", cmp_mon_cltime_all +
	 * ""); addResult("cmp_mon_cltime_reasoning", cmp_mon_cltime_reasoning +
	 * ""); addResult("cmp_mon_cltime_st", cmp_mon_cltime_st + "");
	 * 
	 * AnalysisUtils.p(er); Map<String, List<ExperimentSubsumptionTest>>
	 * mono_sat = getSatTestMapForMonolithicReasoner(er);
	 * AnalysisUtils.p(subtest_record.keySet().size());
	 * AnalysisUtils.p(mono_sat.keySet().size());
	 * 
	 * /* if (subtest_record.keySet().size() > 0) { // AnalysisUtils.pause(); }
	 * 
	 * AnalysisUtils.pp("ST:::"+subtest_record.keySet().size()); for (String
	 * subest : subtest_record.keySet()) { List<ExperimentSubsumptionTest> tests
	 * = subtest_record .get(subest); List<Long> testtimes = new
	 * ArrayList<Long>(); String testid = null; for (ExperimentSubsumptionTest
	 * st : tests) { if (testid == null) { testid = st.getId(); }
	 * testtimes.add(st.getDuration()); } long fastest =
	 * AnalysisUtils.getMin(testtimes); long slowest =
	 * AnalysisUtils.getMax(testtimes); long first = getFirstTestTime(tests);
	 * 
	 * //TT_SAT_UNIQUE_BC += fastest; //TT_SAT_UNIQUE_WC += slowest;
	 * //TT_SAT_UNIQUE_FIRST += first; long sum =
	 * AnalysisUtils.getSum(testtimes); //TT_SAT_RED_BC += (sum - fastest);
	 * //TT_SAT_RED_WC += (sum - slowest); //TT_SAT_RED_FIRST += (sum - first);
	 * //TT_SAT_ALL += sum; if (mono_sat.containsKey(testid)) {
	 * ExperimentSubsumptionTest st_mon = mono_sat.get(testid) .get(0); if
	 * (AnalysisUtils.approximatelyEqual(st_mon.getDuration(), fastest,
	 * SIMILARITY_THRESHOLD)) { SAT_CT_SAME_BC++; } else if
	 * (st_mon.getDuration() > fastest) { SAT_CT_EASIER_BC++; } else {
	 * SAT_CT_HARDER_BC++; } if
	 * (AnalysisUtils.approximatelyEqual(st_mon.getDuration(), slowest,
	 * SIMILARITY_THRESHOLD)) { SAT_CT_SAME_WC++; } else if
	 * (st_mon.getDuration() > slowest) { SAT_CT_EASIER_WC++; } else {
	 * SAT_CT_HARDER_WC++; } if
	 * (AnalysisUtils.approximatelyEqual(st_mon.getDuration(), first,
	 * SIMILARITY_THRESHOLD)) { SAT_CT_SAME_FIRST++; } else if
	 * (st_mon.getDuration() > first) { SAT_CT_EASIER_FIRST++; } else {
	 * SAT_CT_HARDER_FIRST++; } for (long testtime : testtimes) { if
	 * (AnalysisUtils.approximatelyEqual( st_mon.getDuration(), testtime,
	 * SIMILARITY_THRESHOLD)) { SAT_CT_SAME_OVERALL++; } else if
	 * (st_mon.getDuration() > testtime) { SAT_CT_EASIER_OVERALL++; } else {
	 * SAT_CT_HARDER_OVERALL++; } } } else { SAT_CT_ADDED_MONO++; } } long
	 * totalduration_sat_mon = 0; for (Entry<String,
	 * List<ExperimentSubsumptionTest>> st : mono_sat .entrySet()) {
	 * ExperimentSubsumptionTest st_mon = st.getValue().get(0); if
	 * (!subtest_record.containsKey(st_mon.getId())) { SAT_CT_AVOIDED_MONO++; }
	 * totalduration_sat_mon += st_mon.getDuration(); }
	 * addResult("cmp_mon_cltime_subtests", totalduration_sat_mon + "");
	 * addResult("cmp_mon_sat_ct_total", mono_sat.size() + "");
	 * addResult("cmp_mon_runid", er.getRunid() + "");
	 * addResult("sat_ct_avoided_mono", SAT_CT_AVOIDED_MONO + "");
	 * addResult("sat_ct_added_mono", SAT_CT_ADDED_MONO + "");
	 * addResult("sat_ct_easier_wc", SAT_CT_EASIER_WC + "");
	 * addResult("sat_ct_easier_bc", SAT_CT_EASIER_BC + "");
	 * addResult("sat_ct_easier_first", SAT_CT_EASIER_FIRST + "");
	 * addResult("sat_ct_easier_overall", SAT_CT_EASIER_OVERALL + "");
	 * addResult("sat_ct_harder_overall", SAT_CT_HARDER_OVERALL + "");
	 * addResult("sat_ct_same_overall", SAT_CT_SAME_OVERALL + "");
	 * addResult("sat_ct_harder_first", SAT_CT_HARDER_FIRST + "");
	 * addResult("sat_ct_harder_wc", SAT_CT_HARDER_WC + "");
	 * addResult("sat_ct_harder_bc", SAT_CT_HARDER_BC + "");
	 * addResult("sat_ct_same_wc", SAT_CT_SAME_WC + "");
	 * addResult("sat_ct_same_bc", SAT_CT_SAME_BC + "");
	 * addResult("sat_ct_same_first", SAT_CT_SAME_FIRST + "");
	 * addResult("diff_sat_ct_harder_easier_overall", (SAT_CT_EASIER_OVERALL -
	 * SAT_CT_HARDER_OVERALL) + ""); addResult("diff_sat_ct_avoided_redundant",
	 * (SAT_CT_AVOIDED_MONO - SAT_CT_RED) + ""); addResult(
	 * "cmp_cltime_all_pdiff",
	 * AnalysisUtils.percentageDifference(cmp_mon_cltime_all, cltime_all) + "");
	 * addResult( "cmp_cltime_all_pchange",
	 * AnalysisUtils.percentageChange(cmp_mon_cltime_all, cltime_all) + "");
	 * addResult("cmp_cltime_all_fchange",
	 * AnalysisUtils.foldChange(cmp_mon_cltime_all, cltime_all) + "");
	 * addResult( "cmp_cltime_reasoning_pdiff",
	 * AnalysisUtils.percentageDifference( cmp_mon_cltime_reasoning,
	 * cltime_reasoning) + ""); addResult( "cmp_cltime_reasoning_pchange",
	 * AnalysisUtils.percentageChange(cmp_mon_cltime_reasoning,
	 * cltime_reasoning) + ""); addResult( "cmp_cltime_reasoning_fchange",
	 * AnalysisUtils.foldChange(cmp_mon_cltime_reasoning, cltime_reasoning) +
	 * "");
	 * 
	 * } }
	 */
}
