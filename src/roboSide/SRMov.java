package roboSide;

import lejos.nxt.*;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.localization.OdometryPoseProvider;

public class SRMov {
  
  //global variables
  public static int columns; //number of columns in the arena
  public static int rows   ; //number of rows    in the arena
  public static int dist   ; //distance taken to move into a different cell
  public static int degree ; //rotational strength of the robot
  public static int facing ; //state var to indicate where robot is facing
  
  //object declarations
  public static SRModel modObj;
  public static DifferentialPilot pilot = new DifferentialPilot(3.22, 19, Motor.B, Motor.C);
  public static OdometryPoseProvider opp = new OdometryPoseProvider(pilot);
  
  
  //constructor
  SRMov(int di, int c, int r, int de) {
    columns = c ; //number of X axis cells
    rows    = r ; //number of Y axis cells
    dist    = di; //distance to move forward
    degree  = de; //strength of turning
    facing  = 1 ; //direction the robot is facing (1 = up ... 4 = left)
  }
  
  //constructor including the SRModel class
  SRMov(int di, int c, int r, int de, SRModel m) {
	this(di, c, r, de); //use base constructor
	modObj  = m ;       //store local copy of mapping object
    
  }
  
  //method to turn the robot left
  public static void turnLeft() {
    pilot.rotate(-degree); //Turns left
    
    //if facing up (1), set to 4; decrement otherwise
    if(facing == 1) {
      facing = 4;
    } else {
      --facing; 
    }
  }
  
  //method to turn the robot right
  public static void turnRight() {
    pilot.rotate(degree); //Turns right
    
    //if facing right (4), set to 1; increment otherwise
    if(facing == 4) {
      facing = 1;
    } else {
      ++facing; 
    }
  }
  
  //method to turn and face a particular direction
  public static void turnTo(int result) {
    while(facing != result) { //when not facing the desired direction
      //turn left if result is anti-clockwise; else turn right
      if (facing > result)
        turnLeft();
      else
        turnRight();
    }
  }
  
  //outputs the current direction the robot is facing
  public static int getFacing() {
    return facing;
  }
  
  //updates the Modelling object
  public static void setModelObject(SRModel m) {
    modObj = m;
  }
  
  //gets local copy of mapping object - used to update object in SRMain
  public static SRModel getModelObject() {
    return modObj;
  }
  
  //move forward a cell
  public static void moveFwd() {
    pilot.travel(dist);
  }
  
  //method which moves to a target cell; does not check for objects
  //this method is not suitable for use
  public static int[] moveTo(int x, int y, int[] cPos) {
    int[] z = cPos; //create a local copy of current position
    
    while(z[0] != x) {       //while the robot is not on the same X axis
      if(x > z[0] ) {        //if the destination is to the right of the robot
        turnTo(2);           //face right
        moveFwd();           //move forward a cell
        ++z[0]   ;           //increment X axis local copy
      } else if(x < z[0] ) { //if the destination is to the left of the robot
        turnTo(4);           //face left
        moveFwd();           //move forward a cell
        --z[0]   ;           //decrement X axis local copy
      }
    }
    
    while(z[1] != y) {       //while the robot is not on the same Y axis
      if(y > z[1] ) {        //if the destination is 'above' the robot
        turnTo(1);           //face up
        moveFwd();           //move forward a cell
        ++z[1]   ;           //increment Y axis local copy
      } else if(y < z[1] ) { //if the destination is 'below' the robot
        turnTo(3);           //face down
        moveFwd();           //move forward a cell
        --z[1]   ;           //decrement Y axis local copy
      }
    }
    
    return z; //output what is now the robot's current position
  }
  
  //moves to a destination, using the map for help
  public static int[] moveTo(int x, int y, int[] cPos, SRModel map) {
    int[] z = cPos; //create a local copy of current position
    setModelObject(map);
    
    //while robot is not at the target position
    while(z[0] != x && z[1] != y) {
      while(z[0] != x) {       //while the robot is not on the same X axis
        if(x > z[0] ) {        //if the dest. is to the right of the robot
          
          turnTo(2);           //face to the right
          if(modObj.canMove(z, facing) ) {
            moveFwd();         //if right-cell is at least scanned and empty,
            ++z[0];            //move into it, increment X axis position.
            
          } else {             //if right-cell is unknown or has an obstacle
            turnTo(1);         //face up
            if(modObj.canMove(z, facing) ) {
              moveFwd();       //if above-cell is at least scanned and empty
              ++z[1];          //move into it, increment Y axis position
              
            } else {           //if above-cell is unknown or has an obstacle
              turnTo(3);       //face down
              if(modObj.canMove(z, facing) ) {
                moveFwd();     //if below-cell is at least scanned and empty
                --z[1];        //move into it, decrement Y axis position
                
              } else {         //if up, down and right cell is unknown/occupied
                turnTo(2);
                boolean[] scnRes = modObj.scanAll(dist);
                modObj.impScan(scnRes, facing);
                if(modObj.obsAtTarget(x, y) ) { //if obs. in target pos.
                  return z; //end method, return current position
                }
                break;
              }
            }
          }
        } else if(x < z[0] ) { //if the dest. is to the left of the robot
          
          turnTo(4);           //face to the left
          if(modObj.canMove(z, facing) ) {
            moveFwd();         //if left-cell is at least scanned and empty,
            --z[0];            //move into it, decrement X axis position.
            
          } else {             //if left-cell is unknown or has an obstacle
            turnTo(1);         //face up
            if(modObj.canMove(z, facing) ) {
              moveFwd();       //if above-cell is at least scanned and empty
              ++z[1];          //move into it, increment Y axis position
              
            } else {           //if above-cell is unknown or has an obstacle
              turnTo(3);       //face down
              if(modObj.canMove(z, facing) ) {
                moveFwd();     //if below-cell is at least scanned and empty
                --z[1];        //move into it, decrement Y axis position
                
              } else {         //if up, down and left cell is unknown/occupied
                turnTo(4);
                boolean[] scnRes = modObj.scanAll(dist);
                modObj.impScan(scnRes, facing);
                if(modObj.obsAtTarget(x, y) ) { //if obs. in target pos.
                  return z; //end method, return current position
                }
                break;
              }
            }
          }
        }
      } //end of X axis while loop
      
      while(z[1] != y) {       //while the robot is not on the same Y axis
        if(y > z[1] ) {        //if the dest. is 'above' the robot
          
          turnTo(1);           //face up
          if(modObj.canMove(z, facing) ) {
            moveFwd();         //if above-cell is at least scanned and empty,
            ++z[1];            //move into it, increment Y axis position.
            
          } else {             //if above-cell is unknown or has an obstacle
            turnTo(2);         //face right
            if(modObj.canMove(z, facing) ) {
              moveFwd();       //if right-cell is at least scanned and empty
              ++z[0];          //move into it, increment X axis position
              
            } else {           //if right-cell is unknown or has an obstacle
              turnTo(4);       //face left
              if(modObj.canMove(z, facing) ) {
                moveFwd();     //if left-cell is at least scanned and empty
                --z[0];        //move into it, decrement X axis position
                
              } else {         //if up, right and left cell is unknown/occupied
                turnTo(4);
                boolean[] scnRes = modObj.scanAll(dist);
                modObj.impScan(scnRes, facing);
                if(modObj.obsAtTarget(x, y) ) { //if obs. in target pos.
                  return z; //end method, return current position
                }
                break;         //perform a scan and update the map
              }
            }
          }
          
        } else if(y < z[1] ) { //if the dest. is 'below' the robot
          
          turnTo(3);           //face down
          if(modObj.canMove(z, facing) ) {
            moveFwd();         //if below-cell is at least scanned and empty,
            --z[1];            //move into it, increment Y axis position.
            
          } else {             //if below-cell is unknown or has an obstacle
            turnTo(2);         //face right
            if(modObj.canMove(z, facing) ) {
              moveFwd();       //if right-cell is at least scanned and empty
              ++z[0];          //move into it, increment X axis position
              
            } else {           //if right-cell is unknown or has an obstacle
              turnTo(4);       //face left
              if(modObj.canMove(z, facing) ) {
                moveFwd();     //if left-cell is at least scanned and empty
                --z[0];        //move into it, decrement X axis position
                
              } else {         //if down, right, left cell is unknown/occupied
                turnTo(4);
                boolean[] scnRes = modObj.scanAll(dist);
                modObj.impScan(scnRes, facing);
                if(modObj.obsAtTarget(x, y) ) { //if obs. in target pos.
                  return z; //end method, return current position
                }
                break;         //perform a scan and update the map
              }
            }
          }
        }
      } //end of Y   axis while loop
    
    }   //end of X/Y axis while loop
    
    return z; //output what is now the robot's current position
  }
  
}
