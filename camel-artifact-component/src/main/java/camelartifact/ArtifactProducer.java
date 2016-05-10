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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//import resources.SimpleLogger;

import camelartifact.CamelArtifact;

/**
 * TODO: Produce artifacts stuffs
 */
public class ArtifactProducer extends DefaultProducer {

	//For JaCaMo project, use SimpleLogger instead of log4j
	// Log allows: Trace, Debug, Info, Warn, Error and Fatal messages
	private static final transient Logger LOG = LoggerFactory.getLogger(ArtifactProducer.class);
	//private static SimpleLogger LOG = new SimpleLogger();

	private CamelArtifact camelartif = null;
	//private Map<String, Map<String, Object>> opQueue = new HashMap<String, Map<String, Object>>();
	private ConcurrentLinkedQueue<OpRequest> incomingOpQueue;

	ArtifactEndpoint endpoint;

	public ArtifactProducer(ArtifactEndpoint endpoint) {
		super(endpoint);
		LOG.trace("Creating artifact producer endpoint...");
		this.endpoint = endpoint;
		LOG.info("Artifact producer endpoint created successfully!");

		//camelartif = endpoint.getCamelArtifact();
		incomingOpQueue = endpoint.getIncomingOpQueue();
	}

	/**
	 * TODO Cleber: Get new values from the route and deliver to the artifact
	 */
	public void process(Exchange exchange) throws Exception {

		/**
		 * Getinstance of artifact Create new event objects in the
		 * concurrentqueue to call linkedop step by step
		 */

		//if (camelartif.getListenCamelRoute()) 
		{

			try {

				/*
				 * The default message 
				 */
				LOG.debug("Producer received: " + exchange.getIn().getBody(Map.class).toString());

				String artifactName = exchange.getIn().getHeader("ArtifactName").toString();
				if (artifactName == null) {
					LOG.error("Error on header, ArtifactName received: " + artifactName);
					throw new Exception("No artifact name found!");
				}

				/**
				 * TODO Cleber: Check with Cranefield, I think we can have the
				 * operations in the body, which has in short <operations,
				 * parameters>
				 */
				String operationName = exchange.getIn().getHeader("OperationName").toString();
				if (operationName == null) {
					LOG.error("Error on header, OperationName received: "+ operationName);
					throw new Exception("No operation name found!");
				}

				OpRequest newOp = new OpRequest();
				newOp.setArtifactName(artifactName);
				newOp.setOpName(operationName);
				LOG.debug("Added in the queue: " + artifactName + ": " + operationName);
				newOp.setParams(exchange.getIn().getBody(Map.class));
				incomingOpQueue.add(newOp);

				//camelartif.receiveMsg(artifactName, operationName, exchange.getIn().getBody());

				// camelartif.writeinput(data.toString());
				// Testing methods... end

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}				