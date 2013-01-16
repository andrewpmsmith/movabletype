package com.andrewpmsmith.movabletype.gameframework;

public abstract class WidgetAnimation {

	protected Widget mWidget;

	private boolean mAnimating = false;

	private final long mAnimationDuration;
	private long mAnimationProgress = 0;

	private boolean mAnimationRepeat = true;

	public WidgetAnimation(Widget widget, long duration, boolean animationRepeat) {
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

			double fractionComplete = 
					(double) mAnimationProgress / mAnimationDuration;

			if (fractionComplete >= 1) {
				fractionComplete = (mAnimationRepeat)
						? fractionComplete - 1
						: 1;
			}

			animationCallback(fractionComplete);

			if (fractionComplete >= 1 && !mAnimationRepeat) {
				mAnimating = mAnimationRepeat;
				mAnimationProgress = (mAnimationRepeat) ? 0
						: mAnimationProgress - mAnimationDuration;
				complete();
			}
		}
	}

	public void cancel() {
		// Child classes may provide an implementation
	}

	public void complete() {
		// Child classes may provide an implementation
	}

	abstract public void animationCallback(double fractionComplete);

}
