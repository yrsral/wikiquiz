package com.homegrownapps.wikiquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class TextActivity extends Activity {
	
	public final static String EXTRA_MESSAGE = "com.homegrownapps.wikiquiz.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text);
	}
	
	public void onClick (View v){
		Intent intent = new Intent(this, MainActivity.class);
		EditText editText = (EditText)findViewById(R.id.editText1);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
}
