package roboSide;

import java.util.Random;
import java.util.logging.Logger;
import java.util.Queue;

import lejos.nxt.*;
import lejos.nxt.addon.ColorHTSensor;
import lejos.util.Delay;

//class used to model/map the world
public class SRModel {
  
  //Global Variables
    //actual value-changing variables
  public static int     xLimit; //number of cells on the X axis
  public static int     yLimit; //number of cells on the Y axis
  
      //cPos and map use the top down perspective.  This is important when
      //reading the ultrasonic scan methods
  public static int[][] map   ; //robot's interpretation of the arena
  public static int[]   cPos  ; //stores the X and Y axis of the robot

    //class/object variables
  public static SRMov     moveO;
  public static UltrasonicSensor us = new UltrasonicSensor(SensorPort.S3);
  public static ColorHTSensor    cs = new ColorHTSensor(   SensorPort.S4);
  
    //constants
  public static final int UNKNOWN_ID  = 6; //cell with 'fog of war'
  public static final int SCANNED_ID  = 5; //scanned with US but not entered
  public static final int EMPTY_ID    = 4; //cell the robot has entered
  public static final int OBSTACLE_ID = 9; //cell with an obstacle
  public static final int RED_VIC     = 0; //cell with red   'victim'
  public static final int BLU_VIC     = 1; //cell with blue  'victim'
  public static final int GRN_VIC     = 2; //cell with green 'victim'


  //constructor
  SRModel(int x, int y) {
    xLimit  = x                ;
    yLimit  = y                ;
    map     = new int[x][y]    ;
    populateMap();
    cPos    = new int[2]       ;
    cPos[0] = 0                ; //X axis position
    cPos[1] = 0                ; //Y axis position
    us.continuous();
  }
  
  //constructor including Movement object
  SRModel(int x, int y, SRMov m) {
	  this(x, y);
	  moveO = m;
    
  }
  
  //method to fill the map with 'fog of war'
  public static void populateMap() {
    for(int loop1 = 0; loop1 < xLimit; ++loop1) {
      for(int loop2 = 0; loop2 < yLimit; ++loop2) {
        map[loop1][loop2] = UNKNOWN_ID;
      }
    }
  }
  
  //method to scan ahead - could be used in the moveTo() method in SRMov
  public static boolean scanAhead(int d) {
    boolean result    ;
    int     dest   = 0;
    Motor.A.rotateTo(0)    ; //rotate to front
    Delay.msDelay(200)     ; //wait for accurate reading
    dest = us.getDistance(); //scan
    Delay.msDelay(50)      ; //wait for reading to be confirmed

    if(dest < d) {           //if directly ahead
      result = true ;        //set true when an obstacle is there
    } else {
      result = false;        //set false when an obstacle is not
    }
    
    return result;
  }
  
  public static boolean[] scanAll(int d)  {
    boolean[] results = new boolean[3];
    int       dest    = 0             ;
    Motor.A.rotateTo(0)    ; //rotate to front
    Delay.msDelay(200)     ; //wait for accurate reading
    dest = us.getDistance(); //scan
    Delay.msDelay(50)      ; //wait for reading to be confirmed

    if(dest < d) {           //if directly ahead
      results[1] = true ;    //set true when an obstacle is there
    } else {
      results[1] = false;    //set false when an obstacle is not
    }
    
    Motor.A.rotateTo(-650) ; //rotate to left
    Delay.msDelay(200)     ;
    dest = us.getDistance(); //scan
    Delay.msDelay(50)      ;
    
    if(dest < d) {           //if directly ahead
      results[0] = true ;
    } else {
      results[0] = false;
    }
    
    Motor.A.rotateTo(650)  ; //rotate to right        
    Delay.msDelay(200)     ;
    dest = us.getDistance(); //scan 
    Delay.msDelay(50)      ;
    
    if(dest < d) {        //if directly ahead
      results[2] = true ;
    } else {
      results[2] = false;
    }                  
    
    Motor.A.rotateTo(0) ; //rotate to front
    
    return results;
  }
  
  //method to check whether an edge of the arena is to the left of the robot
  public static boolean scanLimitLeft(int f) {
    switch(f) { //f = facing in movement object
      case(1):  //when facing up
        if( (cPos[0] - 1) == -1) {
          return true;
        } else {
          map[cPos[0] - 1][cPos[1] ] = SCANNED_ID;
          return false;
        }
	case(2):  //when facing right
        if( (cPos[1] + 1) == yLimit) {
          return true;
        } else {
          map[cPos[0] ][cPos[1] + 1] = SCANNED_ID;
          return false;
        }
      case(3):  //when facing down
        if( (cPos[0] + 1) == xLimit) {
          return true;
        } else {
          map[cPos[0] + 1][cPos[1] ] = SCANNED_ID;
          return false;
        }
      case(4):  //when facing left
        if( (cPos[1] - 1) == -1) {
          return true;
        } else {
          map[cPos[0] ][cPos[1] - 1] = SCANNED_ID;
          return false;
        }
    }
    return false;
  }
  
  //method to check whether an edge of the arena is in front of the robot
  public static boolean scanLimitUp(int f) {
    switch(f) { //f = facing in movement object
      case(1):  //when facing up
        if( (cPos[1] + 1) == yLimit) {
          return true;
        } else {
          map[cPos[0] ][cPos[1] + 1] = SCANNED_ID;
          return false;
        }
        
      case(2):  //when facing right
        if( (cPos[0] + 1) == xLimit) {
          return true;
        } else {
          map[cPos[0] + 1][cPos[1] ] = SCANNED_ID;
          return false;
        }
      case(3):  //when facing down
        if( (cPos[1] - 1) == -1) {
          return true;
        } else {
          map[cPos[0] ][cPos[1] - 1] = SCANNED_ID;
          return false;
        }
      case(4):  //when facing left
        if( (cPos[0] - 1) == -1) {
          return true;
        } else {
          map[cPos[0] - 1][cPos[1] ] = SCANNED_ID;
          return false;
        }
    }
    return false;
  }
  
  //method to check whether an edge of the arena is to the right of the robot
  public static boolean scanLimitRight(int f) {
    switch(f) { //f = facing in movement object
      case(1):  //when facing up
        if( (cPos[0] + 1) == xLimit) {
          return true;
        } else {
          map[cPos[0] + 1][cPos[1] ] = SCANNED_ID;
          return false;
        }
      case(2):  //when facing right
        if( (cPos[1] - 1) == -1) {
          return true;
        } else {
          map[cPos[0] ][cPos[1] - 1] = SCANNED_ID;
          return false;
        }
      case(3):  //when facing down
        if( (cPos[0] - 1) == -1) {
          return true;
        } else {
          map[cPos[0] - 1][cPos[1] ] = SCANNED_ID;
          return false;
        }
      case(4):  //when facing left
        if( (cPos[1] + 1) == yLimit) {
          return true;
        } else {
          map[cPos[0] ][cPos[1] + 1] = SCANNED_ID;
          return false;
        }
    }
    return false;
  }
  
  public static void obstacleAtLeft(int f) {
    switch(f) { //f = facing in movement object
      case(1):  //when facing up
        map[cPos[0] - 1][cPos[1] ] = OBSTACLE_ID;
        break;
      case(2):  //when facing right
        map[cPos[0] ][cPos[1] + 1] = OBSTACLE_ID;
        break;
      case(3):  //when facing down
        map[cPos[0] + 1][cPos[1] ] = OBSTACLE_ID;
        break;
      case(4):  //when facing left
        map[cPos[0] ][cPos[1] - 1] = OBSTACLE_ID;
        break;
    }
  }
  
  public static void obstacleAtUp(int f) {
    switch(f) { //f = facing in movement object
      case(1):  //when facing up
        map[cPos[0] ][cPos[1] + 1] = OBSTACLE_ID;
        break;
      case(2):  //when facing right
        map[cPos[0] + 1][cPos[1] ] = OBSTACLE_ID;
        break;
      case(3):  //when facing down
        map[cPos[0] ][cPos[1] - 1] = OBSTACLE_ID;
        break;
      case(4):  //when facing left
        map[cPos[0] - 1][cPos[1] ] = OBSTACLE_ID;
        break;
    }
  }
  
  //method to check whether an edge of the arena is to the right of the robot
  public static void obstacleAtRight(int f) {
    switch(f) { //f = facing in movement object
      case(1):  //when facing up
        map[cPos[0] + 1][cPos[1] ] = OBSTACLE_ID;
        break;
      case(2):  //when facing right
        map[cPos[0] ][cPos[1] - 1] = OBSTACLE_ID;
        break;
      case(3):  //when facing down
        map[cPos[0] - 1][cPos[1] ] = OBSTACLE_ID;
        break;
      case(4):  //when facing left
        map[cPos[0] ][cPos[1] + 1] = OBSTACLE_ID;
        break;
    }
  }
  
  public static boolean canMove(int[] cp, int f) {
    switch(f) {
      case(1): //facing up
        if( (map[cp[0] ][cp[1] + 1] < UNKNOWN_ID) && (cp[1] + 1 != yLimit) ) {
          return true;
        } else {
          return false;
        }
      case(2): //facing right
        if( (map[cp[0] + 1][cp[1] ] < UNKNOWN_ID) && (cp[0] + 1 != xLimit) ) {
          return true;
        } else {
          return false;
        }
      case(3): //facing down
        if( (map[cp[0] ][cp[1] - 1] < UNKNOWN_ID) && (cp[1] - 1 != -1) ) {
          return true;
        } else {
          return false;
        }
      case(4): //facing left
        if( (map[cp[0] - 1][cp[1] ] < UNKNOWN_ID) && (cp[0] - 1 != -1) ) {
          return true;
        } else {
          return false;
        }
      
    }
    return false;
  }
  
  public static boolean obsAtTarget(int x, int y) {
    if(map[x][y] == OBSTACLE_ID) {
      return true;
    } else {
      return false;
    }

  }
  
  public static void impScan(boolean[] r, int f) {
    if(r[0] && !scanLimitLeft(f) ) {
      obstacleAtLeft(f);
    }
    if(r[1] && !scanLimitUp(f) ) {
      obstacleAtUp(f);
    }
    if(r[2] && !scanLimitRight(f) ) {
      obstacleAtRight(f);
    }
  }
  
  public static void impScan(boolean r, int f) {
    if(r && !scanLimitUp(f) ) {
      obstacleAtUp(f);
    }
  }
  
  public static void setMovementObject(SRMov m) {
    moveO = m;
  }
  
  public static SRMov getMovementObject() {
    return moveO;
  }
  
  //method to return a string for Jason; outputs victim colour
  public String getColour() {
    int colour = cs.getColorID(); //scan colour
    String literal = "";

    if(colour == 0) {         //red   victim
      literal = String.format("victim(%d,%d,%d)", RED_VIC, cPos[0], cPos[1] );
      
      //add victim to map and the list of victims
      map[cPos[0] ][cPos[1] ] = RED_VIC;
      
    } else if (colour == 2)	{ //blue  victim
      //translate colour ID 2 to 1 as blue has higher priority
      literal = String.format("victim(%d,%d,%d)", BLU_VIC, cPos[0], cPos[1] );
      
      //add victim to map and the list of victims
      map[cPos[0] ][cPos[1] ] = BLU_VIC;
      
    } else if (colour == 1)	{ //green victim
      //translate colour ID 1 to 2 as green has lower priority
      literal = String.format("victim(%d,%d,%d)", GRN_VIC, cPos[0], cPos[1] );
      
      //add victim to map and the list of victims
      map[cPos[0] ][cPos[1] ] = GRN_VIC;
      
    } else {
      map[cPos[0] ][cPos[1] ] = EMPTY_ID; //set cell to empty & traversed
      //send colour to agent - agent deals with error checking
      literal = String.format("victim(%d, %d, %d)", colour, cPos[0], cPos[1] );
    }
    
    return literal;
  }
  

}
