package telegram;

import cartago.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

public class BotArtifact extends CamelArtifact {

	private String token = null;
	private BufferedReader brTest;
	
	public void init() throws IOException {
		brTest = new BufferedReader(new FileReader("../../sensitiveData/" + getId().toString() + ".token"));
		token = brTest.readLine();
	}

	@OPERATION
	public void startCamel(String chatId) {
		String telegramURI = "telegram:bots/" + this.token + "?" + "chatId=" + chatId;
		final CamelContext camelContext = new DefaultCamelContext();

		// This simple application has only one component receiving messages from the route and producing operations
		camelContext.addComponent("artifact", new ArtifactComponent(this.getIncomingOpQueue(),this.getOutgoingOpQueue()));
		camelContext.addComponent("telegram", new TelegramComponent());
		/* Create the routes */
		try {
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() {
					from(telegramURI)
							.transform(body().convertToString()).to("artifact:cartago");

					from("artifact:cartago").process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setBody(exchange.getIn().getBody().toString());
						}
					}).to(telegramURI);
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
	public void sendString(String msg) {
		List<Object> params  = new ArrayList<Object>();
		params.add(getId().toString());
		params.add(msg);
		sendMsg(getId().toString(),"telegram",params);
	}	

}
