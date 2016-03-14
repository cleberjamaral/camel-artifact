
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;

import com.summit.camel.opc.Opcda2Component;

import camelartifact.ArtifactComponent;
import camelartifact.ArtifactContainer;
import camelartifact.test.App;

public class RouteTestArtifactOPCDA extends CamelTestSupport{

	static ArtifactContainer container;
	static Opcda2Component opcda2;
	static String containerId;
	
	static String domain = "localhost";
	static String user = "cleber";
	static String password = "tna24hps";
	static String clsid = "f8582cf2-88fb-11d0-b850-00c0f0104305";
	static String host = "localhost";
    
	public static void main(String[] args) throws Exception {

	    container = new ArtifactContainer(App.class.getClassLoader(), App.class.getPackage());
		final CamelContext camel = new DefaultCamelContext();
		camel.addComponent("artifact", new ArtifactComponent(container));
		camel.addComponent("opcda2", new Opcda2Component());
		
		/* Create the routes */
		camel.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                String uriString = "opcda2:opcdaTest/Simulation Items/Random?delay=1000&host=" + host + "&clsId=" + clsid + "&username=" + user + "&password=" + password + "&domain=" + domain;
                from(uriString).to("log:OPCTest?level=info").to("mock:result").to("artifact:shopfloor/loader");
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
