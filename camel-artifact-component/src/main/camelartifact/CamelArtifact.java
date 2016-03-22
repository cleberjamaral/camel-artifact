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

//TODO Cleber: Ask Cranefield how to make testing class to run on mvn

/**
 * Artifact out ports TODO Cleber: Ask Cranefield, is it necessary to be in
 * Interface too? Is it part of the class and inherited?
 */
@ARTIFACT_INFO(outports = { @OUTPORT(name = "out-1"), @OUTPORT(name = "in-1") })
/**
 * @author cleber 
 * 
 * The plan is to have an artifact "camel" other artifacts would
 * use it as a way to print and read messages from a camel route, but I
 * am not sure how to do it. At this beginning I am assuming that the
 * app should do the route and the linkedoperation with among artifacts
 */
public class CamelArtifact extends Artifact {

	private boolean listenCamelRoutes = false;

	@OPERATION
	void setListenCamelRoute(boolean value) {
		listenCamelRoutes = value;
		System.out.println("Camel Artifact 'listenCamelRoutes' changed to "
				+ value);
	}

	public boolean getListenCamelRoute() {
		return listenCamelRoutes;
	}

	@OPERATION
	void inc() {
		ObsProperty prop = getObsProperty("count");
		prop.updateValue(prop.intValue() + 1);
		signal("tick");
	}

	/**
	 * This operation is writing the received data (from camel) to the port. In
	 * this case the application must declare a @LINK method called writeinput
	 * to receive the related data
	 */
	@OPERATION
	void writeinput(String datainput) {
		try {
			System.out.println("Camel artifact writeinput by out-1: "
					+ datainput.toString());
			execLinkedOp("out-1", "writeinputAr", "tst");

			// System.out.println("Camel artifact writeinput by in-1: "
			// + datainput.toString());
			// execLinkedOp("in-1", "writeinputAr", "tst");
			//
			System.out.println("Writeinput in-1 and out-1 done: "
					+ datainput.toString());

		} catch (OperationException e) {
			System.out.println("writeinput ERROR! " + e.getMessage());
			e.printStackTrace();
		}
	}

	@OPERATION
	void callLinkedArtifactOperation(String artifactName, String operationName,
			Object parameters) {

		System.out.println("CamelArtif writeinput by " + artifactName + ", op: "
				+ operationName);
		
		ArtifactId aid;
		try {
			aid = lookupArtifact(artifactName);
			System.out.println("assembler id: " + aid.getName() + ", " + aid.getId()
					+ ", " + aid.getArtifactType() + ", " + aid.toString());

			execLinkedOp(aid, operationName, "tst");
		} catch (OperationException e) {
			e.printStackTrace();
		}

	}
}
