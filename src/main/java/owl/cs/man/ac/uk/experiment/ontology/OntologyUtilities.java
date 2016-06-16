package owl.cs.man.ac.uk.experiment.ontology;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owl.cs.man.ac.uk.experiment.repair.profiles.*;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.*;
import owl.cs.man.ac.uk.experiment.dataset.OntologySerialiser;

public class OntologyUtilities {
	
	public static Set<Class<? extends OWLProfileViolation>> permittedViolationsForRepair = preparePermittedViolations();
	
	private static Set<Class<? extends OWLProfileViolation>> preparePermittedViolations() {
		Set<Class<? extends OWLProfileViolation>> permittedViolationsForRepair = new HashSet<Class<? extends OWLProfileViolation>>();
		//permittedViolationsForRepair.add(EmptyOneOfAxiom.class);
		permittedViolationsForRepair.add(InsufficientIndividuals.class);
		permittedViolationsForRepair.add(InsufficientPropertyExpressions.class);
		permittedViolationsForRepair.add(InsufficientAxiomOperands.class);
		permittedViolationsForRepair.add(InsufficientObjectExpressionOperands.class);
		permittedViolationsForRepair.add(OntologyIRINotAbsolute.class);
		permittedViolationsForRepair.add(OntologyVersionIRINotAbsolute.class);
		//permittedViolationsForRepair.add(UseOfNonAbsoluteIRI.class);
		permittedViolationsForRepair.add(UseOfUndeclaredAnnotationProperty.class);
		permittedViolationsForRepair.add(UseOfUndeclaredClass.class);
		permittedViolationsForRepair.add(UseOfUndeclaredDataProperty.class);
		permittedViolationsForRepair.add(UseOfUndeclaredDatatype.class);
		permittedViolationsForRepair.add(UseOfUndeclaredObjectProperty.class);
		//permittedViolationsForRepair.add(CycleInDatatypeDefinition.class);
		//permittedViolationsForRepair.add(DatatypeIRIAlsoUsedAsClassIRI.class);
		//permittedViolationsForRepair.add(IllegalPunning.class);
		//permittedViolationsForRepair.add(InsufficientOperands.class);
		//permittedViolationsForRepair.add(LastPropertyInChainNotInImposedRange.class);
		//permittedViolationsForRepair.add(LexicalNotInLexicalSpace.class);
		//permittedViolationsForRepair.add(UseOfAnonymousIndividual.class);
		//permittedViolationsForRepair.add(UseOfBuiltInDatatypeInDatatypeDefinition.class);
		//permittedViolationsForRepair.add(UseOfDataOneOfWithMultipleLiterals.class);
		//permittedViolationsForRepair.add(UseOfDefinedDatatypeInDatatypeRestriction.class);
		//permittedViolationsForRepair.add(UseOfIllegalAxiom.class);
		//permittedViolationsForRepair.add(UseOfIllegalClassExpression.class);
		//permittedViolationsForRepair.add(UseOfIllegalDataRange.class);
		//permittedViolationsForRepair.add(UseOfIllegalFacetRestriction.class);
		//permittedViolationsForRepair.add(UseOfNonAbsoluteIRI.class);
		//permittedViolationsForRepair.add(UseOfNonAtomicClassExpression.class);
		//permittedViolationsForRepair.add(UseOfNonEquivalentClassExpression.class);
		//permittedViolationsForRepair.add(UseOfNonSimplePropertyInAsymmetricObjectPropertyAxiom.class);
		//permittedViolationsForRepair.add(UseOfNonSimplePropertyInCardinalityRestriction.class);
		//permittedViolationsForRepair.add(UseOfNonSimplePropertyInDisjointPropertiesAxiom.class);
		//permittedViolationsForRepair.add(UseOfNonSimplePropertyInFunctionalPropertyAxiom.class);
		//permittedViolationsForRepair.add(UseOfNonSimplePropertyInInverseFunctionalObjectPropertyAxiom.class);
		//permittedViolationsForRepair.add(UseOfNonSimplePropertyInIrreflexivePropertyAxiom.class);
		//permittedViolationsForRepair.add(UseOfNonSimplePropertyInObjectHasSelf.class);
		//permittedViolationsForRepair.add(UseOfNonSubClassExpression.class);
		//permittedViolationsForRepair.add(UseOfNonSuperClassExpression.class);
		//permittedViolationsForRepair.add(UseOfObjectOneOfWithMultipleIndividuals.class);
		//permittedViolationsForRepair.add(UseOfObjectPropertyInverse.class);
		//permittedViolationsForRepair.add(UseOfPropertyInChainCausesCycle.class);
		//permittedViolationsForRepair.add(UseOfReservedVocabularyForAnnotationPropertyIRI.class);
		//permittedViolationsForRepair.add(UseOfReservedVocabularyForClassIRI.class);
		//permittedViolationsForRepair.add(UseOfReservedVocabularyForDataPropertyIRI.class);
		//permittedViolationsForRepair.add(UseOfReservedVocabularyForIndividualIRI.class);
		//permittedViolationsForRepair.add(UseOfReservedVocabularyForObjectPropertyIRI.class);		
		//permittedViolationsForRepair.add(UseOfTopDataPropertyAsSubPropertyInSubPropertyAxiom.class);		
		//permittedViolationsForRepair.add(UseOfUnknownDatatype.class);
		return permittedViolationsForRepair;
	}
	
	public static <T> String createSpaceSeperatedStringFromSet(Set<T> set) {
		StringBuilder builder = new StringBuilder();
		for(T s:set) {
			builder.append(s.toString());
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

	public static Set<OWLProfileViolation> repair(OWLOntology ontology, OWLOntologyManager manager) {
		Set<OWLProfileViolation> fixed = repairOntology(ontology, new OWL2Profile());
		fixed.addAll(repairOntology(ontology, new OWL2DLProfile()));
		return fixed;
	}
	
	public static Map<String,Set<OWLProfileViolation>> dlify(OWLOntology ontology) {
		Map<String,Set<OWLProfileViolation>> map = new HashMap<String,Set<OWLProfileViolation>>();
		Set<OWLProfileViolation> fixed1 = fullyRepairOntology(ontology, new OWL2Profile());
		fixed1.addAll(fullyRepairOntology(ontology, new OWL2DLProfile()));
		Set<OWLProfileViolation> fixed2 = fullyRepairOntology(ontology, new OWL2Profile());
		fixed2.addAll(fullyRepairOntology(ontology, new OWL2DLProfile()));
		map.put("fixed_round1", fixed1);
		map.put("fixed_round2", fixed2);
		return map;
	}
	
	private static Set<OWLProfileViolation> fullyRepairOntology(OWLOntology o, OWLProfile profile) {
		Set<OWLProfileViolation> fixedViolations = new HashSet<OWLProfileViolation>();
		List<OWLProfileViolation> violations = profile.checkOntology(o).getViolations();

		for (OWLProfileViolation violation : violations) {
			o.getOWLOntologyManager().applyChanges(violation.repair());
			fixedViolations.add(violation);
		}
		return fixedViolations;
	}
	
	private static Set<OWLProfileViolation> repairOntology(OWLOntology o, OWLProfile profile) {
		Set<OWLProfileViolation> fixedViolations = new HashSet<OWLProfileViolation>();
		List<OWLProfileViolation> violations = profile.checkOntology(o).getViolations();

		for (OWLProfileViolation violation : violations) {
			if(permittedViolationsForRepair.contains(violation.getClass())) {
				// in case of undeclaredEntity, check whether reserved vocabulary!
				o.getOWLOntologyManager().applyChanges(violation.repair());
				fixedViolations.add(violation);
			}
		}
		return fixedViolations;
	}

	public static Set<OWLProfileViolation> repair(OWLOntology o) {
		return repair(o,o.getOWLOntologyManager());		
	}

	public static OWLOntology saveOntologyMergedOWLXML(File out, OWLOntology origOntology, String id) {
		try {
			OWLOntology mergedOntology = OntologySerialiser.mergeImports(origOntology,"corpus-ontology-" + id);
			OntologyUtilities.repair(mergedOntology, mergedOntology.getOWLOntologyManager());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out));
			mergedOntology.getOWLOntologyManager().saveOntology(mergedOntology, new OWLXMLOntologyFormat(), bos);
			bos.close();
			System.out.println("    ... merge successful.");
			return mergedOntology;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

}
