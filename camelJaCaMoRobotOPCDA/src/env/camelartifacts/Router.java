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

import com.summit.camel.opc.Opcda2Component;

import camelartifact.CamelArtifact;
import camelartifact.ArtifactComponent;
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

	static Opcda2Component opcda2;
	static String containerId;
	
	static String domain = "localhost";
	static String user = "cleber";
	static String password = "MAS4opc2016";
	static String clsid = "f8582cf2-88fb-11d0-b850-00c0f0104305";
	static String host = "192.168.0.107";

	int contagem = 0;

	void init() {

		// final Random rand = new Random();
		final CamelContext camelContext = new DefaultCamelContext();

		// This simple application has only one component receiving messages from the route and producing operations
		camelContext.addComponent("artifact", new ArtifactComponent(this.getIncomingOpQueue(),this.getOutgoingOpQueue()));


		/* Create the routes */
		try {
			camelContext.addRoutes(new RouteBuilder() {
				int testAsimov = 0;

				@Override
				public void configure() {
					
					log("Receiving opc messages...");
/*					
					//***********************************************************************************
					//MQTT Tests step 1: Generate a temporized message coming from a route invoking inc
					from("timer:test?period=5000").process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "inc");
						}
					})
	                .to("artifact:cartago").to("log:MQTTLogger1?level=info");
					//MQTT Tests step 2: Send to mosquitto a message
					from("artifact:cartago").process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setBody("Hello MosQuiTTo!");
						}
					}).to("mqtt:camelArtifact?host=tcp://192.168.0.113:1883&publishTopicName=test.mqtt.topic")
					.to("log:camelArtifactLogger1?level=info");
					//MQTT Tests step 3: Receive from mosquitto same message
					from("mqtt:camelArtifact?host=tcp://192.168.0.113:1883&subscribeTopicName=test.mqtt.topic")
					.process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "reply");
							exchange.getIn().setBody("");
						}
					})
					.to("artifact:cartago").to("log:MQTTLogger3?level=info");
					//MQTT Tests END
					//***********************************************************************************
*/
/*					
					//***********************************************************************************
					//OPC-DA Tests step 1: Receiving a message from OPC-DA
					//Matrikon simulation server is sending a unique message continuously without any asking proccess
	                //Bellow this project is testing camel artifact producer with this feature of matrikon server
	                //The expected response is like ...Bucket Brigade.ArrayOfString={value=[Ljava.lang.String;@14070c0}, 
					//Bucket Brigade.Boolean={value=false}, Bucket Brigade.Int1={value=1}, Bucket Brigade.Int2={value=2}... 
					String uriString = "opcda2:Matrikon.OPC.Simulation.1?delay=2000&host=" 
	                		+ host + "&clsId=" + clsid + "&username=" + user + "&password=" + password + "&domain=" 
	                		+ domain + "&diffOnly=false";
	                from(uriString).process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "setIntTag");
							Map<String, Map<String, Object>> body = exchange.getIn().getBody(Map.class);
							List<Object> listObj = new ArrayList<Object>();
							for (String tagName : body.keySet()) {
								Object value = body.get(tagName).get("value");
								//log("Tag received: " + tagName + " = " + value.toString());
								//For this test we are looking for Bucket Brigade.Int1 tag. It is simulating
								//a receiving process of a tag, so this tagname and tagvalue are being added
								//in the object list to be processed be producer
								if (tagName.equals("Bucket Brigade.Int1")){
									log("Adding tag" + tagName + " = " + value.toString() + " in the queue");
									listObj.add("Bucket Brigade.Int1");
									listObj.add(value);
								}
							}
							exchange.getIn().setBody(listObj);
						}
					})
	                .to("artifact:cartago").to("log:OPCDALogger1?level=info");
					//OPC-DA Tests step 2: Setting a tag
					from("artifact:cartago").process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							log.trace("Processing sending msgs...");
							//The expected format is something like: "Bucket Brigade.Int1={value=1}"
							Map<String, Map<String, Object>> data = new TreeMap<String, Map<String, Object>>();
							Map<String, Object> dataItems = new TreeMap<String, Object>();
							List<Object> params  = exchange.getIn().getBody(List.class);
							dataItems.put("value",params.get(1));
							data.put(params.get(0).toString(),dataItems);
							exchange.getIn().setBody(data);
						}
					}).to(uriString).to("log:OPCDALogger2?level=info");
					//OPC-DA Tests END
					//***********************************************************************************
*/				
					
					//***********************************************************************************
					//Asimov Tests step 1: Generate a temporized message coming from a route invoking goRobot
					from("timer:test?period=5000").process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "goRobot");
						}
					})
	                .to("artifact:cartago").to("log:ASIMOVLogger1?level=info");
					//Asimov Tests step 2: Send a command to asimov
					from("artifact:cartago").process(new Processor() {					
						public void process(Exchange exchange) throws Exception {
							log.trace("Processing sending msgs...");
							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "goRobot");
							if (testAsimov == 1) //Anda para direita
								exchange.getIn().setBody("       100         0         5         0         0         0        30         0       100        90");
							else if (testAsimov == 2) //Anda para esquerda
								exchange.getIn().setBody("       100         0         5         0         0         0        30         0       100       270");
							else //Fica parado
								exchange.getIn().setBody("       100         0         5         0         0         0         0         0         0         0");
														//1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
							if (testAsimov < 2) testAsimov++; else testAsimov = 0; 	
						}
					}).to("netty4:tcp://192.168.0.114:2000?textline=true&sync=false&delimiter=NULL")
					.to("log:ASIMOVLogger2?level=info");
					//Asimov Tests step 3: Send a command to asimov
					from("netty4:tcp://192.168.0.114:2000?textline=true&sync=false&delimiter=NULL")
					.process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "reply");
							exchange.getIn().setBody("");
						}
					})
					.to("artifact:cartago").to("log:ASIMOVLogger3?level=info");

					//Testing a message to be consumed by the router
					log("Generating a 'remote' test message without parameters...");
					//ASIMOV Tests END
					//***********************************************************************************
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
	void inc() {
		log("Router: inc called!");
		if (contagem++ % 2 == 0) {
			sendMsg("Router","inc",null);
		}
	}

	@INTERNAL_OPERATION
	void goRobot() {
		log("Router: goRobot called!");
		if (contagem++ % 2 == 0) {
			sendMsg("Router","goRobot", null);
		}
	}

	@INTERNAL_OPERATION
	void inc3(String str, int i) {
		log("Router:inc3 called! A tick signal is going to be send. Parameters: " + str + ", " + i);
		signal("tick");
		execInternalOp("inc4");
	}

	@INTERNAL_OPERATION
	void setIntTag(String str, int i) {
		log("Router: setIntTag called!");
		List<Object> params  = new ArrayList<Object>();
		params.add("Bucket Brigade.Int1");
		params.add(contagem++);
		sendMsg("Router","setIntTag",params);
	}
	
	@INTERNAL_OPERATION
	void reply() {
		log("Router:reply called!");
	}
}

