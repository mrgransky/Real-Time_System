package invertedPendulumControllerWithOutFilterChooise;

public class BangBangController {
	
	// Declarations
	private double z;
	
	// CONSTANTS FOR THE PROCESS
	private final double OMEGA = 1;		// [Hz]    NEEDS TO BE CHANGED

	public int calculateOutPut(double angle, double angleVel){
		z = ( 1/    (2* (  Math.pow(OMEGA, 2) ) )) *  Math.pow(angleVel,2) + (Math.cos(angle)) - 1;  
		return (int) Math.signum(z*angleVel*Math.cos(angle));
	}
}
