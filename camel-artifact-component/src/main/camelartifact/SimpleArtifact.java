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

import java.io.Serializable;
import java.util.*;
import java.util.logging.*;

import cartago.*;

/**
 */
public class SimpleArtifact extends Artifact implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SimpleArtifact.class.getName());
    private ArtifactContainer container;
    private boolean hasStarted;
    private String name;
    private Vector<ArtifactConsumer> myConsumers;  

    public SimpleArtifact(ArtifactContainer container, String filePathOrURL, String name) {
        this.container = container;
        this.name = name;
        hasStarted = false;
        
    	myConsumers = new Vector<ArtifactConsumer>();
        
    }

    public void addToMyConsumers(ArtifactConsumer myConsumer)
    {
    	synchronized (myConsumers) {    		
    		myConsumers.add( myConsumer);				
		}    	
    }   
    
    /**
	 * Artifact run() process, just to keep some thread and see
	 * the routes working
	 */
	public void run() {
		hasStarted = true;
		Thread thread = new Thread() {
			public void run() {
				try {
					System.out.println("Running...");
					while (true) ;
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Run error", e);
					hasStarted = false;
				}
			}
		};
		thread.start();
	}
    
    public boolean getHasStarted() {
		return hasStarted;
	}

	public void setHasStarted(boolean hasStarted) {
		this.hasStarted = hasStarted;
	}

	/**
     * @param consumerType
     * @return
     * Returns the set of consumers for a given consumer type (messages or actions)
     */
    public List<ArtifactConsumer> getValidConsumers(String consumerType)
    {
    	List<ArtifactConsumer> consumers = new ArrayList<ArtifactConsumer>();
    	for(ArtifactConsumer myConsumer: myConsumers)
    	{    		
    		if (myConsumer.toString().contains(consumerType))
    			consumers.add(myConsumer);
    	}
    	return consumers;
    }
}
