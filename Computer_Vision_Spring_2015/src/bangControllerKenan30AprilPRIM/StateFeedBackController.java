package bangControllerKenan30AprilPRIM;

public class StateFeedBackController {
	
	private double x1_feedBack;
	private double x2_feedBack;
	private double x3_feedBack;
	private double x4_feedBack;
	
	private final double L_1 =-0.7332 ; 
	private final double L_2 =-1.5275 ; 
	private final double L_3 =-20.54 ; 
	private final double L_4 =-3.4582 ; 
	
	private double temp_u; 
	
	private final double OFF_SET_POS=322 ;	// [pixels]
	private final double METER_PER_PIXEL =1/318.7 ;
	private final double RADIANS_PER_DEGREE =Math.PI/180 ;
	
	private DataLogger dataLoggerStateFeedBack;
	
	
	/* Constructor */
	public StateFeedBackController(){
		this.dataLoggerStateFeedBack = new DataLogger("dataLogger_State_FeedBack");
	}
	
	/* pos [pixels], vel [m/s], angle [degrees], angleVel [angle/sec] */
	public double calculateOutPut(double pos, double vel, double angle, double angleVel){
		
		/*Preparing the position*/
		pos =pos-OFF_SET_POS;    // remove the offset   [pixels]
		pos = pos *METER_PER_PIXEL ;   // transfer to meters [m]
		
		angle = angle * RADIANS_PER_DEGREE;		// [radians]
		/*Recalculating the angle to compensate it for angles between 330 and 360*/
		
		angleVel = angleVel*RADIANS_PER_DEGREE;
	
		
		
		x1_feedBack = L_1*pos;
		x2_feedBack = L_2*vel;
		x3_feedBack = L_3 *angle;
		x4_feedBack = L_4*angleVel;
		
		
		/*Writing to the dataLogger to find the error*/
		dataLoggerStateFeedBack.writeSingleSample((int)(System.currentTimeMillis()));
		dataLoggerStateFeedBack.writeSingleSample((int)(x1_feedBack*1000));
		dataLoggerStateFeedBack.writeSingleSample((int)(x2_feedBack*1000));
		dataLoggerStateFeedBack.writeSingleSample((int)(x3_feedBack*1000));
		dataLoggerStateFeedBack.writeSingleSample((int)(x4_feedBack*1000));
		
		temp_u = x1_feedBack +x2_feedBack+x3_feedBack+x4_feedBack;
		dataLoggerStateFeedBack.writeSingleSample((int)(temp_u*1000));
		
		/*Make the jump in the file where the data is stored*/
		dataLoggerStateFeedBack.newLine();
		
		
		return -temp_u;
	}

}
