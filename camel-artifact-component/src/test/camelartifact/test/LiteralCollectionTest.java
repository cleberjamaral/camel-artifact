package camelartifact.test;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;
import java.util.Collection;
import java.util.ArrayList;

import camelartifact.*;

public class LiteralCollectionTest {

	static ArtifactContainer container;
	static String containerId;

	public static void main(String[] args) throws Exception {

		container = new ArtifactContainer(LiteralCollectionTest.class.getClassLoader(),
				LiteralCollectionTest.class.getPackage());
		final CamelContext camel = new DefaultCamelContext();
		camel.addComponent("agent", new ArtifactComponent(container));

		/* Create the routes */
		camel.addRoutes(new RouteBuilder() {
			@Override
			public void configure() {
				from("timer:test?period=200").process(new Processor() {
					public void process(Exchange exchange) throws Exception {
						Long counter = exchange.getProperty(Exchange.TIMER_COUNTER, Long.class);
					}
				}).to("agent:percept?persistent=false&updateMode=replace");
			}
		});

		// start routing
		camel.start();
		System.out.println("Starting router...");

		// Start the agents after starting the routes
		container.startAllArtifacts();

		System.out.println("... ready.");
	}

}