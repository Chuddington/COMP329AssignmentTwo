import lejos.nxt.*;
import lejos.nxt.addon.ColorHTSensor;

class ColourStuff {
	ColorHTSensor cs = new ColorHTSensor(SensorPort.S4);	//create colour sensor object
	int x = 0, y = 0;		//coordinates that need to be initialised when object of this class is created, set to 0 for now
	
	/* Scans for a victim. Uses the colour it scans
	 * to create a string literal for Jason. Returns
	 * that literal.
	 */
	public String getColour() {
		int colour = cs.getColorID();		//scan colour
		String literal = "";				
		
		if(colour == 0)												//red
			literal = String.format("victim(0,%d,%d)", x, y);
		else if (colour == 2)										//blue
			literal = String.format("victim(1,%d,%d)", x, y);		//translate colour ID 2 to 1 as blue has higher priority
		else if (colour == 1)										//green
			literal = String.format("victim(2,%d,%d)", x, y);		//translate colour ID 1 to 2 as green has lower priority
		return literal;
	}	
}
