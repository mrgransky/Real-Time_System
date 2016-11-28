package optimizeParameters;


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

	// Created here FOR THE OTHER COLOR
	private Param H_min_G;
	private Param S_min_G;
	private Param V_min_G;

	private Param H_max_G;
	private Param S_max_G;
	private Param V_max_G;

	// Created here FOR THE RED COLOR
	private Param H_min_R;
	private Param S_min_R;
	private Param V_min_R;

	private Param H_max_R;
	private Param S_max_R;
	private Param V_max_R;



	public InitParam(){
		// For the min values for the other color
		this.H_min_G = new Param("H_min_G", 4, 0, 255);
		this.S_min_G = new Param("S_min_G",52,0,255);
		this.V_min_G = new Param("V_min_G",178,0,255);

		// For the max values for the other color
		this.H_max_G = new Param("H_max_G", 72, 0, 255);
		this.S_max_G = new Param("S_max_G",149,0,255);
		this.V_max_G = new Param("V_max_G",254,0,255);	

		// For the min values for the RED color
		this.H_min_R = new Param("H_min_R", 0, 0, 255);
		this.S_min_R = new Param("S_min_R",119,0,255);
		this.V_min_R = new Param("V_min_R",2,0,255);

		// For the max values for the RED color
		this.H_max_R = new Param("H_max_R", 3, 0, 255);
		this.S_max_R = new Param("S_max_R",152,0,255);
		this.V_max_R = new Param("V_max_R",255,0,255);	

	}

	// THE METHODS FOR THE COMMUNICATION FOR THE visionProcesssing FOR THE OTHER COLOR
	//min values
	public Param getParam_H_min_G(){
		return this.H_min_G;
	}
	public Param getParam_S_min_G(){
		return this.S_min_G;
	}
	public Param getParam_V_min_G(){
		return this.V_min_G;
	}
	// max values
	public Param getParam_H_max_G(){
		return this.H_max_G;
	}
	public Param getParam_S_max_G(){
		return this.S_max_G;
	}
	public Param getParam_V_max_G(){
		return this.V_max_G;
	}

	// THE METHODS FOR THE COMMUNICATION FOR THE visionProcesssing FOR THE RED COLOR
	//min values
	public Param getParam_H_min_R(){
		return this.H_min_R;
	}
	public Param getParam_S_min_R(){
		return this.S_min_R;
	}
	public Param getParam_V_min_R(){
		return this.V_min_R;
	}
	// max values
	public Param getParam_H_max_R(){
		return this.H_max_R;
	}
	public Param getParam_S_max_R(){
		return this.S_max_R;
	}
	public Param getParam_V_max_R(){
		return this.V_max_R;
	}
}