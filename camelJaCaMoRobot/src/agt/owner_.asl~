/* Initial beliefs and rules */

/* Initial goals */
!ask(beer).

/* Plans */
@o1
+!ask(beer) : true 
	<- .send(robot, achieve, bringbeer(owner_,beer));
	.print("Go robot, bring a beer!").
	
@o2
+has(owner_,beer)[source(robot)] : true
	<- 	!drink(beer);
		.print("I want more!");
		!ask(beer).	

@o3
+!drink(beer) : has(owner_,beer)
	<- .send(robot, achieve, masterdrunkabeer(owner_));
	.print("Yeah, I have drunk a cold beer!").

	