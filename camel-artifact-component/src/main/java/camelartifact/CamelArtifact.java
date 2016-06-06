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

import cartago.*;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;//Operation queue

/**
 * For JaCaMo project, use SimpleLogger instead of log4j
 * Log allows: Trace, Debug, Info, Warn, Error and Fatal messages
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simplelogger.SimpleLogger;

/**
 * @author cleber
 * 
 *         This "camel-artifact" should exists in a MAS project to allow communication between artifacts and external
 *         sources. It can also be a router, sending and receiving messages from other (linked) artifacts. For this
 *         there are two queue lists for messages that the artifact received and for the ones to me send. Here the
 *         meaning adopted is: "Incoming" refers to packets which originate elsewhere and arrive at the artifact, while
 *         "outgoing" refers to packets which originate at the artifact and arrive elsewhere.
 *         http://serverfault.com/questions/443038/what-does-incoming-and-outgoing-traffic-mean
 */

public class CamelArtifact extends Artifact {

	// See import comments for detalis about LOG
	//private static final transient Logger LOG = LoggerFactory.getLogger(CamelArtifact.class);
	private static SimpleLogger LOG = new SimpleLogger();
	protected static ConcurrentLinkedQueue<OpRequest> incomingOpQueue = new ConcurrentLinkedQueue<OpRequest>();
	protected static ConcurrentLinkedQueue<OpRequest> outgoingOpQueue = new ConcurrentLinkedQueue<OpRequest>();
	private boolean listenCamelRoutes = false;

	/**
	 * Since it is an operation, means it can be accessed by agents It is also public to be accessed by java code of a
	 * host class
	 * 
	 */
	@OPERATION
	public void setListenCamelRoute(final boolean value) {

		listenCamelRoutes = value;
		LOG.trace("Camel Artifact 'listenCamelRoutes' is " + listenCamelRoutes);

		Thread thread = new Thread() {

			public void run() {
				try {
					OpRequest newOp;
					LOG.debug("Listening by reading the incoming queue...");
					while (value) {
						if ((newOp = incomingOpQueue.poll()) != null) {
							LOG.debug("A message was founded in the incoming queue! Artifact:" + newOp.getArtifactName()
									+ ", op:" + newOp.getOpName() + ", body " + newOp.getParams().toString());
							receiveMsg(newOp.getArtifactName(), newOp.getOpName(), newOp.getParams());
						}
					}
					LOG.debug("Listening process stopped!");
				} catch (Exception ex) {
					LOG.error("Error on listening incoming queue!");
				}
			}
		};
		thread.start();

	}

	public ConcurrentLinkedQueue<OpRequest> getIncomingOpQueue() {
		return incomingOpQueue;
	}

	public ConcurrentLinkedQueue<OpRequest> getOutgoingOpQueue() {
		return outgoingOpQueue;
	}

	public boolean getListenCamelRoute() {
		return listenCamelRoutes;
	}

	/**
	 * Some message was received by the route
	 */
	@OPERATION
	void receiveMsg(String artifactName, String operationName, List<Object> parameters) {

		try {

			/**
			 * Lookup command is here used to get the ArtifactID by the artifactName received
			 */
			LOG.debug("Getting artifact id of " + artifactName);
			ArtifactId aid = lookupArtifact(artifactName);

			/**
			 * The received message is addressed to other artifact
			 */
			if (this.getId() != aid) {

                LOG.debug("artifact name/id/type: " + aid.getName() + "/" + aid.getId() + "/" + aid.getArtifactType());

                /**
                 * To avoid error, if the parameters are null the InternalOp will receive it The list o Objects inside of
                 * OpRequest is always created, but if is empty none Objects was received
                 */
                if (parameters.isEmpty()) {
                    LOG.debug("Forwarding " + operationName + " without parameters.");
                    LOG.debug("..");
                    try {
                        execLinkedOp(aid, operationName);
                    } catch (OperationException e) {
                        LOG.error("Error on execLinkedOp without parameters!");
                        e.printStackTrace();
                    } finally {
                        LOG.debug("...");    
                    }
                    LOG.debug("....");
                    
                } else {
                    /**
                     * execLinkedOp is waiting for a set of objects, not a list o objects (ArtifactId aid, String opName,
                     * Object... params)
                     */
                    LOG.debug("Forwarding " + operationName + " with following parameters: " + parameters);
                    LOG.debug("..");
                    try {
                        execLinkedOp(aid, operationName, parameters.toArray());
                    } catch (OperationException e) {
                        LOG.error("Error on execLinkedOp without parameters!");
                        e.printStackTrace();
                    } finally {
                        LOG.debug("...");    
                    }
                    LOG.debug("....");
                }
                
			} else {

				/**
				 * To avoid error, if the parameters are null so, the InternalOp cannot receive it The list o Objects
				 * inside of OpRequest is always created, but if is empty none Objects was received
				 */
				if (parameters.isEmpty()) {
					LOG.debug("Executing " + operationName + " without parameters.");
					execInternalOp(operationName);
				} else {
					/**
					 * It is expect that the called InternalOp has the number and type of parameters declared, example:
					 * operationName = inc; parameters[] = {"testString", 4}. So, the inc() @INTERNAL_OPERATION may
					 * looks like: inc(String st, int i)
					 */
					LOG.debug("Executing " + operationName + " with following parameters: " + parameters);
					execInternalOp(operationName, parameters.toArray());
				}

			}

		} catch (OperationException e) {
			LOG.error("Error receiving a message!");
			e.printStackTrace();
		}
		
	}

	/**
	 * Add a message to the outgoing queue
	 */
	@OPERATION
	public void sendMsg(String artifactName, String operationName, List<Object> parameters) {

		try {
			LOG.debug("A message is being send to camel route...");
			
			OpRequest newOp = new OpRequest();
			newOp.setArtifactName(artifactName);
			newOp.setOpName(operationName);
			newOp.setParams(parameters);
			outgoingOpQueue.add(newOp);
			
			LOG.debug("Message added in the outgoing queue!");
			
		} catch (Exception e) {
			LOG.error("Error adding a message in the outgoing queue!");
			e.printStackTrace();
		}

	}

}
