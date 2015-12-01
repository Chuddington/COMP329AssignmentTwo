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

public class MarsEnv extends Environment {

    public static final int GSize = 7; // grid size
    public static final int GARB  = 16; // garbage code in grid model

    //translation for Jason Events
    public static final Term    ns = Literal.parseLiteral("next(slot)");
    public static final Term    pg = Literal.parseLiteral("pick(garb)");
    public static final Term    dg = Literal.parseLiteral("drop(garb)");
    public static final Term    bg = Literal.parseLiteral("burn(garb)");
    public static final Literal g1 = Literal.parseLiteral("garbage(r1)");
    public static final Literal g2 = Literal.parseLiteral("garbage(r2)");

    //add a logging variable for output
    static Logger logger = Logger.getLogger(MarsEnv.class.getName());

    //Global variables for the Environment and viewpoint
    private MarsModel model;
    private MarsView  view;
    
    @Override
    public void init(String[] args) {
        //Define global variables
        model = new MarsModel();
        view  = new MarsView(model);
        model.setView(view);
        
        updatePercepts(); //update the environment perception
    }
    
    @Override
    //Method to select the action that is called from Jason
    public boolean executeAction(String ag, Structure action) {
        logger.info(ag+" doing: "+ action);
        try {
            if (action.equals(ns)) { //Change to the next slot
                model.nextSlot();
                
            //Move forward a cell
            } else if (action.getFunctor().equals("move_towards")) {
                int x = (int)((NumberTerm)action.getTerm(0)).solve();
                int y = (int)((NumberTerm)action.getTerm(1)).solve();
                model.moveTowards(x,y);
            
            //pick up the garbage
            } else if (action.equals(pg)) {
                model.pickGarb();
                
            //drop the garbage
            } else if (action.equals(dg)) {
                model.dropGarb();
                
            //burn the garbage
            } else if (action.equals(bg)) {
                model.burnGarb();
                
            //don't do anything
            } else {
                return false;
            }
            
        //if it errors, output the stack trace
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        updatePercepts(); //update environment perception

        try {
            Thread.sleep(200); //wait for 0.2 seconds
        } catch (Exception e) {}
        
        //inform the agents the environment has changed
        informAgsEnvironmentChanged();
        return true;
    }
    
    /** creates the agents perception based on the MarsModel */
    void updatePercepts() {
        clearPercepts(); //clean the currently stored perception
        
        //obtain current positions
        Location r1Loc = model.getAgPos(0);
        Location r2Loc = model.getAgPos(1);
        
        //store the positions in a Literal variable
        Literal pos1 = Literal.parseLiteral("pos(r1," + r1Loc.x + "," + r1Loc.y + ")");
        Literal pos2 = Literal.parseLiteral("pos(r2," + r2Loc.x + "," + r2Loc.y + ")");

        //add the locations to the perception
        addPercept(pos1);
        addPercept(pos2);
        
        //If there is an obstacle in the way (X axis?)
        if (model.hasObject(GARB, r1Loc)) {
            addPercept(g1); //add garbage perception to r1 Location
        }
        //If there is an obstacle in the way (Y axis?)
        if (model.hasObject(GARB, r2Loc)) {
            addPercept(g2); //add garbage perception to r2 Location
        }
    }

    //class for modelling the environment
    class MarsModel extends GridWorldModel {
        
        public static final int MErr = 2; // max error in pick garb
        int nerr; // number of tries of pick garb
        boolean r1HasGarb = false; // whether r1 is carrying garbage or not

        //create a random value
        Random random = new Random(System.currentTimeMillis());

        //constructor
        private MarsModel() {
            super(GSize, GSize, 2);
            
            // initial location of agents
            try {
                setAgPos(0, 0, 0); //define the list of positions
                
                //set to be in the middle of the arena
                Location r2Loc = new Location(GSize/2, GSize/2);
                
                //set this value to the list of positions
                setAgPos(1, r2Loc);
                
            //if it errors
            } catch (Exception e) {
                e.printStackTrace(); //output error log
            }
            
            //define locations of garbage
            add(GARB, 3, 0);
            add(GARB, GSize-1, 0);
            add(GARB, 1, 2);
            add(GARB, 0, GSize-2);
            add(GARB, GSize-1, GSize-1);
        }
        
        //method which is for mapping the next location
        void nextSlot() throws Exception {
            //obtain current location
            Location r1 = getAgPos(0);
            r1.x++; //increment X axis
            
            //if X axis is the same as the width of the arena (in the wall)
            if (r1.x == getWidth()) {
                r1.x = 0; //reset the X axis
                r1.y++  ; //increment the Y axis
            }
            // finished searching the whole grid
            if (r1.y == getHeight()) { //if Y axis is the same (thinks it's in a wall)
                return; //end method
            }
            
            //set the location values for the robot
            setAgPos(0, r1);
            setAgPos(1, getAgPos(1)); // just to draw it in the view
        }
        
        //method to move forward a cell
        void moveTowards(int x, int y) throws Exception {
            Location r1 = getAgPos(0); //obtain the location of the robot
            
            //if mapping system's X axis is smaller than parameter
            if (r1.x < x)
                r1.x++; //increment mapping system's X axis
            else if (r1.x > x) //if 'MS' is greater than system's X axis
                r1.x--; //decrement mapping system's X axis
                
            //if mapping system's Y axis is smaller than parameter
            if (r1.y < y)
                r1.y++; //increment mapping system's Y axis
            else if (r1.y > y) //if 'MS' is greater than system's Y axis
                r1.y--; //decrement mapping system's Y axis
                
            //store the modified values in the mapping system
            setAgPos(0, r1);
            setAgPos(1, getAgPos(1)); // just to draw it in the view
        }
        
        //method to pick up the garbage in front of it
        void pickGarb() {
            // r1 location has garbage
            if (model.hasObject(GARB, getAgPos(0))) {
                // sometimes the "picking" action doesn't work
                // but never more than MErr times
                
                //nextError == MAX_ERROR
                if (random.nextBoolean() || nerr == MErr) {
                    remove(GARB, getAgPos(0)); //take the garbage from the environment
                    nerr = 0; //reset the error count
                    r1HasGarb = true; //state to say robot has garbage
                    
                } else { //if the robot didn't pick up the garbage
                    nerr++; //increment failure count
                }
            }
        }
        
        //method to leave the garbage at a location
        void dropGarb() {
            if (r1HasGarb) { //if holding garbage
                r1HasGarb = false; //state to say the robot does not have garbage
                add(GARB, getAgPos(0)); //set garbage location
            }
        }
        
        //method to remove garbage from the environment
        void burnGarb() {
            // r2 location has garbage
            if (model.hasObject(GARB, getAgPos(1))) {
                remove(GARB, getAgPos(1)); //delete garbage from the mapping system
            }
        }
    }
    
    //models the environment for the robot
    class MarsView extends GridWorldView {

        //constructor
        public MarsView(MarsModel model) {
            super(model, "Mars World", 600); //define the model
            defaultFont = new Font("Arial", Font.BOLD, 18); // change default font
            setVisible(true); //make the world visible
            repaint(); //redraw the output for the world
        }

        /** draw application objects */
        @Override
        public void draw(Graphics g, int x, int y, int object) {
            switch (object) { 
                //if the object is garbage, call a method
                case MarsEnv.GARB: drawGarb(g, x, y);  break;
            }
        }

        @Override //method to draw the agent in the environment world
        public void drawAgent(Graphics g, int x, int y, Color c, int id) {
            String label = "R"+(id+1); //generate a label for the drawn agent
            c = Color.blue; //set a colour for the label
            if (id == 0) { //if the agent is the first one
                c = Color.yellow; //set the colour to yellow
                if (((MarsModel)model).r1HasGarb) { //if the first agent has garbage
                    label += " - G"; //concatenate label with Garbage suffix
                    c = Color.orange; //set the colour to orange
                }
            }
            //call the parent method with modified parameters
            super.drawAgent(g, x, y, c, -1);
            if (id == 0) { //if the first agent
                g.setColor(Color.black); //set colour to black
            } else {
                g.setColor(Color.white); //otherwise set colour to white
            }
            
            //draw the parent method with the label and location
            super.drawString(g, x, y, defaultFont, label);
        }

        //method to draw the garbage on a map
        public void drawGarb(Graphics g, int x, int y) {
            super.drawObstacle(g, x, y); //parent method to draw obstacle
            g.setColor(Color.white);     //set colour to white
            drawString(g, x, y, defaultFont, "G"); //output a white 'G' for garbage
        }

    }    
}
//EOF