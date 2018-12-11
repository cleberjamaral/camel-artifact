package camelartifacts;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import camelartifact.ArtifactComponent;
import camelartifact.CamelArtifact;
import cartago.ARTIFACT_INFO;
import cartago.OPERATION;
import cartago.OUTPORT;

/**
 * CArtAgO artifact code for project camelJaCaMoRobot This is just an application of CamelArtifact component showing a
 * full use of simple Jason Agents and simple CArtAgO artifacts. The Router CamerArtifact is a common artifact and also
 * a gateway which means that some messages that are addressed to Router will be processed here using internal
 * operations and messages addressed to others artifacts will be forwarded
 */
@ARTIFACT_INFO(outports = { @OUTPORT(name = "out-1") })

public class ArtifactC extends CamelArtifact {
	
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
					
					interceptSendToEndpoint("mqtt:mytest?host=tcp://broker.mqttdashboard.com:1883&publishTopicName=camelArtifactB&mqttQosPropertyName=ExactlyOnce")
					.when(body().contains("ArtifactC")).skipSendToOriginalEndpoint();								

					interceptSendToEndpoint("mqtt:mytest?host=tcp://broker.mqttdashboard.com:1883&publishTopicName=camelArtifactC&mqttQosPropertyName=ExactlyOnce")
					.when(body().contains("ArtifactB")).skipSendToOriginalEndpoint();
					
					from("artifact:cartago")
					.process(new Processor() { 
						public void process(Exchange exchange) throws Exception {
							Map<String, Object> headerItems = new TreeMap<String, Object>();
							headerItems.putAll(exchange.getIn().getHeaders());
							if (headerItems.get("ArtifactName").toString().equals("ArtifactC"))
								exchange.getIn().setBody(exchange.getIn().getBody().toString()+" - ResourceC do something!");
							else
								exchange.getIn().setBody(exchange.getIn().getBody().toString());
						}
					})
					//.transform().mvel("(request.body[0] * 1.8 + 32).toString()") // Tests done for the paper showing a convertion fahrenheit - celsius
					.to("mqtt:mytest?host=tcp://broker.mqttdashboard.com:1883&publishTopicName=camelArtifactB&mqttQosPropertyName=ExactlyOnce")
					.to("mqtt:mytest?host=tcp://broker.mqttdashboard.com:1883&publishTopicName=camelArtifactC&mqttQosPropertyName=ExactlyOnce")
					.to("log:CamelArtifactLoggerOut?level=info");
					
					from("mqtt:camelArtifact?host=tcp://broker.mqttdashboard.com:1883&subscribeTopicName=camelArtifactB&mqttQosPropertyName=ExactlyOnce")
					.transform(body().convertToString())
					.process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setHeader("ArtifactName", exchange.getIn().getBody().toString().replaceAll("\\[", "").replaceAll("\\]",""));
							exchange.getIn().setHeader("OperationName", "kaBackB");
							exchange.getIn().setBody(null);
							/* Tests done for the paper showing a convertion fahrenheit - celsius
							List listp = new ArrayList<Double>();
							//listp.add(Float.valueOf(new DecimalFormat("#.##").format((exchange.getIn().getBody(Float.class) - 32) / 1.8)));
							listp.add(Double.valueOf((exchange.getIn().getBody(Double.class) - 32) / 1.8));
							exchange.getIn().setBody(listp);*/

					}})
					.to("artifact:cartago").to("log:CamelArtifactLoggerOut?level=info");
					
					from("mqtt:camelArtifact?host=tcp://broker.mqttdashboard.com:1883&subscribeTopicName=camelArtifactC&mqttQosPropertyName=ExactlyOnce")
					.log("MQTT:::: Body class is ${body.class}")
					//.transform().mvel("[ (request.body[0].toString() - 32) / 1.8 ]") // Tests done for the paper showing a convertion fahrenheit - celsius
					.process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setHeader("ArtifactName", "ArtifactC");
							exchange.getIn().setHeader("OperationName", "kaBackC");
							exchange.getIn().setBody(null);
					}})
					//.setHeader("ArtifactName", simple("ArtifactC")) // Tests done for the paper showing a convertion fahrenheit - celsius
					//.setHeader("OperationName", simple("kaBackC")) // Tests done for the paper showing a convertion fahrenheit - celsius
					.to("artifact:cartago")
					.log("CARTAGO::::: Body class is ${body.class}")
					.to("log:CamelArtifactLoggerOut?level=info");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		// start routing
		log("Starting camel... (context: "+camelContext+" route definitions: "+camelContext.getRouteDefinitions().toString()+") ");
		try {
			camelContext.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log("Starting artifact...");
	}

	@OPERATION
	void sendKA() {
		log("trying to send keepalive message...");
		List<Object> params  = new ArrayList<Object>();
		params.add("ArtifactC: Keep alive!");
		sendMsg("ArtifactC","KA",params);
	}

	@OPERATION
	void kaBackC() {
		log("received keepalive back!");
	}	

	/* Tests done for the paper showing a convertion fahrenheit - celsius
	@OPERATION
	void sendKA() {
		log("trying to send keepalive message...");
		List<Object> params  = new ArrayList<Object>();
		//params.add(getId());
		float temperatureCelsius = 25.0f;
		params.add(temperatureCelsius);
		sendMsg("ArtifactC","KA",params);
	}

	@OPERATION
	void kaBackC(double p) {
		log("\n\n************** received keepalive back! with p=" + p + "\n\n");
	}
	*/
	
}
