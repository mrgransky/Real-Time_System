package bangControllerKenan30April;

import invertedPendulumControllerWithOutFilterChooise.Regul;
import se.lth.control.realtime.AnalogOut;
import se.lth.control.realtime.IOChannelException;
import se.lth.control.realtime.Semaphore;



/*THIS CLASS IS CRETAED AFTER THE SIMULINK BLOCK REALSYSTEM IN LAB 3 IN FRTN05 AT LTH*/
public class RealSystem extends Thread {

	// just to check that the acceleration and velocity is as it should be
	private DataLogger dataLogger;

	// A flag to stop the process
	private boolean stop_flag = false;


	// Classes created elseWhere, needs to be added
	private SignalAndPlotter accePlotter;
	private SignalAndPlotter velocityPlotter;

	// THE CONSTANTS FROM THE LAB3 FROM THE COURSE FRTN05
	private final double ACCE_TO_PROCESS = 1000/Math.pow(2, 15);
	private final double VELOCITY_TO_PROCESS= Math.pow(2, 5)*1000/Math.pow(2, 17);

	// The monitor 
	private AcceVelMonitor acceVelMon;

	// For the calculations in this class
	private double vel_state;
	private double temp_acce;
	private double temp_vel;
	
	// For the calculations in this class under method run
	private double temp_acce_run;
	private double temp_vel_run;
	

	//For the sleep process
	private final long H = 10 ; // [ms], sleep time
	long t,duration;

	// Created elseWhere
	private Regul regul;

	//The ports to the process
	private AnalogOut analogOutAcceleration;
	private AnalogOut analogOutVelocity;


	// The Semaphore for the shutDown with others
	private Semaphore mutex;
	private boolean weShouldRun=true;


	// INNER CLASS, BEGIN_01
	private class AcceVelMonitor{
		private double acce;
		private double vel;

		public synchronized void setAcce(double acce) {
			this.acce = limitAcce(acce);
		}
		public synchronized double getAcce() {
			return this.acce;
		}
		public synchronized void setVel(double vel) {
			this.vel = vel;
		}
		public synchronized double getVel() {
			return this.vel;
		}

	}
	//INNER CLASS, END_01



	/* Constructor */
	public RealSystem(int pri) throws IOChannelException{
		this.acceVelMon = new AcceVelMonitor();			// Creating the monitor for the acce
		setPriority(pri);							// Setting the priority
		this.mutex = new Semaphore(1);
		//this.dataLogger = new DataLogger("Acce[m/s^2]*1000,Velocity[m/s]*1000,time [ms]*1000");
		this.dataLogger = new DataLogger("RealSystemData");

		// Creating the ports to the process
		this.analogOutVelocity = new AnalogOut(30);
		this.analogOutAcceleration = new AnalogOut(31);
		this.analogOutVelocity.set(0);
		this.analogOutAcceleration.set(0);

	}

	// The internal methods for this class

	/* Limit the signal */
	private static double limitAcce(double acce){
		if(acce>7){
			System.out.println("acce over the upper bound 7 [m/s]");
			return 7;
		}else if(acce < -7){
			System.out.println("acce under the under bound -7 [m/s");
			return -7;
		}else{
			return acce;
		}
	}
	/* Update the velocity */
	private void updateVelocityState(){
		synchronized(acceVelMon){
			this.temp_acce = acceVelMon.getAcce();
			this.temp_vel = acceVelMon.getVel();
			this.temp_vel= this.temp_vel + (   ((double)this.H)  /1000  )*this.temp_acce;
			//System.out.println("vel state; " + this.vel_state);
			acceVelMon.setVel(this.temp_vel);
		}
	}

	/* Sets the velocity to zero */

	/*  */


	// Set up communication with Regul
	public void setRegul(Regul regul){
		this.regul=regul;
	}


	public void run(){
		mutex.take();
		this.t = System.currentTimeMillis();
		long time_offset = System.currentTimeMillis();
		int i = 0 ;
		while(weShouldRun){
			synchronized(acceVelMon){
				this.temp_acce_run =  acceVelMon.getAcce();
				this.temp_vel_run  = acceVelMon.getVel() ;
			}

			this.dataLogger.writeSingleSample((int)(this.temp_acce_run));
			this.dataLogger.writeSingleSample((int)(this.temp_vel_run*1000));
			this.dataLogger.writeSingleSample((int)(10*i));
			this.dataLogger.newLine();
			try {
				analogOutAcceleration.set(this.temp_acce_run*ACCE_TO_PROCESS);
			} catch (IOChannelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.accePlotter.setAnalogSinkSignal(this.temp_acce_run);
			try {
				analogOutVelocity.set(this.temp_vel_run*VELOCITY_TO_PROCESS);
				this.velocityPlotter.setAnalogSinkSignal(this.temp_vel_run);
			} catch (IOChannelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			updateVelocityState();
			// sleep
			this.t = this.t+this.H;
			//System.out.println("t    " + this.t);

			this.duration = this.t-System.currentTimeMillis();
			i++;
			if(this.duration > 0){
				try {
					sleep(this.duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				this.velocityPlotter.setAnalogSinkSignal(-1000);
			}

		}
		mutex.give();
	}




	/*Communication from outside this class */

	public void setAcce(double acce){
		this.acceVelMon.setAcce(acce);
	}

	public double getAcce(){
		return this.acceVelMon.getAcce();
	}

	public void setVel(double vel){
		this.acceVelMon.setVel(vel);
	}

	public double getVel(){
		return this.acceVelMon.getVel();
	}



	/* For the plotter communication */
	public void setAccePlotter(SignalAndPlotter accePlotter){
		this.accePlotter = accePlotter ;
	}
	public void setVelocityPlotter(SignalAndPlotter velocityPlotter){
		this.velocityPlotter = velocityPlotter ;
	}




}

