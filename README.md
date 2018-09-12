# camel-artifact

camel-artifact is an Apache Camel component to connect Artifacts (virtual entities) from MultiAgent Systems to other systems and devices. The current version is only compatible with CArtAgO (http://cartago.sourceforge.net/) artifacts. All tests were done using Jason agents by JaCaMo (http://jacamo.sourceforge.net/) platform.

This component works as a CArtAgO artifact for the system, it extends original CArtAgO artifact class with the aditional feature to create camel routes. The artifact may work as a final artifact with this interoperability improvement or as a router, which means that an artifact may also forward external messages among virtual artifacts.

Besides the Camel Component (camel-artifact-component folder), this repository brings some examples, specially "camelJaCaMoRobotOPCDA" a JaCaMo implementation of a small MultiAgent System connecting with industrial devices in the Industry 4.0 context. In this sample project MQTT, OPC-DA and pure TCP/IP connections are tested.
