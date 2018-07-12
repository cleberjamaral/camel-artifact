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

package artifacts;

import java.util.ArrayList;
import java.util.List;

import cartago.*;

/**
 * Artifact out ports
 */

@ARTIFACT_INFO(outports = { @OUTPORT(name = "out-2") })

public class ArtifactB extends Artifact {

	void init() throws Exception {
	}
	
	@OPERATION
	void sendKAB() throws OperationException {
		log("trying to send keepalive message...");
		List<Object> params  = new ArrayList<Object>();
		params.add("ArtifactB: Keep alive!");
		execLinkedOp("out-2","sendMsg","ArtifactB","KA",params);
	}
	
	@LINK 
	void kaBackB() throws OperationException {
		try {
			log("received keepalive back!");
		} catch (Exception e) {
			log("("+this.getId().getName()+") Error receiving keepalive back!");
			e.printStackTrace();
		}
	}	
}

