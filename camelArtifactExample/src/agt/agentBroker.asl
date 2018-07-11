// Agent sample_agent in project camelArtifactExample
/* Initial beliefs and rules */

/* Initial goals */
!start.
!listenA.
!listenC.

@r9
+!start: true <-
	.print("Building artifacts..."); 
	makeArtifact("ArtifactB","artifacts.ArtifactB",[],ArtifactBid);
	makeArtifact("ArtifactC","camelartifacts.ArtifactC",[],ArtifactCid);
	.print("Linking artifacts..."); 
	linkArtifacts(ArtifactCid,"out-1",ArtifactBid);
	makeArtifact("ArtifactA","camelartifacts.ArtifactA",[],ArtifactAid);
	.print("Artifact are ready for use!");
	!!sayHelloPlanA;
	!!sayHelloPlanC.
	
+!listenA: true <-
	focusWhenAvailable("ArtifactA");
	.print("Start listening on artifact A..."); 
	lookupArtifact("ArtifactA",ArtifactAid);
	focus(ArtifactAid);
	setListenCamelRoute(true)[artifact_id(ArtifactAid)]; //Start listening on artifact A
	.print("Listening on artifact A ready!").
	
+!listenC: true <-
	focusWhenAvailable("ArtifactB");
	focusWhenAvailable("ArtifactC");
	.print("Start listening on artifact C..."); 
	lookupArtifact("ArtifactC",ArtifactCid);
	focus(ArtifactCid);
	setListenCamelRoute(true)[artifact_id(ArtifactCid)]; //Start listening on artifact A
	.print("Listening on artifact C ready!").

+!sayHelloPlanA: true <- 
	focusWhenAvailable("ArtifactA");
	lookupArtifact("ArtifactA",ArtifactAid);
	.print("Say hello ArtifactA!");
	focus(ArtifactAid);
	sayHelloA[artifact_id(ArtifactAid)];
	.wait(2000);
	!!sayHelloPlanA.

+!sayHelloPlanC: true <- 
	focusWhenAvailable("ArtifactC");
	lookupArtifact("ArtifactC",ArtifactCid);
	.print("Say hello ArtifactC!");
	focus(ArtifactCid);
	sayHelloC[artifact_id(ArtifactCid)];
	.wait(2000);
	!!sayHelloPlanC.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
