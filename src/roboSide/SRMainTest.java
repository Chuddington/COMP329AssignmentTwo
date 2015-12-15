package roboSide;

import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Random;
import java.util.logging.Logger;

import lejos.nxt.*;
import lejos.nxt.addon.ColorHTSensor;
import lejos.pc.comm.NXTConnector;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.util.Delay;


public class SRMain {
  
  //global variables here
    //constants
  public static final int DEGREE  = 108;
  public static final int COLUMNS = 4  ;
  public static final int ROWS    = 5  ;
  public static final int DIST    = 25 ;
  
    //Jason String Literals
  public static final Term explore   = Literal.parseLiteral("explore()");
  public static final Term outputMap = Literal.parseLiteral("getMap()");
  public static final Term mvToDest  = Literal.parseLiteral("");
  public static Logger log = Logger.getLogger(SRMain.class.getName());

    //object creation
  public static SRModel modelObj;
  public static SRMov   movObj  ;
  public static BtStuff btObj   ;
  public static Thread  btThread; //

  
  //methods to connect with SREnv and obtain commands
  
  /*
   * Information required: 
   * start BT connection
   * get the explore command
   * output the list of victims
   * get the command to move to a particular cell (red victim)
   * moveTo(x, y, modelObj);
   * boolean hasVic = pickUpVictim();
   * moveTo(0, 0, modelObj);
   */
   
   
   /*
   *	I presume the main method goes in here? Possibly something like
   *	
		public static void main(String[] args){
			
			btConn();		creates a new bluetooth connection
			listen();		starts the listener for new commands
		
		}
   *
   *
   */
   //Start the listener thread, allowing jason commands to be taken from the 
   //Scout
   private static void listen(){
		Thread listener = new Thread(new CommandListener(dis,mainEnv));
		listener.start();
   
   }
   //The bluetooth connection method to connect from the scout
   public static void btConn(){
	
		btObj.Bluetooth.waitForConnection();
		
		if(btObj == null){
						//Whatever error is thrown in this case
		}
		//Input and output streams
		dos = btObj.openDataOutputStream();
		dis = btObj.openDataInputStream();
		  
   }
   //Actions called from the the command listener, corresponds to the 
   //Parse literals defined above
   //The method variables are just thrown in as necessary, they aren't
   //Exactly correct at this point
   public boolean executeAction(){
	
	try{
		if(action.equals(explore){
			explore();
		}
		if(action.equals(checkCell){
			//If jason sends request to check cells around
			//Call methods in the SRModel class
			modelObj.scanAll(d);		
		}
		if(action.equals(mvFwd){
			//Testing if the robot can move to the next cell
			boolean able = modelObj.canMove(cp, f);
				if(able){
					movObj.moveTo(x, y, cPos, map);
				}else{
					modelObj.impScan(r,f);
				}
		}
		
		}
		catch(Exception e){return false;}
		return true;
	}
    }
  
  public static void initialisation() {
    movObj   = new SRMov(int DIST, int COLUMNS, int ROWS, int DEGREE);
    modelObj = new SRModel(COLUMNS, ROWS, movObj);
  }
  
  public static int[][] explore() {
    
		//This is where the methods from SRMov are called to 
		//To traverse the arena 
		//Once a victim is found, call the addVictim method
		
		//What is d?
		modelObj.scanAll(d);
		//All of these need initializing etc, just showing where they
		//Probably go 
		movObj.moveTo(x, y,cPos, map);
		//Needs algorithm for moving around the arena		
	
	//boolean res = modelObj.scanAhead();
    //String colOutput = modelObj.getColour();
    //if(!colOutput.equals(NULL) ) {
      //btObj.sendString(colOutput);
    }
    
    return modelObj.map;
  }
  
}
