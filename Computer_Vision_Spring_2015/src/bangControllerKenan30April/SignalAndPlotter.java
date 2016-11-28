package bangControllerKenan30April;

import java.awt.FlowLayout;

import javax.swing.JFrame;

import SimEnvironment.AnalogSink;
import SimEnvironment.Plotter;

public class SignalAndPlotter {
	// Created here
	private Plotter plotter;
	private AnalogSink signal;
	


	public SignalAndPlotter(String signalName,double initValue,int minValueY, int maxValueY,long sampleTime){
		createPlotter(1,maxValueY,minValueY,sampleTime);
		this.signal = new AnalogSink(0);
		signal.setPlotter(this.plotter,0);
		JFrame frame = new JFrame(signalName);                                	//skapa ett f�nster
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(this.plotter.getPanel());             	// l�gg till plotterpanel
		frame.pack();
		frame.setVisible(true);
		
		
		
	}
	private void createPlotter(int nbrOfChannels,int ymax,int ymin,long sampleTime){
		this.plotter = new Plotter(1, sampleTime, ymax, ymin);
	}
	
	// We might not need a monitor to write values to the AnalogSink
	public void setAnalogSinkSignal(double value){
		this.signal.set(value);
	}
}
