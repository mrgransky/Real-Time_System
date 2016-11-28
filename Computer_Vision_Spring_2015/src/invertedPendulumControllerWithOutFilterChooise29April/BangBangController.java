package invertedPendulumControllerWithOutFilterChooise29April;

public class BangBangController {
	
	// Declarations
	private double z;
	
	// CONSTANTS FOR THE PROCESS
	private final double FREQ = 1.25;		// [Hz]    

	// OBS!!! angle and angleVel is in radians
	public int calculateOutPut(double angle, double angleVel){
		// Transfer the degrees to radians
		angle = angle * (Math.PI/180);
		angleVel = angleVel * (Math.PI/180);
		// Transfer end
		z = ( 1/    (2* (  Math.pow(FREQ, 2) ) )) *  Math.pow(angleVel,2) + (Math.cos(angle)) - 1;  
		return (int) Math.signum(z*angleVel*Math.cos(angle));
	}
}
