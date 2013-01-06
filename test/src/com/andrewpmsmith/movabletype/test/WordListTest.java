package com.andrewpmsmith.movabletype.test;

import android.test.AndroidTestCase;

import com.andrewpmsmith.movabletype.WordList;

import junit.framework.Assert;
import junit.framework.TestCase;

public class WordListTest extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void test() {
		WordList wl = new WordList(mContext);
		Assert.assertNotNull(wl);
		
		Assert.assertTrue(wl.wordInDictionary("THE"));
		Assert.assertFalse(wl.wordInDictionary("ABCD"));
	}

}
