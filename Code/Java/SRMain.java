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

public class SRMain extends Environment {
  
  //global variables here
    //constants
  public static final int DEGREE  = 108;
  public static final int COLUMNS = 4  ;
  public static final int ROWS    = 5  ;
  public static final int DIST    = 25 ;
  
    //Jason String Literals
  public static final Term explore   = Literal.parseLiteral("explore()");
  public static final Term outputMap = Literal.parseLiteral("getMap()");
  public static final Term mvToDest  = Literal.parseLiteral("");
  public static Logger log = Logger.getLogger(SRMain.class.getName());

    //object creation
  public static SREnv   envObj  ;
  public static SRModel modelObj;
  public static SRMov   movObj  ;  

  
  @Override
  public void init(String[] args) {
    //Define global variables
    movObj = new SRMov(DIST, COLUMNS, ROWS, DEGREE);
    modObj = new SRModel(COLUMNS, ROWS, movObj);
    movObj.setModelObject(modObj);
  }
  
}
