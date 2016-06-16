package owl.cs.man.ac.uk.experiment.analysis;

public class ExperimentOntology {
	
	final String ontologyname;
	
	public ExperimentOntology(String ontologyname) {
		this.ontologyname = ontologyname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ontologyname == null) ? 0 : ontologyname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExperimentOntology other = (ExperimentOntology) obj;
		if (ontologyname == null) {
			if (other.ontologyname != null)
				return false;
		} else if (!ontologyname.equals(other.ontologyname))
			return false;
		return true;
	}

	public String toString() {
		return ontologyname;
	}
}
