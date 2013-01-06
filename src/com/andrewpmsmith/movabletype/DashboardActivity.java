package com.andrewpmsmith.movabletype;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DashboardActivity extends Activity
		implements OnItemClickListener, OnItemLongClickListener {
	
	private String[] mFixedOptions;
	private List<Long> mSavedGames;
	private ListView mListView;
	
	private List<String> mOptions;
	private ArrayAdapter<String> mAdapter;
	
	private GameDataBase mSavedGameDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dashboard);
		
		mListView = (ListView) findViewById(R.id.dashboardOptions);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		
		Resources res = getResources();
		mFixedOptions = res.getStringArray(R.array.dashboard_options);
		
		mSavedGameDB = new GameDataBase(this);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		mSavedGames = mSavedGameDB.getAllGameKeys();
		
		mOptions = new LinkedList<String>();
		
		for (int i=0; i<mFixedOptions.length; ++i) {
			mOptions.add(mFixedOptions[i]);
		}
		
		String savedGame = getResources().getString(R.string.saved_game);
		
		for (int i=0; i<mSavedGames.size(); ++i) {
			mOptions.add(savedGame + " " + (i+1));
		}
		
		mAdapter = new ArrayAdapter<String>(this, 
		        android.R.layout.simple_list_item_1, mOptions);
		
		mListView.setAdapter(mAdapter);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos,
			long id) {
		
		if (pos == 0) {
			Intent newGameintent =
					new Intent(DashboardActivity.this,
							GameActivity.class);
			
			startActivity(newGameintent);
		}
		else if (pos == 1) {
			Intent instructionsIntent =
					new Intent(DashboardActivity.this,
							InstructionsActivity.class);
			
			startActivity(instructionsIntent);
		}
		else {
			Intent savedGameIntent =
					new Intent(DashboardActivity.this,
							GameActivity.class);
			
			int gameIndex = pos - mFixedOptions.length;
			
			savedGameIntent.putExtra(GameActivity.EXTRA_GAME_ID,
					mSavedGames.get(gameIndex));
			startActivity(savedGameIntent);
		}
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int pos, long id) {
		
		if (pos<mFixedOptions.length) return false;
		
		final int selection = pos;
		final AdapterView<?> adapter = arg0;
		
		Resources res = getResources();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(res.getString(R.string.delete_game_dialog))
		.setNegativeButton(res.getString(R.string.do_not_delete_button), null)
        .setPositiveButton(res.getString(R.string.delete_button), new OnClickListener(){

			@Override
			public void onClick(DialogInterface di, int arg1) {
				
				mSavedGameDB.deleteGame(mSavedGames.get(selection - mFixedOptions.length));
				mSavedGames.remove(selection - mFixedOptions.length);
				
				mAdapter.remove(mAdapter.getItem(selection));
				adapter.requestLayout();
				
			}
        	
        }).show();
		
		return false;
	}

}
