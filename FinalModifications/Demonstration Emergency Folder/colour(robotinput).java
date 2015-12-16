package robot;

import jason.asSemantics.*; 	//to do unification
import jason.asSyntax.*; 		//to handle AgentSpeak

public class colour extends DefaultInternalAction {

	//attributes for communication
	Communication comm;
	Thread commThread;
	
	int x = 0, y = -1, heading = 1;
	
	/* colour() starts the Communication
	 * thread so to get information from 
	 * the robot.
	 */
	public colour() {
		comm = new Communication("LEGOBOT-06", "00:16:53:11:16:9d");		//name and address of the robot.
		commThread = new Thread(comm);
		commThread.start();
	}

	/* This returns the object to the agent.
	 * The object it returns is a victim with
	 * colour and position information in the
	 * form victim(C,X,Y), where C is the
	 * colour ID and X & Y are coordinates.
	 */
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		
        //traverse virtual 4x5 map
		y = y + heading;
		if(y == 7) {
			y = 6;
			heading = (-1) * heading;
			x++;
		} else if(y == -1) {
			y = 0;
			heading = (-1) * heading;
			x++;
		}
		
		int c = comm.read();        //get colour reading from robot.
       
       if(c == 1) {                     //if green
            c = 2;                      //lower priority
        } else if (c == 2) {            //if blue
            c = 1;                      //raise priority
        }
		
        String s = String.format("victim(%s,%d,%d)",c,x,y);     //get the string with information on a victim from the robot.						
		Literal result = ASSyntax.parseLiteral(s);		        //create a literal from a string to pass to the agent.
		return un.unifies(result, args[0]);				        //returns the literal to the agent.
	}
	
}