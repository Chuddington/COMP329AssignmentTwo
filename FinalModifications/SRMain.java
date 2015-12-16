/*
 * File Purpose: Main class for the robot - controls other class files
 * Author/s    : Michael Chadwick
 * Student IDs : 200882675
 */

import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.Bluetooth;

public class SRMain {
  
  //global variables here
    //constants
  public static final int DEGREE  = 108; //turning strength for robot
  public static final int COLUMNS = 4  ; //quantity of X axis cells
  public static final int ROWS    = 5  ; //quantity of Y axis cells
  public static final int DIST    = 25 ; //distance to travel
  
    //object/variable creation
  public static SRModel         modelObj    ; //var. for Mapping  class
  public static SRMov           movObj      ; //var. for Movement class
  public static BtStuff         btObj       ; //var. for output
  public static SRInput         inputObj    ; //var. for input
  public static Thread          btThread    ; //object for threading output
  public static Thread          inThread    ; //object for threading input
  public static NXTConnection   connObj     ; //holds connection type (BT)
  public static String          currentCmd  ; //holds command from input thread
  public static int             loop1, loop2; //used in for loops
  public static boolean         running     ; //used in main() while loop
  
  
  
  //main method on the robot
  public static void main(String[] args) {
	    running  = true; //switch to keep the while loop running
	    
	    //set the class values using constants
	    movObj   = new SRMov(DIST, COLUMNS, ROWS, DEGREE);
	    modelObj = new SRModel(COLUMNS, ROWS, movObj);
	    
	    //set up the connection type
	    System.out.print("Waiting for connection");
	    connObj  = Bluetooth.waitForConnection();
	    
	    //set up objects used in threads
	    inputObj = new SRInput(connObj);
	    btObj    = new BtStuff(connObj);

	    inThread = new Thread(inputObj); //create input thread
	    inThread.setDaemon(true);        //create daemon for the thread
	    inThread.start();                //start the input thread
	    
	    btThread = new Thread(btObj);    //create output thread
	    btThread.setDaemon(true);        //create daemon for the thread
	    btThread.start();                //start the output thread

    try{
      while(running) { //when the quit() command hasn't been said
        if(inputObj.cmdList.empty() ) { //if there were no commands sent
          
        } else { //if there are commands in the queue waiting to be called

          currentCmd = inputObj.getCurrentCmd(); //obtain agent command output

          if(currentCmd.equals("explore") ) {  //if told to explore
            explore(); //run exmplore command
            
          } else if(currentCmd.equals("moveTo()") ) {    //go to a target
            int targetX = Integer.parseInt(inputObj.getCurrentCmd() ); //get X value
            int targetY = Integer.parseInt(inputObj.getCurrentCmd()); //get Y value

            mvToDest(targetX, targetY);                  //move to set cell
            modelObj = movObj.getModelObject();          //update map object

          } else if(currentCmd.equals("quit") ) {      //told to quit
            running = false;                             //kill while loop
          }
        }
      }
    } catch (Exception e) {
      
    }
  }
  
  //method to make the robot go through the entire arena
  public static void explore() {
    for(loop1 = 0; loop1 < COLUMNS; ++loop1) { //for every row (X cell)
      if( (loop1 % 2) == 0) { //if the row is 'odd' (moving upward)
        for(loop2 = 0; loop2 < ROWS; ++loop2) {
          addVictim(    modelObj.getColour()   ); //floor colour to output q.

          boolean res = modelObj.scanAhead(DIST); //scans ahead
          modelObj.impScan(res, movObj.facing  ); //add to map - front only

          mvToDest(loop1, loop2);                 //move to set cell
          modelObj = movObj.getModelObject();     //update mapping object
        }
      } else { //if the row is even (moving downwards)
        for( loop2 = ROWS - 1; loop2 >= 0; --loop2) {
          addVictim(    modelObj.getColour()   ); //floor colour to output q.

          boolean res = modelObj.scanAhead(DIST); //scans ahead
          modelObj.impScan(res, movObj.facing  ); //add to map - front only

          mvToDest(loop1, loop2);                 //move to set cell
          modelObj = movObj.getModelObject();     //update mapping object
        }
      }
    }
    addVictim("explored = true;");
  }
  
  //method to call the moveTo() method in mapping class; re-use of code
  public static void mvToDest(int x, int y) { 
    //update current position after arriving at the destination
    modelObj.cPos = movObj.moveTo(x, y, modelObj.cPos, modelObj);
  }
  
  //pushes a victim (coloured floor) to the output queue
  public static void addVictim(String s) {
    btObj.vicList.push(s);
    
  }
  
}
