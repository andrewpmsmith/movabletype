package com.andrewpmsmith.movabletype.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.andrewpmsmith.movabletype.R;

public class WordList extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "wordlist";
	private static final String TABLE_WORDS = "words";
	private static final String KEY_WORD = "word";

	protected Context mContext;

	public WordList(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		addWordsTable(db);
		addWords(db);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
		onCreate(db);

	}
	
	/*
	 * Used for testing only
	 */
	public void reCreate() {
		SQLiteDatabase db = getReadableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
		onCreate(db);
	}

	private void addWordsTable(SQLiteDatabase db) {

		String CREATE_WORD_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
				+ KEY_WORD + " TEXT PRIMARY KEY" + ")";
		db.execSQL(CREATE_WORD_TABLE);

	}

	/*
	 * Prepare a word for storage in the DB. Reject words less than two letters
	 * in length. Reject words with non-alphabetical characters (e.g.
	 * apostrophes/hyphens). Convert words to upper case.
	 */
	private String normaliseWord(String word) {

		if (word == null)
			return null;
		if (word.length() < 2)
			return null;
		word = word.toUpperCase(Locale.getDefault());
		if (!word.matches("^[A-Z]+$"))
			return null;
		return word;

	}

	/*
	 * Add the word list to the DB. This may take several minutes.
	 */
	public void addWords(SQLiteDatabase db) {

		InputStream is = mContext.getResources()
				.openRawResource(R.raw.wordlist);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		db.beginTransaction();

		String line;
		try {
			while ((line = reader.readLine()) != null) {

				String word = normaliseWord(line);
				if (word == null)
					continue;

				ContentValues values = new ContentValues();
				values.put(KEY_WORD, word);
				try {
					db.insert(TABLE_WORDS, null, values);
				} catch (SQLiteConstraintException e) {
					// Duplicate word in file, or from prev attempt to populate
					// the word list. Consume exception and keep going
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.setTransactionSuccessful();
		db.endTransaction();

	}

	public boolean wordInDictionary(String word) {
		
		if (word==null || !word.matches("^[A-Z]+$")) {
			return false;
		}
		
		SQLiteDatabase db = getReadableDatabase();

		Cursor c = db.rawQuery("SELECT * FROM " + TABLE_WORDS + " WHERE "
				+ KEY_WORD + "= '" + word + "'", null);
		boolean ret = (c != null && c.getCount() > 0);

		c.close();
		db.close();

		return ret;
	}

}
