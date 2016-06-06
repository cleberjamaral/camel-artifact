
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;

import com.summit.camel.opc.Opcda2Component;

import camelartifact.ArtifactComponent;
import camelartifact.CamelArtifact;

public class RouteTestArtifactOPCDA extends CamelTestSupport{

	static Opcda2Component opcda2;
	static String containerId;
	
	static String domain = "localhost";
	static String user = "cleber";
	static String password = "MAS4opc2016";
	static String clsid = "f8582cf2-88fb-11d0-b850-00c0f0104305";
	static String host = "192.168.0.107";
    
	public static void main(String[] args) throws Exception {

		final CamelContext camel = new DefaultCamelContext();
//		camel.addComponent("artifact", new ArtifactComponent());
		CamelArtifact camelartif = new CamelArtifact();
		camelartif.setListenCamelRoute(true);
		
		camel.addComponent("artifact", new ArtifactComponent(camelartif.getIncomingOpQueue(),camelartif.getOutgoingOpQueue()));
		
		camel.addComponent("opcda2", new Opcda2Component());
		
		/* Create the routes */
		camel.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                String uriString = "opcda2:Matrikon.OPC.Simulation.1?delay=1000&host=" + host + "&clsId=" + clsid + "&username=" + user + "&password=" + password + "&domain=" + domain;
                from(uriString).process(new Processor() {
					public void process(Exchange exchange) throws Exception {

						exchange.getIn().setHeader("ArtifactName", "NotInTest");
						exchange.getIn().setHeader("OperationName", "inc");
					}
				})
                .to("artifact:cartago");
            }
		});
		
		// start routing
		System.out.println("Starting camel...");
		camel.start();
		System.out.println("Starting router...");

		Thread thread = new Thread() {
			public void run() {
				try {
					System.out.println("Running...");
					while (true) ;
				} catch (Exception e) {
				}
			}
		};
		thread.start();

		System.out.println("Ready!");
	}
}
