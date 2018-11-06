my_price(2000+math.random*200).

+!confirmYouAreIn : my_price(P)<- 
    ?chatIdTelegram(C);
    startCamel(C);
    +present;
	.print("My max: ", P);
    getIn.

+!link(A, B) <-
	-+auctionArt(A);
	-+telegramArt(B);
	lookupArtifact(A,Aid);
	lookupArtifact(B,Bid);
	linkArtifacts(Aid,"out-1",Bid);
	linkArtifacts(Bid,"out-1",Aid);
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
