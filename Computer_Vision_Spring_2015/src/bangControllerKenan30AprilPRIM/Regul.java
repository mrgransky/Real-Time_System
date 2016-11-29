package bangControllerKenan30AprilPRIM;

import se.lth.control.realtime.Semaphore;


public class Regul extends Thread {

	// For the bang bang controller, limit the bang bang controller to only act bangBang
	private double temp_u;


	// Created elseWhere
	private RealSystem realSystem;
	private boolean zeroing_flag=true;
	private int counterToStart=0;

	private double cartVel;
	private double cartAcce;


	/* For the hard coding part */

	private double go_right_counter = 0;
	private final double GO_RIGHT_COUNTER=35;

	private double go_left_counter = 0;
	private final double GO_LEFT_COUNTER = 31;
	private double go_right_counter_1=0;
	private double go_right_counter_2=0;
	private double go_left_counter_1= 0;
	private double go_left_counter_2 =0;
	private double go_right_counter_3=0;
	private double go_left_counter_3=0;
	private double go_right_counter_4=0;
	private double go_left_counter_4 = 0;
	private double go_right_counter_5 = 0;
	private boolean swing_up_started = false;
	private long timer_start_BBC;
	private int timer_semafor_BBC;
	private long timer_duration_BBC;
	private final long TIMER_DURATION_BBC=3000;		// 3 seconds = 3000 ms

	// For the missing of circles in the auto mode
	private int found_in_seq_semafor;
	private int found_in_seq_semafor_2;
	private boolean SFC_flag;
	
	private double MIN_START_BBC = 420-90;
	private double MAX_START_BBC = 440-90;




	// For the error searching 
	private double temp_cart_pos;








	// Created here
	private BangControllerPRIM bangController;
	private StateFeedBackController stateFeedBackController;
	private SignalAndPlotter controlSignal;
	private SignalAndPlotter statePlotter;
	// The monitors for the angle and the angleVelocity
	private VisionMessurementMonitor visionMessurementMon;
	private DataLogger dataLoggerRegul;
	private DataLogger dataLoggerError;
	private DataLogger dataLoggerError2;



	// For the activation and deactivation
	private boolean activeAutonoums;

	// THE MIN ANGLE FOR activation of stateFeedback, +- 5 degrees
	private final double MIN_ANGLE=-30;
	private final double MAX_ANGLE=30;

	// For the different states for the controller
	private final int OFF = 0;
	private final int ON = 1;
	private final int BANG_BANG_CONTROLLER = 2;
	private final int STATE_FEEDBACK_CONTROLLER = 3;
	
	
	// For the security code boundaries used in both state feedback controller AND bang bang controller
	private final int MIN_SEC_BOUND=110;
	private final int MAX_SEC_BOUND = 500;
	

	// THE MODEMONITOR 
	private ModeMonitor modeMon;

	// The Semaphore for the shutDown with others
	private Semaphore mutex;
	private boolean weShouldRun=true;


	// ProcessParameters which are retried from the ProcessParam class
	private double angle;
	private double angleVel;
	private double posCart;
	private double nbrOfBeamCircles;

	// For the timer in the controller of bangBang
	private final long PAUSE_TIME_BBC=5000;			// 5 000 milliseconds = 5 seconds 
	private long timer_duration;
	private long timer_start_time;
	private long timer_end_time;
	private int timer_semafor=0;


	// For the auto mode 
	private double temp_angle_auto;
	private int temp_nbrOfBeamCircles;


	// INNER CLASS, BEGIN_01
	private class ModeMonitor{
		private int mode;

		public synchronized void setMode(int newMode) {
			this.mode = newMode;
		}
		public synchronized int getMode() {
			return this.mode;
		}
	}
	//INNER CLASS, END_01


	// INNER CLASS, BEGIN_02
	private class VisionMessurementMonitor{
		private double angle;
		private double angleVel;
		private double posCart;
		private double cartVel;
		private double nbrOfBeamCircles;

		public synchronized void setAngle(double angle) {
			this.angle = angle;
		}
		public synchronized double getAngle() {
			return this.angle;
		}
		public synchronized void setAngleVel(double angleVel) {
			this.angleVel = angleVel;
		}
		public synchronized double getAngleVel() {
			return this.angleVel;
		}
		public synchronized void setPosCart(double posCart) {
			this.posCart = posCart;
		}
		public synchronized double getPosCart() {
			return this.posCart;
		}

		public synchronized void setCartVel(double cartVel) {
			this.cartVel = cartVel;
		}
		public synchronized double getCartVel() {
			return this.cartVel;
		}

		// For the number of circles found on the beam
		public synchronized void setNbrOfBeamCircles(double nbrOfBeamCicles) {
			//System.out.println("nbrOfBeamCircles INNER CLASS , REGUL; " + nbrOfBeamCircles);		FOR TROUBLESHOUTING
			this.nbrOfBeamCircles = nbrOfBeamCicles ;
		}

		public synchronized double getNbrOfBeamCircles() {
			//System.out.println("nbrOfBeamCircles INNER CLASS GET METHOD, REGUL; " + this.nbrOfBeamCircles);	
			return this.nbrOfBeamCircles;
		}

	}
	//INNER CLASS, END_02




	/* Constructor */
	public Regul(int pri){
		setPriority(pri);
		this.modeMon = new ModeMonitor();
		modeMon.setMode(OFF);
		this.mutex = new Semaphore(1);
		// Creation of all the used classes
		this.bangController = new BangControllerPRIM();
		this.stateFeedBackController = new StateFeedBackController();
		this.controlSignal = new SignalAndPlotter("ControlSignal", 0, -1, 12, 40);
		this.statePlotter = new SignalAndPlotter("SFC DATA", 0,0,5,20);
		this.visionMessurementMon = new VisionMessurementMonitor();

		/* For the dataLogger to extract information on the controller */
		this.dataLoggerRegul = new DataLogger("ControlData");
		this.dataLoggerError = new DataLogger("ErrorData");
		this.dataLoggerError2 = new DataLogger("DataLoggerError2");
		// Create the inner classes for the angel and the angular Velocity

	}

	public void run(){
		long t,h,duration;
		int i=0;
		h = 5;  // [ms] 
		t = System.currentTimeMillis();
		mutex.take();




		while(weShouldRun){

			// For error searching
			this.dataLoggerError2.writeSingleSample((int)(System.currentTimeMillis()));		// writes the time

			synchronized(visionMessurementMon){
				this.temp_cart_pos = visionMessurementMon.getPosCart(); 

			}



			dataLoggerRegul.writeSingleSample((int)(System.currentTimeMillis()));		// writes the time
			dataLoggerError.writeSingleSample((int)(System.currentTimeMillis()));		// writes the time

			// Plot the angle for error searching 
			//controlSignal.setAnalogSinkSignal(100);


			/*RUNS ONLY IF THE AUTONOUMS MODE IS ACTIVATED BY A BUTTON
			 * START*/ 
			if(activeAutonoums){
				this.dataLoggerRegul.newLine();
				this.dataLoggerRegul.newLine();
				synchronized(visionMessurementMon){
					this.temp_angle_auto = visionMessurementMon.getAngle(); 
					this.temp_nbrOfBeamCircles =(int) (visionMessurementMon.getNbrOfBeamCircles()) ;
				}
				this.dataLoggerRegul.writeSingleSample(this.temp_nbrOfBeamCircles);
				// For not switching if we drop the beam red circle twise in a row we should still run SFC, START
				if(this.temp_nbrOfBeamCircles == 0){

					if((this.found_in_seq_semafor > 1 )    && (this.found_in_seq_semafor_2 > 1)     ){
						this.SFC_flag = true;
						this.found_in_seq_semafor =0;
					}else if(   (this.found_in_seq_semafor == 0) && (this.found_in_seq_semafor_2 > 1)    ){
						this.SFC_flag = true;
						this.found_in_seq_semafor_2 = 0;
					}else{
						this.SFC_flag = false;
					}
				}else if(this.nbrOfBeamCircles == 1){
					this.found_in_seq_semafor++;
					this.found_in_seq_semafor++;
					this.SFC_flag=true;
				}
				// For not switching if we drop the beam red circle twise in a row we should still run SFC, END 



				if(   (temp_angle_auto > MIN_ANGLE && temp_angle_auto < MAX_ANGLE) && SFC_flag   ){
					//System.out.println("visionProcessing messurement REGUL main loop; " + visionMessurementMon.getNbrOfBeamCircles()  );

					//	System.out.println("IF    SET_SF_MODE");
					setSFMode();
				}else{
					//		System.out.println("IF    SET_BB_MODE");
					setBBMode();
				}
			}
			/*END*/
			dataLoggerRegul.writeSingleSample((int)(visionMessurementMon.getAngle())*1000);
			dataLoggerRegul.writeSingleSample((int)(visionMessurementMon.getAngleVel()*1000));


			switch (modeMon.getMode()) {
			case OFF:{
				this.statePlotter.setAnalogSinkSignal(4);
				deActivateMode();
				//controlSignal.setAnalogSinkSignal(0);
				realSystem.setAcce(0);
				realSystem.setVel(0);

				break;
			}
			case ON:{
				this.statePlotter.setAnalogSinkSignal(3);
				activateAutonoumusMode();
				//controlSignal.setAnalogSinkSignal(10);
				realSystem.setAcce(0);
				realSystem.setVel(0);
				break;
			}
			case BANG_BANG_CONTROLLER: {



				this.statePlotter.setAnalogSinkSignal(1);

				//System.out.println("BANG BANG REGUL");


				// Gets the values START
				synchronized(visionMessurementMon){
					this.angle = visionMessurementMon.getAngle();
					this.angleVel = visionMessurementMon.getAngleVel();
					this.posCart = visionMessurementMon.getPosCart();
					this.nbrOfBeamCircles= visionMessurementMon.getNbrOfBeamCircles();
					//System.out.println("posCart; REGUL " + this.posCart);
				}
				// Gets the values END

				// If the angle is outside of the boundary OR we have not found any circles at all
				//System.out.println( "nbrOfBeamCicles; " + this.nbrOfBeamCircles);

				if(this.angle > MAX_ANGLE && this.angle < MIN_ANGLE || (this.nbrOfBeamCircles == 0) ){	// else starts up the hardcoding
					this.cartVel = realSystem.getVel();
					this.cartAcce = realSystem.getAcce();
					//this.temp_u = this.bangController.calculateOutPut(this.posCart,this.cartVel,this.cartAcce,this.angle,this.angleVel, zeroing_flag,counterToStart);

					this.dataLoggerError.writeSingleSample(1);

					/* HARDCODIING THE BANGBANG CONTROLLER START*/
					if(      ( this.MIN_START_BBC > this.posCart || this.posCart > this.MAX_START_BBC  )   &&  !swing_up_started){		/*For moving to the inital position*/
						//System.out.println("second if");
						this.dataLoggerError.writeSingleSample(2);
						if(this.MIN_START_BBC > this.posCart){
							if(this.cartVel < 0 ){
								realSystem.setAcce(0);
								realSystem.setVel(0);	
							}else{
								realSystem.setAcce(0.2);
							}
						}else if(this.posCart > this.MAX_START_BBC){

							if(this.cartVel > 0){
								realSystem.setAcce(0);
								realSystem.setVel(0);
							}else{
								realSystem.setAcce(-0.2);
							}
						}



						/* For the transition between the initial position transfer to bang bang */
					}else if(  (this.posCart > this.MIN_START_BBC && this.posCart < this.MAX_START_BBC)  && (!swing_up_started)  ){
						realSystem.setAcce(0);				// Stop the cart
						realSystem.setVel(0);
						this.dataLoggerError.writeSingleSample(3);
						this.timer_semafor++;
						//System.out.println("timer_semafor; " + timer_semafor);
						// START TIMER
						if(this.timer_semafor == 1){
							this.timer_start_time = System.currentTimeMillis();
							this.dataLoggerError.writeSingleSample(7777777);
							//this.dataLoggerError.writeSingleSample((int)       this.timer_start_time       );
						}else{
							this.timer_end_time = System.currentTimeMillis();
							//this.dataLoggerError.writeSingleSample((int)       this.timer_end_time       );
							this.timer_duration = this.timer_end_time-this.timer_start_time ;
						}
						this.dataLoggerError.writeSingleSample((int)this.timer_duration);
						this.dataLoggerError.writeSingleSample((int)       this.timer_start_time       );
						this.dataLoggerError.writeSingleSample((int)       this.timer_end_time       );

						//System.out.println("timer start value; " + this.timer_start_time );
						//System.out.println("timer duration; " + this.timer_duration);
						//if(        -2 < this.angleVel   &&   this.angleVel < 2  && this.angle > 180-5 && this.angle < 180+5  ){		// For stopping the pendulum
						if(     this.timer_duration > PAUSE_TIME_BBC  ){
							realSystem.setAcce(0);				// Stop the cart
							realSystem.setVel(0);
							swing_up_started = true;
							this.timer_duration = 0;			// resetting the duration of the timer
							this.dataLoggerError.writeSingleSample(4);
							this.timer_semafor = 0;
						}



					}else{
						/*Reached the initial position with the cart*/
						//swing_up_started = true;


						this.dataLoggerError.writeSingleSample(5);
						// Security code if the boundary is reached, START

						//System.out.println("Starting hard code!!");


						// Security code if the boundary is reached, END

						/*1*/
						if(go_right_counter < 100){
							realSystem.setAcce(-0.9);
							go_right_counter++;
							this.dataLoggerError.writeSingleSample(1);

						}else if(go_right_counter == 100){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter++;
							this.dataLoggerError.writeSingleSample(2);
						}


						/*2*/
						else if((go_left_counter < 100  )){
							realSystem.setAcce(0.9);
							go_left_counter++;
							this.dataLoggerError.writeSingleSample(3);
						}else if(go_left_counter == 100){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_left_counter++;
							this.dataLoggerError.writeSingleSample(4);
						}


						/*3*/
						else if(go_right_counter_1 < 100){
							realSystem.setAcce(-0.9);
							go_right_counter_1++;
							this.dataLoggerError.writeSingleSample(5);
						}else if(go_right_counter_1 == 100){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter_1++;
							this.dataLoggerError.writeSingleSample(6);
						}

						/*4*/
						else if(go_left_counter_1 < 100){
							realSystem.setAcce(0.9);
							go_left_counter_1++;
							this.dataLoggerError.writeSingleSample(7);
						}else if(go_left_counter_1 == 100){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_left_counter_1++;
						}

						/*5*/
						else if(go_right_counter_2 < 110){
							realSystem.setAcce(-0.9);
							go_right_counter_2++;
							this.dataLoggerError.writeSingleSample(8);
						}else if(go_right_counter_2 == 110){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter_2++;
							this.dataLoggerError.writeSingleSample(9);
						}

						/*6*/
						else if(go_left_counter_2 < 150){
							realSystem.setAcce(0.9);
							go_left_counter_2++;
							this.dataLoggerError.writeSingleSample(10);
						}else if(go_left_counter_2 == 150){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_left_counter_2++;
							this.dataLoggerError.writeSingleSample(11);
						}

						/*7*/
						else if(go_right_counter_3 < 130){
							realSystem.setAcce(-0.9);
							go_right_counter_3++;
							this.dataLoggerError.writeSingleSample(12);
						}else if(go_right_counter_3 == 130){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter_3++;
							this.dataLoggerError.writeSingleSample(13);
						}

						/*8*/
						else if(go_left_counter_3 < 150){
							realSystem.setAcce(0.9);
							go_left_counter_3++;
							this.dataLoggerError.writeSingleSample(14);
						}else if(go_left_counter_3 == 150){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_left_counter_3++;
							this.dataLoggerError.writeSingleSample(15);
						}

						/*9*/
						else if(go_right_counter_4 < 130){
							realSystem.setAcce(-2);
							go_right_counter_4++;
							this.dataLoggerError.writeSingleSample(16);
						}else if(go_right_counter_4 == 130){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter_4++;
							this.dataLoggerError.writeSingleSample(17);
						}

						/*10*/
						else if(go_left_counter_4 < 120){
							realSystem.setAcce(2);
							go_left_counter_4++;
							this.dataLoggerError.writeSingleSample(18);

						}else if(go_left_counter_4 == 120){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_left_counter_4++;
							this.dataLoggerError.writeSingleSample(19);

						}else if(go_left_counter_4 == 121){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_left_counter_4++;
							this.dataLoggerError.writeSingleSample(20);
						}



						/*11*/
						else if(go_right_counter_5 < 40){
							realSystem.setAcce(-2);
							go_right_counter_5++;
							this.dataLoggerError.writeSingleSample(21);

						}else if(go_right_counter_5 == 40){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter_5++;
							this.dataLoggerError.writeSingleSample(22);

						}

						else if(  (go_right_counter_5 == 41)   && (timer_duration_BBC <=     TIMER_DURATION_BBC)){
							this.dataLoggerError.writeSingleSample(23);
							realSystem.setAcce(0);
							realSystem.setVel(0);
							timer_semafor_BBC++;
							if(timer_semafor_BBC == 1){
								timer_start_BBC = System.currentTimeMillis();
							}
							timer_duration_BBC = System.currentTimeMillis()- timer_start_BBC;
							//System.out.println("duration; " + timer_duration_BBC);
						}

						else if(timer_duration_BBC >     TIMER_DURATION_BBC){
							this.dataLoggerError.writeSingleSample(24);
							timer_semafor_BBC = 0;

							go_right_counter =0;
							go_left_counter =0;
							go_left_counter_1=0;
							go_right_counter_1=0;
							go_right_counter_2=0;
							go_left_counter_2 =0;
							go_right_counter_3=0;
							go_left_counter_3=0;
							go_right_counter_4=0;
							go_left_counter_4=0;
							go_right_counter_5=0;
							swing_up_started=false;
							this.dataLoggerError.writeSingleSample(18);
							realSystem.setAcce(0);
							realSystem.setVel(0);
						}
					}













					/* HARDCODIING THE BANGBANG CONTROLLER END */
					//						counterToStart++;
					//						// If stopp is necessary
					//						if(this.temp_u == 10){
					//							realSystem.setAcce(0);
					//							realSystem.setVel(0);
					//							zeroing_flag = true;
					//						}else{
					//							this.temp_u = (this.temp_u*0.5);
					//							realSystem.setAcce(this.temp_u);
					//							zeroing_flag = false;
					//						}
					
					
					// Security code!!!, START
				}else if(   (this.MIN_SEC_BOUND < this.posCart) &&  (this.posCart < this.MAX_SEC_BOUND)  ){
					realSystem.setAcce(0);
					realSystem.setVel(0);
				}
				// Security code!!!, END
				
				
				dataLoggerRegul.writeSingleSample((int)(temp_u*1000));
				controlSignal.setAnalogSinkSignal(temp_u);
				dataLoggerRegul.writeSingleSample((int)(this.posCart));
				dataLoggerRegul.writeSingleSample((int)(this.cartVel*1000));
				dataLoggerRegul.writeSingleSample(0);
				//System.out.println("cartVel " + this.cartVel);
				this.dataLoggerError.newLine();
				break;
			}
			case STATE_FEEDBACK_CONTROLLER: {

				// To restart the hardcoding START
				timer_semafor_BBC = 0;
				go_right_counter =0;
				go_left_counter =0;
				go_left_counter_1=0;
				go_right_counter_1=0;
				go_right_counter_2=0;
				go_left_counter_2 =0;
				go_right_counter_3=0;
				go_left_counter_3=0;
				go_right_counter_4=0;
				go_left_counter_4=0;
				go_right_counter_5=0;
				swing_up_started=false;


				//To restart the hardcoding END






				//TO-DO

				this.dataLoggerError.newLine();
				this.dataLoggerError.writeSingleSample(77);
				this.statePlotter.setAnalogSinkSignal(2);
				//System.out.println("STATEFEEDBACK ACTIVE");
				synchronized(visionMessurementMon){
					this.angle = visionMessurementMon.getAngle();
					this.angleVel = visionMessurementMon.getAngleVel();
					this.posCart = visionMessurementMon.getPosCart();
					this.cartVel = visionMessurementMon.getCartVel();
					//System.out.println("posCart; REGUL " + this.posCart);
				}

				//OVER writing the cartVel from the process
				this.cartVel = realSystem.getVel();



				//
				// before 280 364
				if( (this.MIN_SEC_BOUND < this.posCart && this.posCart < this.MAX_SEC_BOUND) && (this.angle > this.MIN_ANGLE  && this.angle < this.MAX_ANGLE) ){		


					temp_u= this.stateFeedBackController.calculateOutPut(this.posCart, this.cartVel, this.angle,this.angleVel );
					temp_u = limit_control(temp_u);
					this.dataLoggerError2.writeSingleSample((int)   (temp_u * 1000));
					//	System.out.println("controlSignal calc; "+ temp_u);
					this.realSystem.setAcce(temp_u);
					this.dataLoggerError.writeSingleSample((int)(temp_u*1000));


					// Testing the control signal och velocity calculation
					dataLoggerRegul.writeSingleSample((int)(temp_u*1000));
					dataLoggerRegul.writeSingleSample((int)(this.posCart));
					dataLoggerRegul.writeSingleSample((int)(this.cartVel*1000));
					dataLoggerRegul.writeSingleSample(1);
					//System.out.println("cartVel " + this.cartVel);
					temp_u=0;			// resetting the control signal

				}


				else {
					
					if((this.MIN_SEC_BOUND < this.posCart && this.posCart > this.MAX_SEC_BOUND)){
						realSystem.setAcce(0);
						realSystem.setVel(0);	
					}

//					realSystem.setAcce(0);
//					realSystem.setVel(0);
					// Testing the control signal och velocity calculation
					dataLoggerRegul.writeSingleSample((int)(temp_u*1000));
					dataLoggerRegul.writeSingleSample((int)(this.posCart));
					dataLoggerRegul.writeSingleSample((int)(this.cartVel*1000));
					dataLoggerRegul.writeSingleSample(3);
					//System.out.println("cartVel " + this.cartVel);
					temp_u=0;			// resetting the control signal


				}


				//System.out.println("STATE_FEEDBACK_CONTROLLER REGUL");

				break;
			}
			default :{
				System.out.println("Illegal mode");
			}
			}
			dataLoggerRegul.newLine();
			this.dataLoggerError2.newLine();
			i++;
			// sleep
			t = t+h;
			duration = t- System.currentTimeMillis();
			if(duration > 0){
				try {
					sleep(duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		mutex.give();
	}


	/*For the communication from both outside and in this class
	 * START*/
	public synchronized void setSFMode() {
		modeMon.setMode(STATE_FEEDBACK_CONTROLLER);
	}
	public synchronized void setBBMode() {
		modeMon.setMode(BANG_BANG_CONTROLLER);
	}

	public synchronized void setOFFMode(){
		deActivateMode();
		modeMon.setMode(OFF);
	}
	public synchronized void setOnMode(){
		activateAutonoumusMode();
		modeMon.setMode(ON);
	}

	public void activateAutonoumusMode(){
		activeAutonoums = true;
	}
	public void deActivateMode(){
		activeAutonoums = false;
	}
	/*END*/

	public synchronized void shutDown(){
		weShouldRun = false;
		mutex.take();
		// Safety code goes here when the real process ONLINE!!!!!!!!!!!!!!
	}

	// For getting the angle form this class and from other classes outside this class
	public double getAngle(){
		return this.visionMessurementMon.getAngle();
	}
	// For setting the angle form this class and from other classes outside this class
	public void setAngle(double angle){
		this.visionMessurementMon.setAngle(angle);
	}


	// For getting the angleVelocity form this class and from other classes outside this class
	public double getAngleVel(){
		return this.visionMessurementMon.getAngleVel();
	}
	// For setting the angleVelocity form this class and from other classes outside this class
	public void setAngleVel(double angleVel){
		this.visionMessurementMon.setAngleVel(angleVel);
	}
	// // For setting the cartPos form this class and from other classes outside this class
	public void setPosCart(double cartPos){
		this.visionMessurementMon.setPosCart(cartPos);
	}

	public void setCartVel(double cartVel){
		this.visionMessurementMon.setCartVel(cartVel);
	}



	// For setting the nbrOfBeamCircles from the visionProcesing thread
	public void setNbrOfBeamCircles(double nbrOfBeamCircles){
		this.visionMessurementMon.setNbrOfBeamCircles(nbrOfBeamCircles);
	}





	/* For the communication to the process through the class RealSystem */

	public void setRealSystem(RealSystem realSystem){
		this.realSystem = realSystem;
	}

	/* For the limitation of the state feedback, (-7 m/s²  to 7 m/s²) */


	private double limit_control(double controlSignal){
		if(controlSignal > 6){
			controlSignal = 6;
		}else if(controlSignal < -6){
			controlSignal = -6;
		}
		return controlSignal;
	}
	
	
	
	/*  */
}