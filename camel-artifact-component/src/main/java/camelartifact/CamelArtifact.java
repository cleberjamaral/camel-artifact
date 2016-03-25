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

import cartago.*;
//import java.util.concurrent.ConcurrentLinkedQueue;

//TODO Cleber: Ask Cranefield how to make testing class to run on mvn

/**
 * @author cleber
 * 
 *         The plan is to have an artifact "camel" other artifacts would use it
 *         as a way to print and read messages from a camel route, but I am not
 *         sure how to do it. At this beginning I am assuming that the app
 *         should do the route and the linkedoperation with among artifacts
 */
public class CamelArtifact extends Artifact {

	// Log allows: Trace, Debug, Info, Warn, Error and Fatal messages
	private static final transient Logger LOG = LoggerFactory
			.getLogger(CamelArtifact.class);
	private boolean listenCamelRoutes = false;
	ReadCmd cmd;
	boolean receiving;

	// protected ConcurrentLinkedQueue<OpRequest> incomingOpQueue = new
	// ConcurrentLinkedQueue<OpRequest>();

	/**
	 * Since it is an operation, means it can be accessed by agents It is also
	 * public to be accessed by java code of a host class
	 * 
	 * @param value
	 */
	@OPERATION
	public void setListenCamelRoute(boolean value) {

		cmd = new ReadCmd();
		receiving = false;
		// cmd.exec();

		listenCamelRoutes = value;
		LOG.trace("Camel Artifact 'listenCamelRoutes' is " + listenCamelRoutes);
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
			cmd.setMsgReceived(true);
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

	/*
	 * @OPERATION void receiveMessage(OpFeedbackParam msg, OpFeedbackParam
	 * sender) { if (getListenCamelRoute()) { await(cmd);
	 * //msg.set(cmd.getMsg()); //sender.set(cmd.getSender()); } }
	 */
	@OPERATION
	void startReceiving() {
		receiving = true;
		execInternalOp("receiving");
	}

	@INTERNAL_OPERATION
	void receiving() {
		if (getListenCamelRoute()) {
			while (true) {
				await(cmd);
				// signal("new_msg", cmd.getMsg(), cmd.getSender());

			}
		}
	}

	@OPERATION
	void stopReceiving() {
		receiving = false;
	}

	class ReadCmd implements IBlockingCmd {

		private boolean msgReceived;

		public ReadCmd() {
		}

		public void exec() {
			try {
				if (msgReceived) {
					msgReceived = false;

					LOG.debug("MSG!!!!");

					int tst = 1;
					callLinkedArtifactOperation("counter", "writeinputAr", tst);
				}

			} catch (Exception ex) {
			}
		}

		public void setMsgReceived(boolean value) {
			msgReceived = value;
		}
	}
}
