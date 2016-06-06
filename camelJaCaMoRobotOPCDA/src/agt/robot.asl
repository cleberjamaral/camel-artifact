/* Initial beliefs and rules */
// initially, I believe that there are N beers in the fridge
available(beer,fridge).
stock(beer,3).

/* Initial goals */
!makeArtifs.

/* Plans */
@r1
+!bringbeer(owner,beer)
	: available(beer,fridge)
	<- -at(robot,owner); // Have sure this old belief is out
	!at(robot,fridge);
	.print("Grabbing a beer to my master.");
	!at(robot,owner);
	.date(YY,MM,DD); .time(HH,NN,SS);
	.send(owner, tell, has(owner,beer));
	.print("Here is your beer my master.");
	?stock(beer,Inventory);
	!updateStock(beer,Inventory);
	!incCounter;
	+consumed(YY,MM,DD,HH,NN,SS,beer).

@r2
+!bringbeer(owner,beer)
	: not available(beer,fridge)
	<- .print("Sorry, there is no beer!").
	
@r3
+!at(robot,P) : true// : not at(robot,P)
	<- -at(robot,_); // Have sure the old belief is out
	+at(robot,P);
	.print("Robot is at ",P).	
@r4	
+!masterdrunkabeer(owner) : true
	<- .send(owner, untell, has(owner,beer)).
	
@r5
+!updateStock(beer,Inventory) : Inventory > 1
	<- ?stock(beer,Tmp);
	Inv = Tmp - 1; 
	-+stock(beer,Inv);
	.print("Inventory had ",Inventory," beer(s). A beer is being taken, now the fridge has ",Inv," beer(s).").

@r6 //last beer
+!updateStock(beer,Inventory) : Inventory == 1
	<- -+stock(beer,0);
	-available(beer,fridge);
	.print("Inventory had its last beer. Master needs to ask supermarket for more beer!").
	
@r7	
+!updateStock(beer,Inventory) : Inventory == 0
	<- -available(beer,fridge);
	.print("Did I already told that there is no beer?").
	
@r8
+addstock(beer,N) : N > 0
	<- -+stock(beer,N);
	+available(beer,fridge);
	.print("More ",N," beers just arrived! There are available beer again!").

@r9
+!makeCounter: true 
	<-.print("Making artifact counter..."); 
	makeArtifact("counter","artifacts.Counter",[10],C);
	focus(C);
	.print("Artifact counter made!").
	
@r10
+!setCounter: true
	<- lookupArtifact("counter",C);
	focus(C); 
	setValue(-1,D);
	inc(E);
	.print("New is (-1) old number is: ",D).
	
@r11
+!incCounter: true
	<- lookupArtifact("counter",C);
	focus(C); 
	inc(D);
	.print("New number: ",D).

@r12
+!makeRouter: true 
	<-.print("Making artifact router..."); 
	makeArtifact("router","camelartifacts.Router",[],RouterId);
	focus(RouterId);
	.print("Artifact router made!").
	
@r13
+!linkArtifacts: true 
	<-.print("Linking router and counter..."); 
	lookupArtifact("counter",CounterId);
	lookupArtifact("router",RouterId);
	linkArtifacts(RouterId,"out-1",CounterId);
	setListenCamelRoute(true);
	.print("Router and counter are linked!").


@r15
+!makeArtifs : true
	<- !makeCounter;
	!setCounter;
	!makeRouter;
	!linkArtifacts;
	!receive_msgs.

@r14
+!receive_msgs
	<- lookupArtifact("router",RouterId);
	focus(RouterId);
	//receiveMessage(Msg,Sender);
	//startReceiving;
	setListenCamelRoute(true);
	lookupArtifact("counter",CounterId);
	focus(CounterId);
	inc3("d",0);
	inc2;
	.print("Robot is ready to receive messages!").
	
+new_msg(Msg,Sender)
	<- //stopReceiving;
	setListenCamelRoute(false);
	println("New msg received ",Msg," from ",Sender). 
