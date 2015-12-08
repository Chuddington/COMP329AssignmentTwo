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
  
  public static DifferentialPilot pilot = new DifferentialPilot(3.22, 19, Motor.B, Motor.C);
  public static OdometryPoseProvider opp = new OdometryPoseProvider(pilot);
  
  Movement(int di, int c, int r, int de) {
    columns = c ;
    rows    = r ;
    dist    = di;
    degree  = de;
    facing  = 1 ;
  }
    
  public static void turnLeft() {
    pilot.rotate(-degree); //Turns left
    
    //if facing up (1), set to 4; decrement otherwise
    facing = (facing == 1) ? facing = 4 : --facing;
    
  }
  
  public static void turnRight() {
    pilot.rotate(degree); //Turns right
    
    //if facing right (4), set to 1; increment otherwise
    facing = (facing == 4) ? facing = 1 : ++facing;
    
  }
  
  //method to turn to face a particular direction
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
  
  //move forward a cell
  public static void moveFwd() {
    pilot.travel(dist);
  }
  
}