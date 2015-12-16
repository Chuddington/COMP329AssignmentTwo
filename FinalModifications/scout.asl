/* scout.asl
 * scout agent that looks for victims and reports them to the doctor
 * 
 * Author: Johnathan Edwards 200965323, ROBOT-06
 *	   Adam Large 200963462
 * Time: 15:17:47	 Date: 14/12/2015
 * 
 */


/* Plans */

/* Scout tells us it is ready
   once doctor has told it to start */
+!print_ready[source(A)]
	: 	true
	<-	.print("I have recieved orders from ",A," to look for victims!");
		+scout_ready;
		.wait(1000);
		!start_explore;		//Starts traversal
		!find_victims.		//starts to look for victims

/* Starting the area traversal */
+!start_explore
	<-	explore.
	
/*Calls explore() until explored is true */	
+!explore
	: 	not explored
	<- 	explore.

+!explored 
	: true.

/*Prints area explored once traversal has finished */
+explored
	: 	true
	<- .print("Area explored").
	
/* Gets robot to move to specified X,Y coordinates */
+moveTo(X,Y): true
	<- 	.print("Moving to ", X , Y , " ");
	   	moveTo(X,Y).

/* Recursively find's victims using the robot
   and tells doctor their colour and location */
+!find_victims
	:	scout_ready
	<-	.print("Looking for victims...");
		
		robot.colour(Object);			//uses an Object (literal victim(C,Y,X)) from colour.java in robot package
		.print("Found ", Object); 
		.send(doctor,tell,Object);		//tells the doctor of the victim
		.wait(1000);
		!find_victims. 				//recur
		
/* When scout is not ready to find victims */
+!find_victims
	: 	not scout_ready
	<-	.print("Not yet ready!");
		.wait(1000).

/* Stops the scout looking for victims
   when the doctor has told it to */
+!stop[source(A)]
	<-	.drop_desire(find_victims);			//stop recurring find_victims
		.print(A," has told me I am finished.").	
	
/* Reports the priority victim
   when doctor tells it which victim is priority */
+priority_victim(C,X,Y)[source(A)]
	<-	if (C == 0) {.print(A," tells me the priority victim is red at (",X,",",Y,")")};		//if red
		if (C == 1) {.print(A," Tells me the priority victim is blue at (",X,",",Y,")")};		//if blue
		if (C == 2) {.print(A," Tells me the priority victim is green at (",X,",",Y,")")};		//if green
		!moveTo(X,Y).		//get robot to move to the priority victim.
		
