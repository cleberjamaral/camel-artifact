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

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import camelartifact.*;

public class App {

	static ArtifactContainer container;
	static String containerId;

	public static void main(String[] args) throws Exception {

		container = new ArtifactContainer(App.class.getClassLoader(), App.class.getPackage());
		final CamelContext camel = new DefaultCamelContext();
		camel.addComponent("artifact", new ArtifactComponent(container));
		

		/* Create the routes */
		camel.addRoutes(new RouteBuilder() {
			@Override
			public void configure() {
				from("timer:test?period=200").transform(simple("tick(${property.CamelTimerCounter})"))
						//.to("file:data/inbox?delay=5000");
				.to("artifact:shopfloor/loader")
                .to("mock:result");
				
				from("artifact:shopfloor/loader").to("log:ArtifactTest?level=info").to("mock:result");

			}
		});
		
		// start routing
		System.out.println("Starting camel...");
		camel.start();
		System.out.println("Starting router...");

		// Start the artifacts after starting the routes
		container.startAllArtifacts();

		System.out.println("Ready!");
	}
}

