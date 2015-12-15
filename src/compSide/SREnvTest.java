package compSide;

import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import java.util.logging.Logger;

public class SREnv extends Environment {
  
  //global variables
    //Jason String Literals
  public static final Term mvFwd = Literal.parseLiteral("moveForward()");
  public static final Term explore = Literal.parseLiteral("explore()");
  public static final Term checkCell = Literal.parseLiteral("check(cell)");
  public static DataInputStream dis;
  //Boolean for checking if the arena has been explored
  public static boolean explored = false;
  public static SRComms src;
  
  //Created init environment
  @Override
  public void init(String[] args){
  
		System.out.println("Connecting to NXT");
		// Not sure of syntax here
		//BluetoothConnection btc = new BluetoothConnection("LEGOBOT-06", 
		NXTConnector conn = btc.accept();
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		dis = new DataInputStream(conn.getInputStream());
		src = new SRComms(dos);  
  }
  //Creates and execute action method which executes environment actions from
  //Jason
  @Override
  public boolean executeAction(String ag, Structure action){
	
	try{
		//If the action is explore(), send the action to the robot
		if(action.equals(explore)){
			src.bluetooth_send("explore()");
			
			//Once the arena has been fully explored
			//Explored will be equal to true
			//Therefore
			if(explored){
				addPercept("scout", Literal.parseLiteral("explored"));
			}
		
		}
		if(action.equals(checkCell){
			src.bluetooth_send("check(cell)");
		}
		if(action.equals(mvFwd){
			src.bluetooth_send("moveForward()");
		}		
		//else if(action.equals(){}
		
	}
  
  
  }
  //Adds a percept to Jason, with colour and grid position
  void addVictim(int[]pos, int colour){
	
	addPrecept("scout", Literal.parseLiteral("victim(" + colour + ", "+ pos[0]+" , " + pos[1] + ")"));
	
  }
    //Object declarations
    
	//stuff for updating perception and stuff
}
