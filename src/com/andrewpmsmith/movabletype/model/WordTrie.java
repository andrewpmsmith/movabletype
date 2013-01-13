package com.andrewpmsmith.movabletype.model;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.HashMap;

public class WordTrie implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String UPPERCASE_A_TO_Z_REGEX = "^[A-Z]+$";
	
	public static final int INVALID_WORD_ERROR = -1;

	private final Node mRoot;
	private int mWordIndex = 0;

	@SuppressLint("UseSparseArrays")
	// Use HashMap because SparseIntArray is not serializable
	private class Node implements Serializable {

		private static final long serialVersionUID = 1L;

		private static final int FIRST_CHAR = 'A';

		public int mIndex = -1;
		private HashMap<Integer, Node> mLetters;

		private int convertIndex(char c) {
			return c - FIRST_CHAR;
		}

		public Node get(char c) {

			if (mLetters == null)
				return null;

			int index = convertIndex(c);
			return (index >= 0) ? mLetters.get(index) : null;
		}

		public int add(char c, Node n) {

			if (mLetters == null)
				mLetters = new HashMap<Integer, Node>();

			int index = convertIndex(c);
			if (index >= 0) {
				mLetters.put(index, n);
				return index;
			} else {
				return INVALID_WORD_ERROR;
			}
		}

	} // class Node

	public WordTrie() {
		mRoot = new Node();
	}

	public static WordTrie deserialize(byte[] stream) {
		return (WordTrie) Serializer.deserialize(stream);
	}

	public byte[] serialize() {
		return Serializer.serialize(this);
	}

	public int add(String word) {

		if (word == null) {
			return INVALID_WORD_ERROR;
		}
		
		if (!word.matches(UPPERCASE_A_TO_Z_REGEX))
			return INVALID_WORD_ERROR;

		Node currentNode = mRoot;

		for (int i = 0; i < word.length(); ++i) {

			char c = word.charAt(i);
			Node next = currentNode.get(c);

			if (next == null) {
				next = new Node();
				currentNode.add(c, next);
			}

			currentNode = next;
		}

		currentNode.mIndex = mWordIndex++;

		return currentNode.mIndex;
	}

	public boolean contains(String word) {

		if (word == null) {
			return false;
		}

		Node currentNode = mRoot;

		for (int i = 0; i < word.length(); ++i) {

			currentNode = currentNode.get(word.charAt(i));

			if (currentNode == null) {
				return false;
			}

		}

		return currentNode.mIndex != -1;

	}

	public boolean containsPrefix(String prefix) {

		if (prefix == null) {
			return false;
		}

		Node currentNode = mRoot;

		for (int i = 0; i < prefix.length(); ++i) {

			currentNode = currentNode.get(prefix.charAt(i));

			if (currentNode == null) {
				return false;
			}

		}

		return true;

	}

}
