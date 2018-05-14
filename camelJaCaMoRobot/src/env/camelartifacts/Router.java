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
@ARTIFACT_INFO(outports = { @OUTPORT(name = "out-1") })

public class Router extends CamelArtifact {
	
	void init() {
		
		// final Random rand = new Random();
		final CamelContext camelContext = new DefaultCamelContext();

		// This simple application has only one component receiving messages from the route and producing operations
		camelContext.addComponent("artifact", new ArtifactComponent(this.getIncomingOpQueue(),this.getOutgoingOpQueue()));

		/* Create the routes */
		try {
			camelContext.addRoutes(new RouteBuilder() {
				//int testAsimov = 0;
				
				@Override
				public void configure() {
					/*
					//Testing a message to be consumed by the router
					log("Generating a 'local' test message without parameters...");
					from("timer:test?period=900").process(new Processor() {
						public void process(Exchange exchange) throws Exception {

							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "rinc2");

						}
					}).to("artifact:cartago");// .to("log:CamelArtifactLogger?level=info");

					//Testing a message to be forwarded to counter artifact
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

					
					//Testing a message to be consumed by the router
					log("Generating a 'remote' test message without parameters...");
					from("timer:test?period=2500").process(new Processor() {
						public void process(Exchange exchange) throws Exception {

							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "inc2");
							exchange.getIn().setBody("Hello MosQuiTTo! I am the router sending you a message!");
						}
					}).to("mqtt:camelArtifact?host=tcp://192.168.0.113:1883&publishTopicName=test.mqtt.topic")
					.to("log:CamelArtifactLoggerOut?level=info");
					
					from("mqtt:camelArtifact?host=tcp://192.168.0.113:1883&subscribeTopicName=test.mqtt.topic")
					.transform(body().convertToString())
					.to("log:CamelArtifactLoggerOut?level=info");

					from("netty4:tcp://192.168.0.114:2000?textline=true&sync=false&delimiter=NULL")
						.to("log:CamelArtifactLoggerOut?level=info");
	
					from("timer:test?period=2000").process(new Processor() {					
						public void process(Exchange exchange) throws Exception {
							
							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "inc2");
							if (testAsimov == 1) //Anda para direita
								exchange.getIn().setBody("       100         0         5         0         0         0        30         0       100        90");
							else if (testAsimov == 2) //Anda para esquerda
								exchange.getIn().setBody("       100         0         5         0         0         0        30         0       100       270");
							else //Fica parado
								exchange.getIn().setBody("       100         0         5         0         0         0         0         0         0         0");
														//1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
							if (testAsimov < 2) testAsimov++; else testAsimov = 0; 
							
						}
					}).to("netty4:tcp://192.168.0.114:2000?textline=true&sync=false&delimiter=NULL");					
					
					//Sending a message to the route
					from("artifact:cartago").process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							log.trace("Processing sending msgs...");
						}
					}).to("log:CamelArtifactLogger?level=info");
*/
					from("timer:test?period=2000").process(new Processor() {					
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setBody("Olá Mundo!");
						}
					}).to("log:CamelArtifactLoggerOut?level=info");

					from("timer:test?period=1000")//.transform(body().prepend("Olá Mundo! "))
					  .to("mqtt:mytest?host=tcp://192.168.0.113:1883&publishTopicName=test.mqtt.topic");

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
	void rinc2() {
		log("Router:rinc2 called! A tick signal is going to be send.");
		signal("tick");
	}

	@INTERNAL_OPERATION
	void rinc3(String str, int i) {
		log("Router:rinc3 called! A tick signal is going to be send. Parameters: " + str + ", " + i);
		signal("tick");
		execInternalOp("rinc4");
	}

	@INTERNAL_OPERATION
	void rinc4() {
		log("Router:rinc4 called!");
		List<Object> params  = new ArrayList<Object>();
		params.add(4);
		sendMsg("Router","rinc3",params);
	}
}
