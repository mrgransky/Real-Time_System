package optimizeParameters;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;  
import java.util.List;  

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;  
import org.opencv.core.Mat;   
import org.opencv.core.Point;  
import org.opencv.core.Scalar;  
import org.opencv.core.Size;  
import org.opencv.highgui.VideoCapture;  
import org.opencv.imgproc.Imgproc;  
import org.opencv.core.CvType;

import bangControllerKenan30April.SignalAndPlotter;




class Panel extends JPanel {
	private static final long serialVersionUID = 1L;

	// Global variables
	private BufferedImage image;
	// In the method matToBufferedImage
	private final int COLS = 640;			// PIXEL WITDH OF THE CAMERA IN USE
	private final int ROWS = 480;			// PIXEL HEIGHT OF THE CAMERA IN USE
	private final int PIXEL_BLACK_AND_WHITE = 1;
	private final int PIXEL_COLOR = 3;
	private byte[] data;
	private	byte[] data_1 = new byte[COLS* ROWS * PIXEL_BLACK_AND_WHITE];					//byte[] data = new byte[cols * rows * elemSize];
	private	byte[] data_3 = new byte[COLS* ROWS * PIXEL_COLOR];	
	private byte b;
	private BufferedImage image_GRAY = new BufferedImage(COLS, ROWS, BufferedImage.TYPE_BYTE_GRAY);
	private BufferedImage image_BGR = new BufferedImage(COLS, ROWS,BufferedImage.TYPE_3BYTE_BGR);


	// For the generall way
	private int cols ;
	private int rows ;
	private int elemSize;



	// Create a constructor method
	public Panel() {
		super();
	}

	private BufferedImage getimage() {
		return image;
	}

	public void setimage(BufferedImage newimage) {
		image = newimage;
	}

	public void setimagewithMat(Mat newimage) {
		image = this.matToBufferedImage(newimage);
	}

	/**
	 * Converts/writes a Mat into a BufferedImage.
	 * 
	 * @param matrix
	 *            Mat of type CV_8UC3 or CV_8UC1
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
		}else{	// For the general case
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
	
	private double[] thetaVec = new double[5] ;
	private double theta;
	private double smoothedAngle;
	private double smoothedAngleOld;
	private double smoothedAngleDer;
	private double smoothedAngleDerOld;
	private double hAngle =.035; // 50 [ms] approx. vision processing update time

	//Created eleseWhere FOR OTHER COLOR
	private Param H_min_G;
	private Param S_min_G;
	private Param V_min_G;

	private Param H_max_G;
	private Param S_max_G;
	private Param V_max_G;

	// //Created eleseWhere FOR RED COLOR
	private Param H_min_R;
	private Param S_min_R;
	private Param V_min_R;

	private Param H_max_R;
	private Param S_max_R;
	private Param V_max_R;

	
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
	private Size size_9_9 = new Size(9,9);
	private Point point_150_50 = new Point(150,50);
	private Point point_202_200 = new Point(202,200);
	private Scalar scalar_100_10_10 = new Scalar(100,10,10);
	private Point point_210_210 = new Point(210,210);
	private Point point_30_30 = new Point(30, 30);
	private Scalar scalar_100_10_10_255 = new Scalar(100,10,10,255);
	private Point point_1_1 = new Point(1, 1);
	private Scalar scalar_255_255_255 = new Scalar(255, 255, 255);
	private Scalar scalar_100 = new Scalar(100);
	private Scalar scalar_255_0_255 = new Scalar(255, 0, 255);


	// For the counting of the red circles (LIMITING THE NBR)
	float[] circleElement_red;
	float[] rows_red_vec_0 = new float[0];
	float[] rows_red_vec_1 = new float[1*3];  // will be 0 or 3, will contain the info [(x-coordinate), (y- coordinate), (r (radius))]
	float[] rows_red_vec_2 = new float[2*3];
	int rows_red;
	int nbrOfCircles_red;
	int nbrOfCircles_red_limit;   // used for the loops and for limiting the nbr of loops and creation of unnecessary vectors

	// For the counting of the other color circles (LIMITING THE NBR), 
	float[] circleElement_blue;
	float[] rows_blue_vec_0 = new float[0];
	float[] rows_blue_vec_1 = new float[1*3];	// will be 0 or 3, will contain the info [(x-coordinate), (y- coordinate), (r (radius))]
	float[] rows_blue_vec_2 = new float[2*3];
	int rows_blue;  
	int nbrOfCircles_blue;
	int nbrOfCircles_blue_limit;             // used for the loops and for limiting the nbr of loops and creation of unnecessary vectors





	public VisionProcessing(int priority){

		this.priority = priority;

	}

	// Setting the min values for the communication beetwen the classes
	//For the min values below FOR THE OTHER COLOR
	public void setHminParam_G(Param H_min){
		this.H_min_G = H_min;
	}
	public void setSminParam_G(Param S_min){
		this.S_min_G = S_min;
	}
	public void setVminParam_G(Param V_min){
		this.V_min_G = V_min;
	}

	//For the max values below FOR THE OTHER COLOR
	public void setHmaxParam_G(Param H_max){
		this.H_max_G = H_max;
	}
	public void setSmaxParam_G(Param S_max){
		this.S_max_G = S_max;
	}
	public void setVmaxParam_G(Param V_max){
		this.V_max_G = V_max;
	}

	//For the min values below FOR THE RED COLOR
	public void setHminParam_R(Param H_min){
		this.H_min_R = H_min;
	}
	public void setSminParam_R(Param S_min){
		this.S_min_R = S_min;
	}
	public void setVminParam_R(Param V_min){
		this.V_min_R = V_min;
	}

	//For the max values below FOR THE OTHER COLOR
	public void setHmaxParam_R(Param H_max){
		this.H_max_R = H_max;
	}
	public void setSmaxParam_R(Param S_max){
		this.S_max_R = S_max;
	}
	public void setVmaxParam_R(Param V_max){
		this.V_max_R = V_max;
	}








	// For OTHER COLOR reading from the buffer in the Param class as a inner class
	// For the min values
	private double getHmin_G(){
		return this.H_min_G.getParam();
	}
	private double getSmin_G(){
		return this.S_min_G.getParam();
	}
	private double getVmin_G(){
		return this.V_min_G.getParam();
	}
	// For the max values
	private double getHmax_G(){
		return this.H_max_G.getParam();
	}
	private double getSmax_G(){
		return this.S_max_G.getParam();
	}
	private double getVmax_G(){
		return this.V_max_G.getParam();
	}


	// For RED COLOR reading from the buffer in the Param class as a inner class
	// For the min values
	private double getHmin_R(){
		return this.H_min_R.getParam();
	}
	private double getSmin_R(){
		return this.S_min_R.getParam();
	}
	private double getVmin_R(){
		return this.V_min_R.getParam();
	}
	// For the max values
	private double getHmax_R(){
		return this.H_max_R.getParam();
	}
	private double getSmax_R(){
		return this.S_max_R.getParam();
	}
	private double getVmax_R(){
		return this.V_max_R.getParam();
	}



	public void setNbrOfGreenCircles(SignalAndPlotter nbrOfGreenCircles){
		this.nbrOfGreenCircles = nbrOfGreenCircles  ;
	}

	public void setNbrOfRedCircles(SignalAndPlotter nbrOfRedCircles){
		this.nbrOfRedCircles = nbrOfRedCircles  ;
	}
	
	public void setVisionProcessingTime(SignalAndPlotter time){
		this.visionProcessingTime = time ;
	}
	
	public void setSmoothedAngle(SignalAndPlotter smoothedAngle){
		this.smoothedAnglePlotter = smoothedAngle  ;
	}
	public void setSmoothedAngleDer(SignalAndPlotter smoothedAngleDer){
		this.smoothedAngleDerPlotter = smoothedAngleDer  ;
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

		//-- 2. Read the video stream  
		VideoCapture capture =new VideoCapture(0);  
		Mat webcam_image=new Mat();  
		Mat hsv_image=new Mat();  
		Mat thresholded_red=new Mat();  
		Mat thresholded2_red=new Mat();  

		Mat thresholded_tot_red = new Mat();
		Mat thresholded_tot_blue = new Mat();



		Mat thresholded_maxima_blue = new Mat();
		Mat thresholded_maxima_red = new Mat();
		Mat thresholded_color_maxima = new Mat();
		Mat circles_blue = new Mat();
		Mat circles_red = new Mat();



		capture.read(webcam_image);  
		frame1.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		//		frame2.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		//		frame3.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		frame4.setSize(webcam_image.width()+40,webcam_image.height()+60); 
		frame5.setSize(webcam_image.width()+40,webcam_image.height()+60); 

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



		double[] data = new double[3];  
		if( capture.isOpened())  
		{  

			// THE LOOP OF IMG PROC
			while( true )  
			{  
				capture.read(webcam_image);  
				if( !webcam_image.empty() )  
				{  
					// One way to select a range of colors by Hue  
					Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);  // to HSV




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
					Core.inRange(hsv_image, hsv_min_red, hsv_max_red, thresholded_tot_red);        //    


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
					Core.inRange(hsv_image, hsv_min_blue, hsv_max_blue, thresholded_tot_blue);



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
					Core.inRange(distance,min_distance, max_distance, thresholded_color_maxima); 
					// Threshold the most intense colors with the red color

					// Threshold the most intense colors with the red color
					Core.bitwise_and(thresholded_tot_red, thresholded_color_maxima,thresholded_maxima_red);

					// Threshold the most intense colors with the blue color
					Core.bitwise_and(thresholded_tot_blue, thresholded_color_maxima,thresholded_maxima_blue);


					// For the red circle, pixel radius is 17 < < 23
					Imgproc.GaussianBlur(thresholded_maxima_red, thresholded_maxima_red, size_9_9,2,2);  
					Imgproc.HoughCircles(thresholded_maxima_red, circles_red, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded_maxima_red.height()/2, 20, 50, 17, 30); 
					//minimum and maximum detectable radios (MinRadio and MaxRadio)
					//Imgproc.HoughCircles(thresholded_maxima_red, circles_red,Imgproc.CV_HOUGH_GRADIENT, 2,webcam_image.height() / 20, 500, 50, webcam_image.height()/20, webcam_image.height()/6);

					// For the blue circles
					Imgproc.GaussianBlur(thresholded_maxima_blue, thresholded_maxima_blue, size_9_9,2,2); 
					Imgproc.HoughCircles(thresholded_maxima_blue, circles_blue, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded_maxima_blue.height()/2, 20, 50, 17, 30);
					//minimum and maximum detectable radios (MinRadio and MaxRadio) 
					//Imgproc.HoughCircles(thresholded_maxima_blue, circles_blue,Imgproc.CV_HOUGH_GRADIENT, 2,webcam_image.height() / 20, 500, 50, webcam_image.height()/20, webcam_image.height()/6);


					//-- 4. Add some info to the image  
//					Core.line(webcam_image, point_150_50, point_202_200,scalar_100_10_10 /*CV_BGR(100,10,10)*/, 3);  
//					Core.circle(webcam_image,point_210_210 , 10, scalar_100_10_10,3);  
//					data=webcam_image.get(210, 210);  
//					Core.putText(webcam_image,String.format("("+String.valueOf(data[0])+","+String.valueOf(data[1])+","+String.valueOf(data[2])+")"),point_30_30 , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
//							,1.0,scalar_100_10_10_255,3); 

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

							center.x=circleElement_red[i];
							center.y= circleElement_red[i + 1];
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
							center.x=circleElement_blue[i];
							center.y=circleElement_blue[i + 1];
							// Draws the circle in the image
							Core.circle(webcam_image, center,(int) circleElement_blue[2] , scalar_255_0_255 );
						}  
					} 
					// calculating the angle:

					
					

										if (nbrOfCircles_red == 1 && nbrOfCircles_blue == 1){
											circles_red.get(0, 0, circleElement_red);
											Point originCenter = new Point(circleElement_blue[0], circleElement_blue[1]+30);
											Point redCenter = new Point(circleElement_red[0], circleElement_red[1]);
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
					//					panel2.setimagewithMat(hsv_image);  
					//panel2.setimagewithMat(S);  
					//distance.convertTo(distance, CvType.CV_8UC1);  
					//					panel3.setimagewithMat(distance);  
					panel4.setimagewithMat(thresholded_maxima_red); 
					panel5.setimagewithMat(thresholded_maxima_blue);
					frame1.repaint();  
					//					frame2.repaint();  
					//					frame3.repaint();  
					frame4.repaint(); 
					frame5.repaint();
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
	}  
}