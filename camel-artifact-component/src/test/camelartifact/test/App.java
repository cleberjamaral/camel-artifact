package camelartifact.test;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
				.to("artifact:shopfloor/loader").process(new Processor() {
					public void process(Exchange exchange) throws Exception 
					{
						Long counter = exchange.getProperty(Exchange.CONTENT_ENCODING, Long.class);
					}
				})
                .to("mock:result");

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

