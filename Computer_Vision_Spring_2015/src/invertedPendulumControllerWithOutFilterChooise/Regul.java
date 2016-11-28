package invertedPendulumControllerWithOutFilterChooise;

import se.lth.control.realtime.IOChannelException;
import se.lth.control.realtime.Semaphore;


public class Regul extends Thread {

	// Created elseWhere
	private RealSystem realSystem;

	// Created here
	private BangBangController bangBangController;
	private StateFeedBackController stateFeedBackController;
	private SignalAndPlotter controlSignal;
	
	// The monitors for the angle and the angleVelocity
	private VisionMessurementMonitor visionMessurementMon;
	
	// Temp variables
	private double temp_u;
	
	

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
		
		
		
	}
	//INNER CLASS, END_02

	
	

	/* Constructor */
	public Regul(int pri) throws IOChannelException{
		setPriority(pri);
		this.modeMon = new ModeMonitor();
		modeMon.setMode(OFF);
		this.mutex = new Semaphore(1);
		// Creation of all the used classes
		this.bangBangController = new BangBangController();
		this.stateFeedBackController = new StateFeedBackController();
		this.controlSignal = new SignalAndPlotter("ControlSignal", 5, 0, 360, 40);
		this.visionMessurementMon = new VisionMessurementMonitor();
		this.realSystem = new RealSystem(10);
		
		// Create the inner classes for the angel and the angular Velocity
		
	}

	public void run(){
		long t,h,duration;
		h = 50;  // [ms] 
		t = System.currentTimeMillis();
		mutex.take();
		while(weShouldRun){
			
			// Plot the angle for error searching 
			controlSignal.setAnalogSinkSignal(100);
			
			
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

			switch (modeMon.getMode()) {
			case OFF:{
				deActivateMode();
				controlSignal.setAnalogSinkSignal(0);
				break;
			}
			case ON:{
				activateAutonoumusMode();
				controlSignal.setAnalogSinkSignal(10);
				break;
			}
			case BANG_BANG_CONTROLLER: {
				synchronized(visionMessurementMon){
					this.angle = visionMessurementMon.getAngle();
					this.angleVel = visionMessurementMon.getAngleVel();
				}
				synchronized(bangBangController){	// If we add a slider that controls the value of the omega (natural frequency of the process)
					this.temp_u = this.bangBangController.calculateOutPut(this.angle, this.angleVel);
					realSystem.setAcce(this.temp_u);		
					// Updating the signal to the processInputBuffer 
					controlSignal.setAnalogSinkSignal(this.temp_u);
				//	controlSignal.setAnalogSinkSignal(30);		// For error searching
				//	System.out.println("BANGBANG REGUL");
				}
				break;
			}
			case STATE_FEEDBACK_CONTROLLER: {
				//TO-DO
				//controlSignal.setAnalogSinkSignal(this.stateFeedBackController.calculateOutPut(this.angle, this.angleVel));
				//controlSignal.setAnalogSinkSignal(70);
				//System.out.println("STATE_FEEDBACK_CONTROLLER REGUL");
				break;
			}
			default :{
				System.out.println("Illegal mode");
			}
			}
			// sleep
			t = t+h;
			duration = System.currentTimeMillis()-t;
			if(duration > 0){
				try {
					System.out.println("SLEEP");
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
	
	// For the communication between regul and RealSystem
	public void setRealSystem(RealSystem realSystem){
		 this.realSystem = realSystem;
	}
	
}