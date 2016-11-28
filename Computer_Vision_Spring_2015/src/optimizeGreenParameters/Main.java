package optimizeGreenParameters;

public class Main {
	
	private static int prioVisionProcessing=10;
	public static void main(String[] args) {

			InitParam initParam = new InitParam();
			VisionProcessing visionProcessing = new VisionProcessing(prioVisionProcessing);
			
			visionProcessing.setHminParam(initParam.getParam_H_min());
			visionProcessing.setSminParam(initParam.getParam_S_min());
			visionProcessing.setVminParam(initParam.getParam_V_min());
			
			
			
			visionProcessing.setHmaxParam(initParam.getParam_H_max());
			visionProcessing.setSmaxParam(initParam.getParam_S_max());
			visionProcessing.setVmaxParam(initParam.getParam_V_max());	
			
			visionProcessing.start();
	}

}
