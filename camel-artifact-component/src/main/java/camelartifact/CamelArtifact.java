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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import resources.SimpleLogger;

import cartago.*;

//Operation queue
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author cleber
 * 
 *         This "camel-artifact" should exists in a MAS project to allow
 *         communication between artifacts and extenal sources. It can also be a
 *         router, sending and receiving messages from other (linked) artifacts.
 */

public class CamelArtifact extends Artifact {

	/**
	 * Setting logging engine. For JaCaMo project, use SimpleLogger instead of
	 * log4j Log allows: Trace, Debug, Info, Warn, Error and Fatal messages
	 */
	//
	private static final transient Logger LOG = LoggerFactory
			.getLogger(CamelArtifact.class);
	// private static SimpleLogger LOG = new SimpleLogger();
	protected static ConcurrentLinkedQueue<OpRequest> incomingOpQueue = new ConcurrentLinkedQueue<OpRequest>();

	private boolean listenCamelRoutes = false;
	boolean receiving;

	/**
	 * Since it is an operation, means it can be accessed by agents It is also
	 * public to be accessed by java code of a host class
	 * 
	 * @param value
	 */
	@OPERATION
	public void setListenCamelRoute(boolean value) {

		receiving = false;

		listenCamelRoutes = value;
		LOG.trace("Camel Artifact 'listenCamelRoutes' is " + listenCamelRoutes);

		Thread thread = new Thread() {

			public void run() {
				try {
					OpRequest newOp;
					LOG.debug("Waiting for a message in the queue...");
					while (true) {
						if ((newOp = incomingOpQueue.poll()) != null) {
							LOG.debug("A message was founded in the queue! Artifact:"
									+ newOp.getArtifactName()
									+ ", op:"
									+ newOp.getOpName()
									+ ", body "
									+ newOp.getParams().toString());

							// callLinkedArtifactOperation("counter",
							// "writeinputAr", tst);
						}
					}
				} catch (Exception ex) {
				}
			}
		};
		thread.start();

	}
	
	public ConcurrentLinkedQueue<OpRequest> getIncomingOpQueue() {
		return incomingOpQueue;
	}

	public boolean getListenCamelRoute() {
		return listenCamelRoutes;
	}

	@OPERATION
	void inc() {
		ObsProperty prop = getObsProperty("count");
		prop.updateValue(prop.intValue() + 1);
		signal("tick");

		// Like Jomi said for example this can sendToCamel();
	}

	/**
	 * Some message was received by the route
	 */
	@OPERATION
	void receiveMsg(String artifactName, String operationName, Object parameters) {

		boolean testingArtifactsDirectly = false;

		if (testingArtifactsDirectly) {

			ArtifactId aid;
			try {

				LOG.debug("receiveMsg to " + artifactName + ", op "
						+ operationName);

				aid = lookupArtifact(artifactName);

				LOG.debug("Artifact ID:" + aid.toString() + ", this "
						+ this.getId().toString());

				/**
				 * The received message is addressed to other artifact
				 */
				if (this.getId() != aid) {
					LOG.debug("receiveMsg 3...");
					callLinkedArtifactOperation(artifactName, operationName,
							parameters);
				} else {
					// Call internal operation
				}
				LOG.debug("receiveMsg 4...");

			} catch (OperationException e) {
				e.printStackTrace();
			}
		} else {
			//cmd.setMsgReceived(true);
		}

	}

	/**
	 * This operation is sending received data (from camel) to the destination
	 * artifact. In this case the application must declare a @LINK method
	 * according with operatioName and parameters received
	 */
	@OPERATION
	void callLinkedArtifactOperation(String artifactName, String operationName,
			Object parameters) {

		LOG.debug("CamelArtif " + artifactName + ", op: " + operationName
				+ ", params: " + parameters.toString());

		ArtifactId aid;
		try {
			aid = lookupArtifact(artifactName);
			LOG.debug("artifact id: " + aid.getName() + ", " + aid.getId()
					+ ", " + aid.getArtifactType() + ", " + aid.toString());

			String tst = "";
			execLinkedOp(aid, operationName, tst);
		} catch (OperationException e) {
			e.printStackTrace();
		}
	}
	
}
