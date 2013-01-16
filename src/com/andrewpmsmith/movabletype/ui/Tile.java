package com.andrewpmsmith.movabletype.ui;

import com.andrewpmsmith.movabletype.gameframework.TextWidget;
import com.andrewpmsmith.movabletype.gameframework.WidgetClickListener;

import android.util.SparseIntArray;

public class Tile extends TextWidget {
	
	static public int widthInGrid;
	static public int widthInWord;
	
	public int mPositionInGrid_x, mPositionInGrid_y;
	
	private final int mIndex;
	
	private static SparseIntArray mTextSizeCalculationHash = new SparseIntArray();
	
	public static Tile tileFactory(int index, String letter,
			int color, int textColor, int x, int y, int width, int height,
			WidgetClickListener clickListener) {
		Tile tile = new Tile(index, color, letter, textColor);
		tile.setClickListener(clickListener);
		tile.applyLayout(x, y, width, height);
		return tile;
	}
	
	public Tile(int index, int color, String letter, int textColor) {
		super(color, letter, textColor);
		mIndex = index;
	}
	
	public int getIndex() {
		return mIndex;
	}
	
	@Override
	public void applyLayout(int x, int y, int width, int height) {
		super.applyLayout(x, y, width, height);
		
		mPositionInGrid_x = x;
		mPositionInGrid_y = y;
	}
	
	/* 
	 * Base calculations on the widest chat 'W', rather than the actual value 
	 * for consistency of scalling across all widgets.
	 * Results are hashed to avoid many per widget calculations
	 */
	@Override
	protected int calculateTextSize(String text, int width) {
		
		int ret;
		
		final int NOT_IN_HASH = -1;
		int hashedValue = mTextSizeCalculationHash.get(width, NOT_IN_HASH);
		
		if (hashedValue>=0) {
			ret = hashedValue;
		} else {
		
			ret = super.calculateTextSize("W", width);
			
			mTextSizeCalculationHash.put(width, ret);
		}
		
		return ret;
	}

	
}