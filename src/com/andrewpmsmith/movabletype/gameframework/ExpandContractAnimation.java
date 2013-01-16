package com.andrewpmsmith.movabletype.gameframework;

/**
 * Animate the width of a widget.
 * 
 * @author Andrew Smith
 */
public class ExpandContractAnimation extends WidgetAnimation {
	
	private int mStartWidth;
	private int mEndWidth;
	private double mOffset = 0;
	
	public ExpandContractAnimation(Widget widget, long duration, int endWidth) {
		super(widget, duration, false);
		mStartWidth = widget.getWidth();
		mEndWidth = endWidth;
	}
	
	@Override
	public void animationCallback(double fractionComplete) {
		double interpolatedCompleteness = Interpolators.cosine(fractionComplete);
		
		int newWidth = (int) (mStartWidth + (mEndWidth - mStartWidth)*interpolatedCompleteness);
		mWidget.setWidth(newWidth);
		
		double offset = (mStartWidth - newWidth)/2.0;
		int offsetDelta = (int)(offset - mOffset);
		mOffset = offset;
		mWidget.setX(mWidget.getX() + offsetDelta);
		
	}
	
}