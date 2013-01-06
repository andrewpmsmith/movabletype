package GraphicsFramework;

public abstract class WidgetAnimation {
		
	protected Widget mWidget;
	
	private boolean mAnimating = false;
	
	private final long mAnimationDuration;
	private long mAnimationProgress = 0;
	
	private boolean mAnimationRepeat = true;
		
	public WidgetAnimation(Widget widget, long duration, boolean animationRepeat){
		mWidget = widget;
		mAnimationDuration = duration;
		mAnimationRepeat = animationRepeat;
	}
	
	public boolean isAnimating() {
		return mAnimating;
	}
	
	public void start() {
		mAnimating = true;
	}
	
	public void animate(long timeSinceLastFrame) {
		
		if (mAnimating) {
			
			mAnimationProgress += timeSinceLastFrame;
			
			double fractionComplete = (double)mAnimationProgress / mAnimationDuration;
			
			if (fractionComplete>=1) {
				
				fractionComplete = (mAnimationRepeat)?fractionComplete-1:1;
				
			}
			
			animationCallback(fractionComplete);
			
			if (fractionComplete>=1 && !mAnimationRepeat) {
				mAnimating = false;
				mAnimationProgress = 0;
				//mWidget.cancelAnimation(this);
				complete();
			}
		}
	}
	
	public void cancel() {}
	
	public void complete() {}
	
	abstract public void animationCallback( double  fractionComplete);
	
}
