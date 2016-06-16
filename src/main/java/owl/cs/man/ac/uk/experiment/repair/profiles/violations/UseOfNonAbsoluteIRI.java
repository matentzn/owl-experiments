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
package owl.cs.man.ac.uk.experiment.repair.profiles.violations;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolation;
import owl.cs.man.ac.uk.experiment.repair.profiles.OWLProfileViolationVisitor;

/** Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 03-Aug-2009 */
@SuppressWarnings("javadoc")
public class UseOfNonAbsoluteIRI extends OWLProfileViolation {
    private final IRI iri;
    private final OWLOntology ontology;

    public UseOfNonAbsoluteIRI(OWLOntology ontology, OWLAxiom axiom, IRI iri) {
        super(ontology, axiom);
        this.iri = iri;
        this.ontology = ontology;
    }

    @Override
    public void accept(OWLProfileViolationVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return toString("IRI is relative: %s", iri);
    }
    
    @Override
    public List<OWLOntologyChange> repair() {
    	OWLEntityRenamer r = new OWLEntityRenamer(ontology.getOWLOntologyManager(), ontology.getOWLOntologyManager().getImportsClosure(ontology));
    	String frag = iri.getFragment();
    	try {
			frag = URLEncoder.encode(frag, "UTF-8");
			IRI newIRI = IRI.create("http://www.absoluteiri.edu/"+frag);
	    	return r.changeIRI(iri, newIRI);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.err.println("Cannot rewrite IRI, not changing anything.");
    	return new ArrayList<OWLOntologyChange>();
    }
    
    
}
