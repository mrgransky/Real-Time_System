package bangControllerKenan30AprilPRIM;

import se.lth.control.realtime.IOChannelException;




public class Main {
	
	// Used components in this main program
	private static Regul regul;
	private static OpCom opcom;
	private static OpComSignals opcomSignals;
	private static VisionProcessing visionProcessing;
	private static RealSystem realSystem;
	
	private static OpComCartCoommunication opcomCOM;
	private static DataLogger dataLogger;
	

	
	
	
	// TO DO communication between the visionProcessing and the paramProcess
	
	public static void main(String[] args) throws IOChannelException{
		regul = new Regul(10);
		opcom = new OpCom();
		realSystem = new RealSystem(7);
		// New 
		opcomSignals = new OpComSignals();
		visionProcessing = new VisionProcessing(8);
		// Set all the information to visionProcessing
		visionProcessing.setAngleReal(opcomSignals.getAngleReal());
		visionProcessing.setAngle(opcomSignals.getAngle());
		visionProcessing.setVisionProcessingTime(opcomSignals.getVisionProcessingTime());
		visionProcessing.setBeamXCorrdinate(opcomSignals.getBeamXCoordinate());
		visionProcessing.setBeamYCorrdinate(opcomSignals.getBeamYCoordinate());
		visionProcessing.setNbrOfGreenCircles(opcomSignals.getnbrOfGreenCircles());
		visionProcessing.setNbrOfRedCircles(opcomSignals.getnbrOfRedCircles());
		visionProcessing.setSmoothedAngle(opcomSignals.getSmoothedAngle());
		visionProcessing.setSmoothedAngleDer(opcomSignals.getSmoothedAngleDer());
		visionProcessing.setCartXCorrdinate(opcomSignals.getCartXCoordinate());
		visionProcessing.setCartVel(opcomSignals.getCartVel());
		visionProcessing.setRegul(regul);
		
		opcomCOM = new OpComCartCoommunication();
		
		/* Setting up the communication line between the classes */
		realSystem.setAccePlotter(opcomCOM.getAccePlotter());
		realSystem.setVelocityPlotter(opcomCOM.getVelocityPlotter());
		opcomCOM.setRealSystem(realSystem);
		
		
		regul.setRealSystem(realSystem);
		// new end
		
		opcom.setRegul(regul);
		opcom.initGUI();
		regul.start();
		//New
		visionProcessing.start();
		realSystem.start();
		
		//New end
	}

}
