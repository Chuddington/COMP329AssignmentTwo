import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;

public class BtStuff extends Runnable {

  //global variables
  public static NXTConnection   connO     ; //holds the type of connection (BT)
  public static DataInputStream dis       ;
  public static Queue<String>   cmdList   ;
  
  public BtStuff(NXTConnection nxtConn) {
    connO = nxtConn;
    cmdList = new Queue<String>;
    dis = connO.openDataInputStream();
    System.out.println("Input Stream Linked");
  }

  public void run() {
    try{
      while(true) {
        while(cmdList.empty() ) {
          addCmd(dis.readUTF() );

        }
        thread.yield();
      }
    } catch (Exception e) {
      
    }
  }
  
  public static void addCmd(String s) {
    cmdList.push(s);
  }
  
  public static String getCurrentCmd() {
    String x = (String)cmdList.pop();
    return x;
  }
  
}