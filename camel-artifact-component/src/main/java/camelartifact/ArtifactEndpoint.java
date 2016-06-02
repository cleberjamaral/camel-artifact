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

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.ScheduledPollEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

/**
 * For JaCaMo project, use SimpleLogger instead of log4j
 * Log allows: Trace, Debug, Info, Warn, Error and Fatal messages
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplelogger.SimpleLogger;

/**
 * TODO: Cleber: Some functions and variables are related with jason agents, must replace by cartago artifacts stuffs In
 * case of "INOUT" messages we can use setExchangePattern(ExchangePattern exchangePattern) of
 * org.apache.camel.support.ServiceSupport
 * 
 */
@UriEndpoint(scheme = "artifact")
public class ArtifactEndpoint extends ScheduledPollEndpoint {

	// See import comments for detalis about LOG
	private static final transient Logger LOG = LoggerFactory.getLogger(ArtifactProducer.class);
	//private static SimpleLogger LOG = new SimpleLogger();

	private String uriContextPath; /* Which contains workspace and artifact */
	private String workspace;

	private final Map<String, String> artifactProperties = new TreeMap<String, String>();
	public static final String VALUE = "value";
	private ConcurrentLinkedQueue<OpRequest> incomingOpQueue;
	private ConcurrentLinkedQueue<OpRequest> outgoingOpQueue;

	public ArtifactEndpoint() {
	}

	public ArtifactEndpoint(String uri, ArtifactComponent component, ConcurrentLinkedQueue<OpRequest> incomingOpQueue,
			ConcurrentLinkedQueue<OpRequest> outgoingOpQueue) {
		super(uri, component);
		setUriContextPath();
		this.incomingOpQueue = incomingOpQueue;
		this.outgoingOpQueue = outgoingOpQueue;
	}

	@SuppressWarnings("deprecation")
	public ArtifactEndpoint(String endpointUri) {
		super(endpointUri);
	}

	public Map<String, String> getArtifactProperties() {
		return artifactProperties;
	}

	/**
	 * TODO Cleber: talk with Cranefield, it seems to be very different the conception of agent and opc endpoints. This
	 * last one seems to have only one connection with the server and treat almost everything as variable So actually,
	 * the context sounds to be more interesting if we use to address the workspace and the artifact, than, we can use
	 * operation to read and write doing like: tag=value means write and tag=_ means read (may be we gotta avoid "?" for
	 * this function)
	 * 
	 * This uri concept is still not very clear, how the variables are transmited? like opcda, sounds that there is only
	 * configuration params, it is not very clear how tags are transmitted: String uriString =
	 * "opcda2:opcdaTest/Simulation Items/Random/String?delay=1000&
	 * host=" + host + "&clsId=" + clsid + "&username=" + user + "&password=" + password + "&domain=" + domain;
	 * 
	 * A message to be processed should be something like Some examples of commons URIs: "file:data/inbox?delay=5000"
	 * based on "scheme:context_path?options" "agent:percept?persistent=false&updateMode=replace"
	 * "timer:test?period=200"
	 * 
	 * On this first approach the proposed URI was "artifact:shopfloor/loader" which means
	 * "://artifact:workspace/artifact_name?options" In a meeting with Cranefield another concept was proposed by him:
	 * On consumer side: "://artifact:operation?...", "://artifact:property?..." On producer side:
	 * "://artifact:event?...", "://artifact:property?..."
	 * 
	 * Identifies the context (contextpath) of URI which can be WRITE or READ, for producer and consumer respectively
	 */
	private void setUriContextPath() {

		String uri = this.getEndpointUri().substring(this.getEndpointUri().indexOf(":"));
		if (uri.contains("?"))
			uriContextPath = uri.substring(0, uri.indexOf("?"));
		else
			uriContextPath = uri;

		LOG.debug("uriContextPath:" + uriContextPath);
		
		String uriContextPathLessColonSlashs = uriContextPath.substring(uriContextPath.indexOf("://") + 3,
				uriContextPath.length());
		if (uriContextPathLessColonSlashs.contains("/")) {
			setWorkspace(uriContextPathLessColonSlashs.substring(0, uriContextPathLessColonSlashs.indexOf("/")));
		}
		LOG.debug("uriContextPathLessColonSlashs:" + uriContextPathLessColonSlashs);
	}

	public String getUriContextPath() {
		return uriContextPath;
	}

	public String getWorkspace() {
		return workspace;
	}

	public ConcurrentLinkedQueue<OpRequest> getIncomingOpQueue() {
		return incomingOpQueue;
	}

	public ConcurrentLinkedQueue<OpRequest> getOutgoingOpQueue() {
		return outgoingOpQueue;
	}

	private void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	@Override
	public Producer createProducer() throws Exception {
		return new ArtifactProducer(this);
	}

	@UriParam
	private String tst_param = "tst";

	@UriParam
	private int length = 10;

	/**
	 * This component is prepared to create multiples consumers, but its is not managing this list in a container (for
	 * example). It must be managed by an external class
	 * 
	 * @param processor
	 * @return consumer
	 */
	public Consumer createConsumer(Processor processor) throws Exception {
		ArtifactConsumer cons = new ArtifactConsumer(this, processor);
		configureConsumer(cons);
		return cons;
	}

	public boolean isSingleton() {
		return true;
	}

}
