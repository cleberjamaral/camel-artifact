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
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import simplelogger.SimpleLogger;

/**
 * TODO: The "INOUT" messages were not treated, we can use
 * setExchangePattern(ExchangePattern exchangePattern) of
 * org.apache.camel.support.ServiceSupport TODO: Treat different workspaces. The
 * main idea is that it is possible to have artifacts with same name in
 * different workspaces TODO: Treat artifact observable properties. This feature
 * would allow an artifact in a JaCaMo instance be observed by an agent in
 * another instance. The same for observable events. TODO: Treat multiple
 * consumers. The current version is not allowed by Camel to make multiple
 * consumers.
 */
@UriEndpoint(scheme = "artifact")
public class ArtifactEndpoint extends ScheduledPollEndpoint {

	// See import comments for details about LOG
	// private static final transient Logger LOG =
	// LoggerFactory.getLogger(ArtifactEndpoint.class);
	private static SimpleLogger LOG = new SimpleLogger();

	// workspaces is not working in the current version!
	private String uriContextPath; /* Which contains workspace and artifact */
	private String workspace;

	// Artifact properties are not working in the current version!
	// private final Map<String, String> artifactProperties = new TreeMap<String,
	// String>();

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

	// public Map<String, String> getArtifactProperties() {
	// return artifactProperties;
	// }

	/**
	 * A common URI is like "artifact:cartago//workspace" artifact name, operation
	 * and extra parameters are sent by header and body of the message. The
	 * workspace param is not mandatory.
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

	/**
	 * The workspace is not working yet but the plan is about some projects that can
	 * have agents and artifacts in multiple workspaces, so this camel-artifact can
	 * live in more than one environment, to have sure that it is talking with right
	 * artifacts we need to know the workspace
	 * 
	 * @return workspace that this artifact is running.
	 */
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
	 * This component is prepared to create multiples consumers, but its is not
	 * managing this list in a container (for example). Some tests on managing by an
	 * external class also show that it is not allowed by camel. There is something
	 * to think about how to deal with data (from artifacts) that should be
	 * delivered to different datatypes/endpoints. Nowadays, it should bring
	 * compatibility problems.
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
		return false;
	}

}
