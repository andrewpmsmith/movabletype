package com.andrewpmsmith.movabletype.model;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages the creation and the access to the database that stores the saved
 * games. The games are stored as serialized instances of GameState.
 * 
 * @author Andrew Smith
 */
public class GameDataBase extends SQLiteOpenHelper {

	public static final int NULL_PARAMETER_ERROR = -1;

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "gamedatabase";
	private static final String TABLE_GAMES = "savedgames";
	public static final String KEY_ID = "_id";
	private static final String KEY_GAME_BLOB = "game_blob";

	protected Context mContext;

	public GameDataBase(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		addSavedGameTable(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
		onCreate(db);

	}

	private void addSavedGameTable(SQLiteDatabase db) {

		String CREATE_WORD_TABLE = "CREATE TABLE " + TABLE_GAMES + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_GAME_BLOB + " BLOB" + ")";
		db.execSQL(CREATE_WORD_TABLE);

	}

	public long addGame(GameModel game) {

		if (game == null)
			return NULL_PARAMETER_ERROR;

		byte[] blob = game.serialize();

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_GAME_BLOB, blob);

		long key = db.insert(TABLE_GAMES, null, values);
		db.close();

		return key;

	}

	public GameModel getGame(long key) {

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_GAMES, new String[] { KEY_GAME_BLOB },
				KEY_ID + "=" + key, null, null, null, null, null);

		GameModel ret = null;

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			byte[] blob = cursor.getBlob(cursor.getColumnIndex(KEY_GAME_BLOB));
			ret = GameModel.deserialize(blob, mContext);
		}

		cursor.close();
		db.close();

		return ret;

	}

	public List<Long> getAllGameKeys() {

		List<Long> keys = new LinkedList<Long>();

		String selectQuery = "SELECT  * FROM " + TABLE_GAMES;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				long key = cursor.getLong(cursor.getColumnIndex(KEY_ID));
				keys.add(key);
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();

		return keys;

	}

	public int updateGame(long key, GameModel game) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_GAME_BLOB, game.serialize());

		int update = db.update(TABLE_GAMES, values, KEY_ID + "=" + key, null);

		db.close();

		return update;
	}

	public void deleteGame(long key) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_GAMES, KEY_ID + " =" + key, null);
		db.close();
	}

	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
		onCreate(db);

		db.close();
	}

}