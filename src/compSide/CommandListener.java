import java.io.*;


public class CommandListener implements Runnable {

	DataInputStream dis;
	SRMain mainEnv;
	
	public CommandListener(DataInputStream dis, SRMain mainEnv){
		this.dis = dis;
		this.mainEnv;	
	}	
	
	public void run(){
	
		while(true){
		
			String com = null;
			
			try{
				com = dis.readUTF();
				}
			catch (IOException e) {}
			mainEnv.action = com;
			mainEnv.exectuteAction();
	
	}
	}
}