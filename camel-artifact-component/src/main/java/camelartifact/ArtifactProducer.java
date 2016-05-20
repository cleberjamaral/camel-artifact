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

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

/**
 * For JaCaMo project, use SimpleLogger instead of log4j
 * Log allows: Trace, Debug, Info, Warn, Error and Fatal messages
 */
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import simplelogger.SimpleLogger;

/**
 * @author Cleber
 * 
 *         Produce side of CamelArtifact class. This is the side responsible for receive new messages from the route and
 *         deliver it to the artifact. The message can come in any format from some consumer, the app is responsible for
 *         this "translation" to "artifact world" what means that messages must be converted in operations calls to be
 *         performed by artifacts.
 */
public class ArtifactProducer extends DefaultProducer {

	// See import comments for detalis about LOG
	// private static final transient Logger LOG = LoggerFactory.getLogger(ArtifactProducer.class);
	private static SimpleLogger LOG = new SimpleLogger();
	private ConcurrentLinkedQueue<OpRequest> incomingOpQueue;
	ArtifactEndpoint endpoint;

	public ArtifactProducer(ArtifactEndpoint endpoint) {
		super(endpoint);
		LOG.trace("Creating artifact producer endpoint...");
		this.endpoint = endpoint;
		LOG.info("Artifact producer endpoint created successfully!");

		incomingOpQueue = endpoint.getIncomingOpQueue();
	}

	/**
	 * Get a new message from the route and try to add it as an OperationRequest to be performed by some artifact. The
	 * expected message has in the reader two fields: "ArtifactName" and "OperationName" This message also has in the
	 * body a set of object that represents parameters of the related operation
	 */
	public void process(Exchange exchange) throws Exception {

		try {

			// LOG.debug("Producer received: " + exchange.getIn().getBody(List.class).toString());
			String artifactName = exchange.getIn().getHeader("ArtifactName").toString();
			String operationName = exchange.getIn().getHeader("OperationName").toString();
			LOG.debug("OperationRequest received! Artifact: " + artifactName + ", " + operationName);

			// A null artifact is forbidden, there is no meaning and nobody to call
			if (artifactName == null) {
				LOG.error("Error on header, ArtifactName received: " + artifactName);
				throw new Exception("No artifact name found!");
			}

			// A null operation is forbidden, there is no meaning and nothing to call
			if (operationName == null) {
				LOG.error("Error on header, OperationName received: " + operationName);
				throw new Exception("No operation name found!");
			}

			// Add in the queue a neu OperationRequest to be performed by related artifact
			LOG.debug("Adding in the queue: " + artifactName + ": " + operationName);
			OpRequest newOp = new OpRequest();
			newOp.setArtifactName(artifactName);
			newOp.setOpName(operationName);
			
			//Do not add a null object!
			if (exchange.getIn().getBody(List.class) != null)
			{
				List<Object> body = exchange.getIn().getBody(List.class);
				newOp.setParams(body);
				LOG.debug("Parameters details: " + newOp.getParams().toString());
			}
			
			incomingOpQueue.add(newOp);
			LOG.debug("Message added in the queue!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}