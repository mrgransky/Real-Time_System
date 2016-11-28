package virtuelleComWithCartRealSystem29April;

import invertedPendulumControllerWithOutFilterChooise.Regul;
import se.lth.control.realtime.AnalogOut;
import se.lth.control.realtime.IOChannelException;
import se.lth.control.realtime.Semaphore;



/*THIS CLASS IS CRETAED AFTER THE SIMULINK BLOCK REALSYSTEM IN LAB 3 IN FRTN05 AT LTH*/
public class RealSystem extends Thread {
	
	// just to check that the acceleration and velocity is as it should be
	private DataLogger dataLogger;


	// Classes created elseWhere, needs to be added
	private SignalAndPlotter accePlotter;
	private SignalAndPlotter velocityPlotter;
	
	// THE CONSTANTS FROM THE LAB3 FROM THE COURSE FRTN05
	private final double ACCE_TO_PROCESS = 1000/Math.pow(2, 15);
	private final double VELOCITY_TO_PROCESS= Math.pow(2, 5)*1000/Math.pow(2, 17);

	// The monitor 
	private AcceMonitor acceMon;

	// For the calculations in this class
	private double vel_state;

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
	private class AcceMonitor{
		private double acce;

		public synchronized void setAcce(double acce) {
			this.acce = limitAcce(acce);
		}
		public synchronized double getAcce() {
			return this.acce;
		}
	}
	//INNER CLASS, END_01



	/* Constructor */
	public RealSystem(int pri) throws IOChannelException{
		this.acceMon = new AcceMonitor();			// Creating the monitor for the acce
		setPriority(pri);							// Setting the priority
		this.mutex = new Semaphore(1);
		//this.dataLogger = new DataLogger("Acce[m/s^2]*1000,Velocity[m/s]*1000,time [ms]*1000");
		this.dataLogger = new DataLogger("acce,velocity_times_a_thounsand,time_ms");

		// Creating the ports to the process
//		this.analogOutVelocity = new AnalogOut(30);
//		this.analogOutAcceleration = new AnalogOut(31);
//		this.analogOutVelocity.set(0);
//		this.analogOutAcceleration.set(0);

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
		this.vel_state = this.vel_state + (   ((double)this.H)  /100  )*acceMon.getAcce();
		//System.out.println("vel state; " + this.vel_state);
	}

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
			synchronized(acceMon){
				this.dataLogger.writeSingleSample((int)(acceMon.getAcce()));
				this.dataLogger.writeSingleSample((int)(this.vel_state*1000));
				this.dataLogger.writeSingleSample((int)(10*i));
				this.dataLogger.newLine();
				//analogOutAcceleration.set(acceMon.getAcce());
				this.accePlotter.setAnalogSinkSignal(acceMon.getAcce());
				//try {
					//analogOutVelocity.set(this.vel_state);
					this.velocityPlotter.setAnalogSinkSignal(this.vel_state);
//				} catch (IOChannelException e) {
//					 TODO Auto-generated catch block
//					e.printStackTrace();
//				}
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
		this.acceMon.setAcce(acce);
	}

	public double getAcce(){
		return this.acceMon.getAcce();
	}



	/* For the plotter communication */
	public void setAccePlotter(SignalAndPlotter accePlotter){
		this.accePlotter = accePlotter ;
	}
	public void setVelocityPlotter(SignalAndPlotter velocityPlotter){
		this.velocityPlotter = velocityPlotter ;
	}
	
	
}

