package com.homegrownapps.wikiquiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import wikiParse.QuestionCreator;
import wikiParse.QuestionFinder;
import wikiParse.WikiArticleRead;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

// TODO: Parse for wikt:| in used links

public class MainActivity extends Activity {
	
	public static class Global {
		public static String topic;
		public static Context context;
		public static int button;
		public static int progress = 1;
		public static int question = 1;
		public static int score;
	}
	
	private ProgressDialog pdz;
	private Context cont;
	
	private class wikiParse extends AsyncTask<Void, Void, String[]>{
		
		@Override
		protected void onPreExecute() {
			pdz = new ProgressDialog(cont);
			pdz.setTitle("Making question...");
			pdz.setMessage("Please wait.");
			pdz.setCancelable(false);
			pdz.setIndeterminate(true);
			pdz.show();
			Global.score = 0;
		}
		
		@Override
		protected String[] doInBackground(Void... params) {
			// TODO Auto-generated method stub
			WikiArticleRead reader = new WikiArticleRead();
			String unparsed = reader.wikiDownload(Global.topic, "simple");
			if (unparsed != "none"){
				QuestionFinder qf = new QuestionFinder();
				String[] parsed = qf.sentenceParse(unparsed);
				List<String> parse = Arrays.asList(parsed);
				Collections.shuffle(parse);
				parsed = parse.toArray(parsed);
				//Random rand = new Random();
				//int question_no = rand.nextInt(parsed.length-1)+1;
				String[] subobjverb = qf.questionParse(parsed, Global.progress);
				String[] obj1 = qf.questionParse(parsed, Global.progress+1);
				String[] obj2 = qf.questionParse(parsed, Global.progress+2);
				while (obj1[3] == subobjverb[3] | obj2[3] == subobjverb[3]){
					obj1 = qf.questionParse(parsed, Global.progress+2);
					obj2 = qf.questionParse(parsed, Global.progress+3);
				}
				Global.progress = Global.progress + 3;
				Global.question = Global.question + 1;
				//possibly random numbers for question number argument in questionParse
				//uncapitalise 'a' or 'an'
				if (subobjverb[0] == "none"){
					String[] error = {"No questions can be made about the topic", "none"};
					return error;
				}
				QuestionCreator qc = new QuestionCreator();
				String question = qc.questionMake(subobjverb[0], subobjverb[1], subobjverb[2]);
				String[] qa = {question, subobjverb[3], obj1[3], obj2[3]};
				return qa;
			} else {
				String[] error = {"No questions can be made. Try again later", "none"};
				return error;
			}
		}
		
		@Override
		protected void onPostExecute(String[] result) {
			if (pdz != null) {
				try {
					if (pdz.isShowing()) {
				        pdz.dismiss();       
				    }
		            pdz = null;
		          } catch (Exception e) {
		         // nothing
		          }
			}
			TextView tv = (TextView)findViewById(R.id.textView2);
			tv.setText(result[0]);
			tv.invalidate();
			Random r = new Random();
			int randButton = r.nextInt(2);
			//parse till punctuation mark
			Global.button = randButton;
			if (result[1] != "none"){
				if (randButton == 0){
					Button b = (Button)findViewById(R.id.button1);
					b.setText(result[1]);
					b.invalidate();
					Button bu = (Button)findViewById(R.id.button2);
					bu.setText(result[2]);
					bu.invalidate();
					Button but = (Button)findViewById(R.id.button3);
					but.setText(result[3]);
					but.invalidate();
				} else if (randButton == 1){
					Button b = (Button)findViewById(R.id.button2);
					b.setText(result[1]);
					b.invalidate();
					Button bu = (Button)findViewById(R.id.button1);
					bu.setText(result[2]);
					bu.invalidate();
					Button but = (Button)findViewById(R.id.button3);
					but.setText(result[3]);
					but.invalidate();
				} else if (randButton == 2){
					Button b = (Button)findViewById(R.id.button3);
					b.setText(result[1]);
					b.invalidate();
					Button bu = (Button)findViewById(R.id.button2);
					bu.setText(result[2]);
					bu.invalidate();
					Button but = (Button)findViewById(R.id.button1);
					but.setText(result[3]);
					but.invalidate();
				}
			if (Global.question < 10){
				Button playagain = new Button(cont);
				playagain.setText("Next Question");
				LayoutParams para = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				para.addRule(RelativeLayout.BELOW, R.id.button3);
				playagain.setLayoutParams(para);
				playagain.setOnClickListener(new OnClickListener() {
			        @Override
			        public void onClick(View btn) {
			            wikiParse wp = new wikiParse();
			            wp.execute();
			        }
			    });
			} else {
				//score display?
			}
			}
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
	
	// get ticks and crosses
	
	public void onClickA(View v){
		Button a = (Button)findViewById(R.id.button1);
		Button b = (Button)findViewById(R.id.button2);
		Button c = (Button)findViewById(R.id.button3);
		a.setEnabled(false);
		a.invalidate();
		b.setEnabled(false);
		b.invalidate();
		c.setEnabled(false);
		c.invalidate();
		if (Global.button == 0){
			Button b0 = (Button)findViewById(R.id.button1);
			b0.setBackgroundColor(Color.GREEN);
			Global.score = Global.score + 1;
			b0.invalidate();
			TextView tv = (TextView)findViewById(R.id.textView3);
			tv.setText("Score: "+Integer.toString(Global.score));
			tv.invalidate();
		} else {
			Button b_origin = (Button)findViewById(R.id.button1);
			b_origin.setBackgroundColor(Color.GREEN);
			b_origin.invalidate();
			if (Global.button == 0){
				Button b0 = (Button)findViewById(R.id.button1);
				b0.setBackgroundColor(Color.GREEN);
				b0.invalidate();
			} else if (Global.button == 1) {
				Button b1 = (Button)findViewById(R.id.button2);
				b1.setBackgroundColor(Color.GREEN);
				b1.invalidate();
			} else{
				Button b2 = (Button)findViewById(R.id.button3);
				b2.setBackgroundColor(Color.GREEN);
				b2.invalidate();
			}
		}
	}
	
	public void onClickB(View v){
		Button a = (Button)findViewById(R.id.button1);
		Button b = (Button)findViewById(R.id.button2);
		Button c = (Button)findViewById(R.id.button3);
		a.setEnabled(false);
		a.invalidate();
		b.setEnabled(false);
		b.invalidate();
		c.setEnabled(false);
		c.invalidate();
		if (Global.button == 1){
			Button b1 = (Button)findViewById(R.id.button2);
			b1.setBackgroundColor(Color.GREEN);
			Global.score = Global.score + 1;
			b1.invalidate();
			TextView tv = (TextView)findViewById(R.id.textView3);
			tv.setText("Score: "+Integer.toString(Global.score));
			tv.invalidate();
		} else {
			Button b_origin = (Button)findViewById(R.id.button2);
			b_origin.setBackgroundColor(Color.RED);
			b_origin.invalidate();
			if (Global.button == 0){
				Button b0 = (Button)findViewById(R.id.button1);
				b0.setBackgroundColor(Color.GREEN);
				b0.invalidate();
			} else if (Global.button == 1) {
				Button b1 = (Button)findViewById(R.id.button2);
				b1.setBackgroundColor(Color.GREEN);
				b1.invalidate();
			} else{
				Button b2 = (Button)findViewById(R.id.button3);
				b2.setBackgroundColor(Color.GREEN);
				b2.invalidate();
			}
		}
	}
	
	public void onClickC(View v){
		Button a = (Button)findViewById(R.id.button1);
		Button b = (Button)findViewById(R.id.button2);
		Button c = (Button)findViewById(R.id.button3);
		a.setEnabled(false);
		a.invalidate();
		b.setEnabled(false);
		b.invalidate();
		c.setEnabled(false);
		c.invalidate();
		if (Global.button == 2){
			Button b2 = (Button)findViewById(R.id.button3);
			b2.setBackgroundColor(Color.GREEN);
			Global.score = Global.score + 1;
			b2.invalidate();
			TextView tv = (TextView)findViewById(R.id.textView3);
			tv.setText("Score: "+Integer.toString(Global.score));
			tv.invalidate();
		}else {
			Button b_origin = (Button)findViewById(R.id.button3);
			b_origin.setBackgroundColor(Color.RED);
			b_origin.invalidate();
			if (Global.button == 0){
				Button b0 = (Button)findViewById(R.id.button1);
				b0.setBackgroundColor(Color.GREEN);
				b0.invalidate();
			} else if (Global.button == 1) {
				Button b1 = (Button)findViewById(R.id.button2);
				b1.setBackgroundColor(Color.GREEN);
				b1.invalidate();
			} else{
				Button b2 = (Button)findViewById(R.id.button3);
				b2.setBackgroundColor(Color.GREEN);
				b2.invalidate();
			}
		}
	}
	
	@Override
	public void onPause() {
		finish();
		super.onPause();
	}
}
