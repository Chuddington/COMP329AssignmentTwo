/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!print_ready
	<-	.print("Ready!").
	
+!start : true 
	<- 	robot.distance(Object);
		.print("Found a victim: ", Object);
		.send(doctor,tell,Object);
		.wait(1000).

/*+!start
	<-	.send(doctor,tell,victim(3,0,0));
		.wait(1000);
		.send(doctor,tell,victim(2,0,0));
		.wait(1000);
		.send(doctor,tell,victim(1,0,6));
		.wait(1000);
		.send(doctor,tell,victim(1,2,3));
		.wait(1000);
		.send(doctor,tell,victim(1,4,3));
		.wait(1000);
		.send(doctor,tell,victim(1,5,0));
		.wait(1000);
		.send(doctor,tell,victim(3,6,6)).*/
		
+victim(C,X,Y)[source(A)]
	<-	if (C == 1) {.print(A," tells me the priority victim is red at (",X,",",Y,")")};
		if (C == 2) {.print(A," Tells me the priority victim is blue at (",X,",",Y,")")};
		if (C == 3) {.print(A," Tells me the priority victim is green at (",X,",",Y,")")}.

//unused
+hello[source(A)]
 	<-	.print("I receive an hello from ",A);
	 	.send(A,tell,hello).
