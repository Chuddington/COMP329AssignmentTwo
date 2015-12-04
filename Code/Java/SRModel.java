import jason.asSyntax.*;
import jason.environment.Environment;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;
import java.util.logging.Logger;

public class SRModel extends GridWorldModel {
  
  //Global Variables
  public static int xCellTotal; //number of cells on the X axis
  public static int yCellTotal; //number of cells on the Y axis


  //constructor
  SRModel(int x, int y) {
    xCellTotal = x;
    yCellTotal = y;
  }

}
