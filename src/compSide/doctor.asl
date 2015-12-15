/* doctor.asl
 * doctor initiates search for victims then receives their information from scout
 * Victims put in order of priority and highest priority victim printed.
 * Tells scout when to stop and tells it of the highest priority victim.
 * 
 * Author: Johnathan Edwards 200965323, ROBOT-06
 * Time: 15:20:38	 Date: 14/12/2015
 * 
 */


/* Initial Goals */ 

!start.

/* Plans */

/* Tells scout to begin search */
+!start
	<-	.print("I have ordered scout to look the victims!");
		.send(scout,achieve,print_ready).
		
/* Report a victim has been found */
+victim(C,X,Y)[source(A)]
	:	C < 3				//when colour ID is that of a victim
	<- 	if (C == 0) {.print(A," found a red victim at (",X,",",Y,")")};
		if (C == 1) {.print(A," found a blue victim at (",X,",",Y,")")};
		if (C == 2) {.print(A," found a green victim at (",X,",",Y,")")};
		
		!check_finished(X,Y);		//check if finished
		!addlist.			//add victim to list
		
		
/* Report a victim has not been found */
+victim(C,X,Y)[source(A)]
	: C >= 3				//when colour ID is not that of a victim
	<- 	.print(A," Reports no victim at(",X,",",Y,")");
	
		.abolish(victim(C,X,Y));	//remove belief that this is a victim
		!check_finished(X,Y).		//check if finished
		
/* Check if scout is at the end of it's run */
+!check_finished(X,Y)
	: X == 4 & Y == 0		//when position is end of world
	<-	+finished;		//believes it is finished
	
		//tell scout has completed it's task
		.print("scout has reached the end.");
		.send(scout,achieve,stop);
		
		.wait(1000);
			
		//print final results
		.print("This is the final result: ");
		!addlist.
			
/* Empty fail event */
+!check_finished(_,_).
		
/* Put all victims in a list,
   sort the list in order of priority 
   and get the highest priority victim from the list */
+!addlist
	<-	.findall(victim(C,X,Y),victim(C,X,Y),L);				//put victims into list L
		.sort(L,SL);								//sort list L and put in sorted list SL in order of priority using colour ID then X position then Y position
		.min(L,victim(I,O,U));							//get highest priority victim
		.print("Victims in order of priority: ",SL);	//print list
		!printmin(I,O,U).							//print highest priority victim
		
/* Prints out the highest priority victim */
+!printmin(C,X,Y)
	: not finished
	<-	if (C == 0) {.print("Priority victim is red at (",X,",",Y,")")};		//if red
		if (C == 1) {.print("Priority victim is blue at (",X,",",Y,")")};		//if blue
		if (C == 2) {.print("Priority victim is green at (",X,",",Y,")")};		//if green
		.abolish(priority_victim(_,_,_));			//remove any belief of previous priority victim
		+priority_victim(C,X,Y).				//add belief of new priority victim

/* Prints out and sends to scout
   the highest priority victim when scout is finished */	
+!printmin(C,X,Y)
	: finished
	<-	if (C == 0) {.print("Priority victim is red at (",X,",",Y,")")};		//if red
		if (C == 1) {.print("Priority victim is blue at (",X,",",Y,")")};		//if blue
		if (C == 2) {.print("Priority victim is green at (",X,",",Y,")")};		//if green
		.send(scout,tell,priority_victim(C,X,Y)).		//tell scout the priority victim
