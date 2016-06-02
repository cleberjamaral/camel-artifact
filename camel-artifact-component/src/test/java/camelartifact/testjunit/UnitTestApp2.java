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

package camelartifact.testjunit;

import java.util.ArrayList;
import java.util.List;


import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import camelartifact.*;
import camelartifact.test.App;
import camelartifact.OpRequest;

/**
 * This simple testing app is creating artificial operations for the CamelArtifact. Since it is not a MultiAgent System
 * project, actually our camel artifact is not running as an artifact (in an environment with Agents, etc) It is only a
 * Java class processing testing methods.
 * 
 * @author cleber
 * 
 */
public class UnitTestApp2 extends CamelTestSupport {

	private static final transient Logger LOG = LoggerFactory.getLogger(UnitTestApp2.class);
	private static final int expectedMsgsCount = 3;
	private static int msgCount = expectedMsgsCount;

	 
	@Test
	public void testRunSimulation() throws InterruptedException, Exception {

		Assert.assertEquals(runSimulation(), 0);

	}	
	
	public static int runSimulation() throws Exception, InterruptedException {
		
		CamelArtifact camelartif = new CamelArtifact();

		// Add routes and start camel
		LOG.debug("Adding routes and starting camel...");
		CamelContext camelContext = new DefaultCamelContext();
		camelContext.addComponent("artifact", new ArtifactComponent(camelartif.getIncomingOpQueue(),camelartif.getOutgoingOpQueue()));
		camelContext.addRoutes(createRoutes());
		camelContext.start();

		/**
		 * This method should be called to say: start listening the route, so the producer will process the messages
		 */
		camelartif.setListenCamelRoute(false);

		List<Object> params1  = new ArrayList<Object>();
		List<Object> params2  = new ArrayList<Object>();
		List<Object> params3  = new ArrayList<Object>();
		params1.add(1);
		params1.add("tstParam");
		params2.add(2);
		params2.add("tstParam");
		params3.add(3);
		params3.add("tstParam");
		camelartif.sendMsg("TestArtifact", "Op1", params1);
		camelartif.sendMsg("TestArtifact", "Op2", params2);
		camelartif.sendMsg("TestArtifact", "Op3", params3);

		LOG.debug("Running testing loop...");
		ProducerTemplate prodtemplate = camelContext.createProducerTemplate();
		ConsumerTemplate constemplate = camelContext.createConsumerTemplate();
		constemplate.start();
		while (msgCount > 0) {
			prodtemplate.sendBody("direct:start", "");
			Thread.sleep(200); //Without some delay some message may be replaced by new ones	
			msgCount--;
		}
		constemplate.receiveBody("artifact:cartago");
				
		LOG.info("Testing loop finished!");
		
		constemplate.stop();
		camelContext.stop();
		
		camelartif = null;
		return 0;//means successful!
	}
	
	public static RouteBuilder createRoutes() throws Exception {
		/* Create the routes */
		return new RouteBuilder() {
			@Override
			public void configure() {

				LOG.trace("...");
				
				from("artifact:cartago").process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						
						String artifactName = exchange.getIn().getHeader("ArtifactName").toString();
						String operationName = exchange.getIn().getHeader("OperationName").toString();
						LOG.debug("Out Message: Artifact: " + artifactName + ", " + operationName);

						// A null artifact is forbidden, there is no meaning and nobody to call
						if (artifactName == null) {
							LOG.error("Error on header, ArtifactName: " + artifactName);
							throw new Exception("No artifact name found!");
						}

						// A null operation is forbidden, there is no meaning and nothing to call
						if (operationName == null) {
							LOG.error("Error on header, OperationName: " + operationName);
							throw new Exception("No operation name found!");
						}

						LOG.debug("Parameters details: " + exchange.getIn().getBody(List.class).toString());
					}
				}).to("log:CamelArtifactLogger?level=info");            				
				
				LOG.trace("....");
				
				from("direct:start").process(new Processor() {
					public void process(Exchange exchange) throws Exception {

						LOG.trace("Processing receiving msgs...");

						exchange.getIn().setHeader("ArtifactName", "NotInTest");
						exchange.getIn().setHeader("OperationName", "inc");

						List<Object> throwData = new ArrayList<Object>();
						Object obj1 = new Object();
						obj1 = "inc_param";
						throwData.add(obj1);
						Object obj3 = new Object();
						obj3 = msgCount;
						throwData.add(obj3);
						exchange.getIn().setBody(throwData);
						LOG.trace("msg processed!");
					}
				}).to("artifact:cartago");
			
				LOG.trace(".....");
			}
		};
	}
	
	
}




