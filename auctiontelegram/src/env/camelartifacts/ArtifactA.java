package camelartifacts;


import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.telegram.TelegramComponent;
import org.apache.camel.impl.DefaultCamelContext;

import camelartifact.ArtifactComponent;
import camelartifact.CamelArtifact;
import cartago.OPERATION;

/**
 * CArtAgO artifact code for project camelJaCaMoRobot This is just an application of CamelArtifact component showing a
 * full use of simple Jason Agents and simple CArtAgO artifacts. The Router CamerArtifact is a common artifact and also
 * a gateway which means that some messages that are addressed to Router will be processed here using internal
 * operations and messages addressed to others artifacts will be forwarded
 */

public class ArtifactA extends CamelArtifact {
	
	void init() {
		
		// final Random rand = new Random();
		final CamelContext camelContext = new DefaultCamelContext();

		// This simple application has only one component receiving messages from the route and producing operations
		camelContext.addComponent("artifact", new ArtifactComponent(this.getIncomingOpQueue(),this.getOutgoingOpQueue()));
		camelContext.addComponent("telegram", new TelegramComponent());

		/* Create the routes */
		try {
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() {
					from("artifact:cartago")
					.process(new Processor() { 
						public void process(Exchange exchange) throws Exception {exchange.getIn().setBody(exchange.getIn().getBody().toString());}
					})
					.to("mqtt:mytest?host=tcp://broker.mqttdashboard.com:1883&publishTopicName=camel"+getId()+"&mqttQosPropertyName=ExactlyOnce")
					.to("log:CamelArtifactLoggerOut?level=info");
					
					
					from("mqtt:camelArtifact?host=tcp://broker.mqttdashboard.com:1883&subscribeTopicName=camel"+getId()+"&mqttQosPropertyName=ExactlyOnce")
					.transform(body().convertToString())
					.process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setHeader("ArtifactName", exchange.getIn().getBody().toString().replaceAll("\\[", "").replaceAll("\\]",""));
							exchange.getIn().setHeader("OperationName", "kaBackA");
							exchange.getIn().setBody(null);
					}})
					.to("artifact:cartago").to("log:CamelArtifactLoggerOut?level=info");
					
					from("telegram:bots/708413344:AAHcw9VwDKGnNm063_5vjw_VVQlCigy0ZMs?chatId=-274694619")
					.transform(body().convertToString()).to("agent:percept?updateMode=add&persistent=true");

			from("agent:action?exchangePattern=InOut&actionName=auction")
					.log("action: ${header[actionName]}, Params: ${header[params]}")
					.to("telegram:bots/708413344:AAHcw9VwDKGnNm063_5vjw_VVQlCigy0ZMs?chatId=-274694619");

			from("agent:action?actionName=winnerAgent")
					.log("action: ${header[actionName]}, Params: ${header[params]}")
					.to("telegram:bots/708413344:AAHcw9VwDKGnNm063_5vjw_VVQlCigy0ZMs?chatId=-274694619");

			from("agent:action?actionName=present").log("action: ${header[actionName]}, Params: ${header[params]}")
					.to("telegram:bots/708413344:AAHcw9VwDKGnNm063_5vjw_VVQlCigy0ZMs?chatId=-274694619");

			from("agent:action?actionName=left").log("action: ${header[actionName]}, Params: ${header[params]}")
					.to("telegram:bots/708413344:AAHcw9VwDKGnNm063_5vjw_VVQlCigy0ZMs?chatId=-274694619");

			from("agent:action?actionName=iAmTheWinner")
					.log("action: ${header[actionName]}, Params: ${header[params]}")
					.to("telegram:bots/708413344:AAHcw9VwDKGnNm063_5vjw_VVQlCigy0ZMs?chatId=-274694619");
					
					
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
		params.add(getId());
		sendMsg("ArtifactA","KA",params);
	}
	
	@OPERATION
	void kaBackA() {
		log("received keepalive back!");
	}	
}
