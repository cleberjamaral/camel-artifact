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

package camelartifact.test;

import org.junit.Assert;
import org.junit.Test;

import camelartifact.test.App;

/**
 * This simple testing app is creating artificial operations for the CamelArtifact. Since it is not a MultiAgent System
 * project, actually our camel artifact is not running as an artifact (in an environment with Agents, etc) It is only a
 * Java class processing testing methods.
 * 
 * @author cleber
 * 
 */
public class App2 {

	@Test
	public void testRunSimulation() throws InterruptedException, Exception {

		Assert.assertEquals(App.runSimulation(), 0);

	}

}
