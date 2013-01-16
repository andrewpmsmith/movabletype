package com.andrewpmsmith.movabletype.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;

import com.andrewpmsmith.movabletype.R;
import com.andrewpmsmith.movabletype.gameframework.ExpandContractAnimation;
import com.andrewpmsmith.movabletype.gameframework.RenderSurface;
import com.andrewpmsmith.movabletype.gameframework.RotationAnimation;
import com.andrewpmsmith.movabletype.gameframework.TextWidget;
import com.andrewpmsmith.movabletype.gameframework.TranslationAnimation;
import com.andrewpmsmith.movabletype.gameframework.Widget;
import com.andrewpmsmith.movabletype.gameframework.WidgetClickListener;
import com.andrewpmsmith.movabletype.gameframework.WidgetDragListener;
import com.andrewpmsmith.movabletype.model.GameModel;

/**
 * Controls the rendering of the board, including the letter grid, the word,
 * the scores, and the "clear" and "submit" buttons.
 * 
 * The state of the game is managed by a GameModel object passed into the Board's 
 * constructor. The Board will render the game contained in the GameModel. It 
 * will manage the user interaction, passing turn details to the GameModel.
 *
 * @author Andrew Smith
 */
public class Board extends RenderSurface implements WidgetClickListener,
		WidgetDragListener {

	private static final double TILE_IN_WORD_SCALE_FACTOR = 0.6;
	private static final float MIN_DRAG_ROTATION = -8f; // degrees
	private static final float MAX_DRAG_ROTATION = 8f;
	private static final int ANIMATION_DURATION = 400; // milliseconds
	private static final float WOBBLE_ANGLE = 18f;
	private static final int TILE_SHADOW_RADIUS = 10;
	private static final int COLOR_INVISIBLE = 0x00000000;
	private static final int PLACEHOLDER_UNUSED = -1;
	private final static double MIN_HEIGHT_TO_WIDTH = 1.4;

	private GameModel mGameModel;

	private Tile[] mTiles;
	private List<Tile> mWord = new LinkedList<Tile>();
	private List<Tile> mLastPlayedWord;
	private Tile mPlaceHolderTile;
	private int mPlaceHolderIndex = PLACEHOLDER_UNUSED;

	private int mTileWidthInWord;

	private int mGridTop;
	private int mWordTop;
	private int mAddToWordThreshold;

	private static Random mRand = new Random();

	private enum DragAnimation {
		NONE, EXPANDING, CONTRACTING
	};

	private DragAnimation mDragAnimation;
	private ExpandContractAnimation mExpandContractAnimation;

	int mEvenTileColor;
	int mOddTileColor;
	int mPlayer1Color;
	int mPlayer2Color;
	int mPlayer1SurroundedColor;
	int mPlayer2SurroundedColor;
	int mTextColor;
	int mBackgroundColor;
	int mDropShadowColor;

	TextWidget mClearButton;
	TextWidget mPlayButton;
	TextWidget mPlayer1Score;
	TextWidget mPlayer2Score;

	public Board(Context context, GameModel gameModel) {
		super(context);
		mGameModel = gameModel;
		init();
	}

	public Board(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public Board(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {

		Resources res = getResources();
		mEvenTileColor = res.getColor(R.color.tile_even);
		mOddTileColor = res.getColor(R.color.tile_odd);
		mPlayer1Color = res.getColor(R.color.player1);
		mPlayer2Color = res.getColor(R.color.player2);
		mPlayer1SurroundedColor = res.getColor(R.color.player1_surrounded);
		mPlayer2SurroundedColor = res.getColor(R.color.player2_surrounded);
		mTextColor = res.getColor(R.color.tile_text);
		mBackgroundColor = res.getColor(R.color.background);
		mDropShadowColor = res.getColor(R.color.tile_dropshadow);

		final String clearButtonText = res.getString(R.string.clear_button);
		final String submitButtonText = res.getString(R.string.submit_button);

		setBackgroundColor(mBackgroundColor);

		mTiles = new Tile[GameModel.GRID_ITEMS];

		for (int i = 0; i < GameModel.GRID_ITEMS; ++i) {

			int tileColor = getColor(i);

			String letter = String.valueOf(mGameModel.getLetter(i));

			mTiles[i] = new Tile(i, tileColor, letter, mTextColor);
			mTiles[i].setClickListener(this);
			mTiles[i].setDragListener(this);
			addWidget(mTiles[i]);
		}

		mPlaceHolderTile = new Tile(-1, COLOR_INVISIBLE, null, COLOR_INVISIBLE);
		addWidget(mPlaceHolderTile);

		mClearButton = new TextWidget(COLOR_INVISIBLE, clearButtonText,
				Color.BLACK);
		mClearButton.setClickListener(new WidgetClickListener() {

			@Override
			public void onClick(Widget w) {
				returnAllTilesToGrid();
				presentWord();
			}

		});
		addWidget(mClearButton);

		mPlayButton = new TextWidget(COLOR_INVISIBLE, submitButtonText,
				Color.BLACK);
		mPlayButton.setClickListener(new WidgetClickListener() {

			@Override
			public void onClick(Widget widget) {
				submitWord();
			}

		});
		mPlayButton.setColor(mPlayer1Color);
		addWidget(mPlayButton);

		String player1Score = String.valueOf(mGameModel
				.getPoints(GameModel.PLAYER1));
		String player2Score = String.valueOf(mGameModel
				.getPoints(GameModel.PLAYER2));

		mPlayer1Score = new TextWidget(COLOR_INVISIBLE, player1Score,
				mPlayer1SurroundedColor);
		mPlayer2Score = new TextWidget(COLOR_INVISIBLE, player2Score,
				mPlayer2SurroundedColor);
		addWidget(mPlayer1Score);
		addWidget(mPlayer2Score);

	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		// Enforce a minimum height to width ratio for the board

		int measuredWidth = getDefaultSize(getSuggestedMinimumWidth(),
				widthMeasureSpec);
		int measuredHeight = getDefaultSize(getSuggestedMinimumHeight(),
				heightMeasureSpec);

		final double measuredHeightToWidthRatio = (double) measuredHeight
				/ measuredWidth;

		if (measuredHeightToWidthRatio < MIN_HEIGHT_TO_WIDTH) {
			setMeasuredDimension((int) (measuredHeight / MIN_HEIGHT_TO_WIDTH),
					measuredHeight);
		} else {
			setMeasuredDimension(measuredWidth, measuredHeight);
		}

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		layoutBoard();
	}

	private void layoutBoard() {

		int tileWidth = getWidth() / GameModel.GRID_COLUMNS;
		int tileHeight = tileWidth;

		mGridTop = getHeight() - tileHeight * GameModel.GRID_ROWS;

		mWordTop = (int) ((mGridTop / 2.0) - tileHeight / 2.0);
		mAddToWordThreshold = (int) ((mGridTop / 2.0) + tileHeight * 0.25);

		Tile.widthInGrid = tileWidth;

		mTileWidthInWord = (int) (tileWidth * TILE_IN_WORD_SCALE_FACTOR);
		Tile.widthInWord = mTileWidthInWord;

		for (int i = 0; i < GameModel.GRID_ITEMS; ++i) {

			int x = tileWidth * (i % GameModel.GRID_COLUMNS);
			int y = mGridTop + tileWidth * (i / GameModel.GRID_COLUMNS);
			mTiles[i].applyLayout(x, y, tileWidth, tileHeight);

		}

		mPlaceHolderTile.applyLayout(0, mWordTop, Tile.widthInWord, tileHeight);

		mClearButton.applyLayout(0, 0, mTileWidthInWord, mTileWidthInWord);
		mPlayButton.applyLayout(getWidth() - mTileWidthInWord, 0,
				mTileWidthInWord, mTileWidthInWord);
		mPlayer1Score.applyLayout(getWidth() / 2 - mTileWidthInWord, 0,
				mTileWidthInWord, mTileWidthInWord);
		mPlayer2Score.applyLayout(getWidth() / 2, 0, mTileWidthInWord,
				mTileWidthInWord);

	}

	private int getColor(int tileIndex) {
		GameModel.LetterState state = mGameModel.getLetterState(tileIndex);
		switch (state) {
		case PLAYER1_OWNED:
			return mPlayer1Color;
		case PLAYER1_SURROUNDED:
			return mPlayer1SurroundedColor;
		case PLAYER2_OWNED:
			return mPlayer2Color;
		case PLAYER2_SURROUNDED:
			return mPlayer2SurroundedColor;
		case UNPLAYED:
			/* falls through */
		default:
			return (tileIndex % 2 == 0) ? mEvenTileColor : mOddTileColor;
		}
	}

	private void updateGrid() {
		for (int i = 0; i < GameModel.GRID_ITEMS; ++i) {
			int tileColor = getColor(i);
			mTiles[i].setColor(tileColor);
		}
	}

	@Override
	public void onClick(Widget w) {

		Tile t = (Tile) w;

		if (w.getY() == mWordTop) {
			removeTileFromWord(t);
			animateToPosition(t, t.mPositionInGrid_x, t.mPositionInGrid_y,
					Tile.widthInGrid);

		} else {
			addTileToWord(t);
		}
	}

	@Override
	public void onDragStart(Widget w) {

		mDragAnimation = DragAnimation.NONE;
		mPlaceHolderIndex = PLACEHOLDER_UNUSED;

		Tile t = (Tile) w;

		removeTileFromWord(t);

		t.setShadow(TILE_SHADOW_RADIUS, mDropShadowColor);

		float angle = (mRand.nextFloat() * (Math.abs(MIN_DRAG_ROTATION) + Math
				.abs(MAX_DRAG_ROTATION))) + MIN_DRAG_ROTATION;
		t.cancelAllAnimations();
		t.addAnimation(new RotationAnimation(t, ANIMATION_DURATION, angle));
	}

	@Override
	public void onDragEnd(Widget w) {

		Tile t = (Tile) w;

		t.setShadow(0, 0);

		t.cancelAllAnimations();
		t.addAnimation(new RotationAnimation(t, ANIMATION_DURATION, 0));

		if (w.getY() <= mAddToWordThreshold
				&& mPlaceHolderIndex != PLACEHOLDER_UNUSED) {
			mWord.add(mPlaceHolderIndex, t);
		} else {
			animateToPosition(t, t.mPositionInGrid_x, t.mPositionInGrid_y,
					Tile.widthInGrid);
		}

		// remove the place holder tile
		mWord.remove(mPlaceHolderTile);
		mPlaceHolderIndex = PLACEHOLDER_UNUSED;
		presentWord();

	}

	@Override
	public void onDrag(Widget w, int x, int y) {

		Tile t = (Tile) w;

		// Reposition tile according to drag coordinates
		t.setX(x);
		t.setY(y);

		// When dragged above the grid, resize the tile to fit in the word
		if (y < mGridTop && mDragAnimation != DragAnimation.CONTRACTING) {

			mDragAnimation = DragAnimation.CONTRACTING;
			expandContractTile(t, Tile.widthInWord);

		} else if (y >= mGridTop && mDragAnimation != DragAnimation.EXPANDING) {

			mDragAnimation = DragAnimation.EXPANDING;
			expandContractTile(t, Tile.widthInGrid);

		}

		// When dragged above the grid, make space in the word for the tile
		if (y < mGridTop) {

			int word_begin = (int) (getWidth() / 2 - (Tile.widthInWord / 2.0)
					* mWord.size());
			int word_end = word_begin + Tile.widthInWord * mWord.size();

			int index;
			if (x < word_begin) {
				index = 0;
			} else if (x > word_end) {
				index = mWord.size();
			} else {
				index = (int) (x - (word_begin - Tile.widthInWord / 2.0))
						/ Tile.widthInWord;
			}

			if (index != mPlaceHolderIndex) {
				mPlaceHolderIndex = index;
				mWord.remove(mPlaceHolderTile);

				mWord.add(Math.min(index, mWord.size()), mPlaceHolderTile);
				presentWord();
			}
		}
	}

	private void expandContractTile(Tile t, int newWidth) {
		if (mExpandContractAnimation != null)
			t.cancelAnimation(mExpandContractAnimation);
		mExpandContractAnimation = new ExpandContractAnimation(t,
				ANIMATION_DURATION, newWidth);
		t.addAnimation(mExpandContractAnimation);
	}

	private void animateToPosition(Tile t, int x, int y, int width) {
		t.cancelAllAnimations();
		t.addAnimation(new RotationAnimation(t, ANIMATION_DURATION, 0));
		t.addAnimation(new TranslationAnimation(t, ANIMATION_DURATION, x, y));
		t.addAnimation(new ExpandContractAnimation(t, ANIMATION_DURATION, width));
	}

	private void addTileToWord(Tile t) {
		mWord.remove(t);
		mWord.add(t);
		presentWord();
	}

	private void removeTileFromWord(Tile t) {
		mWord.remove(t);
		presentWord();
	}

	private void returnAllTilesToGrid() {

		ListIterator<Tile> it = mWord.listIterator();
		while (it.hasNext()) {
			Tile t = it.next();
			it.remove();
			animateToPosition(t, t.mPositionInGrid_x, t.mPositionInGrid_y,
					Tile.widthInGrid);
		}

	}

	private void resizeTilesToFitWord() {
		// Shrink or expand the tile size if required
		Tile.widthInWord = mTileWidthInWord;
		while (mWord.size() * Tile.widthInWord > getWidth()) {
			Tile.widthInWord *= 0.8;
		}
	}

	private void presentWord() {

		resizeTilesToFitWord();

		int offset = (int) (getWidth() / 2 - (Tile.widthInWord / 2.0)
				* mWord.size());

		List<Integer> letters = new LinkedList<Integer>();
		for (int i = 0; i < mWord.size(); ++i) {

			Tile t = mWord.get(i);
			int x = offset + Tile.widthInWord * i;
			int y = mWordTop;

			animateToPosition(t, x, y, Tile.widthInWord);

			if (t.getIndex() >= 0) {
				letters.add(t.getIndex());
			}
		}

		mGameModel.setWord(letters);

		mPlayer1Score.setText(String.valueOf(mGameModel
				.getPoints(GameModel.PLAYER1)));
		mPlayer2Score.setText(String.valueOf(mGameModel
				.getPoints(GameModel.PLAYER2)));

	}

	private void shakeTiles(List<Tile> tiles) {
		for (Tile t : tiles) {
			t.setRotation(WOBBLE_ANGLE);
			t.addAnimation(new TileWobbleAnimation(t, ANIMATION_DURATION * 4, 0));
		}
	}

	private void submitWord() {

		GameModel.TurnResult turnResult = mGameModel.playTurn();

		final Resources res = getResources();
		final String dismiss = res.getString(R.string.dismiss_message);

		if (turnResult == GameModel.TurnResult.SUCCESS) {

			mLastPlayedWord = new LinkedList<Tile>(mWord);

			returnAllTilesToGrid();
			updateGrid();

			GameModel.GameState state = mGameModel.getGameState();

			if (state != GameModel.GameState.GAME_OVER) {

				String message = res.getString(R.string.turn_played);
				message = String
						.format(message,
								(state == GameModel.GameState.PLAYER1_TURN) ? "1"
										: "2");

				int playerColor = state == GameModel.GameState.PLAYER1_TURN ? mPlayer1Color
						: mPlayer2Color;
				mPlayButton.setColor(playerColor);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getContext());
				builder.setMessage(message)
						.setPositiveButton(dismiss,
								new AlertDialog.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										shakeTiles(mLastPlayedWord);

									}

								}).show();

			} else {
				GameModel.GameResult result = mGameModel.getResult();

				String message;
				if (result == GameModel.GameResult.DRAW) {
					message = res.getString(R.string.game_over_draw);
				} else {
					message = res.getString(R.string.game_over);
					message = String.format(message,
							(result == GameModel.GameResult.PLAYER1_WIN) ? "1"
									: "2");
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getContext());
				builder.setMessage(message)
						.setNeutralButton(dismiss,
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										((Activity) getContext()).finish();
									}
								}).show();

			}

		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

			String word = mGameModel.getWord();
			String message;

			switch (turnResult) {
			case WORD_ALREADY_PLAYED:
				message = res.getString(R.string.already_played_error);
				message = String.format(message, word);
				break;
			case WORD_IS_PREFIX_OF_PREVIOUS_TURN:
				message = res.getString(R.string.prefix_of_previous_turn_error);
				message = String.format(message, word);
				break;
			case WORD_LESS_THAN_TWO_LETTERS:
				message = getResources().getString(
						R.string.word_too_short_error);
				break;
			case WORD_NOT_IN_DICTIONARY:
				message = res.getString(R.string.not_in_dictionary_error);
				message = String.format(message, word);
				break;
			default:
				message = res.getString(R.string.word_error);
			}

			builder.setMessage(message).setPositiveButton(dismiss, null).show();
		}

	}
}
