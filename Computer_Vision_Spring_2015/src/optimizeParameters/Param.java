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

public class Param {
	private ParamMonitor paramMonitor;
	private JSlider slider;


	// INNER CLASS, THE ACTUAL BUFFER (CRITICAL RESOURCE)
	// Class definition for internal ParameterMonitor, BEGIN_01
	private class ParamMonitor {
		private double param;

		public ParamMonitor(double initValue){
			this.param = initValue;
		}

		public synchronized double getParam(){
			return param;
		}
		public synchronized void setParam(double param){
			this.param = param;

		}
	}
	// END_01
	//INNER CLASS ENDS


	public double getParam(){
		return paramMonitor.getParam();
	}
	public void setParam(double value){
		paramMonitor.setParam(value);
	}

	public Param(String sliderName,int initValue,int minValue,int maxValue){
		this.paramMonitor = new ParamMonitor(initValue);
		createSlider(sliderName,initValue, minValue,maxValue);

	}

	private void createSlider(String title, int initValue,int minValue, int maxValue){
		//BEGIN_01; For the slider configuration
		JFrame frame = new JFrame(title);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		this.slider = new JSlider(JSlider.VERTICAL, minValue, maxValue, initValue);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(2);
		slider.setMinorTickSpacing(1);
		slider.setLabelTable(slider.createStandardLabels(10));
		slider.setPaintLabels(true);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				changeParam();
			}
		});
		mainPanel.add(slider);
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//System.exit(0);
			}
		});
		frame.pack();
		frame.setVisible(true);
		// END_01; Ends configuration for the slider
	}
	
	// This method is being called when ever the slider is activated (used)
		private void changeParam() {
			if (!slider.getValueIsAdjusting()) {
				setParam(slider.getValue());
			}
		}

}
