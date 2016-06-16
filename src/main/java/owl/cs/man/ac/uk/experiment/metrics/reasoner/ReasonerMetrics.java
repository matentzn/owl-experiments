package owl.cs.man.ac.uk.experiment.metrics.reasoner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReasonerMetrics {

	public static enum StandardTestType {
		CONCEPT_SATISFIABILITY("satisfiability of concept '{0}'"), CONSISTENCY(
				"ABox satisfiability"), CONCEPT_SUBSUMPTION(
				"concept subsumption '{0}' => '{1}'"), OBJECT_ROLE_SATISFIABILITY(
				"satisfiability of object role '{0}'"), DATA_ROLE_SATISFIABILITY(
				"satisfiability of data role '{0}'"), OBJECT_ROLE_SUBSUMPTION(
				"object role subsumption '{0}' => '{1}'"), DATA_ROLE_SUBSUMPTION(
				"data role subsumption '{0}' => '{1}'"), INSTANCE_OF(
				"class instance '{0}'('{1}')"), OBJECT_ROLE_INSTANCE_OF(
				"object role instance '{0}'('{1}', '{2}')"), DATA_ROLE_INSTANCE_OF(
				"data role instance '{0}'('{1}', '{2}')"), ENTAILMENT(
				"entailment of '{0}'"), DOMAIN("check if {0} is domain of {1}"), RANGE(
				"check if {0} is range of {1}");

		public final String messagePattern;

		StandardTestType(String messagePattern) {
			this.messagePattern = messagePattern;
		}
	}

	public enum STType {

		SATTEST("fullsat"), LOOKUP("lookupsat"), CACHED("cached"), SORTED(
				"sorted"), MODULE("module");

		private STType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		String name;
	}

	public enum ReasonerMetaData {

		nConcepts("concepts"), nTraversalCalls("ntraversalcalls"), CONSISTENT(
				"consistent"), CLASSIFICATION_TIME("classificationtime"), CONSISTENCY_TIME(
				"consistencytime"), INFERREDONTOLOGYGEN_TIME("infontgentime"), REDUNDANTTESTS(
				"redundant_tests"), REDUNDANTTESTS_EASY("redundant_tests_easy"), REDUNDANTTESTS_HARD(
				"redundant_tests_hard"), SUBSUMPTIONTESTS("nsubtests"), SUBSUMPTIONTESTSHARD(
				"hard_subtests"), SUBSUMPTIONTESTSEASY("easy_subtests"), SUBSUMPTIONTESTSPOS(
				"pos_subtests"), SUBSUMPTIONTESTSNEG("neg_subtests"), SUBSUMPTIONTESTSNEGEASY(
				"negeasy_subtests"), SUBSUMPTIONTESTSNEGHARD("neghard_subtests"), SUBSUMPTIONTESTSPOSEASY(
				"poseasy_subtests"), SUBSUMPTIONTESTSPOSHARD("poshard_subtests"), nSubCalls(
				"nsubcalls"), SATTEST("just_sat"), PP_TS("PP_TS"), CC_TS(
				"CC_TS"), PRP_TS("PRP_TS"), ST_TS("ST_TS"), POP_TS("POP_TS"), POPFIN_TS(
				"POPFIN_TS"), DEC_TS("DEC_TS"), DECEND_TS("DECEND_TS"), MODCL_TS(
				"MODCL_TS"), CCFIN_TS("CCFIN_TS"), REASONERNAME("reasoner"), DELEGATEREASONERNAME(
				"delreasoner"), DELEGATETYPE("delreasonertype"), DELEGATEREASONERCLASS(
				"delreasoner_cl"), DELEGATEREASONERVERSION(
				"delreasoner_version"), SUPERCLASS("super"), SUBCLASS("sub"), SATTEST_START(
				"starttime"), SATTEST_END("endtime"), PRINT_TIME("print_time"), DECOMPOSITION_TYPE(
				"dec_type"), DECOMPOSITION_MODULE_TYPE("dec_module_type"), MODULAR_CLASSIFICATION_STRATEGY(
				"mod_cl_strategy"), OWL_DELEGATE_FACTORY("mod_delegate_owl"), EL_DELEGATE_FACTORY(
				"mod_delegate_el"), FACT_CC_TS("fact_ts_sync");

		private ReasonerMetaData(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		String name;
	}

	private final static Map<String, List<SubsumptionTest>> subsbyreasoner = new HashMap<String, List<SubsumptionTest>>();
	private final static Map<String, Map<ReasonerMetaData, Long>> databyreasoner = new HashMap<String, Map<ReasonerMetaData, Long>>();
	private final static Map<String, Map<String, String>> metadadatabyreasoner = new HashMap<String, Map<String, String>>();
	private final static Map<String, Long> writetime = new HashMap<String, Long>();
	private final static Map<String, Long> overheadtime = new HashMap<String, Long>();
	private final static Map<String, String> synonymid_p_r = new HashMap<String,String>();
	private final static Map<String, String> synonymid_r_p = new HashMap<String,String>();
	
	private static String currentscope;
	private static String experimentid;
	private static boolean prepared = false;
	private static boolean postprocessed = false;
	private static BufferedWriter writer;
	private static File subtestfile;
	private static File experimentdir;
	private static File ontdir;

	public static void prepare(File tmpDir, File experiment_dir, String runid) throws IOException {
		subtestfile = new File(tmpDir, System.currentTimeMillis()
				+ "subtestfile.txt");
		experimentid = runid;
		experimentdir = experiment_dir.getParentFile().getParentFile();
		ontdir=experiment_dir;
		writer = new BufferedWriter(new FileWriter(subtestfile));
		prepared = true;
	}

	private static String getCurrentScope() {
		return currentscope;
	}
	
	public static String getExperimentId() {
		return experimentid;
	}

	public static File getExperimentDirectory() {
		return experimentdir;
	}
	
	public static File getOntologyOutDirectory() {
		return ontdir;
	}
	
	private static Map<ReasonerMetaData, Long> getDataEntry(String reasonerid) {
		if (!databyreasoner.containsKey(reasonerid)) {
			databyreasoner.put(reasonerid,
					new HashMap<ReasonerMetaData, Long>());
		}
		return databyreasoner.get(reasonerid);

	}

	public static void clear() {
		synonymid_p_r.clear();
		synonymid_r_p.clear();
		metadadatabyreasoner.clear();
		databyreasoner.clear();
		subsbyreasoner.clear();
		writetime.clear();
		overheadtime.clear();
		currentscope = "";
		postprocessed = false;
		prepared = false;
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		writer = null;
		subtestfile = null;
	}

	public static void setCurrentScope(String scope) {
		currentscope = scope;
	}

	// Pellet and Hermit
	public static void incrementSubTest(String sub, String sup, STType type,
			boolean pos, long started, long finished) {
		if (!prepared) {
			return;
		}
		incrementSubTest(getCurrentScope(), sub, sup, type, pos, started,
				finished);
	}

	// FaCT++
	public static void incrementSubTest(String reasonerid, String sub,
			String sup, STType type, boolean pos, long started, long finished) {
		if (!prepared) {
			return;
		}
		if (reasonerid == null) {
			return;
		}
		StringBuilder sb = new StringBuilder(reasonerid).append(",")
				.append(sub).append(",").append(sup).append(",")
				.append(type.getName()).append(",").append(pos).append(",")
				.append(started).append(",").append(finished).append("\n");
		try {
			long s = System.nanoTime();
			writer.write(sb.toString());
			long e = System.nanoTime();
			long currwt = writetime.get(reasonerid) == null ? 0 : writetime
					.get(reasonerid);
			writetime.put(reasonerid, currwt + (e - s));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// HermiT
	public static void incrementSubTest(String string, STType sattest,
			boolean result, long start, long end, boolean hermit) {
		if (!prepared) {
			return;
		}
		if (getCurrentScope() == null) {
			return;
		}
		incrementSubTest(getCurrentScope(), string, sattest, result, start,
				end, hermit);
	}

	private static void incrementSubTest(String reasonerid, String string,
			STType type, boolean result, long started, long finished,
			boolean hermit) {
		if (!prepared) {
			return;
		}
		if (isHermiTConf(string)) {
			String[] subsup = string.replaceAll("concept subsumption '", "")
					.replaceAll("'", "").replaceAll(" => ", " ").split(" ");
			String sub = subsup[0];
			String sup = subsup[1];
			incrementSubTest(reasonerid, sub, sup, type, result, started,
					finished);
		}

	}

	private static boolean isHermiTConf(String string) {
		if (!string.contains("concept subsumption '")) {
			return false;
		}
		String template = string.substring(0, string.indexOf("'"));

		for (StandardTestType e : StandardTestType.values()) {
			if (e.messagePattern.startsWith(template)) {
				return true;
			}
		}
		return false;
	}

	// SET METADATA

	public static void setData(String reasonerid, ReasonerMetaData rm, long v) {
		long start = System.nanoTime();
		Map<ReasonerMetaData, Long> data = getDataEntry(reasonerid);
		if (data.containsKey(rm)) {
			print(rm.getName()
					+ " could not be recorded, because it is already set!");
		} else {
			data.put(rm, v);
		}
		long end = System.nanoTime();
		incrementOverheadTime(reasonerid, (end - start));
	}

	public static void setData(ReasonerMetaData rm, long v) {
		if (getCurrentScope() == null) {
			return;
		} else {
			setData(getCurrentScope(), rm, v);
		}
	}

	public static void setMetaData(String reasonerid, ReasonerMetaData rm,
			String v) {
		long start = System.nanoTime();
		Map<String, String> data = getMetaDataEntry(reasonerid);
		if (data.containsKey(rm.getName())) {
			print(rm.getName()
					+ " could not be recorded, because it is already set!");
		} else {
			data.put(rm.getName(), v);
		}
		long end = System.nanoTime();
		incrementOverheadTime(reasonerid, (end - start));
	}

	public static void setMetaData(String reasonerid, String key, String value) {
		long start = System.nanoTime();
		Map<String, String> data = getMetaDataEntry(reasonerid);
		if (data.containsKey(key)) {
			print(key + " could not be recorded, because it is already set!");
		} else {
			data.put(key, value);
		}
		long end = System.nanoTime();
		incrementOverheadTime(reasonerid, (end - start));
	}

	public static void setMetaData(String reasonerid, Map<String, String> md) {
		long start = System.nanoTime();
		Map<String, String> data = getMetaDataEntry(reasonerid);
		for (String key : md.keySet()) {
			String value = md.get(key);
			if (data.containsKey(key)) {
				print(key
						+ " could not be recorded, because it is already set!");
			} else {
				data.put(key, value);
			}
		}
		long end = System.nanoTime();
		incrementOverheadTime(reasonerid, (end - start));
	}

	public static void incrementOverheadTime(String reasonerid, long time) {
		long currot = overheadtime.get(reasonerid) == null ? 0l : overheadtime
				.get(reasonerid);
		overheadtime.put(reasonerid, (currot + time));
	}

	private static Map<String, String> getMetaDataEntry(String reasonerid) {
		if (!metadadatabyreasoner.containsKey(reasonerid)) {
			metadadatabyreasoner.put(reasonerid, new HashMap<String, String>());
		}
		return metadadatabyreasoner.get(reasonerid);
	}

	/*
	 * Get Metadata
	 */

	public static List<Map<String, String>> getClassificationMetadata() {
		List<Map<String, String>> cldat = new ArrayList<Map<String, String>>();
		Set<String> ignore = new HashSet<String>();
		for (String reasonerid : databyreasoner.keySet()) {
			if(ignore.contains(reasonerid)) {
				continue;
			}
			String alternative_reasonerid = getSynonymId(reasonerid);
			
			Map<String, String> dataout = new HashMap<String, String>();
			extractData(reasonerid,dataout);
			extractMetaData(reasonerid,dataout);
			
			if(!alternative_reasonerid.equals(reasonerid)) {
				extractData(alternative_reasonerid,dataout);
				extractMetaData(alternative_reasonerid,dataout);
				dataout.put("reasonerid", alternative_reasonerid);
				dataout.put("reasoner_synonym_id", reasonerid);
			}
			else {
				dataout.put("reasonerid", reasonerid);
			}
			
			if (writetime.containsKey(reasonerid)) {
				long wt = writetime.get(reasonerid);
				if(!alternative_reasonerid.equals(alternative_reasonerid)) {
					wt = wt + writetime.get(alternative_reasonerid);
				}
				dataout.put("print_time", wt  + "");
			}
			if (overheadtime.containsKey(reasonerid)) {
				long wt = overheadtime.get(reasonerid);
				if(!alternative_reasonerid.equals(reasonerid)) {
					if (overheadtime.containsKey(alternative_reasonerid)) {
						wt = wt + overheadtime.get(alternative_reasonerid);
					}
				}
				dataout.put("overhead_time", wt  + "");
			}
			
			ignore.add(alternative_reasonerid);
			ignore.add(reasonerid);
			
			cldat.add(dataout);
		}
		return cldat;
	}

	public static String getSynonymId(String reasonerid) {
		String alternative_reasonerid = reasonerid;
		boolean pr = false;
		boolean rp = false;
		if(synonymid_p_r.containsKey(reasonerid)) {
			alternative_reasonerid = synonymid_p_r.get(reasonerid);
			pr=true;
		}
		if(synonymid_r_p.containsKey(reasonerid)) {
			alternative_reasonerid = synonymid_r_p.get(reasonerid);
			rp=true;
		}
		if(pr&&rp) {
			throw new RuntimeException(reasonerid+" is both key and value in synonyms!");
		}
		return alternative_reasonerid;
	}

	private static void extractData(
			String reasonerid, Map<String, String> dataout) {
		if(databyreasoner.containsKey(reasonerid)) {
		Map<ReasonerMetaData, Long> data = databyreasoner.get(reasonerid);
		for (ReasonerMetaData rm : data.keySet()) {
			if(dataout.containsKey(rm.getName())) {
				if(!dataout.get(rm.getName()).equals(data.get(rm.getName()))) {
					throw new RuntimeException("Trying to extract data that was already extracted, aborting");
				}
			}
			dataout.put(rm.getName(), data.get(rm) + "");
		}
		}
	}
	
	private static void extractMetaData(
			String reasonerid, Map<String, String> dataout) {
		if (metadadatabyreasoner.containsKey(reasonerid)) {
			Map<String, String> data = metadadatabyreasoner
					.get(reasonerid);
			for (String rm : data.keySet()) {
				if(dataout.containsKey(rm)) {
					if(!dataout.get(rm).equals(data.get(rm))) {
						throw new RuntimeException("Trying to extract data that was already extracted, aborting");
					}
				}
				dataout.put(rm, data.get(rm));
			}
		}
	}

	public static List<Map<String, String>> getSubsumptionTestMetadata() {
		prepareSubtestMetaData();
		List<Map<String, String>> data = new ArrayList<Map<String, String>>();
		if (!postprocessed) {
			throw new RuntimeException("Reasoner Metadata: Postprocessing was unsuccessfull");
		}
		
		for (String reasonerid : subsbyreasoner.keySet()) {
			for (SubsumptionTest st : subsbyreasoner.get(reasonerid)) {
				String alternative_reasonerid = getSynonymId(reasonerid);
				if(!alternative_reasonerid.equals(reasonerid)) {
					st.getData().put("reasonerid", alternative_reasonerid);
					st.getData().put("reasoner_synonym_id", reasonerid);
				}
				data.add(st.getData());
			}
		}

		return data;
	}

	public static boolean hasSubsumptionMetadata() {
		prepareSubtestMetaData();
		return !subsbyreasoner.isEmpty();
	}

	private static void prepareSubtestMetaData() {
		if (postprocessed) {
			return;
		}
		try {
			writer.close();
			FileInputStream fis = new FileInputStream(subtestfile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			String line = null;
			while ((line = br.readLine()) != null) {

				String[] params = line.split(",");
				String reasonerid = params[0];
				String sub = params[1];
				String sup = params[2];
				String type = params[3];
				boolean pos = Boolean.valueOf(params[4]);
				long started = Long.valueOf(params[5]);
				long finished = Long.valueOf(params[6]);
				List<SubsumptionTest> subs = getSubEntry(reasonerid);
				SubsumptionTest subTest = new SubsumptionTest(reasonerid, sub,
						sup, type, pos);
				subTest.setTimeStarted(started);
				subTest.setTimeFinished(finished);
				subs.add(subTest);
			}
			br.close();
			postprocessed = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<SubsumptionTest> getSubEntry(String reasonerid) {
		if (!subsbyreasoner.containsKey(reasonerid)) {
			subsbyreasoner.put(reasonerid, new ArrayList<SubsumptionTest>());
		}
		return subsbyreasoner.get(reasonerid);
	}

	/*
	 * Utilities
	 */

	private static void print(String string) {
		// System.out.println(string);
	}

	/*
	 * public static void cacheSTConceptsById(String c, Integer i) { if
	 * (!prepared) { return; } conceptbyid.put(i, c); }
	 */
	public static void pause(String string) {
		System.out.println("PAUSE: " + string);
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setSynonymReasonerid(String reasonerid, String prelimid) {
		synonymid_r_p.put(prelimid,reasonerid);
		synonymid_p_r.put(reasonerid,prelimid);
	}
}
