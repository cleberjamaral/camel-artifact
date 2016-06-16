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

package camelartifacts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.summit.camel.opc.Opcda2Component;

import camelartifact.ArtifactComponent;
import camelartifact.CamelArtifact;
import cartago.ARTIFACT_INFO;
import cartago.INTERNAL_OPERATION;
import cartago.OUTPORT;

/**
 * CArtAgO artifact code for project camelJaCaMoRobot This is just an application of CamelArtifact component showing a
 * full use of simple Jason Agents and simple CArtAgO artifacts. The Router CamerArtifact is a common artifact and also
 * a gateway which means that some messages that are addressed to Router will be processed here using internal
 * operations and messages addressed to others artifacts will be forwarded
 */
@ARTIFACT_INFO(outports = { @OUTPORT(name = "out-1"), @OUTPORT(name = "in-1") })
public class Router extends CamelArtifact {

	static Opcda2Component opcda2;
	static String containerId;
	
	static String domain = "localhost";
	static String user = "cleber";
	static String password = "MAS4opc2016";
	static String clsid = "f8582cf2-88fb-11d0-b850-00c0f0104305";
	static String host = "192.168.0.107";

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

					log("Receiving opc messages...");
	                /**
	                 * The Matrikon simulation server is sending a unique message continuously without any asking proccess
	                 * Bellow this project is testing camel artifact producer with this feature of matrikon server
	                 *     
	                 * Using .to("log:CamelArtifactLoggerIn?level=info") I had following response:
	                 * 
	                 * 5757 [Camel (camel-1) thread #0 - opcda2://Matrikon.OPC.Simulation.1] 
	                 * INFO CamelArtifactLoggerIn - Exchange[ExchangePattern: InOnly, BodyType: 
	                 * java.util.TreeMap, Body: {#MonitorACLFile={value=true}, 
	                 * @Clients={value=[Ljava.lang.String;@68662676}, Bucket Brigade.ArrayOfReal8={value=[Ljava.lang.Double;@a674286}, 
	                 * Bucket Brigade.ArrayOfString={value=[Ljava.lang.String;@14070c0}, Bucket Brigade.Boolean={value=false}, 
	                 * Bucket Brigade.Int1={value=1}, Bucket Brigade.Int2={value=2}, Bucket Brigade.Int4={value=4}, 
	                 * ... 
	                 * Write Only.UInt4={value=org.jinterop.dcom.core.VariantBody$EMPTY@1d3d888e}}] 
	                 */
					
					String uriString = "opcda2:Matrikon.OPC.Simulation.1?delay=2000&host=" 
	                		+ host + "&clsId=" + clsid + "&username=" + user + "&password=" + password + "&domain=" 
	                		+ domain + "&diffOnly=false";
	                from(uriString).process(new Processor() {
						public void process(Exchange exchange) throws Exception {
							exchange.getIn().setHeader("ArtifactName", "router");
							exchange.getIn().setHeader("OperationName", "inc4");
							Map<String, Map<String, Object>> body = exchange.getIn().getBody(Map.class);
							List<Object> listObj = new ArrayList<Object>();
							for (String tagName : body.keySet()) {
								Object value = body.get(tagName).get("value");
								log("Tag received: " + tagName + " = " + value.toString());
								/**
								 * For this test we are looking for Bucket Brigade.Int1 tag. It is simulating
								 * a receiving process of a tag, so this tagname and tagvalue are being added
								 * in the object list to be processed be producer
								 */
								if (tagName.equals("Bucket Brigade.Int1")){
									log("Adding tag" + tagName + " = " + value.toString() + " in the queue");
									listObj.add("Bucket Brigade.Int1");
									listObj.add(value);
								}
							}
							exchange.getIn().setBody(listObj);
						}
					})
	                .to("artifact:cartago");//to("log:CamelArtifactLoggerIn?level=info");
					
					from("artifact:cartago").process(new Processor() {
						public void process(Exchange exchange) throws Exception {

							log.trace("Processing sending msgs...");

							//The expected format is something like: "Bucket Brigade.Int1={value=1}"
							Map<String, Map<String, Object>> data = new TreeMap<String, Map<String, Object>>();
							Map<String, Object> dataItems = new TreeMap<String, Object>();

							List<Object> params  = exchange.getIn().getBody(List.class);
							dataItems.put("value",params.get(1));
							data.put(params.get(0).toString(),dataItems);
							
							exchange.getIn().setBody(data);

						}
					}).to(uriString).to("log:CamelArtifactLoggerOut?level=info");
					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		// start routing
		log("Starting camel...");
		try {
			camelContext.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log("Starting router...");
	}

	@INTERNAL_OPERATION
	void inc2() {
		log("Router:inc2 called! A tick signal is going to be send.");
		signal("tick");
	}

	@INTERNAL_OPERATION
	void inc3(String str, int i) {
		log("Router:inc3 called! A tick signal is going to be send. Parameters: " + str + ", " + i);
		signal("tick");
		execInternalOp("inc4");
	}

	@INTERNAL_OPERATION
	void inc4(String str, int i) {
		log("Router:inc4 called!");
		List<Object> params  = new ArrayList<Object>();
		params.add("Bucket Brigade.Int1");
		params.add(3);
		sendMsg("Router","inc4",params);
	}
}

