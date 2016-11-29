package bangControllerKenan30AprilPRIM;

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
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;  
import org.opencv.core.Size;  
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;  
import org.opencv.imgproc.Imgproc;  
import org.opencv.core.CvType;

//import bangControllerKenan30April.SignalAndPlotter;


class Panel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;

	// Global variables

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
		return;
	}

	public void setimagewithMat(Mat newimage) {
		image = this.matToBufferedImage(newimage);
		return;
	}

	public void setimagewithMatCrop(Mat newimage) {
		image = this.matToBufferedImageCrop(newimage);
		return;
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


	//	/**
	//	 * Converts/writes a Mat into a BufferedImage.
	//	 * 
	//	 * @param matrix
	//	 *            Mat of type CV_8UC3 or CV_8UC1
	//	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	//	 */
	//	public BufferedImage matToBufferedImage(Mat matrix) {
	//		int cols = matrix.cols();
	//		int rows = matrix.rows();
	//		int elemSize = (int) matrix.elemSize();
	//		byte[] data = new byte[cols * rows * elemSize];
	//		int type;
	//		matrix.get(0, 0, data);
	//		switch (matrix.channels()) {
	//		case 1:
	//			type = BufferedImage.TYPE_BYTE_GRAY;
	//			break;
	//		case 3:
	//			type = BufferedImage.TYPE_3BYTE_BGR;
	//			// bgr to rgb
	//			byte b;
	//			for (int i = 0; i < data.length; i = i + 3) {
	//				b = data[i];
	//				data[i] = data[i + 2];
	//				data[i + 2] = b;
	//			}
	//			break;
	//		default:
	//			return null;
	//		}
	//		BufferedImage image2 = new BufferedImage(cols, rows, type);
	//		image2.getRaster().setDataElements(0, 0, cols, rows, data);
	//		return image2;
	//	}
	//
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
	private double[] smoothedAngleDerVec = new double[5] ;
	private double angleDer;
	private double theta;
	private double smoothedAngle;
	private double smoothedAngleOld;
	private double smoothedAngleDer;
	private double smoothedAngleDerOld;
	private double hAngle =.038; // 38 [ms] approx. vision processing update time
	private double smoothing = 2.5;


	private double cartVelSmoothed;
	private double[] velVec = new double[5]; 

	// Classes created elseWhere, needs to be added
	private SignalAndPlotter angle;
	private SignalAndPlotter angleReal;
	private SignalAndPlotter visionProcessingTime;
	private SignalAndPlotter beamXCorrdinate;
	private SignalAndPlotter beamYCorrdinate;
	private SignalAndPlotter cartXCoordinate;
	private SignalAndPlotter nbrOfGreenCircles;
	private SignalAndPlotter nbrOfRedCircles;
	private SignalAndPlotter smoothedAnglePlotter;
	private SignalAndPlotter smoothedAngleDerPlotter;
	private SignalAndPlotter cartVelPlotter;
	private Regul regul;























	private long visionProcessingPeriod;




	private double angVel;
	private double cartPos;
	private double cartAcce;
	private int priority=5;
	private double uc;

	//For the cartVel
	private double cartVel;
	private double oldCartPos;
	private double oldCartVel;

	// For the time 
	private double current_time_millis;




	// For the cropping 
	Rect rec = new Rect();


	// For the plotting 20150423





	double diffX;
	double diffY;



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























	public VisionProcessing(int priority){

		this.priority = priority;
	}
	public VisionProcessing(){
	}

	public void setAngle(SignalAndPlotter angle){
		this.angle = angle ;
	}

	public void setAngleReal(SignalAndPlotter angleReal){
		this.angleReal = angleReal ;
	}
	public void setVisionProcessingTime(SignalAndPlotter time){
		this.visionProcessingTime = time ;
	}
	public void setBeamXCorrdinate(SignalAndPlotter coordinate){
		this.beamXCorrdinate = coordinate  ;
	}
	public void setCartXCorrdinate(SignalAndPlotter coordinate){
		this.cartXCoordinate = coordinate  ;
	}
	public void setBeamYCorrdinate(SignalAndPlotter coordinate){
		this.beamYCorrdinate = coordinate  ;
	}

	public void setNbrOfGreenCircles(SignalAndPlotter nbrOfGreenCircles){
		this.nbrOfGreenCircles = nbrOfGreenCircles  ;
	}

	public void setNbrOfRedCircles(SignalAndPlotter nbrOfRedCircles){
		this.nbrOfRedCircles = nbrOfRedCircles  ;
	}

	public void setSmoothedAngle(SignalAndPlotter smoothedAngle){
		this.smoothedAnglePlotter = smoothedAngle  ;
	}

	public void setSmoothedAngleDer(SignalAndPlotter smoothedAngleDer){
		this.smoothedAngleDerPlotter = smoothedAngleDer  ;
	}

	public void setCartVel(SignalAndPlotter cartVelPlotter){
		this.cartVelPlotter = cartVelPlotter ;
	}



	// For the communication between the visionProcessing and the regul
	public void setRegul(Regul r){
		this.regul = r;
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





		JFrame frame2 = new JFrame("Cropped RED ");  
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame2.setSize(640,480);  
		frame2.setBounds(300,100, frame1.getWidth(), frame1.getHeight());  
		Panel panel2 = new Panel();  
		frame2.setContentPane(panel2);  
		frame2.setVisible(true);  


		JFrame frame3 = new JFrame("Cropped OTHER");  
		frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame3.setSize(640,480);  
		frame3.setBounds(600,200, frame1.getWidth(), frame1.getHeight());  
		Panel panel3 = new Panel();  
		frame3.setContentPane(panel3);  
		frame3.setVisible(true);  




		JFrame frame4 = new JFrame("Threshold for the red color");  
		frame4.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame4.setSize(640,480);  
		frame4.setBounds(900,300, 640, 480);  
		Panel panel4 = new Panel();  
		frame4.setContentPane(panel4);      
		frame4.setVisible(true);



		JFrame frame5 = new JFrame("Threshold for the other color");  
		frame5.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		frame5.setSize(640,480);  
		frame5.setBounds(900,300, 640, 300+480);  
		//  
		Panel panel5 = new Panel();  
		frame5.setContentPane(panel5);      
		frame5.setVisible(true);  




		//-- 2. Read the video stream  
		VideoCapture capture =new VideoCapture(0);  
		Mat webcam_image_original=new Mat();
		Mat webcam_image=new Mat();
		Mat hsv_image=new Mat();  
		Mat thresholded_red=new Mat();  
		Mat thresholded2_red=new Mat();  
		Mat thresholded_blue = new Mat();
		Mat thresholded_tot_red = new Mat();
		Mat thresholded_tot_blue = new Mat();
		Mat thresholded_tot_BR = new Mat();
		Mat thresholded = new Mat();
		Mat thresholded_tot_BR_color_maxima = new Mat();
		Mat thresholded2 = new Mat();
		Mat thresholded_maxima_blue = new Mat();
		Mat thresholded_maxima_red = new Mat();
		Mat thresholded_color_maxima = new Mat();
		Mat circles_blue = new Mat();
		Mat circles_red = new Mat();



		capture.read(webcam_image_original);  
		webcam_image = webcam_image_original;
		//		frame1.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		//		frame2.setSize(webcam_image.width()+40,webcam_image.height()+60);  
		//		frame3.setSize(webcam_image.width()+40,webcam_image.height()+60);  

		frame4.setSize(webcam_image_original.width()+40,webcam_image_original.height()+60); 
		frame5.setSize(webcam_image_original.width()+40,webcam_image_original.height()+60); 

		Mat array255=new Mat(webcam_image_original.height(),webcam_image_original.width(),CvType.CV_8UC1);  
		array255.setTo(new Scalar(255));  
		/*Mat S=new Mat();  
		S.ones(new Size(hsv_image.width(),hsv_image.height()),CvType.CV_8UC1);  
		Mat V=new Mat();  
		V.ones(new Size(hsv_image.width(),hsv_image.height()),CvType.CV_8UC1);  
		    Mat H=new Mat();  
		H.ones(new Size(hsv_image.width(),hsv_image.height()),CvType.CV_8UC1);*/  
		Mat distance=new Mat(webcam_image_original.height(),webcam_image_original.width(),CvType.CV_8UC1);  
		//new Mat();//new Size(webcam_image.width(),webcam_image.height()),CvType.CV_8UC1);  
		List<Mat> lhsv = new ArrayList<Mat>(3);      
		Mat circles = new Mat(); // No need (and don't know how) to initialize it.  
		// The function later will do it... (to a 1*N*CV_32FC3)  




		// For the red color filtering, the red occurs from both sides of the color spectrum
		Scalar hsv_min_red = new Scalar(0, 60, 0, 0);  
		Scalar hsv_max_red = new Scalar(210, 255, 248, 0);   




		// For the Green color filtering, the Green occurs only once in the spectrum
		Scalar hsv_min_blue = new Scalar(0, 74, 113, 0); 
		Scalar hsv_max_blue = new Scalar(198, 255, 255, 0); 

		//		Scalar hsv_min_blue = new Scalar(60, 255, 109, 0); 
		//		Scalar hsv_max_blue = new Scalar(75, 255, 255, 0); 

		// Our variable for get the angle


		// New variable for optimazation
		Scalar scalar10 = new Scalar(10.0);
		Scalar scalar255 = new Scalar(255.0);
		Size size9and9 = new Size(9,9);
		Point point150and50 = new Point(150,50);
		Point point202and200 = new Point(202,200);
		Scalar scalar100and10and10 = new Scalar(100,10,10);
		Point point210and210 = new Point(210,210);
		Point point30and30 = new Point(30, 30);
		Point point1and1 = new Point(1, 1);
		Scalar scalar255and0and255 = new Scalar(255, 0, 255);
		Scalar scalar100and10and10and255 = new Scalar(100,10,10,255);
		Scalar scalar100 = new Scalar(100);
		Scalar scalar255and0and0 = new Scalar(255,0,0);

		// Pre -definitions for the red color
		Mat cropped_RED_image = new Mat(480, 640, CvType.CV_8UC3,new Scalar(0,0,0) ); 			// Must define the type of pixel coding
		Mat cropped_RED_image_HSV = new Mat(480,640,CvType.CV_8UC3,new Scalar(0,0,0));	

		// Pre definition of the other color
		Mat cropped_OTHER_image = new Mat(480, 640, CvType.CV_8UC3,new Scalar(0,0,0) ); // http://www.tutorialspoint.com/java_dip/color_space_conversion.htm
		Mat cropped_OTHER_image_HSV = new Mat(480,640,CvType.CV_8UC3,new Scalar(0,0,0));		

		Mat webcam_image_copy = new Mat();
		Rect roi = new Rect();
		roi.x = 1 ;
		roi.y = 1;
		roi.height = 240;
		roi.width = 639;

		System.out.println("roi" + roi.toString());


		double[] data = new double[3];  
		if( capture.isOpened())  
		{  

			// THE LOOP OF IMG PROC
			while( true )  
			{  
				capture.read(webcam_image_original);  
				if( !webcam_image_original.empty() )  
				{  




					webcam_image=webcam_image_original;	
					webcam_image_copy=webcam_image_original.clone();


					// Cropping for the red color
					cropped_RED_image = webcam_image_original.submat(0,111,78,601);
					//Highgui.imwrite("Upper_part_RED.png",cropped_RED_image);

					//Highgui.imwrite("res.png",cropped_RED_image);
					// Cropping for the other color
					cropped_OTHER_image = webcam_image_original.submat(225,259,98,601);






					// TO HSV

					// One way to select a range of colors by Hue  
					Imgproc.cvtColor(webcam_image, hsv_image, Imgproc.COLOR_BGR2HSV);  // to HSV



					// For the red color TO HSV
					Imgproc.cvtColor(cropped_RED_image, cropped_RED_image_HSV, Imgproc.COLOR_BGR2HSV);  // to HSV
					//Highgui.imwrite("res_HSV.jpg",cropped_RED_image_HSV);

					// For the other color TO HSV
					Imgproc.cvtColor(cropped_OTHER_image, cropped_OTHER_image_HSV, Imgproc.COLOR_BGR2HSV);  // to HSV








					// Threshold out the red color once
					Core.inRange(cropped_RED_image_HSV, hsv_min_red, hsv_max_red, cropped_RED_image_HSV);            


					// Threshold out the other color once
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
					int rows_s =S.rows();
					int cols_s = S.cols();
					//System.out.println("Rows; " + cols_s + " Cols; " + rows_s);


					//Core.subtract(array255, S, S);  
					//Core.subtract(array255, V, V);  
					S.convertTo(S, CvType.CV_32F);  
					V.convertTo(V, CvType.CV_32F);  
					//Core.magnitude(S, V, distance);  
					// Find the most intense colors from the range 10 to 250
					//Core.inRange(distance,scalar10, scalar255, thresholded_color_maxima); 
					// Threshold the most intense colors with the red color

					// Threshold the most intense colors with the red color
					//Core.bitwise_and(thresholded_tot_red, thresholded_color_maxima,thresholded_maxima_red);

					// Threshold the most intense colors with the blue color
					//Core.bitwise_and(thresholded_tot_blue, thresholded_color_maxima,thresholded_maxima_blue);


					// For the red circle, pixel radius is 17 < < 23




					Imgproc.GaussianBlur(cropped_RED_image_HSV, cropped_RED_image_HSV, size9and9,2,2);  
					Imgproc.HoughCircles(cropped_RED_image_HSV, circles_red, Imgproc.CV_HOUGH_GRADIENT, 2, cropped_RED_image_HSV.height()/2, 20, 50, 10, 23); 





					//minimum and maximum detectable radios (MinRadio and MaxRadio)
					//Imgproc.HoughCircles(thresholded_maxima_red, circles_red,Imgproc.CV_HOUGH_GRADIENT, 2,webcam_image.height() / 20, 500, 50, webcam_image.height()/20, webcam_image.height()/6);

					// For the blue circles
					Imgproc.GaussianBlur(cropped_OTHER_image_HSV, cropped_OTHER_image_HSV, size9and9,2,2); 
					//Imgproc.HoughCircles(thresholded_maxima_blue, circles_blue, Imgproc.CV_HOUGH_GRADIENT, 2, thresholded_maxima_blue.height()/2, 500, 50, webcam_image.height()/150, webcam_image.height()/2);
					Imgproc.HoughCircles(cropped_OTHER_image_HSV, circles_blue, Imgproc.CV_HOUGH_GRADIENT, 2, cropped_OTHER_image_HSV.height()/2, 100, 50, 10, 32);


					//minimum and maximum detectable radios (MinRadio and MaxRadio) 
					//Imgproc.HoughCircles(thresholded_maxima_blue, circles_blue,Imgproc.CV_HOUGH_GRADIENT, 2,webcam_image.height() / 20, 500, 50, webcam_image.height()/20, webcam_image.height()/6);


					//-- 4. Add some info to the image  
					//					Core.line(webcam_image, point150and50, point202and200, scalar100and10and10/*CV_BGR(100,10,10)*/, 3);  
					//					Core.circle(webcam_image, point210and210, 10, scalar100and10and10,3);  
					//					data=webcam_image.get(210, 210);  
					//					Core.putText(webcam_image,String.format("("+String.valueOf(data[0])+","+String.valueOf(data[1])+","+String.valueOf(data[2])+")"),point30and30 , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
					//							,1.0,scalar100and10and10and255,3); 

					// For the red color  
					int rows_red = circles_red.rows();  
					int nbrOfCircles_red = circles_red.cols();
					float[] circleElement_red = new float[rows_red*3]; // will be 0 or 3, will contain the info [(x-coordinate), (y- coordinate), (r (radius))]
					//System.out.println("Found red circles:  " + nbrOfCircles_red);

					// For the blue color
					int rows_blue = circles_blue.rows();  
					int nbrOfCircles_blue = circles_blue.cols();
					float[] circleElement_blue = new float[rows_blue*3]; // will be 0 or 3, will contain the info [(x-coordinate), (y- coordinate), (r (radius))]
					//					System.out.println("Found blue circles: " + nbrOfCircles_blue);
					nbrOfGreenCircles.setAnalogSinkSignal(nbrOfCircles_blue);
					nbrOfRedCircles.setAnalogSinkSignal(nbrOfCircles_red);
					regul.setNbrOfBeamCircles( nbrOfCircles_red );

					Point center =point1and1;
					// Drawing out all the red circles
					if (circleElement_red.length>0){  // circleElement will always be 3 or 0, 0 if no circle are found,3 if any circles are found

						for (int j = 0; j < nbrOfCircles_red; j++) {

							circles_red.get(0, j, circleElement_red); // retrives the element from the circles which is a row vector(each element is a vector [x y r] for each circle)


							int i = 0;
							//Point center = new Point(circleElement_red[i], circleElement_red[i + 1]);
							center.x=circleElement_red[i]+78;
							center.y= circleElement_red[i + 1]+0;
							// Draws the circle in the image
							Core.circle(webcam_image, center,(int) circleElement_red[2] , scalar255and0and255);
							//System.out.println("Red Circle Radius: " + circleElement_red[i + 2]);
							beamXCorrdinate.setAnalogSinkSignal(center.x);
							beamYCorrdinate.setAnalogSinkSignal(center.y);
							//System.out.println("Red circles coordinates ");
							//System.out.println("Coordinate x : " + center.x);
							//System.out.println("Coordinate y : " + center.y);

						}  
					} 



					if (circleElement_blue.length>0){  // circleElement will always be 3 or 0, 0 if no circle are found,3 if any circles are found

						for (int j = 0; j < nbrOfCircles_blue; j++) {

							circles_blue.get(0, j, circleElement_blue); // it retrieves the element from the circles which is a row vector(each element is a vector [x y r] for each circle)


							int i = 0;
							//Point center = new Point(circleElement_blue[i], circleElement_blue[i + 1]);
							center.x=circleElement_blue[i]+98;
							cartXCoordinate.setAnalogSinkSignal(center.x);
							regul.setPosCart(center.x);
							center.y=circleElement_blue[i + 1]+225;



							// For the calculation of the cart velocity


							cartVel = (center.x -oldCartPos) / 0.040 ;		//[pixel/sec]

							velVec[4] = velVec[3];
							velVec[3] = velVec[2];
							velVec[2] = velVec[1];
							velVec[1] = velVec[0];
							velVec[0] = cartVel;

							cartVelSmoothed =  0.2*(velVec[0] +velVec[1] +velVec[2] +velVec[3] +velVec[4]) ;


							cartVelPlotter.setAnalogSinkSignal(cartVelSmoothed);

							regul.setCartVel(cartVelSmoothed);
							oldCartPos = center.x;


							// Draws the circle in the image
							Core.circle(webcam_image, center,(int) circleElement_blue[2] , scalar255and0and0);

							//							System.out.println("Blue Circle Radius: " + circleElement_blue[i + 2]);
							//							
							//							
							//							System.out.println("Blue circles coordinates : ");
							//							System.out.println("Coordinate x :" + center.x);
							//							System.out.println("Coordinate y :" + center.y);
						}  
					} 

					//System.out.println("F");

					// calculating the angle:

					double diffX;
					double diffY;


					if (nbrOfCircles_red == 1 && nbrOfCircles_blue == 1){
						circles_red.get(0, 0, circleElement_red);
						Point originCenter = new Point(circleElement_blue[0]+98, circleElement_blue[1]+225 );
						Point redCenter = new Point(circleElement_red[0]+78, circleElement_red[1]+0);
						
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
					if(theta > 180  ){
						theta=theta-360;
					}
					angle.setAnalogSinkSignal(theta);
					regul.setAngle(theta);
					//					System.out.println("theta" + theta);

					// For the real angle before the filter
					angleReal.setAnalogSinkSignal(theta);

					// For the filter	
					thetaVec[4] = thetaVec[3];
					thetaVec[3] = thetaVec[2];
					thetaVec[2] = thetaVec[1];
					thetaVec[1] = thetaVec[0];
					thetaVec[0] = theta;
					smoothedAngleOld = smoothedAngle;
					//smoothedAngle = .25*(thetaVec[0] + 2*thetaVec[1] + thetaVec[2]);
					smoothedAngle = .2*(thetaVec[0] + thetaVec[1] + thetaVec[2] + thetaVec[3] +  thetaVec[4] );


					// Save the smoothed angle


					angleDer = (smoothedAngle -smoothedAngleOld)/0.04;


					smoothedAngleDerVec[4] = smoothedAngleDerVec[3];
					smoothedAngleDerVec[3] = smoothedAngleDerVec[2];
					smoothedAngleDerVec[2] = smoothedAngleDerVec[1];
					smoothedAngleDerVec[1] = smoothedAngleDerVec[0];
					smoothedAngleDerVec[0] = angleDer;


					smoothedAngleDer =0.2* (smoothedAngleDerVec[0]+smoothedAngleDerVec[1]+smoothedAngleDerVec[2]+smoothedAngleDerVec[3]+ smoothedAngleDerVec[4]);



					// Sends the updated angle and angleVelocity to regul
					regul.setAngle(smoothedAngle);
					regul.setAngleVel(smoothedAngleDer);



					// Writes direct to plotter 
					smoothedAnglePlotter.setAnalogSinkSignal(smoothedAngle);
					smoothedAngleDerPlotter.setAnalogSinkSignal(smoothedAngleDer);


					// For the velocity of the cart





					current_time_millis = System.currentTimeMillis();

					//System.out.println("theta : " + theta);
					//					Core.line(hsv_image,point150and50, point202and200, scalar100and10and10/*CV_BGR(100,10,10)*/, 3);  
					//					Core.circle(hsv_image, point210and210, 10, scalar100and10and10,3);  
					//					data=hsv_image.get(210, 210);  
					//					Core.putText(hsv_image,String.format("("+String.valueOf(data[0])+","+String.valueOf(data[1])+","+String.valueOf(data[2])+")"),point30and30 , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
					//							,1.0,scalar100and10and10and255,3);  
					//					distance.convertTo(distance, CvType.CV_8UC1);  
					//					Core.line(distance, point150and50, point202and200, scalar100/*CV_BGR(100,10,10)*/, 3);  
					//					Core.circle(distance, point210and210, 10, scalar100,3);  
					//					data=(double[])distance.get(210, 210);  
					//					Core.putText(distance,String.format("("+String.valueOf(data[0])+")"),point30and30 , 3 //FONT_HERSHEY_SCRIPT_SIMPLEX  
					//							,1.0,scalar100,3);   
					//-- 5. Display the image  
					panel1.setimagewithMat(webcam_image);
					//Highgui.imwrite("main camera.jpg",webcam_image);



					panel2.setimagewithMatCrop(cropped_RED_image);  
					//panel2.setimagewithMat(S);  
					//distance.convertTo(distance, CvType.CV_8UC1);  
					panel3.setimagewithMatCrop(cropped_OTHER_image);  




					panel4.setimagewithMatCrop(cropped_RED_image_HSV); 
					panel5.setimagewithMatCrop(cropped_OTHER_image_HSV);
					frame1.repaint();  
					frame2.repaint();  
					frame3.repaint();  
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
