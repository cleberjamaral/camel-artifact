scenarioBtest(false).
nArtifacts(10).
startTime(0).

/* Initial goals */
!start.

+!start: scenarioBtest(L) & nArtifacts(N) <-
	-+startTime(system.time);
	?startTime(T);
	!!stopTest;
	.print("Building and linking artifacts..."); 
	makeArtifact("ArtifactC","camelartifacts.ArtifactC",[],ArtifactCid);

	if (L)
	{
		//Cenario B
 		//- ArtefatoA camel artifact
 		//- n x ArtefatoB default cartago artefact linked to C
 		//- ArtefatoC camel artifact (router)
	    	for ( .range(I,0,N-1) ) {
       			.concat("ArtifactB",I,X);
       	 		makeArtifact(X,"artifacts.ArtifactB",[],Artid0);
			linkArtifacts(ArtifactCid,"out-1",Artid0);
			linkArtifacts(Artid0,"out-2",ArtifactCid);
     		}
                for ( .range(I,0,N-1) ) {
			.concat("ArtifactB",I,X);
                        !!sendKAmsg(X);
                }
		makeArtifact("ArtifactA0","camelartifacts.ArtifactA",[],ArtifactAid);
     		!!listen("ArtifactA0");
		.print("Starting sending (B): ",system.time - T);
     		!!sendKAmsg("ArtifactA0");
     		!!listen("ArtifactC");
     		!!sendKAmsg("ArtifactC")
	} else {
		//Cenario A
 		//- n x ArtefatoA camel artifact with its own routes
 		//- ArtefatoB default cartago artefact
 		//- ArtefatoC camel artifact (router)
		for ( .range(I,0,N-1) ) {
	       		.concat("ArtifactA",I,X);
       			makeArtifact(X,"camelartifacts.ArtifactA",[],Artid0);
       		}
                for ( .range(I,0,N-1) ) {
			.concat("ArtifactA",I,X);
                        !!listen(X);
                }
                for ( .range(I,0,N-1) ) {
			.concat("ArtifactA",I,X);
                        !!sendKAmsg(X);
                }
		makeArtifact("ArtifactB0","artifacts.ArtifactB",[],ArtifactBid);
		linkArtifacts(ArtifactCid,"out-1",ArtifactBid);
		linkArtifacts(ArtifactBid,"out-2",ArtifactCid);
		.print("Start sending (A): ",system.time - T);
       		!!sendKAmsg("ArtifactB0");
       		!!listen("ArtifactC");
       		!!sendKAmsg("ArtifactC")
	}	
	.print("Artifact are ready for use!").
	
+!listen(Art): true <-
	focusWhenAvailable(Art); //Just to make sure the artifact was already created
	.print("Start listening on ",Art,"..."); 
	lookupArtifact(Art,Aid);
	listenRoutes(true)[artifact_id(Aid)]. //Start listening on artifact (blocking command)

+!sendKAmsg(Art): true <- 
	focusWhenAvailable(Art); //Just to make sure the artifact was already created
	lookupArtifact(Art,Aid);
	.wait(3000);
	sendKA[artifact_id(Aid)];
	.print("Keepalive message sent to ",Art,"!");
	.wait(3000);
	!!sendKAmsg(Art).
	
+!stopTest: startTime(T) & scenarioBtest(L) & L & (system.time - T > 110000) <-
	.print("* * * * END scenario B * * * * *");
	.stopMAS.

+!stopTest: startTime(T) & nArtifacts(M) & scenarioBtest(L) & not L & ((system.time - T) > (M * 1700 + 110000)) <-
	.print("* * * * END scenario A * * * * *");
	.stopMAS.

+!stopTest: startTime(T) <-
	.print("Elapsed time: ",system.time - T);
	.wait(1000);
	!stopTest.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
