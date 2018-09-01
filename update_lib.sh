#!/bin/bash

#mvn -Dtest=UnitTestApp clean install

mvn install

cp camel-artifact-component/target/camelartifact-0.0.1.jar camelArtifactExample/lib/
cp camel-artifact-component/target/camelartifact-0.0.1.jar camelJaCaMoRobot/lib/
cp camel-artifact-component/target/camelartifact-0.0.1.jar camelJaCaMoRobotOPCDA/lib
cp camel-artifact-component/target/camelartifact-0.0.1.jar route-test-artifact-opcda/lib
read -p "Press [ENTER] to return"

