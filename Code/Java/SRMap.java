import lejos.nxt.*;

public class SRMap {
  
  //global variables
  static int     cellSize          ;
  static int[][] map               ;
  static int[]   limit = new int[2];
  static int[]   currentPos        ;
  static int     facing            ;
  
  static UltrasonicSensor us = new UltrasonicSensor(SensorPort.S3);
  
  //constructor
  SRMap(int c, int r, int d) {
    map = new int[c][r]; //set the map size
    
    limit[0] = (c - 1) ; //set the X axis limit
    limit[1] = (r - 1) ; //set the Y axis limit
    
    cellSize = d       ; //travelling distance == cell size
    us.continuous()    ; //sets the sonar sensor to continually ping
  }
}