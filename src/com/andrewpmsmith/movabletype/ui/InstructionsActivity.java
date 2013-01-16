package com.andrewpmsmith.movabletype.ui;

import com.andrewpmsmith.movabletype.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

/**
 * The activity that explains how to play the game.
 *
 * @author Andrew Smith
 */
public class InstructionsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_instructions);
	}

}
