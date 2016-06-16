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

import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;

/** Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 03-Aug-2009 Describes a violation of an OWLProfile by an axiom.
 * Ultimately, there may be part of the axiom that violates the profile rather
 * than the complete axiom. */
public abstract class OWLProfileViolation {
    protected final OWLOntology ontology;
    protected final OWLDataFactory df;
    protected final OWLAxiom axiom;

    /** @param ontology
     *            the ontology with the violation
     * @param axiom
     *            the axiom with the violation */
    public OWLProfileViolation(OWLOntology ontology, OWLAxiom axiom) {
        this.axiom = axiom;
        this.ontology = ontology;
        df = ontology.getOWLOntologyManager().getOWLDataFactory();
    }

    /** @return the offending axiom */
    public OWLAxiom getAxiom() {
        return axiom;
    }

    /** @return a set of changes to fix the violation - it might be just an axiom
     *         removal, or a rewrite, or addition of other needed axioms. */
    // public abstract Collection<OWLOntologyChange> repair();
    public List<OWLOntologyChange> repair() {
        // default fix is to drop the axiom
        return list(new RemoveAxiom(ontology, axiom));
    }

    protected AddAxiom addDeclaration(OWLEntity e) {
        return new AddAxiom(ontology, df.getOWLDeclarationAxiom(e));
    }

    /** visitor accept method
     * 
     * @param visitor */
    public abstract void accept(OWLProfileViolationVisitor visitor);

    protected String toString(String template) {
        return String.format(template + " [%s in %s]", axiom, ontology.getOntologyID());
    }

    protected String toString(String template, Object object) {
        return String.format(template + " [%s in %s]", object, axiom,
                ontology.getOntologyID());
    }

    protected String toString(String template, Object object1, Object object2) {
        return String.format(template + " [%s in %s]", object1, object2, axiom,
                ontology.getOntologyID());
    }

    protected List<OWLOntologyChange> list(OWLOntologyChange... changes) {
        return Arrays.asList(changes);
    }
}
