package robot;

import jason.asSyntax.*;
import jason.environment.Environment;

//import java.io.*;

public class SREnv extends Environment {
  
  //Jason String Literals
  public static final Term quit = Literal.parseLiteral("quit")            ;
  public static final Term explore = Literal.parseLiteral("explore")    ;
  //Boolean to tell when robot has finished exploring
  public static boolean explored = false                                  ;
  public static SRComms src                                               ;
  Thread commThread													      ;
// sDataInputStream dis													  ;
// DataOutputStream dos													  ;

  
  @Override 
  public void init(String[] args){
    //Data Output Stream to send data to the robot
    //dos = new DataOutputStream(src.readItem());
    //Input stream to take from the robot
    //dis = new DataInputStream(src.getInputStream());
    //New instance of SRComms, creating the BT connection
   
     src = new SRComms("LEGOBOT-12", "00:16:53:1c:64:97");		//name and address of the robot.
	 commThread = new Thread(src);
	 commThread.start();
  }
  //Override function to assess the Jason commands
  @Override 
  public boolean executeAction(String ag, Structure action){
    try{
        //Once the explore command is recieved, this is sent
        //To the robot, casuing it to begin movement
        if(action.equals(explore) ) {
          src.sendBluetooth("explore");
          if(explored){
            addPercept("scout", Literal.parseLiteral("explored"));
            }
        }
        else if(action.getFunctor().equals("moveTo") ) {
          int x = (int)((NumberTerm)action.getTerm(0)).solve();
          int y = (int)((NumberTerm)action.getTerm(1)).solve();
          src.sendBluetooth("moveTo()");
          src.sendBluetooth(Integer.toString(x) );
          src.sendBluetooth(Integer.toString(y) );
        }
        else if(action.equals(quit) ) {
          src.sendBluetooth("quit");
        }
        //Catches an exception and returns false. 
        //There incase of jason errors
    }catch(Exception e){return false;} 
    return true;
  }
}
