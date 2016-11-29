package optimizeParameters;

import bangControllerKenan30April.SignalAndPlotter;


public class OpComSignals {


	private SignalAndPlotter nbrOfGreenCircles;
	private SignalAndPlotter nbrOfRedCircles;
	private SignalAndPlotter visionProcessingTime;
	private SignalAndPlotter smoothedAngle;
	private SignalAndPlotter smoothedAngleDer;



	public OpComSignals(){

		this.nbrOfGreenCircles = new SignalAndPlotter("nbeOfGreenCircles", 0, 0, 5, (long) 42 );
		this.nbrOfRedCircles = new SignalAndPlotter("nbrOfRedCircles" , 0, 0, 5, (long) 42);
		this.visionProcessingTime = new SignalAndPlotter("VisionProcessingTime", 0, 0, 42, (long) 42 );
		this.smoothedAngle = new SignalAndPlotter("smoothedAngle" , 0, -90, 90, (long) 42);
		this.smoothedAngleDer = new SignalAndPlotter("smoothedAngleDer" , 0, -1000, 1000, (long) 42);
	}


	public SignalAndPlotter getnbrOfGreenCircles(){
		return this.nbrOfGreenCircles;
	}

	public SignalAndPlotter getnbrOfRedCircles(){
		return this.nbrOfRedCircles;
	}
	
	public SignalAndPlotter getVisionProcessingTime(){
		return this.visionProcessingTime;
	}
	
	public SignalAndPlotter getSmoothedAngle(){
		return this.smoothedAngle;
	}
	public SignalAndPlotter getSmoothedAngleDer(){
		return this.smoothedAngleDer;
	}
}

