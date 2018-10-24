my_price(2000+math.random*200).

+!focus(A) <- 
	lookupArtifact(A,ToolId);
    focus(ToolId);
    ?chatIdTelegram(C);
    startCamel(C);
    +present;
    sendString(getIn);
    getIn.

+minOffer(N) : my_price(MP) & present <-
	if (N > MP) {
		-present;
		sendString(getOut);
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
