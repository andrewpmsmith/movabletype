package com.andrewpmsmith.movabletype.ui;

import java.util.LinkedList;
import java.util.List;

import android.R.id;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andrewpmsmith.movabletype.R;
import com.andrewpmsmith.movabletype.model.GameDataBase;
import com.andrewpmsmith.movabletype.model.WordList;

public class DashboardActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener {
	
	private static final int FIXED_OPTIONS = 2;
	private static final int NEW_GAME = 0;
	private static final int HOW_TO_PLAY = 1;
	
	private List<Long> mSavedGames;
	private ListView mListView;

	private List<String> mOptions;
	private DashboardListAdapter mListItemsAdapter;

	private GameDataBase mSavedGameDB;

	private class LoadDBTask extends AsyncTask<Void, Void, Boolean> {

		private final Context mContext;
		private ProgressDialog progressDialog;

		public LoadDBTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected void onPreExecute() {
			mListView.setEnabled(false);
			progressDialog = ProgressDialog.show(mContext,
					getString(R.string.loading),
					getString(R.string.loading_message));
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			// Ensure the database has been initialised
			WordList db = new WordList(mContext);
			SQLiteDatabase temp = db.getReadableDatabase();
			temp.close();

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				mListView.setEnabled(true);
				progressDialog.dismiss();
			}
		}
	} // LoadDBTask

	private class DashboardListAdapter extends ArrayAdapter<String> {
		private final Context mContext;
		private final List<String> mValues;
		private final static int ITEM_LAYOUT = R.layout.dashboart_list_item;

		public DashboardListAdapter(Context context, List<String> values) {
			super(context, ITEM_LAYOUT, values);
			mContext = context;
			mValues = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View view = convertView;
			
			if (convertView==null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(ITEM_LAYOUT, parent, false);
			}
			
			TextView textView = (TextView) view.findViewById(id.text1);
			if (textView!=null) textView.setText(mValues.get(position));
			
			if (position<FIXED_OPTIONS) {
				ImageView icon = (ImageView) view.findViewById(R.id.list_image);
				Bitmap bm;
				if (position==NEW_GAME) {
					bm = BitmapFactory.decodeResource(getResources(), R.drawable.plus_icon);
				} else {
					bm = BitmapFactory.decodeResource(getResources(), R.drawable.help_icon);
				}
				icon.setImageBitmap(bm);
			}
			
			return view;
		}

	} // DashboardListAdapter

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dashboard);

		mListView = (ListView) findViewById(R.id.dashboardOptions);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mListView.setEnabled(false);

		mSavedGameDB = new GameDataBase(this);

		LoadDBTask l = new LoadDBTask(this);
		l.execute();
	}

	@Override
	public void onResume() {

		super.onResume();

		mSavedGames = mSavedGameDB.getAllGameKeys();

		mOptions = new LinkedList<String>();
		
		mOptions.add(getString(R.string.new_game));
		mOptions.add(getString(R.string.how_to_play));

		String savedGame = getResources().getString(R.string.saved_game);

		for (int i = 0; i < mSavedGames.size(); ++i) {
			mOptions.add(savedGame + " " + (i + 1));
		}
		
		mListItemsAdapter = new DashboardListAdapter(this, mOptions);

		mListView.setAdapter(mListItemsAdapter);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

		if (pos == NEW_GAME) {
			Intent newGameintent = new Intent(DashboardActivity.this,
					GameActivity.class);

			startActivity(newGameintent);
		} else if (pos == HOW_TO_PLAY) {
			Intent instructionsIntent = new Intent(DashboardActivity.this,
					InstructionsActivity.class);

			startActivity(instructionsIntent);
		} else {
			Intent savedGameIntent = new Intent(DashboardActivity.this,
					GameActivity.class);

			int gameIndex = pos - FIXED_OPTIONS;

			savedGameIntent.putExtra(GameActivity.EXTRA_GAME_ID,
					mSavedGames.get(gameIndex));
			startActivity(savedGameIntent);
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos,
			long id) {

		if (pos < FIXED_OPTIONS) {
			return false;
		}

		final int selection = pos;
		final AdapterView<?> adapter = arg0;

		Resources res = getResources();

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(res.getString(R.string.delete_game_dialog))
				.setNegativeButton(
						res.getString(R.string.do_not_delete_button), null)
				.setPositiveButton(res.getString(R.string.delete_button),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface di, int arg1) {

								mSavedGameDB.deleteGame(mSavedGames
										.get(selection - FIXED_OPTIONS));
								mSavedGames.remove(selection
										- FIXED_OPTIONS);

								mListItemsAdapter.remove(mListItemsAdapter.getItem(selection));
								adapter.requestLayout();

							}

						}).show();

		return false;
	}

}
