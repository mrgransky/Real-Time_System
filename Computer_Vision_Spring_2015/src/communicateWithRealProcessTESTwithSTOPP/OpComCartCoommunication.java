package communicateWithRealProcessTESTwithSTOPP;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class OpComCartCoommunication {
	
	// The plotter for the velocity and the acce
	private SignalAndPlotter accePlotter;
	private SignalAndPlotter velocityPlotter;
	private RealSystem realSystem;
	
	// For the stopp button
	private JFrame stoppFrame;
	private JButton stoppButton; 
	private JPanel stoppPanel;
	
	// For the slider configuration
	private JSlider slider;
	private JFrame frame;

	
	
	public OpComCartCoommunication(){
		this.accePlotter = new SignalAndPlotter("Acce [m/s^2]", 0, -7, 7, (long) 20 ); 
		this.velocityPlotter= new SignalAndPlotter("Velocity [m/s]", 0, -3, 3, (long) 20 );
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
				this.slider = new JSlider(JSlider.VERTICAL, -2, 2, 0);
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
				
				//BEGIN_02; For the STOPP button configuration
				
				// The frame
				this.stoppFrame = new JFrame("Control station (velocity [m/s])");
				
				// The panel
				this.stoppPanel = new JPanel();
				this.stoppPanel.setLayout(new FlowLayout());
				this.stoppPanel.setBorder(BorderFactory.createEtchedBorder());
				
				// Create the buttons
				this.stoppButton = new JButton("STOPP");
//				this.onButton = new JRadioButton("ON");
				
//				// Group the buttons
//				this.group = new ButtonGroup();
//				this.group.add(this.stoppButton);
//				this.group.add(this.onButton);
				
				// Add buttons to panel
				this.stoppPanel.add(this.stoppButton,BorderLayout.CENTER);
			//	this.stoppPanel.add(this.onButton,BorderLayout.WEST);
				
				// Add the panel to the frame
				this.stoppFrame.add(this.stoppPanel,BorderLayout.CENTER);
				
				// Pack the components of the window
				this.stoppFrame.pack();
				this.stoppFrame.setVisible(true);
				this.stoppFrame.setLocation((int)(dimWindow.getWidth()/1.7),(int)(dimWindow.getHeight()/5) );
				
				
				// Action buttons listners
				stoppButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						setVelocityToZero();
						System.out.println("STOPP");
					}
				});
				
				
				
				
				// END_02; Ends configuration for the STOPP button (velocity)
				
				
	}
	
	public void setRealSystem(RealSystem realSystem){
		this.realSystem = realSystem;
	}
	
	public void changeAcce(){
		//if (!slider.getValueIsAdjusting()) {
			this.realSystem.setAcce((slider.getValue()));
		//}
		
	}
	public void setVelocityToZero(){
		realSystem.setVel(0);
	}
}
