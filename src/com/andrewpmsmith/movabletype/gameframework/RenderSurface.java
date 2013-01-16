package com.andrewpmsmith.movabletype.gameframework;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Maintains a list of widgets and renders them at up to 60 FPS. Manages
 * touch events, distributing clicks and drags to the appropriate widget.
 * 
 * @author Andrew Smith
 */
public class RenderSurface extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final int FRAMES_RATE = 60;	// frames per second
	private static final int MIN_INTERVAL_BETWEEN_FRAMES = 1000 / FRAMES_RATE; // milliseconds
	
	private int mBackgroundColor = 0xFFFFFFFF;
	
	protected Context mContext;
	
	private DrawThread mDrawThread;
	private long mLastFrameTime;
	
	protected List<Widget> mWidgets;
	
	private Widget mTouchedWidget = null;
	private boolean mDragInProgress = false;
	
	public class DrawThread extends Thread {

		boolean mRunning;
		Canvas mCanvas;
		SurfaceHolder mSurfaceHolder;
		Context mContext;
		RenderSurface mGameView;
		
		public DrawThread(SurfaceHolder surfaceHolder, Context context, RenderSurface gameView) {
			mSurfaceHolder = surfaceHolder;
			mContext = context;
			mRunning = false;
			mGameView = gameView;
		}
		
		void setRunning(boolean run) {
			mRunning = run;
		}
		
		@Override
		public void run() {
			
			super.run();
			
			while(mRunning) {	// Game loop
				
				long currentTime = System.currentTimeMillis();
				long timeSinceLastFrame = currentTime - mLastFrameTime;
				
				while (timeSinceLastFrame < MIN_INTERVAL_BETWEEN_FRAMES) {
					
					try {
						
						long interval = MIN_INTERVAL_BETWEEN_FRAMES - timeSinceLastFrame;
						Thread.sleep(interval);
						timeSinceLastFrame += interval;
						break;
						
					} catch (InterruptedException e) {
						
						// May have been woken up at some other time, so recalculate times
						currentTime = System.currentTimeMillis();
						timeSinceLastFrame = currentTime - mLastFrameTime;
					}
					
				}
				
				mLastFrameTime = currentTime;
				
				mCanvas = mSurfaceHolder.lockCanvas();
				if(mCanvas != null) {
					mGameView.drawFrame(mCanvas, timeSinceLastFrame);
					mSurfaceHolder.unlockCanvasAndPost(mCanvas);
				}
			}
		}
	
	} // class DrawThread
	
	public void drawFrame(Canvas canvas, long timeSinceLastFrame) {
		moveAllWidgets(timeSinceLastFrame);
		drawAllWidgets(canvas);
	}
	
	public RenderSurface(Context context) {
		super(context);
		
		mContext = context;
		
		mWidgets = new LinkedList<Widget>();
		mLastFrameTime = System.currentTimeMillis();
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
	}

	public RenderSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RenderSurface(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// Implementation required by interface SurfaceHolder.Callback
		// Not used.
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		mDrawThread = new DrawThread(arg0, mContext,this);
		mDrawThread.setRunning(true);
		mDrawThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		mDrawThread.setRunning(false);

		boolean retry = true;
		while(retry) {

			try {
				mDrawThread.join();
				retry = false;
			}
			catch(Exception e) {
				Log.v("Exception Occured", e.getMessage());
			}

		}
		
	}
	
	public void setBackgroundColor(int color) {
		mBackgroundColor = color;
	}
	
	public synchronized void moveAllWidgets(long timeSinceLastFrame) {
		for (Widget w : mWidgets) {
			w.move(timeSinceLastFrame);
		}
	}
	
	public synchronized void drawAllWidgets(Canvas canvas) {
		canvas.drawColor(mBackgroundColor);
		for (Widget w : mWidgets) {
			w.preDraw(canvas);
			w.draw(canvas);
			w.postDraw(canvas);
		}
	}
	
	public synchronized void addWidget(Widget w) {
		mWidgets.add(w);
	}
	
	public synchronized void bringWidgetToFront(Widget w) {
		mWidgets.remove(w);
		mWidgets.add(w);
	}
	
	public synchronized void removeWidget(Widget w) {
		mWidgets.remove(w);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent m) {
		
		// Determine if an object has been touched
		// Iterate backwards through mWidgets as higher indexes are closer to the front
		
		if (mDragInProgress) {
			
			if (m.getAction() == MotionEvent.ACTION_UP || m.getAction() == MotionEvent.ACTION_CANCEL) {
				mDragInProgress = false;
			}
			
			return mTouchedWidget.onTouchEvent(m);
			
		} else {
		
			ListIterator<Widget> it = mWidgets.listIterator( mWidgets.size() );
			while (it.hasPrevious()) {
				
				Widget w = it.previous();
				
				if (w.pointInBounds( (int)(m.getX()), (int)(m.getY()) )) {
					
					if ( w.onTouchEvent(m) ) {
						mDragInProgress = true;
						mTouchedWidget = w;
						
						bringWidgetToFront(mTouchedWidget);
						
						return true;
					}
				}
				
			}
			
		}
		
		return false;
	}

}
