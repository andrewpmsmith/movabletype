package com.andrewpmsmith.movabletype;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;

public class GameActivity extends Activity {
	
	public final static String EXTRA_GAME_ID = "gameId";
	
	Board mBoard;
	GameModel mGameModel;
	
	long mSavedGameId;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game);
                
        // Ensure the database has been initialised
        WordList db = new WordList(this);
        
        Intent intent = getIntent();
        
        mSavedGameId = intent.getLongExtra(EXTRA_GAME_ID, -1);
        
        if (mSavedGameId>=0) {
        	GameDataBase gdb = new GameDataBase(this);
        	mGameModel = gdb.getGame(mSavedGameId);
        } else {
        	mGameModel = new GameModel(this);
        }
        
        mBoard = new Board(this, mGameModel);
        setContentView(mBoard);
        
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	
    	GameDataBase gdb = new GameDataBase(this);
    	
    	if (mSavedGameId<0) {
    		mSavedGameId = gdb.addGame(mGameModel);
    		getIntent().putExtra(EXTRA_GAME_ID, mSavedGameId);
    	} else {
    		gdb.updateGame(mSavedGameId, mGameModel);
    	}
    }
    
}
