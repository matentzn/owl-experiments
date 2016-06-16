package owl.cs.man.ac.uk.experiment.ontology;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.io.File;
import java.lang.reflect.Field;

import org.semanticweb.owlapi.model.OWLClass;

import owl.cs.man.ac.uk.experiment.csv.CSVUtilities;

public class MetricsLabels {

	public final static String ABOX_SIZE_INCL = "abox_size_incl";
	public final static String ABOX_SIZE = "abox_size";
	public final static String TBOX_SIZE_INCL = "tbox_size_incl";
	public final static String TBOX_SIZE = "tbox_size";
	public final static String TBOXRBOX_SIZE_INCL = "tboxrbox_size_incl";
	public final static String TBOXRBOX_SIZE = "tboxrbox_size";
	public final static String SIGNATURE_SIZE = "signature_size";
	public final static String SIGNATURE_SIZE_INCL = "signature_size_incl";
	public final static String ANNOTATION_PROP_COUNT = "annotation_property_count";
	public final static String AVG_ASSERT_N_SUBCLASS = "avg_assert_n_subclass";
	public final static String AVG_ASSERT_N_SUBCLASS_INCL = "avg_assert_n_subclass_incl";
	public final static String AVG_ASSERT_N_SUPERCLASS = "avg_assert_n_superclass";
	public final static String AVG_ASSERT_N_SUPERCLASS_INCL= "avg_assert_n_superclass_incl";
	public final static String AVG_INSTANCE_PER_CLASS = "avg_instance_per_class";
	public final static String AVG_INSTANCE_PER_CLASS_INCL = "avg_instance_per_class_incl";
	public final static String AXIOM_COUNT = "axiom_count";
	public final static String AXIOM_COUNT_INCL = "axiom_count_incl";
	public final static String AXIOMTYPE_COUNT = "axiom_type_count";
	public final static String AXIOMTYPE_COUNT_INCL = "axiom_type_count_incl";
	public final static String CLASS_COUNT = "class_count";
	public final static String CLASS_COUNT_INCL = "class_count_incl";
	public final static String CLASS_SGL_SUBCLASS_COUNT = "class_sgl_subcl_count";
	public final static String CLASS_SGL_SUBCLASS_COUNT_INCL = "class_sgl_subcl_count_incl";
	public final static String CONSTRUCTS = "constructs";
	public final static String CONSTRUCTS_INCL = "constructs_incl";
	public final static String DATAPROPERTY_COUNT = "dataproperty_count";
	public final static String DATAPROPERTY_COUNT_INCL = "dataproperty_count_incl";
	public final static String DATATYPE_COUNT = "datatypes_count";
	public final static String DATATYPE_COUNT_INCL = "datatypes_count_incl";
	public final static String DOMAINEXTENSTION = "domain_extension";
	public final static String EXPRESSIVITY = "expressivity";
	public final static String EXPRESSIVITY_INCL = "expressivity_incl";
	public final static String DOMAIN = "domain";
	public final static String FILEEXTENSION = "fileextension";
	public final static String GCI_COUNT = "gci_count";
	public final static String GCI_COUNT_INCL = "gci_count_incl";
	public final static String GCI_HIDDEN_COUNT = "gci_hidden_count";
	public final static String GCI_HIDDEN_COUNT_INCL = "gci_hidden_count_incl";
	public final static String INDIVIDUAL_COUNT = "indiv_count";
	public final static String INDIVIDUAL_COUNT_INCL = "indiv_count_incl";
	public final static String LOGICAL_AXIOM_COUNT = "logical_axiom_count";
	public final static String LOGICAL_AXIOM_COUNT_INCL = "logical_axiom_count_incl";
	public final static String AXIOM_COMPLEXRHS_COUNT = "ax_complexrhs_ct";
	public final static String AXIOM_COMPLEXRHS_COUNT_INCL = "ax_complexrhs_ct_incl";
	public final static String AVG_SIZE_COMPLEXRHS = "avg_size_complexrhs";
	public final static String AVG_SIZE_COMPLEXRHS_INCL = "avg_size_complexrhs_incl";
	public final static String MAX_NUM_NAMED_SUPERCLASS = "max_num_named_supercl";
	public final static String MAX_NUM_NAMED_SUPERCLASS_INCL = "max_num_named_supercl_incl";
	public final static String MULTI_INHERITANCE_COUNT = "multi_inheritance_count";
	public final static String MULTI_INHERITANCE_COUNT_INCL = "multi_inheritence_count_incl";
	public final static String MISSING_INPORTS = "missing_imports";
	public final static String ONTOLOGY_ID = "ontology_id";
	public final static String ONTOLOGY_ID_SCHEME = "ontology_id_prefix";
	public final static String OBJPROPERTY_COUNT = "obj_property_count";
	public final static String OBJPROPERTY_COUNT_INCL = "obj_property_count_incl";
	public final static String RBOX_SIZE = "rbox_size";
	public final static String RBOX_SIZE_INCL = "rbox_size_incl";
	public final static String REF_CLASS_COUNT = "ref_class_count";
	public final static String REF_CLASS_COUNT_INCL = "ref_class_count_incl";
	public final static String REF_DATAPROP_COUNT = "ref_dataprop_count";
	public final static String REF_DATAPROP_COUNT_INCL = "ref_dataprop_count_incl";
	public final static String REF_INDIV_COUNT = "ref_indiv_count";
	public final static String REF_INDIV_COUNT_INCL = "ref_indiv_count_incl";
	public final static String REF_OBJPROP_COUNT = "ref_objprop_count";
	public final static String REF_OBJPROP_COUNT_INCL = "ref_objprop_count_incl";
	public final static String SYNTAX = "syntax";
	public final static String UNDECLARED_ENTITY_COUNT = "undecl_entity_count";
	public final static String UNDECLARED_ENTITY_COUNT_INCL = "undecl_entity_count_incl";
	public final static String VALID_IMPORTS = "valid_imports";
	public final static String BOOL_PROFILE_OWL2 = "owl2";
	public final static String BOOL_PROFILE_OWL2_DL = "owl2_dl";
	public final static String BOOL_PROFILE_OWL2_EL = "owl2_el";
	public final static String BOOL_PROFILE_OWL2_QL = "owl2_ql";
	public final static String BOOL_PROFILE_OWL2_RL = "owl2_rl";
	public final static String VIOLATION_PROFILE_OWL2_DL = "owl2dl_profile_violation";
	public final static String BOOL_PROFILE_RDFS = "rdfs";
	
	public final static String CLASSIFICATION_TIME = "classificationtime";
	public final static String CREATEREASONER_TIME = "createreasonertime";
	public final static String PREPARE_JUSTIFICATIONEXTRACTION_TIME = "pp_justextr_time";
	public final static String JUSTIFICATION_EXTRACTION_TIME = "just_extract_time";
	public final static String EXTRACTMODULE_TIME = "extractmoduletime";
	public final static String EMPTYSIGNATUREMODULE_SIZE = "ems_size";
	public final static String ONTOLOGYLOAD_TIME = "ontologyloadtime";
	public final static String DISPOSEREASONER_TIME = "disposereasonertime";
	public final static String CONSISTENCY_TIME = "consistencytime";
	public final static String INFERREDONTOLOGYGEN_TIME = "infontgentime";
	public final static String CONSISTENT = "consistent";
	public final static String UNSATISFIABLECLASSES_COUNT = "unsatisfiable_class_count";
	public final static String UNSATISFIABLECLASSES = "unsatisfiable_classes";
	public final static String INFERRED_SUBSUMPTIONS_COUNT = "inferred_subsumption_count";
	public final static String ERROR_MESSAGE_REASONING = "reasoner_error_message";
	public final static String INFERRED_ONLY_ENTAILMENT_COUNT = "inferred_only_entailment_count";
	public final static String DATATYPES = "datatypes_builtin";
	public final static String DATATYPES_INCL = "datatypes_builtin_incl";
	public final static String MAX_AXIOMLENGTH = "max_axiomlength";
	public final static String MAX_AXIOMLENGTH_INCL = "max_axiomlength_incl";
	public final static String DATATYPES_NOT_BUILT_IN = "datatypes_not_built_in";
	public final static String DATATYPES_NOT_BUILT_IN_INCL = "datatypes_not_built_in_incl";
	public final static String DATATYPE_AXIOMCOUNT = "datatypes_axiomcount";
	public final static String DATATYPE_AXIOMCOUNT_INCL = "datatypes_axiomcount_incl";
	public final static String OWLAPILOADEXCEPTION = "owlapiloadexception";
	public final static String UNSUCCESSFULREASONINGEXCEPTION = "reasoner_exception";
	public final static String COHERENT = "coherent";
	public final static String REASONERNAME = "reasoner";
	public final static String REASONERNAME_CLASS = "reasoner_class";
	public final static String REASONER_JAR = "reasoner_jar";
	public final static String REASONERVERSION = "reasoner_version";
	public final static String REASONER_TIMEOUT = "reasoner_timeout";
	public final static String DELEGATE_REASONER_CT = "delreasoner_ct";
	public final static String EL_DELEGATE_REASONER_CT = "el_delreasoner_ct";
	public final static String OWL_DELEGATE_REASONER_CT = "owl_delreasoner_ct";
	public final static String FILENAME = "filename";
	public final static String FILEPATH = "filepath";
	public final static String FILESIZE = "filesize";
	public final static String INF_HIER_DIR = "inf_hier_directory";
	public final static String AD_DURATION = "ad_duration";
	public final static String AD_ATOM_COUNT = "atom_ct";
	public final static String AD_MAX_ATOMS_COUNT = "max_atom_ct";
	public final static String AD_GRAPH_DEPTH = "ad_graph_depth";
	public final static String AD_MIN_ATOMS_COUNT = "min_atom_ct";
	public final static String AD_MINMAX_ATOMS_COUNT = "minmax_atom_ct";
	public final static String AD_MIDDLE_ATOMS_COUNT = "middle_atom_ct";
	public final static String AD_CONNECTED_COMPONENTS_COUNT = "connect_comp_ct";
	public final static String AD_EDGE_COUNT = "connect_comp_ct";
	public final static String AD_TYPE_PARAM = "ad_type_param";
	public final static String AD_MODULE_TYPE = "ad_module_type";
	public final static String AD_IMPLEMENTATION_CLASS = "ad_implementation";
	public final static String JAVA_EXCEPTION = "exception";
	public final static String JAVA_EXCEPTION_CAUSE = "exception_cause";
	public final static String EXPERIMENT_ID = "experiment_id";
	public final static String EXPERIMENT_VERSION = "experiment_version";
	public final static String EXPERIMENT_DATE = "experiment_date";
	public final static String JAVA_EXCEPTION_MESSAGE = "exception_message";
	public final static String GRAPH_TRAVERSE_MODE = "graph_traverse";
	public final static String LOGICAL_AXIOM_COUNT_REASONER = "logical_axiom_ct_reasoner";
	public final static String AD_CLTIME_PER_ATOM = "cltime_atoms";
	public final static String COMP_OVERHEAD = "comp_overhead";
	public final static String INFERRED_TAXONOMY_AXIOM_COUNT = "inf_ax_ct";
	public final static String INCREMENTAL_REASONER_NR_CLASSIFY_CALLS = "incr_reas_clfy_ct";
	public final static String ATOM_LABEL = "atom_label";
	public final static String ATOM_AXIOMS = "atom_axioms";
	public final static String ATOM_AXIOM_COUNT = "atom_ax_ct";
	public final static String ATOM_LABEL_SIZE = "atom_label_size";
	public final static String SUBSUMPTIONS_ATOMIC_POSSIBLE = "subs_atomic_possible";
	public final static String SUBSUMPTIONS_ATOMIC_AVOIDED = "subs_atomic_avoid_theory";
	public final static String SUBSUMPTIONS_ATOMIC_AVOIDED_REASONER = "subs_atomic_avoid_reasoner";
	public final static String SUBSUMPTIONS_ATOMIC_REDUNDANT = "subs_atomic_redundant";
	public final static String SUBSUMPTIONS_ATOMIC_REASONER = "subs_atomic_reasoner";
	public final static String SIGNATURE_NO_URIS = "signature_w_o_iris";
	public final static String CLASSIFICATION_TOPIC = "classification_topic";
	public final static String CLASSIFICATION_SCIENCE = "classification_science";
	public final static String CLASSIFICATION_NEWS = "classification_news";
	public final static String CLASSIFICATION_BUSINESS = "classification_business";
	public final static String CLASSIFICATION_WEBPAGECONTENT = "classification_webcontent";
	public final static String CLASSIFICATION_HEALTH = "classification_health";
	public final static String CLASSIFICATION_COMPUTER = "classification_computer";
	public final static String RULE_CT = "rule_ct";
	public final static String RULE_CT_INCL = "rule_ct_incl";
	public final static String TOP_TBOX_AXIOM_TYPE = "top_tbox_axtype";
	public final static String TOP_ABOX_AXIOM_TYPE = "top_abox_axtype";
	public final static String TOP_DATATYPE = "top_datatype";
	public final static String TOP_CLASS_RHS = "top_class_rhs";
	public final static String MAX_CLASSEXPRESSION_NESTING = "max_clexp_nesting";
	public final static String INF_CLASSHIERARCHY_SHAPE = "inf_hier_shape";
	public final static String INF_CLASSHIERARCHY_DEPTH = "inf_hier_depth";
	public final static String AXIOM_TYPES = "axiom_types";
	public final static String AXIOM_TYPES_INCL = "axiom_types_incl";
	public final static String TAUTOLOGYCOUNT = "taut_count";
	public final static String TAUTOLOGYCOUNT_INCL = "taut_count_incl";
	public final static String MINIMAL_ATOM_COUNT = "min_atom_ct";
	public final static String MAXIMAL_ATOM_COUNT = "max_atom_ct";
	public final static String ATOM_COUNT = "atom_ct";
	public final static String ATOM_DEPENDENCIES = "atom_dependencies_ct";
	public final static String ATOM_DEPENDANTS = "atom_dependents_ct";
	public final static String AD_TAUTOLOGY_COUNT = "ad_tauto_ct";
	public final static String CYCLE = "cycle";
	public final static String CYCLE_INCL = "cycle_incl";
	public final static String EXPORT_EXCEPTION = "export_exception";
	public final static String FIXED_VIOLATIONS = "fixed_profile_violations";
	public final static String STRIPPED_AXIOMTYPES = "stripped_axiomtypes";
	
	public final static String STARTPROCESS_TS = "startprocess_ts";
	public final static String OWLAPILOAD_TS = "owlapiload_ts";
	public final static String CREATEREASONER_TS = "createreasoner_ts";
	public final static String CLASSIFICATION_TS = "classification_ts";
	public final static String DISPOSE_TS = "dispose_ts";
	public final static String FINISH_TS = "finishprocess_ts";	
	
	public final static String PREPROCESSING_TS = "preprocessing_ts";
	public final static String CONSISTENCYCHECK_TS = "consistencycheck_ts";
	public final static String PREREASONINGOPTIMISATION_TS = "prereasoningoptimisation_ts";
	public final static String TRAVERSAL_TS = "traversal_ts";
	public final static String POSTPROCESSING_TS = "postprocessing_ts";
	public final static String CLASSIFICATION_CORRECT = "classification_correct";
	public final static String REASONING_PREPROCESSING_TIME = "reason_pp_dur";
	public final static String REASONING_MODULARREASONING_TIME = "reason_mmcl_dur";
	public final static String REASONING_CONSISTENCY_TIME = "reason_cc_dur";
	public final static String REASONING_PREREASONINGOPTIMISATION_TIME = "reason_pro_dur";
	public final static String REASONING_TRAVERSALPHASE_TIME = "reason_trav_dur";
	public final static String REASONING_POSTROCESSING_TIME = "reason_pop_dur";
	public final static String REASONING_DECOMPOSITION_TIME = "reason_dec_dur";
	public final static String REASONER_ID = "reasonerid";
	public final static String REASONER_DISPOSE_TIME = "reasoner_disposetime";
	public final static String RUNID = "experiment_runid";
	public final static String ANNOTATIONS_COUNT = "anno_ct";
	public final static String DATATYPE_BUILTIN_COUNT_INCL = "dt_builtin_ct_incl";
	public final static String DATATYPE_BUILTIN_COUNT = "dt_builtin_ct";
	public final static String DATATYPE_NOTBUILTIN_COUNT_INCL = "dt_notbuiltin_ct_incl";
	public final static String DATATYPE_NOTBUILTIN_COUNT = "dt_notbuiltin_ct";
	
	public static final String PARAM_ONTFILE = "param_ontfile";
	public static final String PARAM_OUTDIR = "param_outdir";
	public static final String PARAM_REASONER = "param_reasoner";
	public static final String PARAM_REASONERTIMEOUT = "param_reasonertimeout";
	public static final String PARAM_GATHERSAT = "param_gathersat";
	public static final String PARAM_GATHERONTOLOGYSTATS = "param_gatherontstat";
	public static final String PARAM_DISPATCHDL = "param_dispatchdl";
	public static final String PARAM_CHECKCORRECTNESS = "param_checkcorrectness";
	public static final String PARAM_SAMPLESIZE = "param_samplesize";
	public static final String PEAK_MEMORY_USAGE = "peak_memory_usage";
	public static final String TBOX_CONTAINS_NOMINALS = "tbox_nominals";
	public static final String TBOX_CONTAINS_NOMINALS_INCL = "tbox_nominals_incl";
	public static final String ABOX_CONTAINS_NOMINALS = "abox_nominals";
	public static final String ABOX_CONTAINS_NOMINALS_INCL = "abox_nominals_incl";
	public static final String MOST_FRQUENTLY_USED_CONCEPT = "most_freq_concept";
	
	public static String createSpaceSeperatedStringFromSet(Set<String> set) {
		StringBuilder builder = new StringBuilder();
		for(String s:set) {
			builder.append(s);
			builder.append(" ");
		}
		return builder.toString();
	}
	
	public static String createSpaceSeperatedStringFromOWLClassSet(
			Set<OWLClass> set) {
		StringBuilder builder = new StringBuilder();
		for(OWLClass s:set) {
			builder.append(s.toString());
			builder.append(" ");
		}
		return builder.toString();
	}
	
	public static String createSpaceSeperatedStringFromMap(Map<String, Integer> map) {
		StringBuilder builder = new StringBuilder();
		Iterator<Entry<String,Integer>> it = map.entrySet().iterator();
	    
		while (it.hasNext()) {
	        Map.Entry<String,Integer> pairs = (Map.Entry<String,Integer>)it.next();
	       
	        builder.append(pairs.getKey());
	        builder.append(":");
	        builder.append(pairs.getValue());
			builder.append(" ");
	    }
		return builder.toString();
	}	
	
	public static void main(String[] args) {
		System.out.println();
		CSVUtilities.writeCSVData(new File("D://codes.csv"), getLabels(), false);
	}
	
	public static Map<String,String> getLabels() {
		 Map<String,String> data = new HashMap<String,String>();
		 	for (Field field : MetricsLabels.class.getDeclaredFields()) {
	            
	            try {
	            	String name = field.getName();
	            	String value = field.get(MetricsLabels.class).toString();
	            	data.put(name, value);
	                
	            } catch (IllegalArgumentException e) {
	            	e.printStackTrace();
	            } catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		 return data;
	}
}
