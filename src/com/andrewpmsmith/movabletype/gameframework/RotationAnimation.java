package com.andrewpmsmith.movabletype.gameframework;

/**
 * Animate the rotation of a widget from its mid-point.
 * 
 * @author Andrew Smith
 */
public class RotationAnimation extends WidgetAnimation {
	
	private float mStartAngle;
	private float mEndAngle;
	
	public RotationAnimation(Widget widget, long duration, float endAngle) {
		super(widget, duration, false);
		
		mStartAngle = widget.getRotation();
		mEndAngle = endAngle;
	}
	
	@Override
	public void animationCallback(double fractionComplete) {
		double interpolatedCompleteness = Interpolators.cosine(fractionComplete);
		float newAngle = (float) (mStartAngle + (mEndAngle - mStartAngle)*interpolatedCompleteness);
		mWidget.setRotation(newAngle);
	}
	
}
