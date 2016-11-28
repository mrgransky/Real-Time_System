package invertedPendulumControllerWithOutFilterChooise29April;

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


public class OpCom {

	// Created here
	// Created here
	private JFrame ONOFFFrame;
	private JFrame controlFrame;
	private JRadioButton offModeButton;
	private JRadioButton onModeButton;
	private JRadioButton BBCModeButton;
	private JRadioButton SFCModeButton;
	private JButton stopButton;

	// FOR ERROR SEARCHING_0001
	private JSlider slider;

	// END_01; Ends configuration for the slider

	private JPanel ONOFFButtonPanel;

	private JPanel manuelPanel;
	private ButtonGroup group;
	private ButtonGroup manualControlGroup;

	// Created elseWhere
	private Regul regul;

	/* Constructor */
	public OpCom(){
		//???Needed
	}


	public void initGUI(){



		// FOR ERROR SEARCHING_0001

//		//BEGIN_01; For the slider configuration
//		Dimension dimWindow = Toolkit.getDefaultToolkit().getScreenSize();
//		JFrame frame = new JFrame("Signal");
//		JPanel mainPanel = new JPanel();
//		mainPanel.setLayout(new FlowLayout());
//		this.slider = new JSlider(JSlider.VERTICAL, 0, 360, 1);
//		slider.setPaintTicks(true);
//		slider.setMajorTickSpacing(2);
//		slider.setMinorTickSpacing(1);
//		slider.setLabelTable(slider.createStandardLabels(10));
//		slider.setPaintLabels(true);
//		slider.setSize((int)(dimWindow.getWidth()/10), (int)(dimWindow.getHeight()/4));
//		slider.addChangeListener(new ChangeListener() {
//			public void stateChanged(ChangeEvent e) {
//				changeSignal();
//			}
//		});
//		mainPanel.add(slider);
//		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
//		frame.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent e) {
//				System.exit(0);
//			}
//		});
//
//		frame.pack();
//		frame.setVisible(true);
//		frame.setLocation((int)(dimWindow.getWidth()/1.7),(int)(dimWindow.getHeight()/10) );
//		//frame.setSize((int)(dimWindow.getWidth()/20), (int)(dimWindow.getHeight()/4));
//
//		// END_01; Ends configuration for the slider




		// ERROR SEARCHIN ENDS_0001

		// Create control frame.
		this.controlFrame = new JFrame("Manual controller Center");

		// Create ONOFF frame
		this.ONOFFFrame = new JFrame("ON / OFF autonoumus control");

		// Create panel for the radio buttons ONOFF .
		this.ONOFFButtonPanel = new JPanel();
		this.ONOFFButtonPanel.setLayout(new FlowLayout());
		this.ONOFFButtonPanel.setBorder(BorderFactory.createEtchedBorder());

		// Create panel for the radio buttons ONOFF .
		this.manuelPanel = new JPanel();
		this.manuelPanel.setLayout(new FlowLayout());
		this.manuelPanel.setBorder(BorderFactory.createEtchedBorder());


		// Create the buttons for the ONOFF CONTROL
		this.offModeButton = new JRadioButton("OFF");
		this.onModeButton = new JRadioButton("ON");
		this.stopButton = new JButton("STOP");

		// Create the buttons for the MANUAL CONTROL
		this.SFCModeButton = new JRadioButton("SFC");
		this.BBCModeButton = new JRadioButton("BBC");



		// Group the radio buttons of the ONOFF CONTROL.
		this.group = new ButtonGroup();
		this.group.add(offModeButton);
		this.group.add(onModeButton);

		// Group the radio buttons of the MANUAL CONTROL
		this.manualControlGroup = new ButtonGroup();
		this.manualControlGroup.add(SFCModeButton);
		this.manualControlGroup.add(BBCModeButton);

		// Button action listeners.
		offModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("OF_Mode");
				regul.setOFFMode();
			}
		});
		onModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("ON_Mode");
				regul.setOnMode();
			}
		});
		SFCModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("SFC_Mode");
				regul.setSFMode();
			}
		});
		BBCModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//System.out.println("BBC_Mode");
				regul.setBBMode();
			}
		});
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regul.shutDown();
				System.exit(0);
			}
		});

		// Add buttons to button panel.
		this.ONOFFButtonPanel.add(offModeButton, BorderLayout.WEST);
		this.ONOFFButtonPanel.add(onModeButton, BorderLayout.WEST);
		this.ONOFFButtonPanel.add(stopButton, BorderLayout.EAST);


		this.manuelPanel.add(SFCModeButton, BorderLayout.SOUTH);
		this.manuelPanel.add(BBCModeButton, BorderLayout.SOUTH);

		// WindowListener that exits the system if the main window is closed.
		controlFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				regul.shutDown();
				System.exit(0);
			}
		});

		ONOFFFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				regul.shutDown();
				System.exit(0);
			}
		});

		// Set guiPanel to be content pane of the ONOFF Frame frame.
		ONOFFFrame.getContentPane().add(ONOFFButtonPanel, BorderLayout.CENTER);

		// Set guiPanel to be content pane of the manuel Frame frame.
		controlFrame.getContentPane().add(manuelPanel, BorderLayout.CENTER);


		// Pack the components of the window.
		controlFrame.pack();
		ONOFFFrame.pack();

		// Position the main window at the screen center.
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension fd = controlFrame.getSize();
		controlFrame.setLocation((sd.width-fd.width)/2, (sd.height-fd.height)/2);

		// Position the main window at the screen center.
		sd = Toolkit.getDefaultToolkit().getScreenSize();
		fd = controlFrame.getSize();
		ONOFFFrame.setLocation((sd.width-fd.width)/2+100, (sd.height-fd.height)/2+100);
		ONOFFFrame.setLocation((int)(sd.getWidth()/1.4),(int) sd.getHeight()/10);

		// Make the window visible.
		controlFrame.setVisible(true);
		ONOFFFrame.setVisible(true);

		ONOFFFrame.setSize((int)sd.getWidth()/5, (int)sd.getHeight()/10);
		controlFrame.setSize((int)sd.getWidth()/5, (int) sd.getHeight()/10);
	}


	// Called from the main program to set the regul to open up a communication line
	public void setRegul(Regul r){
		this.regul = r;
	}
}
