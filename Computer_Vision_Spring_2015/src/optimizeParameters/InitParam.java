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

	// Created here for the radius of the green color
	private Param min_rad_G;
	private Param max_rad_G;


	// Created here for the radius of the red color
	private Param min_rad_R;
	private Param max_rad_R;


	// The cropping borders
	private Param border_x_start_R;
	private Param border_x_end_R;
	private Param border_y_start_R;
	private Param border_y_end_R;
	
	
	// The cropping borders
	private Param border_x_start_G;
	private Param border_x_end_G;
	private Param border_y_start_G;
	private Param border_y_end_G;
	
	




	public InitParam(){
		// For the min values for the other color
		this.H_min_G = new Param("H_min_G", 0, 0, 255);
		this.S_min_G = new Param("S_min_G",74,0,255);
		this.V_min_G = new Param("V_min_G",113,0,255);

		// For the max values for the other color
		this.H_max_G = new Param("H_max_G", 198, 0, 255);
		this.S_max_G = new Param("S_max_G",255,0,255);
		this.V_max_G = new Param("V_max_G",255,0,255);	

		// For the min values for the RED color
		this.H_min_R = new Param("H_min_R", 0, 0, 255);
		this.S_min_R = new Param("S_min_R",60,0,255);
		this.V_min_R = new Param("V_min_R",0,0,255);

		// For the max values for the RED color
		this.H_max_R = new Param("H_max_R", 210, 0, 255);
		this.S_max_R = new Param("S_max_R",255,0,255);
		this.V_max_R = new Param("V_max_R",248,0,255);	


		// For the radius of the other color 
		this.min_rad_G = new Param("Min radius other color", 10, 0, 400);
		this.max_rad_G = new Param("Max radius other color", 32, 0, 400);

		// For the radius of the red color 
		this.min_rad_R = new Param("Min radius red color", 10, 0, 400);
		this.max_rad_R = new Param("Max radius red color", 23, 0, 400);





		// For the borders of the cropping of the RED color
		this.border_x_start_R = new Param("x_start_RED", 78, 0, 639);
		this.border_x_end_R = new Param("x_end_RED", 601, 0, 639);
		this.border_y_start_R = new Param("y_start_RED", 0, 0, 479);
		this.border_y_end_R = new Param("y_end_RED", 111, 0,479 );

		// For the borders of the cropping of the OTHER color
		this.border_x_start_G = new Param("x_start_OTHER", 98, 0, 639);
		this.border_x_end_G = new Param("x_end_OTHER", 601, 0, 639);
		this.border_y_start_G = new Param("y_start_OTHER", 225, 0, 479);
		this.border_y_end_G = new Param("y_end_OTHER", 259, 0,479 );





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


	// THE METHODS FOR THE COMMUNICATION FOR THE visionProcesssing FOR THE OTHER COLOR
	// The radius
	public Param getMin_rad_G(){
		return this.min_rad_G;
	}

	public Param getMax_rad_G(){
		return this.max_rad_G;
	}

	// THE METHODS FOR THE COMMUNICATION FOR THE visionProcesssing FOR THE RED COLOR
	// The radius
	public Param getMin_rad_R(){
		return this.min_rad_R;
	}

	public Param getMax_rad_R(){
		return this.max_rad_R;
	}







	// The methods for the communication for the borders of the cropping
	// RED COLOR
	public Param getBorder_x_start_R(){
		return this.border_x_start_R;
	}

	public Param getBorder_x_end_R(){
		return this.border_x_end_R;
	}

	public Param getBorder_y_start_R(){
		return this.border_y_start_R;
	}

	public Param getBorder_y_end_R(){
		return this.border_y_end_R;
	}
	
	
	
	

	// The methods for the communication for the borders of the cropping
	//GREEN COLOR
	public Param getBorder_x_start_G(){
		return this.border_x_start_G;
	}

	public Param getBorder_x_end_G(){
		return this.border_x_end_G;
	}

	public Param getBorder_y_start_G(){
		return this.border_y_start_G;
	}

	public Param getBorder_y_end_G(){
		return this.border_y_end_G;
	}





}