/**
 * @author Cranefield, Stephen
 * @author Roloff, Mario Lucio 
 * @author Amaral, Cleber Jorge
 * 
 * Based on and with acknowledgments:
 * camel-agent (camel_jason) 2013 by Stephen Cranefield and Surangika Ranathunga
 * camel-opc (opcada component) 2013/2014 by Justin Smith
 * 
 * It is free software: you can redistribute it and/or modify
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *                                             
 * It is distributed in the hope that it will be useful,                  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                  
 * GNU Lesser General Public License for more details.                             
 * You should have received a copy of the GNU Lesser General Public License        
 * along with camel_jason.  If not, see <http://www.gnu.org/licenses/>.            
 */

package camelartifact;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * TODO: Cleber: Some functions and variables are related with jason agents, must replace by cartago artifacts stuffs
 */
public class ArtifactEndpoint extends DefaultEndpoint {

	private String uriContextPath; /*Which contains workspace and artifact*/
	private String workspace;
	private String artifact;
	private ArtifactComponent artifact_component;
	private final Map<String, String> artifactProperties = new TreeMap<String, String>();
	public static final String VALUE = "value";

	public ArtifactEndpoint() {
	}

	public ArtifactEndpoint(String uri, ArtifactComponent component) {
		super(uri, component);
		artifact_component = component;
		setUriContextPath();
	}

    public ArtifactEndpoint(String endpointUri) {
        super(endpointUri);
    }

	public Map<String, String> getArtifactProperties() {
		return artifactProperties;
	}

	/**
	 * TODO Cleber: talk with Cranefield, it seems to be very 
	 * different the conception of agent and opc endpoints. 
	 * This last one seems to have only one connection with 
	 * the server and treat almost everything as variable
	 * So actually, the context sounds to be more interesting
	 * if we use to address the workspace and the artifact,
	 * than, we can use operation to read and write doing like:
	 * tag=value means write and tag=_ means read (may be we 
	 * gotta avoid "?" for this function)
	 *  
	 * This uri concept is still not very clear, how the variables
	 * are transmited? like opcda, sounds that there is only configuration
	 * params, it is not very clear how tags are transmitted:
	 * String uriString = "opcda2:opcdaTest/Simulation Items/Random/String?delay=1000&
	 * host=" + host + "&clsId=" + clsid + "&username=" + user + "&password=" + password + "&domain=" + domain;
	 *   
	 * A message to be processed should be something like
	 * Some examples of commons URIs:
	 * "file:data/inbox?delay=5000" based on "scheme:context_path?options"
	 * "agent:percept?persistent=false&updateMode=replace"
	 * "timer:test?period=200" 
	 * 
	 * On this first approach the proposed URI was "artifact:shopfloor/loader"
	 * which means "://artifact:workspace/artifact_name?options"
	 * In a meeting with Cranefield another concept was proposed by him:
	 * On consumer side: "://artifact:operation?...", "://artifact:property?..."
	 * On producer side: "://artifact:event?...", "://artifact:property?..."
	 *   
	 * Identifies the context (contextpath) of URI which can be
	 * WRITE or READ, for producer and consumer respectively
	 */
	private void setUriContextPath() {

		String uri = this.getEndpointUri().substring(this.getEndpointUri().indexOf(":"));
		if (uri.contains("?"))
			uriContextPath = uri.substring(0, uri.indexOf("?"));
		else
			uriContextPath = uri;
		
		String uriContextPathLessColonSlashs = uriContextPath.substring(uriContextPath.indexOf("://")+3,uriContextPath.length());
		if (uriContextPathLessColonSlashs.contains("/"))
		{
			setWorkspace(uriContextPathLessColonSlashs.substring(0, uriContextPathLessColonSlashs.indexOf("/")));
			setArtifact(uriContextPathLessColonSlashs.substring(uriContextPathLessColonSlashs.indexOf("/")+1,uriContextPathLessColonSlashs.length()));
		}
		else
		{
			setArtifact(uriContextPathLessColonSlashs);
		}
	}

	public String getUriContextPath() {
		return uriContextPath;
	}

	public String getWorkspace() {
		return workspace;
	}

	private void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getArtifact() {
		return artifact;
	}

	private void setArtifact(String artifact) {
		this.artifact = artifact;
	}

	@Override
	public Producer createProducer() throws Exception {
		//return new ArtifactProducer(this, artifact_component);
		return new ArtifactProducer(this);
	}

	public Consumer createConsumer(Processor processor) throws Exception {
		ArtifactConsumer cons = new ArtifactConsumer(this, processor);

		Enumeration<SimpleArtifact> e = ArtifactContainer.getArtifacts().elements();

		while (e.hasMoreElements()) {
			SimpleArtifact j = e.nextElement();
			j.addToMyConsumers(cons);
		}
		return cons;
	}

	public boolean isSingleton() {
		return true;
	}

}
