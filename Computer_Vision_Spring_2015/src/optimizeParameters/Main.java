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
