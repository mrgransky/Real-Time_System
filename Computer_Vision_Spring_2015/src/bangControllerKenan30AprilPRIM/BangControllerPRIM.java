package bangControllerKenan30AprilPRIM;

public class BangControllerPRIM {

	// Manual calibrated measurement taken 30 April 2015 as the camera is mounted now
	private final int MIN_X_COORDINATE = 150;
	private final int MAX_X_COORDINATE = 495; 
	private final int MIDDLE_POS       = (MAX_X_COORDINATE-MIN_X_COORDINATE)/2 + MIN_X_COORDINATE;

	// When to turn looking at the angleVel
	private final int ANGLE_MIN = -5;
	private final int ANGLE_MAX = 5;
	// After how many loops to start looking at the angular velocity
	private final int COUNTER_TO_START= 100 ;

	// Because of the zeroing we must have preemptive 
	private int preemtingNum=0;
	private final int PREEMTING_WEST = 1;
	private final int PREEMTING_EAST = 2;





	public double calculateOutPut(double cartPos,double cartVel,double cartAcce,double angle,double angleVel, boolean zeroing_flag,int counterToStart)  {
		// TODO Auto-generated method stub



		/* If it goes outside the boundary, move in the opposite direction START */
		if( (cartPos >= (MAX_X_COORDINATE-50))    ){	// It is on the right side of the boundary
			if(!zeroing_flag && cartVel>0  ){			// Not allowed to turn around, resets
				return 10;
			}else{										// Allowed to turn around
				return -1;
			}
		}
		if( cartPos <= (MIN_X_COORDINATE+50)   ){	// It is on the left side of the boundary
			if(!zeroing_flag && cartVel<0){						// Not allowed to turn around, resets
				return 10;
			}else{
				return 1;							// Allowed to turn around

			}

		}
		/* If it goes outside the boundary, move in the opposite direction END  */


		//		/* If there was some turning wanted but didn't happen because of zeroing START */
		//		if(preemtingNum == PREEMTING_WEST){
		//			setPreemting(0);
		//			return -1;
		//		}else if(preemtingNum == PREEMTING_EAST){
		//			setPreemting(0);
		//			return 1;
		//		}
		//		/* If there was some turning wanted but didn't happen because of zeroing  END*/








		/* For starting up START */
		System.out.println("PASSED SECURITY CODE");



		/*For the starting up START */ 
		//if(cartVel == 0 && (counterToStart< COUNTER_TO_START ) ){		// Is deactivated after because when stopped by reset we dont want it any more	
		if(counterToStart< COUNTER_TO_START  ){
			if(cartPos >= MIDDLE_POS){
				System.out.println("MOVING LEFT");
				return -1;				// code for moving left
			}else{
				return 1;				// code for moving right
			}
		}
		/* For starting up END */


		/* Should go in the opposite direction if angular velocity is between a bound START */
		if(counterToStart >= COUNTER_TO_START){		// Needed due to having zero angular velocity in the start


			if(   (angle) >= (ANGLE_MIN + 180)  &&  (angle) < (ANGLE_MAX +180) ){		// Reached the top angular velocity when angle = 0
				if(cartVel > 0){
					return -1;
				}else if(cartVel < 0){
					return 1;
				}
			}

			/* Should go in the opposite direction if angular velocity is between a bound END */

			/* Give the same signal as previously because the angular velocity is not zero */
			if(cartVel > 0  ){		// it is moving to the right
				return 1;
			}if(cartVel<0){					// IT IS moving to the left
				return -1;
			}

			/* Cart vel = 0 and we want to keep moving with the same acce  */
			if(cartVel == 0){
				if(cartAcce >0 ){
					return 1;
				}else if(cartAcce <0 ){
					return -1;
				}


			}

		}

		/* Only for the extra  */
		if(cartVel > 0 ){
			return  1;
		}else{
			return -1;
		}



	}


	private void setPreemting(int preemtingNum){
		this.preemtingNum = preemtingNum;
	}

}
