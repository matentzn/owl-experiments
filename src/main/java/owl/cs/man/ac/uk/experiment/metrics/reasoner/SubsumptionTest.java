package owl.cs.man.ac.uk.experiment.metrics.reasoner;

import java.util.HashMap;
import java.util.Map;

import owl.cs.man.ac.uk.experiment.metrics.reasoner.ReasonerMetrics.STType;

public class SubsumptionTest {
	
	final String superClass;
	final String subClass;
	final String reasonerid;
	final String type;
	final boolean pos;
	long started = 0;
	long finished = 0;
	
	SubsumptionTest(String reasonerid, String sub, String sup, String type, boolean pos) {
		superClass=normaliseName(sup);
		subClass=normaliseName(sub);
		this.type=type;
		this.pos=pos;
		this.reasonerid=reasonerid;
	}	

	private String normaliseName(String s) {
		if(s.startsWith("<") && s.endsWith(">")) {
			return s.substring(1,s.length()-1);
		}
		return s;
	}




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (finished ^ (finished >>> 32));
		result = prime * result + (pos ? 1231 : 1237);
		result = prime * result
				+ ((reasonerid == null) ? 0 : reasonerid.hashCode());
		result = prime * result + (int) (started ^ (started >>> 32));
		result = prime * result
				+ ((subClass == null) ? 0 : subClass.hashCode());
		result = prime * result
				+ ((superClass == null) ? 0 : superClass.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SubsumptionTest other = (SubsumptionTest) obj;
		if (finished != other.finished)
			return false;
		if (pos != other.pos)
			return false;
		if (reasonerid == null) {
			if (other.reasonerid != null)
				return false;
		} else if (!reasonerid.equals(other.reasonerid))
			return false;
		if (started != other.started)
			return false;
		if (subClass == null) {
			if (other.subClass != null)
				return false;
		} else if (!subClass.equals(other.subClass))
			return false;
		if (superClass == null) {
			if (other.superClass != null)
				return false;
		} else if (!superClass.equals(other.superClass))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}




	public void setTimeStarted(long started) {
		this.started = started;		
	}
	
	public void setTimeFinished(long finished) {
		this.finished = finished;		
	}

	public Map<String, String> getData() {
		Map<String,String> data = new HashMap<String,String>();
		data.put("sub", subClass);
		data.put("super", superClass);
		data.put("pos", pos+"");
		data.put("type", type);
		data.put("starttime", started+"");
		data.put("endtime", finished+"");
		data.put("testduration", (finished-started)+"");
		data.put("reasonerid", reasonerid+"");
		return data;
	}

}
