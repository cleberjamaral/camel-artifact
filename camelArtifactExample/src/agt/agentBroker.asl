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
	linkArtifacts(ArtifactBid,"out-2",ArtifactCid);
	makeArtifact("ArtifactA","camelartifacts.ArtifactA",[],ArtifactAid);
	.print("Artifact are ready for use!");
	!!sendKAmsgA;
	!!sendKAmsgB;
	!!sendKAmsgC.
	
+!listenA: true <-
	focusWhenAvailable("ArtifactA"); //Just to make sure the artifact was already created
	.print("Start listening on artifact A..."); 
	lookupArtifact("ArtifactA",ArtifactAid);
	focus(ArtifactAid);
	listenRoutes(true)[artifact_id(ArtifactAid)]; //Start listening on artifact A (blocking command)
	.print("Listening process on Artifact A finished!").
	
+!listenC: true <-
	focusWhenAvailable("ArtifactB"); //Just to make sure the artifact was already created
	focusWhenAvailable("ArtifactC"); //Just to make sure the artifact was already created
	.print("Start listening on artifact C..."); 
	lookupArtifact("ArtifactC",ArtifactCid);
	focus(ArtifactCid);
	listenRoutes(true)[artifact_id(ArtifactCid)]; //Start listening on artifact C (blocking command)
	.print("Listening process on Artifact C finished!").

+!sendKAmsgA: true <- 
	focusWhenAvailable("ArtifactA"); //Just to make sure the artifact was already created
	lookupArtifact("ArtifactA",ArtifactAid);
	.print("Sending keepalive message to ArtifactA!");
	focus(ArtifactAid);
	sendKAA[artifact_id(ArtifactAid)];
	.wait(2000);
	!!sendKAmsgA.

+!sendKAmsgB: true <- 
	focusWhenAvailable("ArtifactB"); //Just to make sure the artifact was already created
	lookupArtifact("ArtifactB",ArtifactBid);
	.print("Sending keepalive message to ArtifactB!");
	focus(ArtifactBid);
	sendKAB[artifact_id(ArtifactBid)];
	.wait(2000);
	!!sendKAmsgB.

+!sendKAmsgC: true <- 
	focusWhenAvailable("ArtifactC"); //Just to make sure the artifact was already created 
	lookupArtifact("ArtifactC",ArtifactCid);
	.print("Sending keepalive message to ArtifactC!");
	focus(ArtifactCid);
	sendKAC[artifact_id(ArtifactCid)];
	.wait(2000);
	!!sendKAmsgC.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
