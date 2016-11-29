package optimizeParameters;

public class Main {

	private static int prioVisionProcessing=10;
	public static void main(String[] args) {

		InitParam initParam = new InitParam();
		VisionProcessing visionProcessing = new VisionProcessing(prioVisionProcessing);
		OpComSignals opComSignals = new OpComSignals();

		// For the OTHER COLOR
		visionProcessing.setHminParam_G(initParam.getParam_H_min_G());
		visionProcessing.setSminParam_G(initParam.getParam_S_min_G());
		visionProcessing.setVminParam_G(initParam.getParam_V_min_G());

		visionProcessing.setHmaxParam_G(initParam.getParam_H_max_G());
		visionProcessing.setSmaxParam_G(initParam.getParam_S_max_G());
		visionProcessing.setVmaxParam_G(initParam.getParam_V_max_G());
		
		// For the radious of the OTHER COLOR
		visionProcessing.setRadMinG_Param(initParam.getMin_rad_G());
		visionProcessing.setRadMaxG_Param(initParam.getMax_rad_G());
		
		
		// For the radious of the RED COLOR
		visionProcessing.setRadMaxR_Param(initParam.getMax_rad_R());
		visionProcessing.setRadMinR_Param(initParam.getMin_rad_R());
		
		
		// For the cropping of the RED COLOR
		visionProcessing.setBorderXStartR_Param(initParam.getBorder_x_start_R());
		visionProcessing.setBorderXEndR_Param(initParam.getBorder_x_end_R());
		visionProcessing.setBorderYStartR_Param(initParam.getBorder_y_start_R());
		visionProcessing.setBorderYEndR_Param(initParam.getBorder_y_end_R());
		
		
		// For the cropping of the other COLOR
		visionProcessing.setBorderXStartG_Param(initParam.getBorder_x_start_G());
		visionProcessing.setBorderXEndG_Param(initParam.getBorder_x_end_G());
		visionProcessing.setBorderYStartG_Param(initParam.getBorder_y_start_G());
		visionProcessing.setBorderYEndG_Param(initParam.getBorder_y_end_G());


		// For the RED COLOR
		visionProcessing.setHminParam_R(initParam.getParam_H_min_R());
		visionProcessing.setSminParam_R(initParam.getParam_S_min_R());
		visionProcessing.setVminParam_R(initParam.getParam_V_min_R());

		visionProcessing.setHmaxParam_R(initParam.getParam_H_max_R());
		visionProcessing.setSmaxParam_R(initParam.getParam_S_max_R());
		visionProcessing.setVmaxParam_R(initParam.getParam_V_max_R());



		visionProcessing.setNbrOfGreenCircles(opComSignals.getnbrOfGreenCircles());
		visionProcessing.setNbrOfRedCircles(opComSignals.getnbrOfRedCircles());
		visionProcessing.setVisionProcessingTime(opComSignals.getVisionProcessingTime());
		visionProcessing.setSmoothedAngle(opComSignals.getSmoothedAngle());
		visionProcessing.setSmoothedAngleDer(opComSignals.getSmoothedAngleDer());


		visionProcessing.start();
	}

}

/*
  nbr of blue circles; 1
hsv_min_R;[0.0, 41.0, 146.0, 0.0]
hsv_max_R; [68.0, 232.0, 219.0, 0.0]
hsv_min_G;[4.0, 52.0, 178.0, 0.0]
hsv_max_G; [72.0, 149.0, 254.0, 0.0]
min rad 34
max rad 66
*/