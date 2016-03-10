package camelartifact.test;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import camelartifact.*;

public class App {

	static ArtifactContainer container;
	static String containerId;

	public static void main(String[] args) throws Exception {

		container = new ArtifactContainer(App.class.getClassLoader(), App.class.getPackage());
		final CamelContext camel = new DefaultCamelContext();
		camel.addComponent("agent", new ArtifactComponent(container));

		/* Create the routes */
		camel.addRoutes(new RouteBuilder() {
			@Override
			public void configure() {
				from("timer:test?period=200").transform(simple("tick(${property.CamelTimerCounter})"))
						.to("agent:percept?persistent=false&updateMode=replace");
			}
		});

		// start routing
		camel.start();
		System.out.println("Starting router...");

		// Start the agents after starting the routes
		container.startAllAgents();

		System.out.println("... ready.");
	}
}