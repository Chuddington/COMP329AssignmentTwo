/* colour.java
 * Sets up communication between Jason and Java.
 * Contains a method to create a literal from a string that the Jason code can use.
 *
 */

package robot;

import jason.asSemantics.*; 	//to do unification
import jason.asSyntax.*; 		//to handle AgentSpeak

public class colour extends DefaultInternalAction {
 
	//attributes for communication
	Communication comm;
	Thread commThread;
	
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
		
		String s = comm.read();						//get the string with information on a victim from the robot.
		Literal result = ASSyntax.parseLiteral(s);			//create a literal from a string to pass to the agent.
		return un.unifies(result, args[0]);				//returns the literal to the agent.
	}
	
}
