package bangControllerKenan30AprilPRIM;

public class StateFeedBackController {
	
	private double x1_feedBack;
	private double x2_feedBack;
	private double x3_feedBack;
	private double x4_feedBack;
	
	//private final double L_1 =-0.7332 ; //poles= -[1 2 3 4]
	//private final double L_1 =0;	//	poles = -[0.1 0.1 0.1 0.1]
	//private final double L_1 =-0.1222;
	private final double L_1 =-1.5275 ; //poles= -[  1.5275   2.9022  27.3782   4.7707  ]
//	private final double L_1 =-0.7332; //P
//private final double L_1 =-0.7332;			// latest used
//	private final double L_1 =-0.7332;
	//private final double L_1 =-0.0107 ; //poles= -[1 2 3 4] and normalized and multiplied by three
	//private final double L_1 =-0.0741;	//	poles = -[6 6 0.1 4]
	// 
	
	//private final double L_2 =-1.5275 ; //poles= -[1 2 3 4]
	//private final double L_2 =0.0001 ; //poles = -[0.1 0.1 0.1 0.1]
	//private final double L_2 =  -0.3666 ;
	private final double L_2 =-2.9022 ;     //poles= -[  1.5275   2.9022  27.3782   4.7707  ]
//	private final double L_2=-1.5275; //P
///	private final double L_2=-0.5;
//private final double L_2=-0.1;				// latest used
//	private final double L_2=-0.01;
	//private final double L_2 =-0.21 ; //poles= -[1 2 3 4] and normalized and multiplied by three
	//private final double L_2 =-0.7840;	//	poles = -[6 6 0.1 4]
	
	
	
	//private final double L_3 =-20.54 ; //	poles= -[1 2 3 4]
	//private final double L_3 =-9.8380 ; //  poles = [0.1 0.1 0.1 0.1]
	//private final double L_3 =    -13.7567;
	private final double L_3 =-27.3782;     //poles= -[  1.5275   2.9022  27.3782   4.7707  ]
//	private final double L_3 =-5.54 ; //P
//private final double L_3 =-4.0 ;				// latest used
//	private final double L_3 =-4.0 ;
	//private final double L_3 = -3;
//	private final double L_3 =-6.0;	//	poles = -[6 6 0.1 4]
	
	
	//private final double L_4 =-3.4582 ; //poles= -[1 2 3 4]
	//private final double L_4 =-0.12 ; //poles = -[0.1 0.1 0.1 0.1]
	//private final double L_4  = -1.9100;
	private final double L_4 =-4.7707 ; //poles= -[  1.5275   2.9022  27.3782   4.7707  ]
//	private final double L_4 =-2.0;  //P
//private final double L_4 =-2.0;				// latest used
//	private final double L_4 =-1.0;
	//private final double L_4 = -0.5050;
	//private final double L_4 =-1.0485;	//	poles = -[6 6 0.1 4]
	
	
	
	private double temp_u; 
	
	private final double OFF_SET_POS=331 ;	// [pixels]
	private final double METER_PER_PIXEL =1/508 ;
	private final double RADIANS_PER_DEGREE =Math.PI/180 ;
	
	private DataLogger dataLoggerStateFeedBack;
	
	
	/* Constructor */
	public StateFeedBackController(){
		this.dataLoggerStateFeedBack = new DataLogger("dataLogger_State_FeedBack");
	}
	
	/* pos [pixels], vel [pixels/s], angle [degrees], angleVel [angle/sec] */
	public double calculateOutPut(double pos, double vel, double angle, double angleVel){
		
		/*Preparing the position*/
		pos =pos-OFF_SET_POS;    // remove the offset   [pixels]
		pos = pos *METER_PER_PIXEL ;   // transfer to meters [m]
		
		angle = angle * RADIANS_PER_DEGREE;		// [radians]
		/*Recalculating the angle to compensate it for angles between 330 and 360*/
		
		angleVel = angleVel*RADIANS_PER_DEGREE;
		
		/*Preparing the velocity pre calculation*/ 
		vel=vel*METER_PER_PIXEL;
		
	
		
		
		x1_feedBack = L_1*pos;
		x2_feedBack = L_2*vel;
		x3_feedBack = L_3 *angle;
		x4_feedBack = L_4*angleVel;
		
		
		/*Writing to the dataLogger to find the error*/
		dataLoggerStateFeedBack.writeSingleSample((int)(System.currentTimeMillis()));
		dataLoggerStateFeedBack.writeSingleSample((int)(-x1_feedBack*1000));
		dataLoggerStateFeedBack.writeSingleSample((int)(-x2_feedBack*1000));
		dataLoggerStateFeedBack.writeSingleSample((int)(-x3_feedBack*1000));
		dataLoggerStateFeedBack.writeSingleSample((int)(-x4_feedBack*1000));
		
		temp_u = x1_feedBack +x2_feedBack+x3_feedBack+x4_feedBack;
		dataLoggerStateFeedBack.writeSingleSample((int)(-temp_u*1000));
		
		/*Make the jump in the file where the data is stored*/
		dataLoggerStateFeedBack.newLine();
		
		
//		// For the dead zone
//		if(temp_u < 0.003 && temp_u > 0 && !(temp_u == 0)){
//			temp_u = 0.003 + temp_u;
//		}else if(temp_u >-0.003 && temp_u < 0 && !(temp_u==0)){
//			temp_u = -0.003 + temp_u;
//		}
		
		return -temp_u;
	}

}
