package com.homegrownapps.wikiquiz;

import wikiParse.QuestionCreator;
import wikiParse.QuestionFinder;
import wikiParse.WikiArticleRead;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public static class Global {
		public static String topic;
		public static Context context;
	}
	
	private ProgressDialog pdz;
	private Context cont;
	
	private class wikiParse extends AsyncTask<Void, Void, String>{
		
		@Override
		protected void onPreExecute() {
			pdz = new ProgressDialog(cont);
			pdz.setTitle("Making question...");
			pdz.setMessage("Please wait.");
			pdz.setCancelable(false);
			pdz.setIndeterminate(true);
			pdz.show();
		}
		
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			WikiArticleRead reader = new WikiArticleRead();
			String unparsed = reader.wikiDownload(Global.topic, true);
			QuestionFinder qf = new QuestionFinder();
			String[] parsed = qf.sentenceParse(unparsed);
			String[] subobjverb = qf.questionParse(parsed, 1);
			QuestionCreator qc = new QuestionCreator();
			String question = qc.questionMake(subobjverb[0], subobjverb[1], subobjverb[2]);
			return question;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (pdz != null) {
				pdz.dismiss();
			}
			TextView tv = (TextView)findViewById(R.id.textView2);
			tv.setText(result);
			tv.invalidate();
		}		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cont = MainActivity.this;
		Global.context = cont;
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		Global.topic = intent.getStringExtra(TextActivity.EXTRA_MESSAGE);
		wikiParse wp = new wikiParse();
		wp.execute();
	}
	
	public void onClickA(View v){
		
	}
	
	public void onClickB(View v){
		
	}
	
	public void onClickC(View v){
		
	}
}
