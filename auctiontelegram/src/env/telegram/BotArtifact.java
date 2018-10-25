package telegram;

import cartago.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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

@ARTIFACT_INFO(outports = { @OUTPORT(name = "out-1") })

public class BotArtifact extends CamelArtifact {
	
	@OPERATION
	public void startCamel(String chatId) {
		String token = null;
		BufferedReader telegramtoken;
		try {
			telegramtoken = new BufferedReader(new FileReader("../../sensitiveData/" + getId().toString() + ".token"));
			token = telegramtoken.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String telegramURI = "telegram:bots/" + token + "?" + "chatId=" + chatId;
		final CamelContext camelContext = new DefaultCamelContext();

		// This simple application has only one component receiving messages from the route and producing operations
		camelContext.addComponent("artifact", new ArtifactComponent(this.getIncomingOpQueue(),this.getOutgoingOpQueue()));
		camelContext.addComponent("telegram", new TelegramComponent());
		/* Create the routes */
		try {
			camelContext.addRoutes(new RouteBuilder() {
//			Telegram message structure
//				IncomingMessage{
//				messageId=139, 
//				date=2018-10-25T14:24:22Z, 
//				from=User{
//					id=NNNNN, 
//					firstName='Cleber', 
//					lastName='null', 
//					username='cleberjamaral'
//				}, 
//				text='getIn', 
//				chat=Chat{id='NNNNN', title='null', type='private'}, 
//				photo=null, video=null, audio=null, document=null
//				}					
				@Override
				public void configure() {
					from(telegramURI)
					.process(new Processor() {
						public void process(Exchange exchange) {
							String str = exchange.getIn().getBody(String.class);
							//System.out.println("String:" + str);
							
							//System.out.println("String2:" + exchange.getIn().getBody().toString());
							
							//Map<String,Object> head = exchange.getIn().getHeaders();
							//System.out.println("Head:" + head);
							
							//HashMap<String, Object> body = exchange.getIn().getBody(HashMap.class);
							//System.out.println("Map:" + body.toString());

							exchange.getIn().setHeader("ArtifactName", getId().toString());
							exchange.getIn().setHeader("OperationName", str);
							exchange.getIn().setBody(null);
					}})
					//.transform(body().convertToString())
					.to("artifact:cartago");

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

	@OPERATION
	public void getIn() {
		List<Object> params  = new ArrayList<Object>();
		params.add(getId().toString());
		params.add("getIn");
		sendMsg(getId().toString(),"telegram",params);
		
		try {
			if (getCurrentOpAgentId() != null)
				execLinkedOp("out-1","getInAuction",getCurrentOpAgentId().getAgentName());
			else
				execLinkedOp("out-1","getInAuction","human");
		} catch (OperationException e) {
			e.printStackTrace();
		}
	}

	@OPERATION
	public void getOut() {
		List<Object> params  = new ArrayList<Object>();
		params.add(getId().toString());
		params.add("getOut");
		sendMsg(getId().toString(),"telegram",params);

		try {
			if (getCurrentOpAgentId() != null)
				execLinkedOp("out-1","getOutAuction",getCurrentOpAgentId().getAgentName());
			else
				execLinkedOp("out-1","getOutAuction","human");

		} catch (OperationException e) {
			e.printStackTrace();
		}
	}
}
