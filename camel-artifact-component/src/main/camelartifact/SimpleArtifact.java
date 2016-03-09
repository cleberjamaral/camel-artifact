///////////////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2013 Cranefield S., Ranathunga S. All rights reserved.               /
// ---------------------------------------------------------------------------------- /
// This file is part of camel_jason.                                                  /

//    camel_jason is free software: you can redistribute it and/or modify             /
//   it under the terms of the GNU Lesser General Public License as published by      /
//    the Free Software Foundation, either version 3 of the License, or               /
//    (at your option) any later version.                                             /

//    camel_jason is distributed in the hope that it will be useful,                  /
//    but WITHOUT ANY WARRANTY; without even the implied warranty of                  /
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                   /
//    GNU Lesser General Public License for more details.                             /

//    You should have received a copy of the GNU Lesser General Public License        /
//    along with camel_jason.  If not, see <http://www.gnu.org/licenses/>.            /  
///////////////////////////////////////////////////////////////////////////////////////

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
    private String name;
    private Vector<ArtifactConsumer> myConsumers;  

    public SimpleArtifact(ArtifactContainer container, String filePathOrURL, String name) {
        this.container = container;
        this.name = name;
    	myConsumers = new Vector<ArtifactConsumer>();
        
    }

    public void addToMyConsumers(ArtifactConsumer myConsumer)
    {
    	synchronized (myConsumers) {    		
    		myConsumers.add( myConsumer);				
		}    	
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
