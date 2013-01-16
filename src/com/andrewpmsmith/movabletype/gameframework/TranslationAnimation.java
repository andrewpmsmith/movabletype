package com.andrewpmsmith.movabletype.gameframework;

/**
 * Animate the coordinates of a widget within the Render Surface.
 * 
 * @author Andrew Smith
 */
public class TranslationAnimation extends WidgetAnimation {
	
	private int mStartX, mStartY, mEndX, mEndY;
	
	public TranslationAnimation(Widget widget, long duration, int endX, int endY) {
		super(widget, duration, false);
		
		mStartX = widget.getX();
		mStartY = widget.getY();
		mEndX = endX;
		mEndY = endY;
	}
	
	@Override
	public void animationCallback(double fractionComplete) {
		
		double interpolatedCompleteness = Interpolators.cosine(fractionComplete);
		
		int newX = (int)(mStartX + (mEndX - mStartX)*interpolatedCompleteness);
		int newY = (int)(mStartY + (mEndY - mStartY)*interpolatedCompleteness);
		
		mWidget.setX(newX);
		mWidget.setY(newY);
	}
	
}
