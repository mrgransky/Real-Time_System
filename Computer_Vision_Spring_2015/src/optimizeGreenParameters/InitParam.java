package optimizeGreenParameters;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import SimEnvironment.AnalogSink;
import SimEnvironment.Plotter;

public class InitParam {
	//MyController myController; 

	// Created here
	private Param H_min;
	private Param S_min;
	private Param V_min;

	private Param H_max;
	private Param S_max;
	private Param V_max;

	public InitParam(){
		// For the min values for the other color
		this.H_min = new Param("H_min", 51, 0, 255);
		this.S_min = new Param("S_min",45,0,255);
		this.V_min = new Param("V_min",49,0,255);

		// For the max values for the other colors
		this.H_max = new Param("H_max", 96, 0, 255);
		this.S_max = new Param("S_max",97,0,255);
		this.V_max = new Param("V_max",230,0,255);	
	}

	// THE METHODS FOR THE COMMUNICATION FOR THE visionProcesssing
	//min values
	public Param getParam_H_min(){
		return this.H_min;
	}
	public Param getParam_S_min(){
		return this.S_min;
	}
	public Param getParam_V_min(){
		return this.V_min;
	}
	// max values
	public Param getParam_H_max(){
		return this.H_max;
	}
	public Param getParam_S_max(){
		return this.S_max;
	}
	public Param getParam_V_max(){
		return this.V_max;
	}
}