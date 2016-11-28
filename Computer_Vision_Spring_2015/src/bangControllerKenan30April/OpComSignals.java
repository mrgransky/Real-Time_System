package bangControllerKenan30April;


public class OpComSignals {

	private SignalAndPlotter angle;
	private SignalAndPlotter visionProcessingTime;
	private SignalAndPlotter beamXCorrdinate;
	private SignalAndPlotter beamYCorrdinate;
	private SignalAndPlotter nbrOfGreenCircles;
	private SignalAndPlotter nbrOfRedCircles;
	private SignalAndPlotter smoothedAngle;
	private SignalAndPlotter smoothedAngleDer;
	private SignalAndPlotter cartXCoordinate;


	public OpComSignals(){
		this.angle = new SignalAndPlotter("angle", 0, -10, 370, (long) 10 ); 
		this.visionProcessingTime = new SignalAndPlotter("VisionProcessingTime", 0, 0, 200, (long) 42 );
		this.beamXCorrdinate = new SignalAndPlotter("beamXCorrdinate", 0, -10, 650, (long) 42 );
		this.beamYCorrdinate = new SignalAndPlotter("beamYCorrdinate", 0, -10, 490, (long) 42 );
		this.nbrOfGreenCircles = new SignalAndPlotter("nbeOfGreenCircles", 0, 0, 3, (long) 42 );
		this.nbrOfRedCircles = new SignalAndPlotter("nbrOfRedCircles" , 0, 0, 3, (long) 42);
		this.smoothedAngle = new SignalAndPlotter("smoothedAngle" , 0, -10, 380, (long) 42);
		this.smoothedAngleDer = new SignalAndPlotter("smoothedAngleDer" , 0, -1000, 1000, (long) 42);
		this.cartXCoordinate = new SignalAndPlotter("cartXCoordinate" , 0, 150, 580, (long) 42);
	}

	public SignalAndPlotter getAngle(){
		return this.angle;
	}
	public SignalAndPlotter getVisionProcessingTime(){
		return this.visionProcessingTime;
	}
	public SignalAndPlotter getBeamXCoordinate(){
		return this.beamXCorrdinate;
	}
	public SignalAndPlotter getCartXCoordinate(){
		return this.cartXCoordinate;
	}
	public SignalAndPlotter getBeamYCoordinate(){
		return this.beamYCorrdinate;
	}

	public SignalAndPlotter getnbrOfGreenCircles(){
		return this.nbrOfGreenCircles;
	}

	public SignalAndPlotter getnbrOfRedCircles(){
		return this.nbrOfRedCircles;
	}

	public SignalAndPlotter getSmoothedAngle(){
		return this.smoothedAngle;
	}

	public SignalAndPlotter getSmoothedAngleDer(){
		return this.smoothedAngleDer;
	}


}

