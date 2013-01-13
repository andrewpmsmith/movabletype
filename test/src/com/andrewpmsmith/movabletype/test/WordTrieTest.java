package com.andrewpmsmith.movabletype.test;

import com.andrewpmsmith.movabletype.model.WordTrie;

import junit.framework.Assert;
import junit.framework.TestCase;

public class WordTrieTest extends TestCase {
	
	/*
	 * Test class's functionality and error handling
	 */
	public void test_functional() {
		
		WordTrie wt = new WordTrie();
		
		int r;
		boolean t;
		
		// Newly constructed trie should not contain any words
		
		t = wt.contains(null);
		Assert.assertFalse(t);
		
		t = wt.contains("");
		Assert.assertFalse(t);
		
		t = wt.contains("word");
		Assert.assertFalse(t);
		
		t = wt.contains("1@��$%^&*");
		Assert.assertFalse(t);
		
		
		// Test adding words to trie
		
		r = wt.add(null);
		Assert.assertFalse(r>0);
		t = wt.contains(null);
		Assert.assertFalse(t);
		
		r = wt.add("");
		Assert.assertFalse(r>0);
		t = wt.contains("");
		Assert.assertFalse(t);
		
		r = wt.add("1@��$%^&* /~");
		Assert.assertFalse(r>0);
		t = wt.contains("1@��$%^&* /~");
		Assert.assertFalse(t);
		
		r = wt.add("lower");
		Assert.assertFalse(r>0);
		t = wt.contains("lower");
		Assert.assertFalse(t);
		
		r = wt.add("UPPERlower");
		Assert.assertFalse(r>0);
		t = wt.contains("UPPERlower");
		Assert.assertFalse(t);
		
		final String longString =
				"LONGLONGLONGLONGLONGLONGLONGLONGLONGLONGLONGLONGLONGLONGLONG";
		
		r = wt.add(longString);
		Assert.assertTrue(r>=0);
		t = wt.contains(longString);
		Assert.assertTrue(t);
		
		
		// Test adding similar words
		
		r = wt.add("WORD");
		Assert.assertTrue(r>=0);
		t = wt.contains("WORD");
		Assert.assertTrue(t);
		
		r = wt.add("PREFIXWORD");
		Assert.assertTrue(r>=0);
		r = wt.add("WORDSUFFIX");
		Assert.assertTrue(r>=0);
		r = wt.add("PREFIXWORDSUFFIX");
		Assert.assertTrue(r>=0);
		r = wt.add("DROW");
		Assert.assertTrue(r>=0);
		
		t = wt.contains("WORD")
				&& wt.contains("PREFIXWORD")
				&& wt.contains("PREFIXWORD")
				&& wt.contains("PREFIXWORDSUFFIX")
				&& wt.contains("DROW");
		Assert.assertTrue(t);
		
		
		// Add duplicate entry
		
		r = wt.add("WORD");
		Assert.assertTrue(r>=0);
		t = wt.contains("WORD");
		Assert.assertTrue(t);
		
		
		// Prefix checking
		
		wt.add("ABCD");
		t = wt.containsPrefix("")
				&& wt.containsPrefix("A")
				&& wt.containsPrefix("AB")
				&& wt.containsPrefix("ABC")
				&& wt.containsPrefix("ABCD");
		Assert.assertTrue(t);
		
		t = wt.containsPrefix("B")
				|| wt.containsPrefix("BCD")
				|| wt.containsPrefix("ABD")
				|| wt.containsPrefix("ABCDE");
		Assert.assertFalse(t);
		
		t = wt.containsPrefix(null);
		Assert.assertFalse(t);
		
	}
	
	
	/**
	 * Returns a unique string of CAPITALS
	 */
	private String characterSequence(int n) {
		StringBuilder sb = new StringBuilder();
		
		while (n>26) {
			sb.append((char)('A' + n%26));
			n -= 26;
		}
		
		return sb.toString();
	}
	
	
	/**
	 * Test that functionality is maintained under high load.
	 */
	public void test_load() {
		
		WordTrie wt = new WordTrie();
		
		final int TEST_LIMIT = 5000;
		
		for (int i=0; i<TEST_LIMIT; ++i) {
			wt.add("WORD" + characterSequence(i));
		}
		
		for (int i=0; i<TEST_LIMIT; ++i) {
			boolean t =
					wt.contains("WORD" + characterSequence(i))
					&& wt.containsPrefix("WORD" + characterSequence(i));
			
			Assert.assertTrue(t);
			
		}
		
	}
	
	public void test_serialization() {
		WordTrie wt = new WordTrie();
		wt.add("WORD");
		
		byte[] s = wt.serialize();
		wt = null;
		System.gc();
		
		WordTrie n = WordTrie.deserialize(s);
		
		Assert.assertNotNull(n);
		Assert.assertTrue(n.contains("WORD"));
		
	}

}
