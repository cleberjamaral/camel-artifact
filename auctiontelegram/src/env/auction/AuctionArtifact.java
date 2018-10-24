package auction;

import cartago.*;
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
import jason.asSyntax.Atom;

public class AuctionArtifact extends CamelArtifact {

	String currentWinner = "no_winner";
	private List<String> participants;
	
	public void init() {
		participants = new ArrayList<String>();
		
		defineObsProperty("minOffer", 1980);
		defineObsProperty("participants", 0);
		defineObsProperty("winner", new Atom(currentWinner)); // Atom is a Jason type
		
		final CamelContext camelContext = new DefaultCamelContext();

		// This simple application has only one component receiving messages from the route and producing operations
		camelContext.addComponent("artifact", new ArtifactComponent(this.getIncomingOpQueue(),this.getOutgoingOpQueue()));
		camelContext.addComponent("telegram", new TelegramComponent());
		/* Create the routes */
		try {
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() {
					from("telegram:bots/708413344:AAHcw9VwDKGnNm063_5vjw_VVQlCigy0ZMs?chatId=-274694619")
							.transform(body().convertToString()).to("artifact:cartago");

					from("artifact:cartago").process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setBody(exchange.getIn().getBody().toString());
						}
					}).to("telegram:bots/708413344:AAHcw9VwDKGnNm063_5vjw_VVQlCigy0ZMs?chatId=-274694619");
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
	public void getIn() {
		ObsProperty opParticipants  = getObsProperty("participants");
		opParticipants.updateValue(opParticipants.intValue()+1);
		participants.add(getCurrentOpAgentId().getAgentName());
		
		List<Object> params  = new ArrayList<Object>();
		params.add(getId());
		sendMsg("ArtifactA","KA",params);
	}

	@OPERATION
	public void getOut() {
		ObsProperty opParticipants  = getObsProperty("participants");
		if (opParticipants.intValue() > 1) {
			opParticipants.updateValue(opParticipants.intValue()-1);
			participants.remove(getCurrentOpAgentId().getAgentName());
		}
		
		if (opParticipants.intValue() == 1) {
			 currentWinner = participants.get(0);
			 getObsProperty("winner").updateValue(new Atom(currentWinner));
		}
	}

	@OPERATION
	public void setOffer(double minValue) {
		ObsProperty opMinOffer  = getObsProperty("minOffer");
		opMinOffer.updateValue(minValue);			
		//System.out.println("Minimum price set as " + minValue + " from " + getCurrentOpAgentId().getAgentName());
	}
}
