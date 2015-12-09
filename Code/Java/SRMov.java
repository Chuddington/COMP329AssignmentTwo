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
  
  public static SRModel modObj;
  public static DifferentialPilot pilot = new DifferentialPilot(3.22, 19, Motor.B, Motor.C);
  public static OdometryPoseProvider opp = new OdometryPoseProvider(pilot);
  
  //constructor
  Movement(int di, int c, int r, int de) {
    columns = c ;
    rows    = r ;
    dist    = di;
    degree  = de;
    facing  = 1 ;
  }
  
  //constructor including the SRModel class
  Movement(int di, int c, int r, int de, SRModel m) {
    columns = c ;
    rows    = r ;
    dist    = di;
    degree  = de;
    facing  = 1 ;
    modObj  = m ;
  }
  
  //method to turn the robot left
  public static void turnLeft() {
    pilot.rotate(-degree); //Turns left
    
    //if facing up (1), set to 4; decrement otherwise
    facing = (facing == 1) ? facing = 4 : --facing;
    
  }
  
  //method to turn the robot right
  public static void turnRight() {
    pilot.rotate(degree); //Turns right
    
    //if facing right (4), set to 1; increment otherwise
    facing = (facing == 4) ? facing = 1 : ++facing;
    
  }
  
  //method to turn and face a particular direction
  public static void turnTo(int result) {
    while(facing != result) { //when not facing the desired direction
      //turn left if result is anti-clockwise; else turn right
      (facing > result) ? turnLeft() : turnRight();
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
  
  //move forward a cell
  public static void moveFwd() {
    pilot.travel(dist);
  }
  
  //method which moves to a cell; does not check for objects
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
  
}
