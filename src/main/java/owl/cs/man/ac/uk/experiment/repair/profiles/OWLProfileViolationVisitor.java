/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package owl.cs.man.ac.uk.experiment.repair.profiles;

import owl.cs.man.ac.uk.experiment.repair.profiles.violations.CycleInDatatypeDefinition;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.DatatypeIRIAlsoUsedAsClassIRI;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.EmptyOneOfExpression;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.IllegalPunning;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.InsufficientAxiomOperands;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.InsufficientDataRangeOperands;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.InsufficientIndividuals;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.InsufficientObjectExpressionOperands;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.InsufficientPropertyExpressions;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.LastPropertyInChainNotInImposedRange;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.LexicalNotInLexicalSpace;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.OntologyIRINotAbsolute;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.OntologyVersionIRINotAbsolute;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfAnonymousIndividual;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfBuiltInDatatypeInDatatypeDefinition;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfDefinedDatatypeInDatatypeRestriction;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfIllegalAxiom;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfIllegalClassExpression;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfIllegalDataRange;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfIllegalFacetRestriction;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonAbsoluteIRI;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonAtomicClassExpression;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonEquivalentClassExpression;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonSimplePropertyInAsymmetricObjectPropertyAxiom;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonSimplePropertyInCardinalityRestriction;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonSimplePropertyInDisjointPropertiesAxiom;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonSimplePropertyInFunctionalPropertyAxiom;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonSimplePropertyInInverseFunctionalObjectPropertyAxiom;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonSimplePropertyInIrreflexivePropertyAxiom;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonSimplePropertyInObjectHasSelf;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonSubClassExpression;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfNonSuperClassExpression;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfObjectPropertyInverse;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfPropertyInChainCausesCycle;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfReservedVocabularyForAnnotationPropertyIRI;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfReservedVocabularyForClassIRI;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfReservedVocabularyForDataPropertyIRI;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfReservedVocabularyForDatatypeIRI;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfReservedVocabularyForIndividualIRI;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfReservedVocabularyForObjectPropertyIRI;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfReservedVocabularyForOntologyIRI;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfReservedVocabularyForVersionIRI;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfTopDataPropertyAsSubPropertyInSubPropertyAxiom;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfUndeclaredAnnotationProperty;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfUndeclaredClass;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfUndeclaredDataProperty;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfUndeclaredDatatype;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfUndeclaredObjectProperty;
import owl.cs.man.ac.uk.experiment.repair.profiles.violations.UseOfUnknownDatatype;

/** Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 03-Aug-2009 */
@SuppressWarnings("javadoc")
public interface OWLProfileViolationVisitor {
    void visit(IllegalPunning violation);

    void visit(CycleInDatatypeDefinition violation);

    void visit(UseOfBuiltInDatatypeInDatatypeDefinition violation);

    //void visit(DatatypeIRIAlsoUsedAsClassIRI violation);

    void visit(UseOfNonSimplePropertyInAsymmetricObjectPropertyAxiom violation);

    void visit(UseOfNonSimplePropertyInCardinalityRestriction violation);

    void visit(UseOfNonSimplePropertyInDisjointPropertiesAxiom violation);

    void visit(UseOfNonSimplePropertyInFunctionalPropertyAxiom violation);

    void visit(UseOfNonSimplePropertyInInverseFunctionalObjectPropertyAxiom violation);

    void visit(UseOfNonSimplePropertyInIrreflexivePropertyAxiom violation);

    void visit(UseOfNonSimplePropertyInObjectHasSelf violation);

    void visit(UseOfPropertyInChainCausesCycle violation);

    void visit(UseOfReservedVocabularyForAnnotationPropertyIRI violation);

    void visit(UseOfReservedVocabularyForClassIRI violation);

    void visit(UseOfReservedVocabularyForDataPropertyIRI violation);

    void visit(UseOfReservedVocabularyForIndividualIRI violation);
    
    void visit(UseOfReservedVocabularyForDatatypeIRI violation);

    void visit(UseOfReservedVocabularyForObjectPropertyIRI violation);

    void visit(UseOfReservedVocabularyForOntologyIRI violation);

    void visit(UseOfReservedVocabularyForVersionIRI violation);

    void visit(UseOfTopDataPropertyAsSubPropertyInSubPropertyAxiom violation);

    void visit(UseOfUndeclaredAnnotationProperty violation);

    void visit(UseOfUndeclaredClass violation);

    void visit(UseOfUndeclaredDataProperty violation);

    void visit(UseOfUndeclaredDatatype violation);

    void visit(UseOfUndeclaredObjectProperty violation);

    void visit(InsufficientPropertyExpressions violation);

    void visit(InsufficientIndividuals violation);

    void visit(InsufficientObjectExpressionOperands violation);

    void visit(InsufficientDataRangeOperands violation);
    
    void visit(InsufficientAxiomOperands violation);
    
    void visit(EmptyOneOfExpression violation);

    void visit(LastPropertyInChainNotInImposedRange lastPropertyInChainNotInImposedRange);

    void visit(OntologyIRINotAbsolute ontologyIRINotAbsolute);

            void
            visit(UseOfDefinedDatatypeInDatatypeRestriction useOfDefinedDatatypeInDatatypeRestriction);

    void visit(UseOfIllegalClassExpression useOfIllegalClassExpression);

    void visit(UseOfIllegalDataRange useOfIllegalDataRange);

    void visit(UseOfUnknownDatatype useOfUnknownDatatype);

    void visit(UseOfObjectPropertyInverse useOfObjectPropertyInverse);

    void visit(UseOfNonSuperClassExpression useOfNonSuperClassExpression);

    void visit(UseOfNonSubClassExpression useOfNonSubClassExpression);

    void visit(UseOfNonEquivalentClassExpression useOfNonEquivalentClassExpression);

    void visit(UseOfNonAtomicClassExpression useOfNonAtomicClassExpression);

    void visit(LexicalNotInLexicalSpace lexicalNotInLexicalSpace);

    void visit(OntologyVersionIRINotAbsolute ontologyVersionIRINotAbsolute);

    void visit(UseOfAnonymousIndividual useOfAnonymousIndividual);

    void visit(UseOfIllegalAxiom useOfIllegalAxiom);

    void visit(UseOfIllegalFacetRestriction useOfIllegalFacetRestriction);

    void visit(UseOfNonAbsoluteIRI useOfNonAbsoluteIRI);
}
