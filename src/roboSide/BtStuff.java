import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BtStuff {

  //global variables
  public static NXTConnection    connObj; //holds the type of connection (BT)
  public static DataOutputStream dos    ;
  public static DataInputStream  dis    ;
  
  
  public BtStuff(NXTConnection nxtConn) {
    connObj = nxtConn;
    System.out.println("Linked via BT..." );
    
    dos = connObj.openDataOutputStream();
    dis = connObj.openDataInputStream() ;
  }

  

  //method to print a String input to the RConsole
  public static void stringToRCon(String s) {
    RConsole.println(s);
  }

  //method to print a Pose input to the RConsole
  public static void poseToRCon(Pose p) {
    RConsole.println(p.toString() );
  }

}
