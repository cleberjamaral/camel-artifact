/* Initial beliefs and rules */

/* Initial goals */
!ask(beer).

/* Plans */
@o1
+!ask(beer) : true 
	<- .send(robot, achieve, bringbeer(owner,beer));
	.print("Go robot, bring a beer!").
	
@o2
+has(owner,beer)[source(robot)] : true
	<- 	!drink(beer);
		.print("I want more!");
		!ask(beer).	

@o3
+!drink(beer) : has(owner,beer)
	<- .send(robot, achieve, masterdrunkabeer(owner));
	.print("Yeah, I have drunk a cold beer!").

	