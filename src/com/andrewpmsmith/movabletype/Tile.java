package com.andrewpmsmith.movabletype;

import android.util.SparseIntArray;
import GraphicsFramework.TextWidget;
import GraphicsFramework.WidgetClickObserver;

public class Tile extends TextWidget {
	
	static public int widthInGrid;
	static public int widthInWord;
	
	public int mPositionInGrid_x, mPositionInGrid_y;
	
	private final int mIndex;
	
	private static SparseIntArray mTextSizeCalculationHash = new SparseIntArray();
	
	public static Tile tileFactory(int index, String letter,
			int color, int textColor, int x, int y, int width, int height,
			WidgetClickObserver clickObserver) {
		Tile tile = new Tile(index, color, letter, textColor);
		tile.setClickObserver(clickObserver);
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
	
	/* Calculate the text size relative to the width of the widget.
	 * Calculate based on the letter "W" (the widest char) so other letters are
	 * scaled evenly.Results are hashed to avoid many per widget calculations */
	@Override
	protected int calculateTextSize(String text, int width) {
		
		int ret;
		
		int hashedValue = mTextSizeCalculationHash.get(width, -1);
		
		if (hashedValue>=0) {
			ret = hashedValue;
		} else {
		
			ret = super.calculateTextSize("W", width);
			
			mTextSizeCalculationHash.put(width, ret);
		}
		
		return ret;
	}

	
}