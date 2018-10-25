+!start <- 
    .print("Auction artifact created for ",product(diamond_ring));
    ?chatIdTelegram(C);
    startCamel(C);
    sendString("Auction artifact created");
    .broadcast(achieve, confirmYouAreIn). 

+!link(A, B) <-
	lookupArtifact(A,Aid);
	lookupArtifact(B,Bid);
	linkArtifacts(Bid,"out-1",Aid);
	linkArtifacts(Aid,"out-1",Bid);
	.print("Artifacts linked: ", A, " and ", B).

+participants(N): total(NT) & N == NT <- !setOffer.
    
+!setOffer : participants(N) & N > 1 & minOffer(P) <- 
	setOffer(P+20);
	.concat("setOffer: ", P+20, X);
	sendString(X);
	.wait(1000);
	!!setOffer.

+!setOffer.
	
+winner(W) : W \== no_winner
   <- .concat("Winner for ", product(diamond_ring), " is ", W, X);
   	  .print(X);
   	  sendString(X);
	  .send(W, tell, winnerag).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }
