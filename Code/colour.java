package robot;

import jason.*;
import jason.asSemantics.*;
import jason.asSyntax.*;

public class colour extends DefaultInternalAction {
	
//	Communication comm;
//	Thread commThread;
	
	public colour() {
	//	comm = new Communication("NXT", "");
	//	commThread = new Thread(comm);
	//	commThread.start();
		
	}
	
	public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		
		Literal result = ASSyntax.parseLiteral("victim(1,0,0)");
		
		return un.unifies(result, args[0]);
		
	}
	
}
