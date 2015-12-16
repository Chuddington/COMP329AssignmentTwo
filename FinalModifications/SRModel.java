/*
 * File Purpose: Mapping class for the robot - contains 2D array and current
 *               Robot position
 * Author/s    : Michael Chadwick
 * Student IDs : 200882675
 */

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
    xLimit  = x            ; //length of the X axis
    yLimit  = y            ; //length of the Y axis
    map     = new int[x][y]; //generate the correct size map
    populateMap()          ; //method to fill with UNKNOWN_ID
    cPos    = new int[2]   ; //array to store current robot position
    cPos[0] = 0            ; //X axis position
    cPos[1] = 0            ; //Y axis position
    us.continuous();         //ultrasonic sensor continually pings
  }
  
  //constructor including Movement object
  SRModel(int x, int y, SRMov m) {
	  this(x, y); //run base constructor
	  moveO = m ; //add moveObj variable
    
  }
  
  //method to fill the map with 'fog of war'
  public static void populateMap() {
    for(int loop1 = 0; loop1 < xLimit; ++loop1) {
      for(int loop2 = 0; loop2 < yLimit; ++loop2) {
        map[loop1][loop2] = UNKNOWN_ID;
      }
    }
    map[0][0] = EMPTY_ID;
  }
  
  //method to scan ahead only - used in SRMain.explore() for faster traversal
  public static boolean scanAhead(int d) {
    int dest = 0;            //store distance to object
    
    Motor.A.rotateTo(0)    ; //rotate to front
    Delay.msDelay(200)     ; //wait for accurate reading
    dest = us.getDistance(); //scan
    Delay.msDelay(50)      ; //wait for reading to be confirmed

    if(dest < d) {           //if obstacle is in the 'next' cell
      return true ;          //set true when an obstacle is there
    } else {
      return false;          //set false when an obstacle is not
    }
    
  }
  
  //method to scan around the robot - left, ahead and right
  public static boolean[] scanAll(int d)  {
    boolean[] results = new boolean[3]; //stores the boolean results
    int dest  = 0;                      //stores distance to object
    
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
      results[0] = true ;    //set left value to true
    } else {
      results[0] = false;    //set left value to false
    }
    
    Motor.A.rotateTo(650)  ; //rotate to right        
    Delay.msDelay(200)     ;
    dest = us.getDistance(); //scan 
    Delay.msDelay(50)      ;
    
    if(dest < d) {           //if directly ahead
      results[2] = true ;    //set right value to true
    } else {
      results[2] = false;    //set right value to false
    }                  
    
    Motor.A.rotateTo(0) ; //rotate to front
    return results;
  }
  
  //method to check whether an edge of the arena is to the left of the robot
  public static boolean scanLimitLeft(int f) {
    switch(f) { //f = facing in movement object
      case(1):  //when facing up
        if( (cPos[0] - 1) == -1) { //decremented X axis is the left of robot
          return true;  //arena edge is found
        } else {
          //update correct map position with scanned, empty cell
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
  //very similar to the scanLimitLeft() method
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
  //very similar to the scanLimitLeft() method
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
  
  //method to set the cell at the left of the robot to be an obstacle
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
  
  //method to set the cell ahead of the robot to be an obstacle
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
  
  //method to set the cell at the right of the robot to be an obstacle
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
  
  //checks whether the robot can move into the cell in front of itself
  //It knows by only being able to move into cells it has either scanned or
  //moved into previously.
  public static boolean canMove(int[] cp, int f) {
    switch(f) {
      case(1): //facing up
        if( (map[cp[0] ][cp[1] + 1] < UNKNOWN_ID) && (cp[1] + 1 != yLimit) ) {
          return true ; //true if scanned, empty or has a victim in cell
        } else {
          return false; //false if cell is not scanned or has an obstacle
        }
      case(2): //facing right
        if( (map[cp[0] + 1][cp[1] ] < UNKNOWN_ID) && (cp[0] + 1 != xLimit) ) {
          return true ;
        } else {
          return false;
        }
      case(3): //facing down
        if( (map[cp[0] ][cp[1] - 1] < UNKNOWN_ID) && (cp[1] - 1 != -1) ) {
          return true ;
        } else {
          return false;
        }
      case(4): //facing left
        if( (map[cp[0] - 1][cp[1] ] < UNKNOWN_ID) && (cp[0] - 1 != -1) ) {
          return true ;
        } else {
          return false;
        }
    }
    return false;
  }
  
  //method to check if the target cell (such as in moveTo() ) contains an
  //obstacle - meaning it is impossible to move into the target cell
  public static boolean obsAtTarget(int x, int y) {
    if(map[x][y] == OBSTACLE_ID) {
      return true ;
    } else {
      return false;
    }
  }
  
  //method to implement the scan results into the map[][].
  //r[x] is from the scanAll() method output; f = SRMov.facing
  public static void impScan(boolean[] r, int f) {
    if(r[0] && !scanLimitLeft(f) ) {
      obstacleAtLeft(f) ;
    }
    if(r[1] && !scanLimitUp(f) ) {
      obstacleAtUp(f)   ;
    }
    if(r[2] && !scanLimitRight(f) ) {
      obstacleAtRight(f);
    }
  }
  
  //method to implement the scan results into the map[][].
  //variant used when only scanAhead() is called
  public static void impScan(boolean r, int f) {
    if(r && !scanLimitUp(f) ) {
      obstacleAtUp(f);
    }
  }
  
  //method to create a local copy of the movement object
  //this method might not be used during this program
  public static void setMovementObject(SRMov m) {
    moveO = m;
  }
  
  //method to obtain the local copy of the movement object
  //this method might not be used - is useful in updating the movement object
  public static SRMov getMovementObject() {
    return moveO;
  }
  
  //method to return a string for Jason; outputs victim colour
  public String getColour() {
    int colour = cs.getColorID(); //scan colour
    String literal = "";
    
    StringBuilder sb = new StringBuilder();
    
    //create initial string for all cases
    sb.append("victim(");

    if(colour == 0) {         //red   victim
    
    //literal = String.format("victim(%d,%d,%d)", RED_VIC, cPos[0], cPos[1] );
      
      /*Used a string builder to create literal as the above commented out code refused to work*/
      sb.append(RED_VIC);
      sb.append(",");
      sb.append(cPos[0]);
      sb.append(",");
      sb.append(cPos[1]);
      sb.append(")");
      
      //add victim to map
      map[cPos[0] ][cPos[1] ] = RED_VIC;
      
    } else if (colour == 2)	{ //blue  victim
     
      //translate colour ID 2 to 1 as blue has higher priority
      //create literal
      sb.append(BLU_VIC);
      sb.append(",");
      sb.append(cPos[0]);
      sb.append(",");
      sb.append(cPos[1]);
      sb.append(")");
      
      //add victim to map
      map[cPos[0] ][cPos[1] ] = BLU_VIC;
      
    } else if (colour == 1)	{ //green victim
    	
      //translate colour ID 1 to 2 as green has lower priority
      //create literal
      sb.append(GRN_VIC);
      sb.append(",");
      sb.append(cPos[0]);
      sb.append(",");
      sb.append(cPos[1]);
      sb.append(")");
      
      //add victim to map
      map[cPos[0] ][cPos[1] ] = GRN_VIC;
      
    } else {
      map[cPos[0] ][cPos[1] ] = EMPTY_ID; //set cell to empty & traversed
      
      //send colour to agent - agent deals with error checking
      //create literal
      sb.append(colour);
      sb.append(",");
      sb.append(cPos[0]);
      sb.append(",");
      sb.append(cPos[1]);
      sb.append(")");
    }
    
    //create string
    literal = sb.toString();
    
    return literal;
  }
  

}
