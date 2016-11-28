package invertedPendulumControllerWithOutFilterChooise;

import se.lth.control.realtime.IOChannelException;



public class Main {
	
	// Used components in this main program
	private static Regul regul;
	private static OpCom opcom;
	private static OpComSignals opcomSignals;
	private static VisionProcessing visionProcessing;
	private static RealSystem realSystem;
	
	
	
	// TO DO communictaion between the visionProcessing and the paramProcess
	
	public static void main(String[] args) throws IOChannelException{
		regul = new Regul(9);
		opcom = new OpCom();
		realSystem = new RealSystem(10);
		// New 
		opcomSignals = new OpComSignals();
		visionProcessing = new VisionProcessing(10);
		visionProcessing.setAngle(opcomSignals.getAngle());
		visionProcessing.setVisionProcessingTime(opcomSignals.getVisionProcessingTime());
		visionProcessing.setBeamXCorrdinate(opcomSignals.getBeamXCoordinate());
		visionProcessing.setBeamYCorrdinate(opcomSignals.getBeamYCoordinate());
		visionProcessing.setNbrOfGreenCircles(opcomSignals.getnbrOfGreenCircles());
		visionProcessing.setNbrOfRedCircles(opcomSignals.getnbrOfRedCircles());
		visionProcessing.setSmoothedAngle(opcomSignals.getSmoothedAngle());
		visionProcessing.setSmoothedAngleDer(opcomSignals.getSmoothedAngleDer());
		visionProcessing.setRegul(regul);
		regul.setRealSystem( realSystem);
		// new end
		
		realSystem.start();
		opcom.setRegul(regul);
		opcom.initGUI();
		regul.start();
		//New
		visionProcessing.start();
		
		//New end
	}

}
