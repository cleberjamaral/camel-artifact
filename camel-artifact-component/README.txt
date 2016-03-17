
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

This project contains an "artifact" component for Apache Camel (http://camel.apache.org), allowing artifacts programmed in CArtAgO (http://cartago.sourceforge.net) to send and receive messages to and from Camel routes as artifacts messages.

camel-artifact is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

camel-artifact is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details (http://www.gnu.org/licenses/).                            

This is a maven project, containing these modules:

* camel-artifact-component: This module contains the source code for the camel artifact component, and builds a jar file for that component

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

If you use Maven, change the <component-path> entry to point to the local system folder that contains <component>.jar

Any configuration parameters can be added to config.properties

Camel Java Router Project
=========================

To build this project use

    mvn install

To run this router from within Maven use

    mvn exec:java

For more help see the Apache Camel documentation

    http://camel.apache.org/

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    