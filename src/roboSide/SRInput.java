package roboSide;

/*
 * File Purpose: Input class for the robot - becomes threaded to obtain commands
                 from the computer agents (explore(), moveTo() and quit() )
 * Author/s    : Michael Chadwick, Adam Large
 * Student IDs : 200882675       , 200963462
 */

import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BtStuff extends Runnable {

  //global variables
  public static NXTConnection   connO     ; //holds the type of connection (BT)
  public static DataInputStream dis       ; //stream to obtain input from agents
  public static Queue<String>   cmdList   ; //queue to hold the input
  
  
  //constructor
  public BtStuff(NXTConnection nxtConn) {
    connO = nxtConn;                           //holds the connection type (BT)
    cmdList = new Queue<String>;               //create command queue
    dis = connO.openDataInputStream();         //open the input stream
    System.out.println("Input Stream Linked"); //user information
  }

  //method called when class is threaded
  public void run() {
    try{
      while(true) {
        while(cmdList.empty() ) { //while there are no commands
          addCmd(dis.readUTF() ); //read input until commands are obtained

        } //when there are commands available
        thread.yield(); //use less resources
      }
    } catch (Exception e) {
      
    }
  }
  
  //method to add a command to the input queue
  public static void addCmd(String s) {
    cmdList.push(s);
  }
  
  //method to output the next available command
  public static String getCurrentCmd() {
    String x = (String)cmdList.pop();
    return x;
  }
  
}