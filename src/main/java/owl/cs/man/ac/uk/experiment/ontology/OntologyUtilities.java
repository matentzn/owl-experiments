package owl.cs.man.ac.uk.experiment.ontology;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2Profile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.profiles.violations.OntologyVersionIRINotAbsolute;
import org.semanticweb.owlapi.profiles.violations.UseOfUndeclaredAnnotationProperty;
import org.semanticweb.owlapi.profiles.violations.UseOfUndeclaredClass;
import org.semanticweb.owlapi.profiles.violations.UseOfUndeclaredDataProperty;
import org.semanticweb.owlapi.profiles.violations.UseOfUndeclaredDatatype;
import org.semanticweb.owlapi.profiles.violations.UseOfUndeclaredObjectProperty;

import com.google.common.base.Optional;

import owl.cs.man.ac.uk.experiment.dataset.OntologySerialiser;


public class OntologyUtilities {
	
	public static Set<Class<? extends OWLProfileViolation>> permittedViolationsForRepair = preparePermittedViolations();
	
	private static Set<Class<? extends OWLProfileViolation>> preparePermittedViolations() {
		Set<Class<? extends OWLProfileViolation>> permittedViolationsForRepair = new HashSet<Class<? extends OWLProfileViolation>>();
		//permittedViolationsForRepair.add(EmptyOneOfAxiom.class);
		//permittedViolationsForRepair.add(InsufficientIndividuals.class);
		//permittedViolationsForRepair.add(InsufficientPropertyExpressions.class);
		//permittedViolationsForRepair.add(InsufficientAxiomOperands.class);
		//permittedViolationsForRepair.add(InsufficientObjectExpressionOperands.class);
		
		//permittedViolationsForRepair.add(OntologyIRINotAbsolute.class);
		//permittedViolationsForRepair.add(OntologyVersionIRINotAbsolute.class);
		//permittedViolationsForRepair.add(UseOfNonAbsoluteIRI.class);
		//permittedViolationsForRepair.add(UseOfUndeclaredAnnotationProperty.class);
		//permittedViolationsForRepair.add(UseOfUndeclaredClass.class);
		//permittedViolationsForRepair.add(UseOfUndeclaredDataProperty.class);
		//permittedViolationsForRepair.add(UseOfUndeclaredDatatype.class);
		//permittedViolationsForRepair.add(UseOfUndeclaredObjectProperty.class);
		
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
	
	private static Set<OWLProfileViolation> fullyRepairOntology(OWLOntology o, OWLProfile owl2Profile) {
		Set<OWLProfileViolation> fixedViolations = new HashSet<OWLProfileViolation>();
		List<OWLProfileViolation> violations = owl2Profile.checkOntology(o).getViolations();

		for (OWLProfileViolation violation : violations) {
			o.getOWLOntologyManager().applyChanges(violation.repair());
			fixedViolations.add(violation);
		}
		return fixedViolations;
	}
	
	private static Set<OWLProfileViolation> repairOntology(OWLOntology o, OWLProfile owl2Profile) {
		Set<OWLProfileViolation> fixedViolations = new HashSet<OWLProfileViolation>();
		List<OWLProfileViolation> violations = owl2Profile.checkOntology(o).getViolations();

		for (OWLProfileViolation violation : violations) {
			if(permittedViolationsForRepair.contains(violation.getClass())) {
				// in case of undeclaredEntity, check whether reserved vocabulary!
				//System.out.println(violation.getClass());
				o.getOWLOntologyManager().applyChanges(violation.repair());
				fixedViolations.add(violation);
			}
			else if(violation instanceof OntologyVersionIRINotAbsolute) {
				o.getOWLOntologyManager().applyChanges(repair(violation,o));
				fixedViolations.add(violation);
			}
		}
		return fixedViolations;
	}

	private static List<? extends OWLOntologyChange> repair(OWLProfileViolation violation, OWLOntology ontology) {
		List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
		if(violation instanceof OntologyVersionIRINotAbsolute) {
			changes.add(new SetOntologyID(ontology, new OWLOntologyID(Optional.of(IRI.create("urn:ontology#"+UUID.randomUUID())),
		            Optional.of(IRI.create("urn:ontology#"+UUID.randomUUID())))));
		}
		return changes;
	}

	public static Set<OWLProfileViolation> repair(OWLOntology o) {
		return repair(o,o.getOWLOntologyManager());		
	}

	public static OWLOntology saveOntologyMergedOWLXML(File out, OWLOntology origOntology, String id) {
		try {
			OWLOntology mergedOntology = OntologySerialiser.mergeImports(origOntology,"corpus-ontology-" + id);
			OntologyUtilities.repair(mergedOntology, mergedOntology.getOWLOntologyManager());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out));
			mergedOntology.getOWLOntologyManager().saveOntology(mergedOntology, new OWLXMLDocumentFormat(), bos);
			bos.close();
			System.out.println("    ... merge successful.");
			return mergedOntology;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

}
