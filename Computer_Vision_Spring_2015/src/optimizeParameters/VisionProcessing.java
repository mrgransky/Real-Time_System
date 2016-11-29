package optimizeParameters;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;

import bangControllerKenan30April.SignalAndPlotter;

class Panel extends JPanel {
	private static final long serialVersionUID = 1L;

	// Global variables
	private BufferedImage image;
	// In the method matToBufferedImage
	private final int COLS = 640; // PIXEL WITDH OF THE CAMERA IN USE
	private final int ROWS = 480; // PIXEL HEIGHT OF THE CAMERA IN USE
	private final int PIXEL_BLACK_AND_WHITE = 1;
	private final int PIXEL_COLOR = 3;
	private byte[] data;
	private byte[] data_1 = new byte[COLS * ROWS * PIXEL_BLACK_AND_WHITE]; // byte[]
																			// data
																			// =
																			// new
																			// byte[cols
																			// *
																			// rows
																			// *
																			// elemSize];
	private byte[] data_3 = new byte[COLS * ROWS * PIXEL_COLOR];
	private byte b;
	private BufferedImage image_GRAY = new BufferedImage(COLS, ROWS,
			BufferedImage.TYPE_BYTE_GRAY);
	private BufferedImage image_BGR = new BufferedImage(COLS, ROWS,
			BufferedImage.TYPE_3BYTE_BGR);

	// For the generall way
	private int cols;
	private int rows;
	private int elemSize;

	// Create a constructor method
	public Panel() {
		super();
	}

	public BufferedImage getimage() {
		return image;
	}

	public void setimage(BufferedImage newimage) {
		image = newimage;
	}

	public void setimagewithMatCrop(Mat newimage) {
		image = this.matToBufferedImageCrop(newimage);
	}
	
	public void setimagewithMat(Mat newimage) {
		image = this.matToBufferedImage(newimage);
	}
	
	
	
	

	 /**
	 * Converts/writes a Mat into a BufferedImage.
	 *
	 * @param matrix
	 * Mat of type CV_8UC3 or CV_8UC1
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	 */
	 public BufferedImage matToBufferedImage(Mat matrix) {
	 // For the general case
	 this.cols = matrix.cols();
	 this.rows = matrix.rows();
	 this.elemSize = (int) matrix.elemSize();
	
	 if(this.elemSize == 3){
	 this.data = this.data_3;
	 }else if(this.elemSize == 1){
	 this.data = this.data_1;
	 }else{ // For the general case
	 this.data = new byte[this.cols*this.rows*this.elemSize];
	 System.out.println("NOT OPTIMIZED, GENERAL CASE matToBufferedImage");
	 }
	
	
	 matrix.get(0, 0, this.data);
	 switch (matrix.channels()) {
	 case 1:
	 //this.type = BufferedImage.TYPE_BYTE_GRAY;
	 image_GRAY.getRaster().setDataElements(0, 0, COLS, ROWS, this.data);
	 return image_GRAY;
	 //break;
	 case 3:
	 //type = BufferedImage.TYPE_3BYTE_BGR;
	 // bgr to rgb
	
	 for (int i = 0; i < this.data.length; i = i + 3) {
	 this.b = this.data[i];
	 this.data[i] = this.data[i + 2];
	 this.data[i + 2] = this.b;
	 }
	
	 this.image_BGR.getRaster().setDataElements(0, 0, COLS, ROWS, this.data);
	 return this.image_BGR;
	 //break;
	 default:
	 return null;
	 }
	
	 }

	/**
	 * Converts/writes a Mat into a BufferedImage.
	 * 
	 * @param matrix
	 *            Mat of type CV_8UC3 or CV_8UC1
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	 */
	public BufferedImage matToBufferedImageCrop(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
		case 1:
			type = BufferedImage.TYPE_BYTE_GRAY;
			break;
		case 3:
			type = BufferedImage.TYPE_3BYTE_BGR;
			// bgr to rgb
			byte b;
			for (int i = 0; i < data.length; i = i + 3) {
				b = data[i];
				data[i] = data[i + 2];
				data[i + 2] = b;
			}
			break;
		default:
			return null;
		}
		BufferedImage image2 = new BufferedImage(cols, rows, type);
		image2.getRaster().setDataElements(0, 0, cols, rows, data);
		return image2;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// BufferedImage temp=new BufferedImage(640, 480,
		// BufferedImage.TYPE_3BYTE_BGR);
		BufferedImage temp = getimage();
		// Graphics2D g2 = (Graphics2D)g;
		if (temp != null)
			g.drawImage(temp, 10, 10, temp.getWidth(), temp.getHeight(), this);
	}
}

public class VisionProcessing extends Thread {

	private double[] thetaVec = new double[5];
	private double theta;
	private double smoothedAngle;
	private double smoothedAngleOld;
	private double smoothedAngleDer;
	private double smoothedAngleDerOld;
	private double hAngle = .035; // 50 [ms] approx. vision processing update
									// time

	// Created eleseWhere FOR OTHER COLOR
	private Param H_min_G;
	private Param S_min_G;
	private Param V_min_G;

	private Param H_max_G;
	private Param S_max_G;
	private Param V_max_G;

	// Created elsewhere FOR THE OTHER COLOR
	private Param min_rad_G;
	private Param max_rad_G;
	private int vec_rad_G[] = new int[2];

	// Created elsewhere FOR THE RED COLOR RADIUS
	private Param min_rad_R;
	private Param max_rad_R;
	private int vec_rad_R[] = new int[2];
	
	
	// Created here FOR THE RED COLOR CROPPING
	private int crop_vec_R[] = new int[4];
	
	// Created here FOR THE OTHER COLOR CROPPING
	private int crop_vec_G[] = new int[4];
	
	

	// //Created eleseWhere FOR RED COLOR
	private Param H_min_R;
	private Param S_min_R;
	private Param V_min_R;

	private Param H_max_R;
	private Param S_max_R;
	private Param V_max_R;
	
	
	
	// Created elesewhere for the cropping of the treshold for the red color
	
	private Param border_x_start_R;
	private Param border_x_end_R;
	private Param border_y_start_R;
	private Param border_y_end_R;
	
	
	
	
	// Created elesewhere for the cropping of the treshold for the other color
	private Param border_x_start_G;
	private Param border_x_end_G;
	private Param border_y_start_G;
	private Param border_y_end_G;
	
	

	// For the plotting of different signals
	private SignalAndPlotter nbrOfGreenCircles;
	private SignalAndPlotter nbrOfRedCircles;
	private SignalAndPlotter visionProcessingTime;
	private SignalAndPlotter smoothedAnglePlotter;
	private SignalAndPlotter smoothedAngleDerPlotter;

	private long visionProcessingPeriod;

	// Created here
	private double angVel;
	private double cartPos;
	private double cartVel;
	private double cartAcce;
	private int priority;
	private double uc;
	double diffX;
	double diffY;

	// Holds the values for the changing HSV min and max values OTHER COLOR
	private double[] hsv_min_vec_G = new double[4];
	private double[] hsv_max_vec_G = new double[4];
	// Holds the values for the changing HSV min and max values RED COLOR
	private double[] hsv_min_vec_R = new double[4];
	private double[] hsv_max_vec_R = new double[4];

	// ALL THE PARAMETERS THAT DON'T NEED TO BEE RECREATED ALL THE TIME
	private Size size_9_9 = new Size(9, 9);
	private Point point_150_50 = new Point(150, 50);
	private Point point_202_200 = new Point(202, 200);
	private Scalar scalar_100_10_10 = new Scalar(100, 10, 10);
	private Point point_210_210 = new Point(210, 210);
	private Point point_30_30 = new Point(30, 30);
	private Scalar scalar_100_10_10_255 = new Scalar(100, 10, 10, 255);
	private Point point_1_1 = new Point(1, 1);
	private Scalar scalar_255_255_255 = new Scalar(255, 255, 255);
	private Scalar scalar_100 = new Scalar(100);
	private Scalar scalar_255_0_255 = new Scalar(255, 0, 255);

	// For the counting of the red circles (LIMITING THE NBR)
	float[] circleElement_red;
	float[] rows_red_vec_0 = new float[0];
	float[] rows_red_vec_1 = new float[1 * 3]; // will be 0 or 3, will contain
												// the info [(x-coordinate), (y-
												// coordinate), (r (radius))]
	float[] rows_red_vec_2 = new float[2 * 3];
	int rows_red;
	int nbrOfCircles_red;
	int nbrOfCircles_red_limit; // used for the loops and for limiting the nbr
								// of loops and creation of unnecessary vectors

	// For the counting of the other color circles (LIMITING THE NBR),
	float[] circleElement_blue;
	float[] rows_blue_vec_0 = new float[0];
	float[] rows_blue_vec_1 = new float[1 * 3]; // will be 0 or 3, will contain
												// the info [(x-coordinate), (y-
												// coordinate), (r (radius))]
	float[] rows_blue_vec_2 = new float[2 * 3];
	int rows_blue;
	int nbrOfCircles_blue;
	int nbrOfCircles_blue_limit; // used for the loops and for limiting the nbr
									// of loops and creation of unnecessary
									// vectors

	public VisionProcessing(int priority) {

		this.priority = priority;

	}

	// Setting the min values for the communication beetwen the classes
	// For the min values below FOR THE OTHER COLOR
	public void setHminParam_G(Param H_min) {
		this.H_min_G = H_min;
	}

	public void setSminParam_G(Param S_min) {
		this.S_min_G = S_min;
	}

	public void setVminParam_G(Param V_min) {
		this.V_min_G = V_min;
	}

	// For the max values below FOR THE OTHER COLOR
	public void setHmaxParam_G(Param H_max) {
		this.H_max_G = H_max;
	}

	public void setSmaxParam_G(Param S_max) {
		this.S_max_G = S_max;
	}

	public void setVmaxParam_G(Param V_max) {
		this.V_max_G = V_max;
	}

	// For the min values below FOR THE RED COLOR
	public void setHminParam_R(Param H_min) {
		this.H_min_R = H_min;
	}

	public void setSminParam_R(Param S_min) {
		this.S_min_R = S_min;
	}

	public void setVminParam_R(Param V_min) {
		this.V_min_R = V_min;
	}

	// For the max values below FOR THE OTHER COLOR
	public void setHmaxParam_R(Param H_max) {
		this.H_max_R = H_max;
	}

	public void setSmaxParam_R(Param S_max) {
		this.S_max_R = S_max;
	}

	public void setVmaxParam_R(Param V_max) {
		this.V_max_R = V_max;
	}

	
	
	
	
	
	
	
	// For the min and max values for the radius of the (other color) circle
	// setting up the ref to the class Param
	public void setRadMinG_Param(Param min_rad_G) {
		this.min_rad_G = min_rad_G;
	}

	public void setRadMaxG_Param(Param max_rad_G) {
		this.max_rad_G = max_rad_G;
	}

	// For OTHER COLOR reading from the buffer in the Param class as a inner
	// class
	// For the radius
	private int getRad_Min_G() {
		return ((int) (this.min_rad_G.getParam()));
	}

	private int getRad_Max_G() {
		return ((int) (this.max_rad_G.getParam()));
	}

	// For the min and max values for the radius of the (other color) circle
	// setting up the ref to the class Param
	public void setRadMinR_Param(Param min_rad_R) {
		this.min_rad_R = min_rad_R;
	}

	public void setRadMaxR_Param(Param max_rad_R) {
		this.max_rad_R = max_rad_R;
	}

	// For OTHER COLOR reading from the buffer in the Param class as a inner
	// class
	// For the radius
	private int getRad_Min_R() {
		return ((int) (this.min_rad_R.getParam()));
	}

	private int getRad_Max_R() {
		return ((int) (this.max_rad_R.getParam()));
	}

	
	
	// Cropping of the red color
	// SETTING UP THE COMMUNICATION
	public void setBorderXStartR_Param(Param border_x_start_R) {
		this.border_x_start_R = border_x_start_R;
	}

	public void setBorderXEndR_Param(Param border_x_end_R) {
		this.border_x_end_R = border_x_end_R;
	}
	
	public void setBorderYStartR_Param(Param border_y_start_R) {
		this.border_y_start_R = border_y_start_R;
	}
	
	public void setBorderYEndR_Param(Param border_y_end_R) {
		this.border_y_end_R = border_y_end_R;
	}
	
	// GETTING THE VALUES
	private int getBorder_x_start_R() {
		return ((int) (this.border_x_start_R.getParam()));
	}
	private int getBorder_x_end_R() {
		return ((int) (this.border_x_end_R.getParam()));
	}
	private int getBorder_y_start_R() {
		return ((int) (this.border_y_start_R.getParam()));
	}
	private int getBorder_y_end_R() {
		return ((int) (this.border_y_end_R.getParam()));
	}
	
	
	
	
	
	// Cropping of the other color
	// SETTING UP THE COMMUNICATION
	public void setBorderXStartG_Param(Param border_x_start_G) {
		this.border_x_start_G = border_x_start_G;
	}

	public void setBorderXEndG_Param(Param border_x_end_G) {
		this.border_x_end_G = border_x_end_G;
	}
	
	public void setBorderYStartG_Param(Param border_y_start_G) {
		this.border_y_start_G = border_y_start_G;
	}
	
	public void setBorderYEndG_Param(Param border_y_end_G) {
		this.border_y_end_G = border_y_end_G;
	}
	
	// GETTING THE VALUES
	private int getBorder_x_start_G() {
		return ((int) (this.border_x_start_G.getParam()));
	}
	private int getBorder_x_end_G() {
		return ((int) (this.border_x_end_G.getParam()));
	}
	private int getBorder_y_start_G() {
		return ((int) (this.border_y_start_G.getParam()));
	}
	private int getBorder_y_end_G() {
		return ((int) (this.border_y_end_G.getParam()));
	}
	
	
	
	
	
	
	
	
	// For OTHER COLOR reading from the buffer in the Param class as a inner
	// class
	// For the min values
	private double getHmin_G() {
		return this.H_min_G.getParam();
	}

	private double getSmin_G() {
		return this.S_min_G.getParam();
	}

	private double getVmin_G() {
		return this.V_min_G.getParam();
	}

	// For the max values
	private double getHmax_G() {
		return this.H_max_G.getParam();
	}

	private double getSmax_G() {
		return this.S_max_G.getParam();
	}

	private double getVmax_G() {
		return this.V_max_G.getParam();
	}

	// For RED COLOR reading from the buffer in the Param class as a inner class
	// For the min values
	private double getHmin_R() {
		return this.H_min_R.getParam();
	}

	private double getSmin_R() {
		return this.S_min_R.getParam();
	}

	private double getVmin_R() {
		return this.V_min_R.getParam();
	}

	// For the max values
	private double getHmax_R() {
		return this.H_max_R.getParam();
	}

	private double getSmax_R() {
		return this.S_max_R.getParam();
	}

	private double getVmax_R() {
		return this.V_max_R.getParam();
	}

	public void setNbrOfGreenCircles(SignalAndPlotter nbrOfGreenCircles) {
		this.nbrOfGreenCircles = nbrOfGreenCircles;
	}

	public void setNbrOfRedCircles(SignalAndPlotter nbrOfRedCircles) {
		this.nbrOfRedCircles = nbrOfRedCircles;
	}

	public void setVisionProcessingTime(SignalAndPlotter time) {
		this.visionProcessingTime = time;
	}

	public void setSmoothedAngle(SignalAndPlotter smoothedAngle) {
		this.smoothedAnglePlotter = smoothedAngle;
	}

	public void setSmoothedAngleDer(SignalAndPlotter smoothedAngleDer) {
		this.smoothedAngleDerPlotter = smoothedAngleDer;
	}

	public void run(){  
		setPriority(priority);
		// Load the native library.  
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);   
		//System.loadLibrary("opencv-java2410.jar");   
		// It is better to group all frames together so cut and paste to  
		// create more frames is easier  
		JFrame frame1 = new JFrame("Camera");  
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame1.setSize(640,480);  
		frame1.setBounds(0, 0, frame1.getWidth(), frame1.getHeight());  
		Panel panel1 = new Panel();  
		frame1.setContentPane(panel1);  
		frame1.setVisible(true);  
		//		JFrame frame2 = new JFrame("HSV");  
		//		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		//		frame2.setSize(640,480);  
		//		frame2.setBounds(300,100, frame2.getWidth()+300, 100+frame2.getHeight());  
		//		Panel panel2 = new Panel();  
		//		frame2.setContentPane(panel2);  
		//		frame2.setVisible(true);  
		//		JFrame frame3 = new JFrame("S,V Distance");  
		//		frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		//		frame3.setSize(640,480);  
		//		frame3.setBounds(600,200, frame3.getWidth()+600, 200+frame3.getHeight());  
		//		Panel panel3 = new Panel();  
		//		frame3.setContentPane(panel3);  
		//		frame3.setVisible(true);  
		JFrame frame4 = new JFrame("Threshold for the red color");  
		frame4.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame4.setSize(640,480);  
		frame4.setBounds(900,300, 640+900, 300+480);  
		Panel panel4 = new Panel();  
		frame4.setContentPane(panel4);      
		frame4.setVisible(true);   
		JFrame frame5 = new JFrame("Threshold for the other color");  
		frame5.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame5.setSize(640,480);  
		frame5.setBounds(900,300, 640+900, 300+480);  
		//  
		Panel panel5 = new Panel();  
		frame5.setContentPane(panel5);      
		frame5.setVisible(true); 
		
		
		
		
		
	   
		// For the red tresholded in real time
		JFrame frame6 = new JFrame("Cropping for the red color");  
		frame6.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame6.setSize(640,480);  
		frame6.setBounds(900,300, frame1.getWidth(), frame1.getHeight());  
		//  
		Panel panel6 = new Panel();  
		frame6.setContentPane(panel6);      
		frame6.setVisible(true); 
		
		

		// For the red tresholded in real time
		JFrame frame7 = new JFrame("Cropping for the other color");  
		frame7.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame7.setSize(640,480);  
		frame7.setBounds(900,300, frame1.getWidth(), frame1.getHeight());  
		//  
		Panel panel7 = new Panel();  
		frame7.setContentPane(panel7);      
		frame7.setVisible(true); 
		
		
		
		
		
		
		

		//-- 2. Read the video stream  
		VideoCapture capture =new VideoCapture(0);  
		Mat webcam_image_original=new Mat();  
		Mat webcam_image=new Mat();  

		Mat hsv_image=new Mat();  
		
		Mat hsv_image_RED = new Mat();
		Mat hsv_image_GREEN = new Mat();


		Mat thresholded_tot_red = new Mat();
		Mat thresholded_tot_blue = new Mat();



		Mat thresholded_maxima_blue = new Mat();
		Mat thresholded_maxima_red = new Mat();
		Mat thresholded_color_maxima = new Mat();
		Mat circles_blue = new Mat();
		Mat circles_red = new Mat();





		capture.read(webcam_image_original);  
		//webcam_image =webcam_image_orginal.submat(0, 479, 0, 639) ;
		webcam_image = webcam_image_original;






		frame1.setSize(webcam_image_original.width()+40,webcam_image_original.height()+60);  
		//		frame2.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		//		frame3.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		frame4.setSize(webcam_image_original.width()+40,webcam_image_original.height()+60); 
		frame5.setSize(webcam_image_original.width()+40,webcam_image_original.height()+60); 

		Mat array255=new Mat(webcam_image.height(),webcam_image.width(),CvType.CV_8UC1);  
		array255.setTo(new Scalar(255));  

		Mat distance=new Mat(webcam_image.height(),webcam_image.width(),CvType.CV_8UC1);  

		List<Mat> lhsv = new ArrayList<Mat>(3);       
		// The function later will do it... (to a 1*N*CV_32FC3)  




		// For the red color filtering, the red occurs from both sides of the color spectrum
		Scalar hsv_min_red = new Scalar(0, 119, 2, 0);  
		Scalar hsv_max_red = new Scalar(8, 152, 255, 0);  





		// For the Green color filtering, the Green occurs only once in the spectrum
		Scalar hsv_min_blue = new Scalar(7, 52, 178, 0); 
		Scalar hsv_max_blue = new Scalar(33, 149, 254, 0); 

		//		Scalar hsv_min_blue = new Scalar(60, 255, 109, 0); 
		//		Scalar hsv_max_blue = new Scalar(75, 255, 255, 0); 

		// Our variable for get the angle

		//Unnessecary variablescreated in the while loop
		Scalar min_distance =  new Scalar(10.0);
		Scalar max_distance = new Scalar(255.0);

		// Scalar objects for the thresholding of the circles OTHER COLOR
		Scalar min_rad_Color_G = new Scalar(246,150,0);
		Scalar max_rad_Color_G = new Scalar(246,0,0);

		Point min_rad_center_G = new Point(webcam_image_original.width()/8,(webcam_image_original.height()/4));
		Point max_rad_center_G = new Point(webcam_image_original.width()/8,(webcam_image_original.height()/4)*2 );

		// Scalar objects for the thresholding of the circles RED COLOR 
		Scalar min_rad_Color_R = new Scalar(246,50,0);
		Scalar max_rad_Color_R = new Scalar(210,0,0);

		Point min_rad_center_R = new Point(webcam_image_original.width()/8*6,(webcam_image_original.height()/4));
		Point max_rad_center_R = new Point(webcam_image_original.width()/8*6,(webcam_image_original.height()/4)*2 );


		// Pre -definitions for the red color
		Mat cropped_RED_image = new Mat(480, 640, CvType.CV_8UC3,new Scalar(0,0,0) ); 			// Must define the type of pixel coding
		Mat cropped_RED_image_HSV = new Mat(480,640,CvType.CV_8UC3,new Scalar(0,0,0));	

		// Pre definition of the other color
		Mat cropped_OTHER_image = new Mat(480, 640, CvType.CV_8UC3,new Scalar(0,0,0) ); // http://www.tutorialspoint.com/java_dip/color_space_conversion.htm
		Mat cropped_OTHER_image_HSV = new Mat(480,640,CvType.CV_8UC3,new Scalar(0,0,0));		
		
		Mat webcam_image_copy = new Mat();
		
		// Color for the border and the points
		int left_x_border= 100;
		int right_x_border = 600;
		
		
		Point border_left_start= new Point(left_x_border,webcam_image_original.height()/6  );
		Point border_left_end = new Point(left_x_border, webcam_image_original.height()   );
		
		Point border_right_start= new Point(right_x_border,webcam_image_original.height()/6  );
		Point border_right_end= new Point(right_x_border,webcam_image_original.height()  );
		
		Scalar color_border = new Scalar(0,120,100);
		
		Mat hsv_image_distance = new Mat();
		
		double red_circle_vec[] = new double[3];
		
		
		
		
		// For the coordinate with the offset taken into account 
		// for the green/blue color
		int x_start_G_with_offset;
		int y_start_G_with_offset;
		// for the red color
		int x_start_R_with_offset;
		int y_start_R_with_offset;
		
		


		double[] data = new double[3];  
		if( capture.isOpened())  
		{  

			// THE LOOP OF IMG PROC
			while( true )  
			{  
				capture.read(webcam_image_original);  
				if( !webcam_image_original.empty() )  
				{  


					webcam_image = webcam_image_original;
					
					
					


					// Cropping for the red color
					crop_vec_R[0] =getBorder_x_start_R() ;
					crop_vec_R[1] =getBorder_x_end_R() ;
					crop_vec_R[2] = getBorder_y_start_R();
					crop_vec_R[3] = getBorder_y_end_R();
					
					
					
					cropped_RED_image = webcam_image_original.submat(  crop_vec_R[2]  ,crop_vec_R[3]   ,  crop_vec_R[0] ,   crop_vec_R[1]);
					System.out.println("RED; " + " x_start; "+ crop_vec_R[0]    + " x_end; " + crop_vec_R[1] +" y_start; " +  crop_vec_R[2] +" y_end; " + crop_vec_R[3]  );
					//Highgui.imwrite("Upper_part.png",cropped_RED_image);
					
					
					// Cropping for the other color
					// Cropping for the red color
					crop_vec_G[0] =getBorder_x_start_G() ;
					crop_vec_G[1] =getBorder_x_end_G() ;
					crop_vec_G[2] = getBorder_y_start_G();
					crop_vec_G[3] = getBorder_y_end_G();
					
					
					
					
					cropped_OTHER_image = webcam_image_original.submat(crop_vec_G[2],crop_vec_G[3],  crop_vec_G[0] ,  crop_vec_G[1]);
					System.out.println("GREEN; " + " x_start; "+ crop_vec_G[0]    + " x_end; " + crop_vec_G[1] +" y_start; " +  crop_vec_G[2] +" y_end; " + crop_vec_G[3]  );
					
					
					//Highgui.imwrite("lower_part.png",cropped_OTHER_image);


					// Convert the whole image for the hsv distance calculation
					Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV); 
					

					// Converting the Cropped images (red & Other) to HSV...START
					
					// red cropped image TO HSV
					
					
					
					Imgproc.cvtColor(cropped_RED_image, cropped_RED_image_HSV, Imgproc.COLOR_BGR2HSV);  // to HSV
					
					
					
					//Highgui.imwrite("OTHER_HSV.jpg",cropped_OTHER_image_HSV_NOT);	
					
					// try to segment in RGB space
					
					
					
					

					// other cropped image TO HSV
					Imgproc.cvtColor(cropped_OTHER_image, cropped_OTHER_image_HSV, Imgproc.COLOR_BGR2HSV);  // to HSV
					//Highgui.imwrite("OTHER_HSV.jpg",cropped_OTHER_image_HSV);								// save in the directory

					// Converting the Cropped images (red & Other) to HSV...END



					// Threshold out the red color once
					//Updating the HSV min AND max from the JSlider  RED COLOR
					// For the hsv min 
					hsv_min_vec_R[0] = getHmin_R();
					hsv_min_vec_R[1] = getSmin_R() ;
					hsv_min_vec_R[2] = getVmin_R();
					hsv_min_vec_R[3] =0 ;	// Not used but the only way to change a Scalar is using a vector with four elements
					hsv_min_red.set(hsv_min_vec_R);
					System.out.println("hsv_min_R;" + hsv_min_red.toString());
					// For the hsv max
					hsv_max_vec_R[0]= getHmax_R();
					hsv_max_vec_R[1] =getSmax_R();
					hsv_max_vec_R[2] =getVmax_R();
					hsv_max_vec_R[3] =0;  // Not used but the only way to change a Scalar is using a vector with four elements
					hsv_max_red.set(hsv_max_vec_R);
					System.out.println("hsv_max_R; " +hsv_max_red.toString());
					
					// try not to go into HSV space
					Core.inRange(cropped_RED_image_HSV, hsv_min_red, hsv_max_red, cropped_RED_image_HSV);        //    


					// Threshold out the blue color once

					//Updating the HSV min ANS max from the JSlider  OTHER COLOR
					// For the hsv min 
					hsv_min_vec_G[0] = getHmin_G();
					hsv_min_vec_G[1] = getSmin_G() ;
					hsv_min_vec_G[2] = getVmin_G();
					hsv_min_vec_G[3] =0 ;	// Not used but the only way to change a Scalar is using a vector with four elements
					hsv_min_blue.set(hsv_min_vec_G);
					System.out.println("hsv_min_G;" + hsv_min_blue.toString());
					// For the hsv max
					hsv_max_vec_G[0]= getHmax_G();
					hsv_max_vec_G[1] =getSmax_G();
					hsv_max_vec_G[2] =getVmax_G();
					hsv_max_vec_G[3] =0;  // Not used but the only way to change a Scalar is using a vector with four elements
					hsv_max_blue.set(hsv_max_vec_G);
					System.out.println("hsv_max_G; " +hsv_max_blue.toString());
					Core.inRange(cropped_OTHER_image_HSV, hsv_min_blue, hsv_max_blue, cropped_OTHER_image_HSV);



					// Notice that the thresholds don't really work as a "distance"  
					// Ideally we would like to cut the image by hue and then pick just  
					// the area where S combined V are largest.  
					// Strictly speaking, this would be something like sqrt((255-S)^2+(255-V)^2)>Range  
					// But if we want to be "faster" we can do just (255-S)+(255-V)>Range  
					// Or otherwise 510-S-V>Range  
					// Anyhow, we do the following... Will see how fast it goes...  
					Core.split(hsv_image, lhsv); // We get 3 2D one channel Mats  
					Mat S = lhsv.get(1);  
					Mat V = lhsv.get(2);  
					//       
					//       // Finding out the diementions of the lhsv
					//       

					//System.out.println("Rows; " + cols_s + " Cols; " + rows_s);


					Core.subtract(array255, S, S);  
					Core.subtract(array255, V, V);  
					S.convertTo(S, CvType.CV_32F);  
					V.convertTo(V, CvType.CV_32F);  
					Core.magnitude(S, V, distance);  
					// Find the most intense colors from the range 10 to 250
					
//					// For the red color
//					Core.inRange(distance,min_distance, max_distance, hsv_image_distance); 
//					// Threshold the most intense colors with the red color
//
//					// Threshold the most intense colors with the red color
//					Core.bitwise_and(hsv_image_distance, cropped_RED_image_HSV,cropped_RED_image_HSV);
//
//					// Threshold the most intense colors with the blue color
//					Core.bitwise_and(hsv_image_distance, cropped_OTHER_image_HSV,cropped_OTHER_image_HSV);


					// The vectors for the max and minimum of radius
					vec_rad_G[0] = getRad_Min_G();
					vec_rad_G[1] = getRad_Max_G();
					System.out.println("min rad G " + vec_rad_G[0]);
					System.out.println("max rad G " + vec_rad_G[1]);

					// Printing out a circle with the min and max other color treshold
					Core.circle(webcam_image,min_rad_center_G , vec_rad_G[0], min_rad_Color_G);
					Core.circle(webcam_image, max_rad_center_G, vec_rad_G[1], max_rad_Color_G);

					// The vectors for the max and minimum of radius
					vec_rad_R[0] = getRad_Min_R();
					vec_rad_R[1] = getRad_Max_R();
					System.out.println("min rad R " + vec_rad_R[0]);
					System.out.println("max rad R " + vec_rad_R[1]);

					// Printing out a circle with the min and max red color treshold
					Core.circle(webcam_image,min_rad_center_R , vec_rad_R[0], min_rad_Color_R);
					Core.circle(webcam_image, max_rad_center_R, vec_rad_R[1], max_rad_Color_R);
					System.out.println("Red color rad; " + vec_rad_R[0]);
					System.out.println("Red color rad; " + vec_rad_R[1]);



					// For the red circle, pixel radius is 17 < < 23
					Imgproc.GaussianBlur(cropped_RED_image_HSV, cropped_RED_image_HSV, size_9_9,2,2);  
					Imgproc.HoughCircles(cropped_RED_image_HSV, circles_red, Imgproc.CV_HOUGH_GRADIENT, 2, cropped_RED_image_HSV.height()/2, 20, 50, vec_rad_R[0], vec_rad_R[1]); 
					//minimum and maximum detectable radios (MinRadio and MaxRadio)
					//Imgproc.HoughCircles(thresholded_maxima_red, circles_red,Imgproc.CV_HOUGH_GRADIENT, 2,webcam_image.height() / 20, 500, 50, webcam_image.height()/20, webcam_image.height()/6);

					// For the blue circles
					Imgproc.GaussianBlur(cropped_OTHER_image_HSV, cropped_OTHER_image_HSV, size_9_9,2,2); 
					Imgproc.HoughCircles(cropped_OTHER_image_HSV, circles_blue, Imgproc.CV_HOUGH_GRADIENT, 2, cropped_OTHER_image_HSV.height()/2, 20, 50, vec_rad_G[0], vec_rad_G[1]);
					//minimum and maximum detectable radios (MinRadio and MaxRadio) 
					//Imgproc.HoughCircles(thresholded_maxima_blue, circles_blue,Imgproc.CV_HOUGH_GRADIENT, 2,webcam_image.height() / 20, 500, 50, webcam_image.height()/20, webcam_image.height()/6);


					//-- 4. Add some info to the image  
					//					Core.line(webcam_image, point_150_50, point_202_200,scalar_100_10_10 /*CV_BGR(100,10,10)*/, 3);  
//					Core.circle(webcam_image,point_210_210 , 10, scalar_100_10_10,3);  
					//					data=webcam_image.get(210, 210);  
//					Core.putText(webcam_image,String.format("("+String.valueOf(data[0])+","+String.valueOf(data[1])+","+String.valueOf(data[2])+")"),point_30_30 , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
//												,1.0,scalar_100_10_10_255,3); 
					
					
					// Putting the borders of the state feed back
					Core.line(webcam_image, border_left_start, border_left_end, color_border, 3);
					Core.line(webcam_image, border_right_start, border_right_end, color_border, 3);
					
					
					
					// For the red color  
					rows_red = circles_red.rows(); 
					//System.out.println("rows_red; " + rows_red);
					nbrOfCircles_red = circles_red.cols();
					//System.out.println("nbr of red circles; " + nbrOfCircles_red);

					// Limit the size of the circleElement_red to two, STARTS
					if(nbrOfCircles_red >= 2){
						circleElement_red = rows_red_vec_2;
						nbrOfCircles_red_limit = 2;
					}else if(nbrOfCircles_red == 1){
						circleElement_red = rows_red_vec_1;		// empty vector with tree places
						nbrOfCircles_red_limit = 1;
						
						
						
						
					}else{
						circleElement_red = rows_red_vec_0;
						nbrOfCircles_red_limit =0;
					}
					// Limit the size of the circleelement_red to two, ENDS


					//float[] circleElement_red = new float[rows_red*3]; // will be 0 or 3, will contain the info [(x-coordinate), (y- coordinate), (r (radius))]
					//System.out.println("Found red circles:  " + nbrOfCircles_red);


					// For the blue color
					rows_blue = circles_blue.rows();  
					nbrOfCircles_blue = circles_blue.cols();
					System.out.println("nbr of blue circles; " + nbrOfCircles_blue);

					// Limit the size of the circleElement_red to two, STARTS
					if(nbrOfCircles_blue >= 2){
						circleElement_blue = rows_blue_vec_2;
						nbrOfCircles_blue_limit = 2;
					}else if(nbrOfCircles_blue == 1){
						//System.out.println("BLUE CIRCLES;  1" );
						circleElement_blue = rows_blue_vec_1;		// empty vector with tree places
						nbrOfCircles_blue_limit = 1;
					}else{
						circleElement_blue = rows_blue_vec_0;
						nbrOfCircles_blue_limit =0;
					}
					// Limit the size of the circleelement_red to two, ENDS




					//float[] circleElement_blue = new float[rows_blue*3]; // will be 0 or 3, will contain the info [(x-coordinate), (y- coordinate), (r (radius))]
					//System.out.println("Found blue circles: " + nbrOfCircles_blue);

					nbrOfGreenCircles.setAnalogSinkSignal(nbrOfCircles_blue);
					nbrOfRedCircles.setAnalogSinkSignal(nbrOfCircles_red);

					Point center =point_1_1; 
					//System.out.println("nbr of red circles; " + nbrOfCircles_red );
					// Drawing out all the red circles
					if (circleElement_red.length>0){  // circleElement will always be 3 or 0, 0 if no circle are found,3 if any circles are found

						for (int j = 0; j < nbrOfCircles_red_limit; j++) {

							circles_red.get(0, j, circleElement_red); // retrives the element from the circles which is a row vector(each element is a vector [x y r] for each circle)
							//System.out.println("LOOP");

							int i = 0;

							center.x=circleElement_red[i] + crop_vec_R[0]  ;
							center.y= circleElement_red[i +1] + crop_vec_R[2];
//							System.out.println("center red"+ center.x );
//							System.out.println("offset; " + crop_vec_R[0] );
							
							
							// Draws the circle in the image
							Core.circle(webcam_image, center,(int) circleElement_red[2] , scalar_255_255_255 );
							//System.out.println("PRINTS RED CIRCLE");


						}  
					} 



					if (circleElement_blue.length>0){  // circleElement will always be 3 or 0, 0 if no circle are found,3 if any circles are found

						for (int j = 0; j < nbrOfCircles_blue_limit; j++) {

							circles_blue.get(0, j, circleElement_blue); // it retrieves the element from the circles which is a row vector(each element is a vector [x y r] for each circle)
							//System.out.println("LOOP");

							int i = 0;
							//Point center = new Point(circleElement_blue[i], circleElement_blue[i + 1]);
							center.x=circleElement_blue[i] + crop_vec_G[0];
							center.y=circleElement_blue[i + 1]+ crop_vec_G[2];
							// Draws the circle in the image
							Core.circle(webcam_image, center,(int) circleElement_blue[2] , scalar_255_0_255 );
						}  
					} 
					// calculating the angle:




					if (nbrOfCircles_red == 1 && nbrOfCircles_blue == 1){
						circles_red.get(0, 0, circleElement_red);
						// with the cropping active there is a need for a offset adding from x start and y start for the red and blue circle cropping
						x_start_G_with_offset = Math.round(circleElement_blue[0])+crop_vec_G[0];
						y_start_G_with_offset =  Math.round(circleElement_blue[1])+ crop_vec_G[2];
						
						x_start_R_with_offset = Math.round(circleElement_red[0])+crop_vec_R[0];
						y_start_R_with_offset = Math.round(circleElement_red[1])+crop_vec_R[2];
						
						
						
						Point originCenter = new Point(    x_start_G_with_offset ,  y_start_G_with_offset   );		// For the green/blue color
						Point redCenter = new Point(   x_start_R_with_offset   ,    y_start_R_with_offset  );			// For the red color
						if(redCenter.x >= originCenter.x){
							diffX =( redCenter.x - originCenter.x);
							if(redCenter.y <= originCenter.y){
								//case 1
								diffY = (originCenter.y - redCenter.y);
								if (diffX == 0){
									theta = 0;
								}else {
									theta = Math.toDegrees(Math.atan(diffX/diffY));
								}
							} else{
								// case 2
								diffY = (redCenter.y - originCenter.y);
								if (diffX == 0) {
									theta = 180;
								}else{
									theta = 90 + (Math.toDegrees(Math.atan(diffY/diffX)));
								}
							}
						}else{
							diffX = ( originCenter.x - redCenter.x);
							if(redCenter.y >= originCenter.y){
								// case 3
								diffY =  (redCenter.y - originCenter.y);
								if (diffY == 0){
									theta = 270;
								}else{
									theta = 180 + (Math.toDegrees(Math.atan(diffX/diffY)));
								}
							}else {
								// case 4
								diffY = (originCenter.y - redCenter.y);
								if(diffX == 0){
									theta = 0;
								}else{
									theta = 270 + (Math.toDegrees(Math.atan(diffY/diffX)));
								}
							}
						}
					}

					//To re- define the angle
					if(theta > 180){
						theta = theta -360;
					}

					// For the filter	
					thetaVec[4] = thetaVec[3];
					thetaVec[3] = thetaVec[2];
					thetaVec[2] = thetaVec[1];
					thetaVec[1] = thetaVec[0];
					thetaVec[0] = theta;
					smoothedAngleOld = smoothedAngle;
					//smoothedAngle = .25*(thetaVec[0] + 2*thetaVec[1] + thetaVec[2]);
					smoothedAngle = .2*(thetaVec[0] + thetaVec[1] + thetaVec[2] + thetaVec[3] +  thetaVec[4] );
					smoothedAnglePlotter.setAnalogSinkSignal(smoothedAngle);

					smoothedAngleDerOld = smoothedAngleDer;
					smoothedAngleDer = 0.4*smoothedAngleDerOld+ 0.6*(smoothedAngle - smoothedAngleOld)/hAngle;
					smoothedAngleDerPlotter.setAnalogSinkSignal(smoothedAngleDer);

					//					Core.line(hsv_image,point_150_50 ,point_202_200 ,scalar_100_10_10 /*CV_BGR(100,10,10)*/, 3);  
					//					Core.circle(hsv_image, point_210_210 , 10, scalar_100_10_10 ,3);  
					//					data=hsv_image.get(210, 210);  
					//					Core.putText(hsv_image,String.format("("+String.valueOf(data[0])+","+String.valueOf(data[1])+","+String.valueOf(data[2])+")"),point_30_30  , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
					//							,1.0,scalar_100_10_10_255 ,3);  
					//					distance.convertTo(distance, CvType.CV_8UC1);  
					//					Core.line(distance, point_150_50 , point_202_200 ,scalar_100 /*CV_BGR(100,10,10)*/, 3);  
					//					Core.circle(distance,point_210_210 , 10,scalar_100 ,3);  
					//					data=(double[])distance.get(210, 210);  
					//					Core.putText(distance,String.format("("+String.valueOf(data[0])+")"),point_30_30 , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
					//							,1.0,scalar_100 ,3);   
					//-- 5. Display the image  
					panel1.setimagewithMat(webcam_image);  
//					try {
//					    // retrieve image
//					    BufferedImage test = panel1.getimage();
//					    File outputfile = new File("saved.png");
//					    ImageIO.write(test, "png", outputfile);
//					} catch (IOException e) {
//					    System.out.println("Error in printing out");
//					}
					
					
					//					panel2.setimagewithMat(hsv_image);  
					//panel2.setimagewithMat(S);  
					//distance.convertTo(distance, CvType.CV_8UC1);  
					//					panel3.setimagewithMat(distance);  
					panel4.setimagewithMatCrop(cropped_RED_image); 
					panel5.setimagewithMatCrop(cropped_OTHER_image);
//					panel6.setimagewithMatCrop(cropped_RED_image);
//					panel7.setimagewithMatCrop(cropped_OTHER_image);
					panel6.setimagewithMatCrop(cropped_RED_image_HSV);
					panel7.setimagewithMatCrop(cropped_OTHER_image_HSV);
					
					frame1.repaint();  
					//					frame2.repaint();  
					//					frame3.repaint();  
					frame4.repaint(); 
					frame5.repaint();
					frame6.repaint();
					frame7.repaint();
					visionProcessingPeriod = System.currentTimeMillis()-visionProcessingPeriod;
					visionProcessingTime.setAnalogSinkSignal(visionProcessingPeriod);
					visionProcessingPeriod = System.currentTimeMillis();
				}  
				else  
				{  
					System.out.println(" --(!) No captured frame -- Break!");  
					break;  
				}  
			}  
		}  
		return;  
	}}