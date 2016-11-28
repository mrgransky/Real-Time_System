package bangControllerKenan30April;

import se.lth.control.realtime.Semaphore;


public class Regul extends Thread {

	// For the bang bang controller, limit the bang bang controller to only act bangBang
	private double temp_u;
	
	
	// Created elseWhere
	private RealSystem realSystem;
	
	private double cartVel;
	
	// Created here
	private BangController bangController;
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
		this.bangController = new BangController();
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
		h = 20;  // [ms] 
		t = System.currentTimeMillis();
		mutex.take();
		while(weShouldRun){
			dataLoggerRegul.writeSingleSample(i*20);		// writes the time
			
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
				synchronized(visionMessurementMon){
					this.angle = visionMessurementMon.getAngle();
					this.angleVel = visionMessurementMon.getAngleVel();
					this.posCart = visionMessurementMon.getPosCart();
					System.out.println("posCart; REGUL " + this.posCart);
				}
					// If we add a slider that controls the value of the omega (natural frequency of the process)
					if(this.angle < MAX_ANGLE && this.angle > MIN_ANGLE){
						this.cartVel = realSystem.getVel();
						this.temp_u = this.bangController.calculateOutPut(this.posCart,this.cartVel,this.angle,this.angleVel,this.temp_u);
						// If stopp is necessary
						if(this.temp_u == 10){
							realSystem.setAcce(0);
							realSystem.setVel(0);
						}else{
							this.temp_u = (this.temp_u);
							realSystem.setAcce(this.temp_u);
						}
					}
					dataLoggerRegul.writeSingleSample((int)(temp_u*1000));
					controlSignal.setAnalogSinkSignal(temp_u);
					dataLoggerRegul.writeSingleSample((int)(this.posCart));
					dataLoggerRegul.writeSingleSample((int)(this.cartVel*1000));
					System.out.println("cartVel " + this.cartVel);
				break;
			}
			case STATE_FEEDBACK_CONTROLLER: {
				//TO-DO
				realSystem.setAcce(0);
				realSystem.setVel(0);
				//controlSignal.setAnalogSinkSignal(this.stateFeedBackController.calculateOutPut(this.angle, this.angleVel));
				//controlSignal.setAnalogSinkSignal(70);
				//System.out.println("STATE_FEEDBACK_CONTROLLER REGUL");
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
	
}