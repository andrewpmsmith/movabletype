package com.andrewpmsmith.movabletype.test;

import java.util.ArrayList;
import java.util.List;

import android.test.AndroidTestCase;

import com.andrewpmsmith.movabletype.model.GameModel;
import com.andrewpmsmith.movabletype.model.GameModel.GameResult;
import com.andrewpmsmith.movabletype.model.GameModel.GameState;
import com.andrewpmsmith.movabletype.model.GameModel.LetterState;
import com.andrewpmsmith.movabletype.model.GameModel.TurnResult;

import junit.framework.Assert;

public class GameModelTest extends AndroidTestCase {
	
	private List<Integer> generateIndexListFromWord(String word, char[] grid) {
		
		if (word==null) return null;
		
		List<Integer> ret = new ArrayList<Integer>(word.length());
		
		for (int i=0; i<word.length(); ++i) {
			
			char c = word.charAt(i);
			
			for (int j=0; j<grid.length; ++j) {
				if (c==grid[j]) {
					ret.add(j);
					break;
				}
			}
			
		}
		
		return ret;
		
	}
	
	/*
	 * Play a full game using sample data. Test that results are calculated
	 * correctly. Test that rules are enforced for invalid turns.
	 */
	public void test_fullGame() {
		
		// Initialise a new game with sample data
		
		final char[] testGrid =
				("ABCDE" +
				 "FGHIJ" +
				 "KLMNO" +
				 "PQRST" +
				 "UVWXY").toCharArray();
		
		GameModel gm = createSampleModel(testGrid);
		
		// Define some sample turns
		
		final String[] turns = {
				"THE",		//*p1= 3  p2= 0
				"QUICK",	// p1= 3 *p2= 6 (1 surround)
				"BROWN",	//*p1= 8  p2= 6
				"FOX",		// p1= 7 *p2= 9 (1 steal)
				"JUMPS",	//*p1=12  p2= 7 (2 steals)
				"OVER",		// p1= 8 *p2=13 (complicated)
				"LAY",		//*p1=11  p2=13
				"DOG"		// p1=11 *p2=14
				};
		
		final String[] invalidTurns = {
				null,
				"",		// too short
				"A",	// too short
				"QY",	// Word not in dictionary
				"THE",	// Already played
				"BROW"	// Prefix of previously played word
		};
		
		final int[][] scores = {
				{ 3, 0},
				{ 3, 6},
				{ 8, 6},
				{ 7, 9},
				{12, 7},
				{ 8,13},
				{11,13},
				{11,14}};
		
		
		assertNotNull(gm);
		
		// Initial score is 0,0
		assertEquals(0, gm.getPoints(GameModel.PLAYER1));
		assertEquals(0, gm.getPoints(GameModel.PLAYER2));
		
		
		for (int i = 0; i < turns.length; i++) {
			
			if (i==3) {
				
				// Attempt some invalid moves
				// Play them after the third move so we can test duplicate and
				// prefix checking
				
				for (int j=0; j<invalidTurns.length; ++j) {
					gm.setWord(generateIndexListFromWord(invalidTurns[j], testGrid));
					TurnResult tr = gm.playTurn();
					
					Assert.assertFalse(tr==TurnResult.SUCCESS);
					
				}
			}
			
			gm.setWord(generateIndexListFromWord(turns[i], testGrid));
			TurnResult tr = gm.playTurn();
			
			// Test the word was played successfully
			assertEquals(tr,TurnResult.SUCCESS);
			
			// Test the score was correctly updated
			assertEquals(
					String.format("move %d player 1 score", i),
					scores[i][0], 
					gm.getPoints(GameModel.PLAYER1));
			assertEquals(
					String.format("move %d player 2 score", i),
					scores[i][1], 
					gm.getPoints(GameModel.PLAYER2));
			
			// Test that player turns switch correctly
			GameState gs = gm.getGameState();
			if (gs!=GameState.GAME_OVER) {
			
				if (i%2==0) {
					Assert.assertEquals(gs, GameState.PLAYER2_TURN);
				} else {
					Assert.assertEquals(gs, GameState.PLAYER1_TURN);
				}
			}
			
		}
		
		// Test game is marked as over
		GameState gs = gm.getGameState();
		Assert.assertEquals(GameState.GAME_OVER, gs);
		
		// Test the correct winner was announced
		GameResult gr = gm.getResult();
		Assert.assertEquals(GameResult.PLAYER2_WIN, gr);
		
		
	}

	private GameModel createSampleModel(final char[] testGrid) {
		final LetterState UNPLAYED = LetterState.UNPLAYED;
		final LetterState[] states = {
				UNPLAYED,UNPLAYED,UNPLAYED,UNPLAYED,UNPLAYED,
				UNPLAYED,UNPLAYED,UNPLAYED,UNPLAYED,UNPLAYED,
				UNPLAYED,UNPLAYED,UNPLAYED,UNPLAYED,UNPLAYED,
				UNPLAYED,UNPLAYED,UNPLAYED,UNPLAYED,UNPLAYED,
				UNPLAYED,UNPLAYED,UNPLAYED,UNPLAYED,UNPLAYED,};
		
		GameState gameState = GameState.PLAYER1_TURN;
		int p1Points = 0;
		int p2Points = 0;
		
		
		// Create the GameModel instance
		
		GameModel gm = new GameModel(testGrid,
				states,
				gameState,
				p1Points,
				p2Points,
				getContext());
		return gm;
	}
	
	/* Capturing an opponent's letter by surrounding it must increase
	 * your score and decrease your opponent's. Player 1 submits "MY"
	 * and player 2 submits "ME" to end up with 3 points to 0.
	 */
	public void testCaptureScore() {
		final char[] testGrid =
			("XXXMY" +
			 "XXXXE" +
			 "XXXXX" +
			 "XXXXX" +
			 "XXXXX").toCharArray();

		GameModel gm = createSampleModel(testGrid);
		
		gm.setWord(generateIndexListFromWord("MY", testGrid));
		TurnResult result1 = gm.playTurn();
		gm.setWord(generateIndexListFromWord("ME", testGrid));
		TurnResult result2 = gm.playTurn();
		int points1 = gm.getPoints(GameModel.PLAYER1);
		int points2 = gm.getPoints(GameModel.PLAYER2);
		
		Assert.assertEquals("turn 1 result", TurnResult.SUCCESS, result1);
		Assert.assertEquals("turn 2 result", TurnResult.SUCCESS, result2);
		Assert.assertEquals("player 1 score", 0, points1);
		Assert.assertEquals("player 2 score", 3, points2);
	}
	
	/*
	 * Test that instances can be serialized and restored
	 */
	public void test_serialization() {
		
		// Serialize our game model
		GameModel gm = new GameModel(getContext());
		byte[] s = gm.serialize();
		Assert.assertNotNull(s);
		
		// Delete it
		gm = null;
		System.gc();
		
		// Restore it
		GameModel gm2 = GameModel.deserialize(s, getContext());
		Assert.assertNotNull(gm2);
		
	}
	
	/*
	 * Test that two passes end the game
	 */
	public void test_two_passes() {
		
		// Initialise a new game with sample data
		final char[] testGrid =
				("ABCDE" +
				 "FGHIJ" +
				 "KLMNO" +
				 "PQRST" +
				 "UVWXY").toCharArray();
		GameModel gm = createSampleModel(testGrid);

		GameState state0 = gm.getGameState();

		gm.passTurn();
		GameState state1 = gm.getGameState();
		
		gm.passTurn();
		GameState state2 = gm.getGameState();
		
		GameResult endResult = gm.getResult();
		
		Assert.assertEquals("start state", GameState.PLAYER1_TURN, state0);
		Assert.assertEquals("after pass 1", GameState.PLAYER2_TURN, state1);
		Assert.assertEquals("after pass 2", GameState.GAME_OVER, state2);
		Assert.assertEquals("end result", GameResult.DRAW, endResult);
	}
	
	/*
	 * Test that a valid turn resets the number of passes.
	 */
	public void test_pass_play_pass() {
		
		// Initialise a new game with sample data
		final char[] testGrid =
				("ABCDE" +
				 "FGHIJ" +
				 "KLMNO" +
				 "PQRST" +
				 "UVWXY").toCharArray();
		GameModel gm = createSampleModel(testGrid);

		GameState state0 = gm.getGameState();

		gm.passTurn();
		GameState state1 = gm.getGameState();
		
		gm.setWord(generateIndexListFromWord("GO", testGrid));
		TurnResult result = gm.playTurn();
		GameState state2 = gm.getGameState();
		
		gm.passTurn();
		GameState state3 = gm.getGameState();

		Assert.assertEquals("start state", GameState.PLAYER1_TURN, state0);
		Assert.assertEquals("after pass 1", GameState.PLAYER2_TURN, state1);
		Assert.assertEquals("play result", TurnResult.SUCCESS, result);
		Assert.assertEquals("after play", GameState.PLAYER1_TURN, state2);
		Assert.assertEquals("after pass 2", GameState.PLAYER2_TURN, state3);
	}
	
	/*
	 * Test that an invalid turn does not reset the number of passes.
	 */
	public void test_pass_fail_pass() {
		
		// Initialise a new game with sample data
		final char[] testGrid =
				("ABCDE" +
				 "FGHIJ" +
				 "KLMNO" +
				 "PQRST" +
				 "UVWXY").toCharArray();
		GameModel gm = createSampleModel(testGrid);

		GameState state0 = gm.getGameState();

		gm.passTurn();
		GameState state1 = gm.getGameState();
		
		gm.setWord(generateIndexListFromWord("ZZ", testGrid));
		TurnResult result = gm.playTurn();
		GameState state2 = gm.getGameState();
		
		gm.passTurn();
		GameState state3 = gm.getGameState();

		GameResult endResult = gm.getResult();
		
		Assert.assertEquals("start state", GameState.PLAYER1_TURN, state0);
		Assert.assertEquals("after pass 1", GameState.PLAYER2_TURN, state1);
		Assert.assertEquals(
				"play result", 
				TurnResult.WORD_LESS_THAN_TWO_LETTERS, 
				result);
		Assert.assertEquals("after play", GameState.PLAYER2_TURN, state2);
		Assert.assertEquals("after pass 2", GameState.GAME_OVER, state3);
		Assert.assertEquals("end result", GameResult.DRAW, endResult);
	}
	
	
}
