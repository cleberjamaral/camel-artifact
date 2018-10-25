my_price(2000+math.random*200).

+!confirmYouAreIn <- 
    ?chatIdTelegram(C);
    startCamel(C);
    +present;
    getIn.

+!link(A, B) <-
	lookupArtifact(A,Aid);
	lookupArtifact(B,Bid);
	linkArtifacts(Bid,"out-1",Aid);
	linkArtifacts(Aid,"out-1",Bid);
	.print("Artifacts linked: ", A, " and ", B).
	
+minOffer(N) : my_price(MP) & present <-
	if (N > MP) {
		-present;
		getOut;
	}.

+minOffer(N).

+winnerag[source(Z)]: true <- 
	.print("I am so so Happy because I am the winner!");
	sendString("I am so so Happy because I am the winner!").
	//.stopMAS.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }
