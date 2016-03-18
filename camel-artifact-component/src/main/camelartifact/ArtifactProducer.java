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

import static camelartifact.ArtifactEndpoint.VALUE;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TODO: Produce artifacts stuffs
 */
public class ArtifactProducer extends DefaultProducer {
	
	private static final transient Logger LOG = LoggerFactory.getLogger(ArtifactEndpoint.class);

	static boolean verboseDebug = true;
	
	ArtifactEndpoint endpoint;

	//public ArtifactProducer(ArtifactEndpoint endpoint, ArtifactComponent bdi_component) {
	public ArtifactProducer(ArtifactEndpoint endpoint) {
		super(endpoint);
		System.out.println("Creating artifact producer endpoint...");
		this.endpoint = endpoint;
		System.out.println("Artifact producer endpoint created successfully!");
	}

	/**
	 * TODO Cleber: Get new values from the route and deliver to the artifact
	 */
	public void process(Exchange exchange) throws Exception {
		/**
		 * Getinstance of artifact
		 * Create new event objects in the concurrentqueue to 
		 * call linkedop step by step  
		 */
		Map<String, String> data = new HashMap<String, String>();
		data = exchange.getIn().getBody(Map.class);

		//String data = (String) exchange.getIn().getBody();
		System.out.println("Content received by producer: "+data.toString());
				
	}
}
