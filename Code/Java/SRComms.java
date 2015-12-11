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

public class SRComms implements Runnable {

  //variables for communication
  public static NXTConnector                connection;
  public static DataOutputStream            outStr    ;
  public static DataInputStream             inStr     ;
  public static String                      received  ;
  public static LinkedBlockingQueue<String> queue     ;

  
  SRComms(String nxtName, String nxtBTAddress) {
    received = null;
    q        = new LinkedBlockingQueue<String>();

    // Open up a BlueTooth connection
    connection        = new NXTConnector()                      ;
    boolean connected = conn.connectTo(nxtName, nxtBTAddress, 2);
    
    if(!connected) {
      System.err.println("Failed to connect!")   ;
      System.exit(1);
    }
    System.out.println("Connected to " + nxtName);
    
    //Set up input (from Bluetooth), and output (to Bluetooth)
    dis = new DataInputStream (connection.getInputStream()  );
    dos = new DataOutputStream(connection.getOutputStream() );
  }
  
  public void run() {
    try {
      while(true) {
        q.put(dis.readUTF() );
        Thread.sleep(100);
      }
    } catch (Exception e) {
      
    }
  }
  
  public String readItem() {
    while(q.size() == 0) {
      Thread.yield();
    }
    return q.poll();
  }

  public void writeItem() {
    while(q.empty() ) {
      Thread.yield();
    }
    dos.writeUTF( (String)q.pop() );
    dos.flush();

  }
  
}