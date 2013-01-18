package com.andrewpmsmith.movabletype.gameframework;

/**
 * The interpolators used by the animation framework.
 * 
 * @author Andrew Smith
 */
final public class Interpolators {

	/**
	 * Cosine interpolator to simulate acceleration and deceleration.
	 * 
	 * @param complete
	 *            Value between 0 and 1 representing the animation progress
	 */
	static public double cosine(double complete) {
		return Math.cos(Math.PI * (1 + complete)) / 2 + 1 / 2.0;
	}

	/**
	 * Elastic interpolator. Will accelerate past destination and then return.
	 * 
	 * @param complete
	 *            Value between 0 and 1 representing the animation progress
	 */
	static public double elastic(double complete) {
		double p = 0.3;
		double s = p / 4.0;
		if (complete == 1)
			return 1.0;
		return Math.pow(2, (-10 * complete))
				* Math.sin((complete - s) * (2 * Math.PI) / complete) + 1.0;
	}

}
