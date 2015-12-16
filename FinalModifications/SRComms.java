package robot;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

import lejos.pc.comm.NXTConnector;

public class SRComms implements Runnable {

  //variables for communication
  public static NXTConnector                connection;
  public static DataOutputStream            outStr    ;
  public static DataInputStream             inStr     ;
  public static String                      received  ;
  public static LinkedBlockingQueue<String> queue     ;

  
  SRComms(String nxtName, String nxtBTAddress) {
    received = null;
    queue        = new LinkedBlockingQueue<String>();

    // Open up a BlueTooth connection
    connection        = new NXTConnector()                      ;
    boolean connected = connection.connectTo(nxtName, nxtBTAddress, 2);
    
    if(!connected) {
      System.err.println("Failed to connect!")   ;
      System.exit(1);
    }
    System.out.println("Connected to " + nxtName);
    
    //Set up input (from Bluetooth), and output (to Bluetooth)
    inStr = new DataInputStream (connection.getInputStream()  );
    outStr = new DataOutputStream(connection.getOutputStream() );
  }
  
  public void run() {
    try {
      while(true) {
        queue.put(inStr.readUTF() );
        Thread.sleep(100);
      }
    } catch (Exception e) {
      
    }
  }
  
  public String readItem() {
    while(queue.size() == 0) {
      Thread.yield();
    }
    return queue.poll();
  }

  public void writeItem() {
    while(queue.isEmpty() ) {
      Thread.yield();
    }
    
    try {
		outStr.writeUTF( (String)queue.poll() );
		outStr.flush();
	} catch (IOException e) {}

  }
  
  public void sendBluetooth(String command){
    try{
        outStr.writeUTF(command);
        outStr.flush();
        outStr.close();
      }catch(IOException e){}
  }
}
