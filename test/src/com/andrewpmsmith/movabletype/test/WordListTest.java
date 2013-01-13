package com.andrewpmsmith.movabletype.test;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import com.andrewpmsmith.movabletype.model.WordList;

public class WordListTest extends AndroidTestCase {
	
	public void test() {
		WordList wl = new WordList(mContext);
		Assert.assertNotNull(wl);
		
		Assert.assertTrue(wl.wordInDictionary("THE"));
		Assert.assertFalse(wl.wordInDictionary("ABCD"));
	}

}
