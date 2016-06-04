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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cleber
 * 
 *         This OpRequest class represents the request messages that came from the route and must be executed by the
 *         artifacts. Main fields: artifactName: Means for what artifact this request is addressed opName: Means which
 *         operation should be executed (represents a method) params: Arguments of the operations in generic objects
 *         (list of) format
 */

public class OpRequest {

	private String artifactName;
	private String opName;
	private List<Object> params;

	OpRequest() {
		this.params = new ArrayList<Object>();
	}

	/**
	 * Get the name of the artifact that this operation is addressed
	 * 
	 * @return is the name of the executor artifact
	 */
	public String getArtifactName() {
		return artifactName;
	}

	/**
	 * Set the name of the artifact that this operation is addressed
	 * 
	 * @param artifactName
	 *            means the name of the executor artifact
	 */
	public void setArtifactName(String artifactName) {
		this.artifactName = artifactName;
	}

	/**
	 * Get an operation that must be executed by an artifact. It probably means
	 * a method known by the artifact
	 * 
	 * @return the operation name
	 */
	public String getOpName() {
		return opName;
	}

	/**
	 * Set an operation that must be executed by an artifact. It probably means a method known by the artifact
	 * 
	 * @param opName
	 *            the name of the operation (probably a method of the artifact)
	 */
	public void setOpName(String opName) {
		this.opName = opName;
	}

	/**
	 * Get parameters of an operation.
	 * 
	 * @return a list of objects that represents parameters of an operation
	 */
	public List<Object> getParams() {
		return params;
	}

	/**
	 * Set parameters of an operation. This method is using variable-length argument lists to fill List<object> params
	 * 
	 * @param params
	 *            represents the arguments of an operation
	 */
	public void setParams(List<Object> params) {
		for (Object obj : params) {
			this.params.add(obj);
		}
	}
}
