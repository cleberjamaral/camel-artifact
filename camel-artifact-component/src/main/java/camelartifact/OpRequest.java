package camelartifact;
import java.util.HashMap;
import java.util.Map;

public class OpRequest {
	private String artifactName;
	private String opName;
	//private Collection<Object> params = new ArrayList<>();
	Map<String, Object> params = new HashMap<String, Object>();
	
	public String getArtifactName() {
		return artifactName;
	}
	public void setArtifactName(String artifactName) {
		this.artifactName = artifactName;
	}
	public String getOpName() {
		return opName;
	}
	public void setOpName(String opName) {
		this.opName = opName;
	}
	public Map<String, Object>/*Collection<Object>*/ getParams() {
		return params;
	}
	public void setParams(Map<String, Object>/*Collection<Object>*/ params) {
		this.params = params;
	}
}
