cd ~/Desktop/camel-artifact/camel-artifact-component/
mvn -Dtest=UnitTestApp clean install
cd target
cp camelartifact-0.0.1.jar ~/Desktop/MAS/mas4ssp-demo-defesa/lib/
cp camelartifact-0.0.1.jar ~/Desktop/Didaticos/camelJaCaMoRobot/lib/
read -p "Press [ENTER] to return"

