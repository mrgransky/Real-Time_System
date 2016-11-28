package invertedPendulumControllerWithOutFilterChooise29April;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class OpComCartCoommunication {
	
	// The plotter for the velocity and the acce
	private SignalAndPlotter accePlotter;
	private SignalAndPlotter velocityPlotter;
	private RealSystem realSystem;
	
	// For the slider configuration
	private JSlider slider;
	private JFrame frame;

	
	
	public OpComCartCoommunication(){
		this.accePlotter = new SignalAndPlotter("Acce [m/s^2]", 0, -8, 8, (long) 20 ); 
		this.velocityPlotter= new SignalAndPlotter("Velocity [m/s]", 0, -7, 7, (long) 20 );
	}

	public SignalAndPlotter getAccePlotter(){
		return this.accePlotter;
	}
	public SignalAndPlotter getVelocityPlotter(){
		return this.velocityPlotter;
	}
	
	
	public void initOpComCartCoommunication(){
		//BEGIN_01; For the slider configuration
				Dimension dimWindow = Toolkit.getDefaultToolkit().getScreenSize();
				this.frame = new JFrame("Acce [m/s^2]");
				JPanel mainPanel = new JPanel();
				mainPanel.setLayout(new FlowLayout());
				this.slider = new JSlider(JSlider.VERTICAL, -8, 8, 0);
				slider.setPaintTicks(true);
				slider.setMajorTickSpacing(2);
				slider.setMinorTickSpacing(1);
				slider.setLabelTable(slider.createStandardLabels(10));
				slider.setPaintLabels(true);
				slider.setSize((int)(dimWindow.getWidth()/10), (int)(dimWindow.getHeight()/4));
				slider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						changeAcce();
					}
				});
				mainPanel.add(slider);
				frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
				frame.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});

				frame.pack();
				frame.setVisible(true);
				frame.setLocation((int)(dimWindow.getWidth()/1.7),(int)(dimWindow.getHeight()/10) );

				// END_01; Ends configuration for the slider
	}
	
	public void setRealSystem(RealSystem realSystem){
		this.realSystem = realSystem;
	}
	
	public void changeAcce(){
		//if (!slider.getValueIsAdjusting()) {
			this.realSystem.setAcce((slider.getValue()));
		//}
		
	}
}
