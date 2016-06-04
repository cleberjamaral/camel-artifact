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

package camelartifacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import camelartifact.ArtifactComponent;
import camelartifact.CamelArtifact;
import cartago.ARTIFACT_INFO;
import cartago.INTERNAL_OPERATION;
import cartago.OUTPORT;

/**
 * CArtAgO artifact code for project camelJaCaMoRobot This is just an application of CamelArtifact component showing a
 * full use of simple Jason Agents and simple CArtAgO artifacts. The Router CamerArtifact is a common artifact and also
 * a gateway which means that some messages that are addressed to Router will be processed here using internal
 * operations and messages addressed to others artifacts will be forwarded
 */
@ARTIFACT_INFO(outports = { @OUTPORT(name = "out-1"), @OUTPORT(name = "in-1") })
public class Router extends CamelArtifact {

	void init() {

		// final Random rand = new Random();
		final CamelContext camelContext = new DefaultCamelContext();

		// This simple application has only one component receiving messages from the route and producing operations
		camelContext.addComponent("artifact", new ArtifactComponent(this.getIncomingOpQueue(),this.getOutgoingOpQueue()));

		/* Create the routes */
		try {
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() {

					log("Generating a 'local' test message without parameters...");
					from("timer:test?period=900").process(new Processor() {
						public void process(Exchange exchange) throws Exception {

							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "inc2");

						}
					}).to("artifact:cartago");// .to("log:CamelArtifactLogger?level=info");

					log("Generating a 'local' test message with parameters...");
					from("timer:test?period=1200").process(new Processor() {
						public void process(Exchange exchange) throws Exception {

							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "inc3");
							List<Object> throwData = new ArrayList<Object>();
							throwData.add("string...test...router");
							Random rand = new Random();
							throwData.add(rand.nextInt(50));
							exchange.getIn().setBody(throwData);

						}
					}).to("artifact:cartago");// .to("log:CamelArtifactLogger?level=info");
/*				
					log("Generating a test message to be forwarded without parameters...");
					from("timer:test?period=800").process(new Processor() {
						public void process(Exchange exchange) throws Exception {

							exchange.getIn().setHeader("ArtifactName", "counter");
							exchange.getIn().setHeader("OperationName", "inc2");

						}
					}).to("artifact:cartago");// .to("log:CamelArtifactLogger?level=info");

					log("Generating a test message to be forwarded with parameters...");
					from("timer:test?period=1000").process(new Processor() {
						public void process(Exchange exchange) throws Exception {

							exchange.getIn().setHeader("ArtifactName", "counter");
							exchange.getIn().setHeader("OperationName", "inc3");
							List<Object> throwData = new ArrayList<Object>();
							throwData.add("string...test...counter");
							Random rand = new Random();
							throwData.add(rand.nextInt(50));
							exchange.getIn().setBody(throwData);

						}
					}).to("artifact:cartago");// .to("log:CamelArtifactLogger?level=info");
					log("Generating a test message to be forwarded with parameters...");
					from("timer:test?period=500").process(new Processor() {
						public void process(Exchange exchange) throws Exception {

							exchange.getIn().setHeader("ArtifactName", "counter");
							exchange.getIn().setHeader("OperationName", "inc3");
							List<Object> throwData = new ArrayList<Object>();
							throwData.add("string...test");
							Random rand = new Random();
							throwData.add(rand.nextInt(50));
							exchange.getIn().setBody(throwData);

						}
					}).to("artifact:cartago");// .to("log:CamelArtifactLogger?level=info");
*/
					
					from("artifact:cartago").process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							log.trace("Processing sending msgs...");
						}
					}).to("log:CamelArtifactLogger?level=info");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		// start routing
		log("Starting camel...");
		try {
			camelContext.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log("Starting router...");
	}

	@INTERNAL_OPERATION
	void inc2() {
		log("Router:inc2 called! A tick signal is going to be send.");
		signal("tick");
	}

	@INTERNAL_OPERATION
	void inc3(String str, int i) {
		log("Router:inc3 called! A tick signal is going to be send. Parameters: " + str + ", " + i);
		signal("tick");
		execInternalOp("inc4");
	}

	@INTERNAL_OPERATION
	void inc4() {
		log("Router:inc4 called!");
		List<Object> params  = new ArrayList<Object>();
		params.add(4);
		sendMsg("Router","inc4",params);
	}
}
