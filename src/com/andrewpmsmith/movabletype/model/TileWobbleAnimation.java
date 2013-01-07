package com.andrewpmsmith.movabletype.model;

import com.andrewpmsmith.movabletype.gameframework.Interpolators;
import com.andrewpmsmith.movabletype.gameframework.Widget;
import com.andrewpmsmith.movabletype.gameframework.WidgetAnimation;

public class TileWobbleAnimation extends WidgetAnimation {
	
	private float mStartAngle;
	private float mEndAngle;
	
	public TileWobbleAnimation(Widget widget, long duration, float endAngle) {
		super(widget, duration, false);
		
		mStartAngle = widget.getRotation();
		mEndAngle = endAngle;
	}
	
	@Override
	public void animationCallback(double fractionComplete) {
		double interpolatedCompleteness = Interpolators.elastic(fractionComplete);
		float newAngle = (float) (mStartAngle + (mEndAngle - mStartAngle)*interpolatedCompleteness);
		mWidget.setRotation(newAngle);
		
		mWidget.setShadow(10,0xFF444444);
	}
	
	@Override
	public void complete(){
		mWidget.setShadow(0,0);
	}
	
	@Override
	public void cancel(){
		complete();
	}
}