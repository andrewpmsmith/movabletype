package GraphicsFramework;

public class Interpolators {

	static public double cosine(double complete) {
		return Math.cos(Math.PI*(1 + complete))/2 + 1/2.0;
	}
	
	static public double elastic(double complete) {
		double p = 0.3;
		double s = p / 4.0;
		if (complete == 1) return 1.0;
		return Math.pow(2, (-10 * complete)) * Math.sin((complete - s) * (2 * Math.PI) / complete) + 1.0;
	}

}
