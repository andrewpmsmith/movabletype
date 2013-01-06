package GraphicsFramework;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class TextWidget extends Widget {
	
	private static final int TEXT_SIZE_DIVISOR = 2;
	
	private Paint mTextPaint;
	protected Rect mTextBounds;
	
	protected Paint mMeasure = new Paint();
	
	private String mText;
	
	public static TextWidget tileFactory(String text, int color, int textColor, 
			int x, int y, int width, int height, 
			WidgetClickObserver clickObserver) {
		TextWidget w = new TextWidget(color, text, textColor);
		w.setClickObserver(clickObserver);
		w.applyLayout(x, y, width, height);
		return w;
	}
	
	public TextWidget(int color, String letter, int textColor) {
		super(color);
		// TODO Auto-generated constructor stub
		
		mText = letter;
		
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		mTextPaint.setColor(textColor);
		mTextPaint.setStyle(Style.FILL);
		
		mTextBounds = new Rect();
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		if (mText!=null) {
			mTextPaint.setTextSize( calculateTextSize(mText, mWidth) );
			mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBounds);
			
			int textX = mX - mTextBounds.left + (mWidth - mTextBounds.width())/2;
			int textY = mY + mHeight - (mHeight - mTextBounds.height())/2;
			
			canvas.drawText(mText, textX, textY, mTextPaint);
		}
	}
	
	/* Calculate the text size relative to the width of the widget.
	 * Calculate based on the letter "W" (the widest char) so other letters are scaled evenly.
	 * Results are hashed to avoid per widget calculations */
	protected int calculateTextSize(String text, int width) {
		
		int textSize = mHeight/TEXT_SIZE_DIVISOR;
		
		mMeasure.setTextSize(textSize);
		mMeasure.getTextBounds(text, 0, text.length(), mTextBounds);
		
		while (mTextBounds.width()>width) {
			textSize *=0.8;
			mMeasure.setTextSize( textSize );
			mMeasure.getTextBounds(text, 0, text.length(), mTextBounds);
		}
			
		return textSize;
	}
	
	public void setText(String text) {
		mText = text;
	}

}
