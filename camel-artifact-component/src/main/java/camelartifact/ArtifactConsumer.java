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
package camelartifact;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.ScheduledPollConsumer;

import simplelogger.SimpleLogger;

/**
 * For JaCaMo project, use SimpleLogger instead of log4j
 * Log allows: Trace, Debug, Info, Warn, Error and Fatal messages
 */
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * @author Cleber
 * 
 *         Consumer side of CamelArtifact class. This is the side responsible for transmissions of artifact messages to
 *         the route, which will be delivered to any camel endpoint (to a producer). The message to be send is in
 *         "OpRequest" format, which is mainly formed by an artifact identification, operation and parameters. Here the
 *         meaning adopted is: "Incoming" refers to packets which originate elsewhere and arrive at the artifact, while
 *         "outgoing" refers to packets which originate at the artifact and arrive elsewhere.
 *         http://serverfault.com/questions/443038/what-does-incoming-and-outgoing-traffic-mean
 */
public class ArtifactConsumer extends ScheduledPollConsumer {

	// See import comments for detalis about LOG
	// private static final transient Logger LOG = LoggerFactory.getLogger(ArtifactProducer.class);
	private static SimpleLogger LOG = new SimpleLogger();
	private ConcurrentLinkedQueue<OpRequest> outgoingOpQueue;
	private final ArtifactEndpoint endpoint;

    public ArtifactConsumer(ArtifactEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
        
        outgoingOpQueue = endpoint.getOutgoingOpQueue();
    }
    
    @Override
    protected int poll() throws Exception {
        Exchange exchange = endpoint.createExchange();

        try {
        	
            // send message to next processor in the route
			OpRequest newOp;
			if ((newOp = outgoingOpQueue.poll()) != null) {
				LOG.debug("A message was founded in the outgoing queue! Artifact:" + newOp.getArtifactName()
						+ ", op:" + newOp.getOpName() + ", body " + newOp.getParams().toString());
				exchange.getIn().setHeader("ArtifactName", newOp.getArtifactName());
				exchange.getIn().setHeader("OperationName", newOp.getOpName());
				exchange.getIn().setBody(newOp.getParams());
	            getProcessor().process(exchange);
	            return 1; // number of messages polled
			}
            return 0; // number of messages polled
			
        } finally {
			// log exception if an exception occurred and was not handled
			if (exchange.getException() != null) {
				getExceptionHandler().handleException("Out: Error processing exchange", exchange,
						exchange.getException());
			}
        }
    }
    
}
