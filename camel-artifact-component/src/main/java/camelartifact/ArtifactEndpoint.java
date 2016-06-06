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
import org.apache.camel.impl.ScheduledPollEndpoint;
import org.apache.camel.spi.UriEndpoint;

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
	//private static final transient Logger LOG = LoggerFactory.getLogger(ArtifactEndpoint.class);
	private static SimpleLogger LOG = new SimpleLogger();

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
	 * A common URI is like "artifact:cartago//workspace" artifact name, operation and extra parameters are sent by
	 * header and body of the message
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
