/* Initial beliefs and rules */
things_to_do(todo).

/* Initial goals */

@just_do_it
+!just_do_it 
	: things_to_do(todo)
	<- .print("There we go!").
