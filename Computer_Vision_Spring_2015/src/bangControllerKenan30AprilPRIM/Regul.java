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
	private boolean swing_up_started = true;
	// Created here
	private BangControllerPRIM bangController;
	private StateFeedBackController stateFeedBackController;
	private SignalAndPlotter controlSignal;
	// The monitors for the angle and the angleVelocity
	private VisionMessurementMonitor visionMessurementMon;
	private DataLogger dataLoggerRegul;



	// For the activation and deactivation
	private boolean activeAutonoums;

	// THE MIN ANGLE FOR activation of stateFeedback, +- 5 degrees
	private final double MIN_ANGLE=5;
	private final double MAX_ANGLE=355;

	// For the different states for the controller
	private final int OFF = 0;
	private final int ON = 1;
	private final int BANG_BANG_CONTROLLER = 2;
	private final int STATE_FEEDBACK_CONTROLLER = 3;

	// THE MODEMONITOR 
	private ModeMonitor modeMon;

	// The Semaphore for the shutDown with others
	private Semaphore mutex;
	private boolean weShouldRun=true;


	// ProcessParameters which are retried from the ProcessParam class
	private double angle;
	private double angleVel;
	private double posCart;


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
		this.visionMessurementMon = new VisionMessurementMonitor();

		/* For the dataLogger to extract information on the controller */
		this.dataLoggerRegul = new DataLogger("ControlData");

		// Create the inner classes for the angel and the angular Velocity

	}

	public void run(){
		long t,h,duration;
		int i=0;
		h = 5;  // [ms] 
		t = System.currentTimeMillis();
		mutex.take();
		while(weShouldRun){
			dataLoggerRegul.writeSingleSample((int)(System.currentTimeMillis()));		// writes the time

			// Plot the angle for error searching 
			//controlSignal.setAnalogSinkSignal(100);


			/*RUNS ONLY IF THE AUTONOUMS MODE IS ACTIVATED BY A BUTTON
			 * START*/ 
			if(activeAutonoums){
				if(visionMessurementMon.getAngle() < MIN_ANGLE ||visionMessurementMon.getAngle() > MAX_ANGLE){
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
				deActivateMode();
				//controlSignal.setAnalogSinkSignal(0);
				break;
			}
			case ON:{
				activateAutonoumusMode();
				//controlSignal.setAnalogSinkSignal(10);
				break;
			}
			case BANG_BANG_CONTROLLER: {
				
				System.out.println("BANG BANG REGUL");
				synchronized(visionMessurementMon){
					this.angle = visionMessurementMon.getAngle();
					this.angleVel = visionMessurementMon.getAngleVel();
					this.posCart = visionMessurementMon.getPosCart();
					//System.out.println("posCart; REGUL " + this.posCart);
				}
				// If we add a slider that controls the value of the omega (natural frequency of the process)
				if(this.angle < MAX_ANGLE && this.angle > MIN_ANGLE){
					this.cartVel = realSystem.getVel();
					this.cartAcce = realSystem.getAcce();
					//this.temp_u = this.bangController.calculateOutPut(this.posCart,this.cartVel,this.cartAcce,this.angle,this.angleVel, zeroing_flag,counterToStart);


					/* HARDCODIING THE BANGBANG CONTROLLER START*/
					if(      ( 420 > this.posCart || this.posCart > 440  )   &&  !swing_up_started){		/*Fot moving to the inital position*/
						if(420 > this.posCart){
							realSystem.setAcce(0.5);
						}else if(this.posCart > 440){
							realSystem.setAcce(-0.5);
						}


						/* For the transition between the initial position transfer to bang bang */
					}else if(this.posCart > 420 && this.posCart < 440  && !swing_up_started){
						realSystem.setAcce(0);				// Stop the cart
						realSystem.setVel(0);
						if(        -2 < this.angleVel   &&   this.angleVel < 2  && this.angle > 180-5 && this.angle < 180+5  ){
							realSystem.setAcce(0);				// Stop the cart
							realSystem.setVel(0);
							swing_up_started = true;
						}



					}else{
						/*Reached the initial position with the cart*/
						//swing_up_started = true;

						/*1*/
						if(go_right_counter < 35){
							realSystem.setAcce(0.8);
							go_right_counter++;
						}else if(go_right_counter == 35){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter++;

							/*2*/
						}else if((go_left_counter < 31 )){
							realSystem.setAcce(-0.8);
							go_left_counter++;
						}else if(go_left_counter == 31){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_left_counter++;
						}
						/*3*/
						else if(go_right_counter_1 < 29){
							realSystem.setAcce(0.8);
							go_right_counter_1++;
						}else if(go_right_counter_1 == 29){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter_1++;
						}

						/*4*/
						else if(go_left_counter_1 < 40){
							realSystem.setAcce(-0.8);
							go_left_counter_1++;
						}else if(go_left_counter_1 == 40){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_left_counter_1++;
						}

						/*5*/
						else if(go_right_counter_2 < 21){
							realSystem.setAcce(0.8);
							go_right_counter_2++;
						}else if(go_right_counter_2 == 21){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter_2++;
						}

						/*6*/
						else if(go_left_counter_2 < 40){
							realSystem.setAcce(-0.8);
							go_left_counter_2++;
						}else if(go_left_counter_2 == 40){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_left_counter_2++;
						}

						/*7*/
						else if(go_right_counter_3 < 31){
							realSystem.setAcce(0.8);
							go_right_counter_3++;
						}else if(go_right_counter_3 == 31){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter_3++;
						}

						/*8*/
						else if(go_left_counter_3 < 47){
							realSystem.setAcce(-0.8);
							go_left_counter_3++;
						}else if(go_left_counter_3 == 47){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_left_counter_3++;
						}

						/*9*/
						else if(go_right_counter_4 < 25){
							realSystem.setAcce(2);
							go_right_counter_4++;
						}else if(go_right_counter_4 == 25){
							realSystem.setAcce(0);
							realSystem.setVel(0);
							go_right_counter_4++;
						}

						else if(go_right_counter_4 == 26){
							go_right_counter =0;
							go_left_counter =0;
							go_left_counter_1=0;
							go_right_counter_1=0;
							go_right_counter_2=0;
							go_left_counter_2 =0;
							go_right_counter_3=0;
							go_left_counter_3=0;
							go_right_counter_4=0;
							swing_up_started=false;
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
				}
				dataLoggerRegul.writeSingleSample((int)(temp_u*1000));
				controlSignal.setAnalogSinkSignal(temp_u);
				dataLoggerRegul.writeSingleSample((int)(this.posCart));
				dataLoggerRegul.writeSingleSample((int)(this.cartVel*1000));
				dataLoggerRegul.writeSingleSample(0);
				//System.out.println("cartVel " + this.cartVel);
				break;
			}
			case STATE_FEEDBACK_CONTROLLER: {
				//TO-DO

				synchronized(visionMessurementMon){
					this.angle = visionMessurementMon.getAngle();
					this.angleVel = visionMessurementMon.getAngleVel();
					this.posCart = visionMessurementMon.getPosCart();
					//System.out.println("posCart; REGUL " + this.posCart);
				}

				this.cartVel = realSystem.getVel();

				//
				if( (280 < this.posCart && this.posCart < 364) && (this.angle > -31  && this.angle < 31) ){		


					temp_u= this.stateFeedBackController.calculateOutPut(this.posCart, this.cartVel, this.angle,this.angleVel );
					temp_u = limit_control(temp_u);
					System.out.println("controlSignal calc; "+ temp_u);
				this.realSystem.setAcce(temp_u);
				}


				else {
					this.realSystem.setVel(0);
					this.realSystem.setAcce(0);
				}


				//System.out.println("STATE_FEEDBACK_CONTROLLER REGUL");
				dataLoggerRegul.writeSingleSample((int)(temp_u*1000));
				dataLoggerRegul.writeSingleSample((int)(this.posCart));
				dataLoggerRegul.writeSingleSample((int)(this.cartVel*1000));
				dataLoggerRegul.writeSingleSample(1);
				//System.out.println("cartVel " + this.cartVel);
				break;
			}
			default :{
				System.out.println("Illegal mode");
			}
			}
			dataLoggerRegul.newLine();
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

	/* For the communication to the process through the class RealSystem */

	public void setRealSystem(RealSystem realSystem){
		this.realSystem = realSystem;
	}

	/* For the limitation of the state feedback, (-7 m/s²  to 7 m/s²) */


	private double limit_control(double controlSignal){
		if(controlSignal > 4){
			controlSignal = 4;
		}else if(controlSignal < -4){
			controlSignal = -4;
		}
		return controlSignal;
	}
}