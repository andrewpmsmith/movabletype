package com.andrewpmsmith.movabletype.test;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import com.andrewpmsmith.movabletype.model.WordList;

public class WordListTest extends AndroidTestCase {
	
	public void test() {
		WordList wl = new WordList(mContext);
		Assert.assertNotNull(wl);
		
		// Delete the wordlist and recreate
		wl.reCreate();
		
		// Check a range of words have been added
		Assert.assertTrue(wl.wordInDictionary("AARDVARK"));
		Assert.assertTrue(wl.wordInDictionary("JOKE"));
		Assert.assertTrue(wl.wordInDictionary("ROOT"));
		Assert.assertTrue(wl.wordInDictionary("ZOO"));
		
		// Fail cases
		
		// Lowercase
		Assert.assertFalse(wl.wordInDictionary("aardvark"));
		Assert.assertFalse(wl.wordInDictionary("zoo"));
		
		// Too short
		Assert.assertFalse(wl.wordInDictionary("A"));
		
		// Invalid word
		Assert.assertFalse(wl.wordInDictionary("ABCD"));
		
		// Invalid characters
		Assert.assertFalse(wl.wordInDictionary("EX-WIFE"));
		Assert.assertFalse(wl.wordInDictionary("LET'S"));
		
		// Other edge cases
		Assert.assertFalse(wl.wordInDictionary(null));
		Assert.assertFalse(wl.wordInDictionary(""));
		
		// SQL injection attack
		Assert.assertFalse(wl.wordInDictionary("; DROP TABLE words;"));
	}

}
