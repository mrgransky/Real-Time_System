package bangControllerKenan30April;

public class BangController {
	
	// Manual calibrated measurement taken 30 April 2015 as the camera is mounted now
	private final int MIN_X_COORDINATE = 268;
	private final int MAX_X_COORDINATE = 480; 
	private final int MIDDLE_POS       = (500-218)/2 + MIN_X_COORDINATE;
	
	
	
	public double calculateOutPut(double cartPos,double cartVel,double angle,double angleVel, double oldControlSignal) {
		// TODO Auto-generated method stub
		
		/*If u are moving in the right position away from the boundary, keep doing that*/
		
		if((oldControlSignal > 0) && (cartPos <= (MIN_X_COORDINATE+50)) ){
			return 1;
		}
		if((oldControlSignal < 0) && (cartPos >= (MAX_X_COORDINATE-50)) ){
			return -1;
		}
		
		
		
		
		/* If it goes outside the boundary, move in the opposite direction */
		if( ((cartPos >= (MAX_X_COORDINATE-50)) && (cartVel == 0) )    ){	// It is on the right side of the boundary
			return -1;
		}
		
		if( (cartPos <= (MIN_X_COORDINATE+50)) && (cartVel == 0)   ){	// It is on the left side of the boundary
			System.out.println("Left BOUNDARY REACHED");
			return 1;
		}
		/* If it goes outside the boundary, move in the opposite direction  */
		
		
		
		
		/* Security check , START */
		if(   (cartPos >= (MAX_X_COORDINATE-50)  ||  cartPos <= (MIN_X_COORDINATE+50)  )){
			System.out.println("cartPos; " + cartPos);
			System.out.println("SECURITY CODE");
			return 10; 
		}
		/* Security check , END */	
		System.out.println("PASSED SECURITY CODE");
		/*For the starting up START */ 
		if(cartVel == 0){				// cart still
			if(cartPos >= MIDDLE_POS){
				System.out.println("MOVING LEFT");
				return -1;				// code for moving left
			}else{
				return 1;				// code for moving right
			}
		}
		/* For starting up END */
		
		/* BELOW WE KNOW THAT; (THE VEL IS NOT ZERO) AND (WE ARE IN THE SAFETY DISTANCE)  */
		if( angleVel> -2   && angleVel < 2 ){		// Now we know that we should switch side in the bang bang sequence
			if(cartVel > 0){
				return -1;
			}else{
				return +1;
			}
		}
			
		
		
		/* Give the same signal as previously because the angular velocity is not zero */
		if(cartVel > 0){		// it is moving to the right
			return 1;
		}else{					// IT IS moving to the left
			return -1;
		}
	}

}
