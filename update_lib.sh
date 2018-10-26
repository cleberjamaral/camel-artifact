#!/bin/bash

#mvn -Dtest=UnitTestApp clean install

mvn install

cp camel-artifact-component/target/camelartifact-0.0.2.jar camelArtifactExample/lib/
cp camel-artifact-component/target/camelartifact-0.0.2.jar camelJaCaMoRobot/lib/
cp camel-artifact-component/target/camelartifact-0.0.2.jar camelJaCaMoRobotOPCDA/lib
cp camel-artifact-component/target/camelartifact-0.0.2.jar route-test-artifact-opcda/lib
cp camel-artifact-component/target/camelartifact-0.0.2.jar auctiontelegram/lib


cp camel-artifact-component/target/camelartifact-0.0.2.jar maven2/nz/ac/otago/camelartifact/0.0.2/.
cp camel-artifact-component/pom.xml maven2/nz/ac/otago/camelartifact/0.0.2/camelartifact-0.0.2.pom

read -p "Press [ENTER] to return"

