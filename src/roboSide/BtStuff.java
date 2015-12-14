import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BtStuff extends Runnable {

  //global variables
  public static NXTConnection    connO  ; //holds the type of connection (BT)
  public static DataOutputStream dos    ;
  public static Queue<String>    vicList;
  
  public BtStuff(NXTConnection nxtConn) {
    connO = nxtConn;
    vicList = new Queue<String>;
    dos = connO.openDataOutputStream();
    System.out.println("Output Stream Linked");
  }

  public void run() {
    try{
      while(true) {
        while(vicList.empty() ) {
          Thread.yield();
        }
        dos.writeUTF( (String)vicList.pop() );
        dos.flush();
      }
    } catch (Exception e) {
      
    }
  }
  
}
