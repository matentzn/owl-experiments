package owl.cs.man.ac.uk.experiment.metrics;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.MissingImportEvent;
import org.semanticweb.owlapi.model.MissingImportListener;

public class MissingImportTracker implements MissingImportListener {

    Set<String> missingImports = new HashSet<String>();

    public void importMissing(MissingImportEvent event) {
        missingImports.add(event.getImportedOntologyURI().toString());

    }
    public Set<String> getMissingImports() {
        return missingImports;
    }
}
