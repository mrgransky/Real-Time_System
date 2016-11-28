package communicateWithRealProcessTESTwithSTOPP;

import se.lth.control.realtime.IOChannelException;


public class Main {
	
	private static OpComCartCoommunication opcom;
	private static RealSystem realSystem;
	private static DataLogger dataLogger;

	public static void main(String[] args) throws IOChannelException {
		opcom = new OpComCartCoommunication();
		realSystem = new RealSystem(10);
		
		
		/* Setting up the communication line between the classes */
		realSystem.setAccePlotter(opcom.getAccePlotter());
		realSystem.setVelocityPlotter(opcom.getVelocityPlotter());
		opcom.setRealSystem(realSystem);
		
		opcom.initOpComCartCoommunication();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		realSystem.start();
		
		

	}

}
