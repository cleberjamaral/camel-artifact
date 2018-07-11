/* Initial goals */
!start.
!listenA.
!listenC.

+!start: true <-
	.print("Building artifacts..."); 
	makeArtifact("ArtifactB","artifacts.ArtifactB",[],ArtifactBid);
	makeArtifact("ArtifactC","camelartifacts.ArtifactC",[],ArtifactCid);
	.print("Linking artifacts..."); 
	linkArtifacts(ArtifactCid,"out-1",ArtifactBid);
	linkArtifacts(ArtifactBid,"out-1",ArtifactCid);
	makeArtifact("ArtifactA","camelartifacts.ArtifactA",[],ArtifactAid);
	.print("Artifact are ready for use!");
	!!sayHelloPlanA;
	!!sayHelloPlanB;
	!!sayHelloPlanC.
	
+!listenA: true <-
	focusWhenAvailable("ArtifactA");
	.print("Start listening on artifact A..."); 
	lookupArtifact("ArtifactA",ArtifactAid);
	focus(ArtifactAid);
	listenRoutes(true)[artifact_id(ArtifactAid)]; //Start listening on artifact A (blocking command)
	.print("Listening process on Artifact A finished!").
	
+!listenC: true <-
	focusWhenAvailable("ArtifactB");
	focusWhenAvailable("ArtifactC");
	.print("Start listening on artifact C..."); 
	lookupArtifact("ArtifactC",ArtifactCid);
	focus(ArtifactCid);
	listenRoutes(true)[artifact_id(ArtifactCid)]; //Start listening on artifact C (blocking command)
	.print("Listening process on Artifact C finished!").

+!sayHelloPlanA: true <- 
	focusWhenAvailable("ArtifactA");
	lookupArtifact("ArtifactA",ArtifactAid);
	.print("Say hello ArtifactA!");
	focus(ArtifactAid);
	sayHelloA[artifact_id(ArtifactAid)];
	.wait(2000);
	!!sayHelloPlanA.

+!sayHelloPlanB: true <- 
	focusWhenAvailable("ArtifactB");
	lookupArtifact("ArtifactB",ArtifactBid);
	.print("Say hello ArtifactB!");
	focus(ArtifactBid);
	sayHelloB[artifact_id(ArtifactBid)];
	.wait(2000);
	!!sayHelloPlanB.

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
