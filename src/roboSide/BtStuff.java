package roboSide;

/*
 * File Purpose: Output class for the robot - becomes threaded to output 
                 locations of victims and when it has finished exploring
 * Author/s    : Michael Chadwick, Adam Large
 * Student IDs : 200882675       , 200963462
 */

import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;

//class dealing with output to the computer agents
public class BtStuff extends Runnable {

  //global variables
  public static NXTConnection    connO  ; //holds the type of connection (BT)
  public static DataOutputStream dos    ; //used to link to the agents
  public static Queue<String>    vicList; //stores found victims
  
  
  //constructor
  public BtStuff(NXTConnection nxtConn) {
    connO = nxtConn;                            //store the connection type 
    vicList = new Queue<String>;                //create Victim Queue
    dos = connO.openDataOutputStream();         //open an output stream
    System.out.println("Output Stream Linked"); //user information
  }

  //method called when threaded
  public void run() {
    try{
      while(true) {
        while(vicList.empty() ) { //while the queue is empty
          Thread.yield();         //don't do anything
        }
        //when vicList queue is populated
        dos.writeUTF( (String)vicList.pop() ); //output value to agents
        dos.flush();                           //force sending of output
      }
    } catch (Exception e) {
      
    }
  }
}
