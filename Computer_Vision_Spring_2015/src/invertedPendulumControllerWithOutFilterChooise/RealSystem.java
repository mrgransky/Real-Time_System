package invertedPendulumControllerWithOutFilterChooise;

import se.lth.control.realtime.AnalogOut;
import se.lth.control.realtime.IOChannelException;
import se.lth.control.realtime.Semaphore;


import invertedPendulumControllerWithOutFilterChooise.Regul;


/*THIS CLASS IS CRETAED AFTER THE SIMULINK BLOCK REALSYSTEM IN LAB 3 IN FRTN05 AT LTH*/
public class RealSystem extends Thread {

	// The monitor 
	private AcceMonitor acceMon;

	// For the calculations in this class
	private double vel_state;

	//For the sleep process
	private long h = 10 ; // [ms], sleep time
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
			this.acce = acce;
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
		this.vel_state = this.vel_state + (   ((double)this.h)  /1000  )*acceMon.getAcce();
	}
	
	/*  */


	// Set up communication with Regul
	public void setRegul(Regul regul){
		this.regul=regul;
	}


	public void run(){
		t = System.currentTimeMillis();
		mutex.take();
		while(weShouldRun){
			
			synchronized(acceMon){
			acceMon.setAcce(limitAcce(acceMon.getAcce()));		// Limiting the acce
			try {
				analogOutAcceleration.set(acceMon.getAcce());
			} catch (IOChannelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				analogOutVelocity.set(this.vel_state);
			} catch (IOChannelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
			updateVelocityState();
			// sleep
			this.t = this.t+this.h;
			this.duration = System.currentTimeMillis()-this.t;
			if(this.duration > 0){
				try {
					sleep(this.duration);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		mutex.give();
	}
	
	
	/*Communication from outside this class */
	
	public void setAcce(double acce){
		this.acceMon.setAcce(acce);
	}
}

