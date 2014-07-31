package wikiParse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.provider.Settings.Global;
import android.util.Log;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class QuestionFinder {
	public String[] sentenceParse(String article){
		//begin wiki parsing
		String article_new = article;
		StringBuffer article_newer = new StringBuffer(article_new.replace("\n*", ""));
		article_new = null;
		//.toLowerCase();
		while (true){
		//removing [[File: for representing photos
		if (article_newer.indexOf("[[File") != -1){
			int photoindex = article_newer.indexOf("[[File");
			int photoend = article_newer.indexOf("]]", photoindex);
			int str_len = article_newer.length()-1;
			if (photoindex == -1 | photoend == -1){
				break;
			} else {
				Log.i(Integer.toString(photoindex), Integer.toString(photoend));
				article_newer.delete(photoindex, photoend+2);
			}
			
			
		} else {
			break;
		}
		}
		
		int photoend;
		while (true){
			if (article_newer.indexOf("<") != -1){
				int photoindex = article_newer.indexOf("<");
				if (article_newer.indexOf("<ref>")==photoindex || article_newer.charAt(article_newer.indexOf(">")-1) != '/'){
					photoend = article_newer.indexOf("</ref>")+6;
				} else {
					photoend = article_newer.indexOf(">");
					int str_len = article_newer.length()-1;
				}
				article_newer.delete(photoindex, photoend+6);
				
				
			} else {
				break;
			}
		}
		String wikinoopenbrackets = article_newer.toString().replaceAll("\\[\\[", "");
		article_newer = null;
		String wikinobrackets = wikinoopenbrackets.replaceAll("\\]\\]", "");
		wikinoopenbrackets = null;
		StringBuffer article_newest = new StringBuffer(wikinobrackets);
		
		String article_final;
		while (true){
		if (article_newest.indexOf("{{") != -1){
			int noteindex = article_newest.indexOf("{{");
			int noteend = article_newest.indexOf("}}", noteindex);
			int str_len = article_newest.length()-1;
			article_newest.delete(noteindex, noteend+2);
		} else {
			break;
		}
		}
		
		while (true){
		if (article_newest.indexOf("== ") != -1){
			int noteindex = article_newest.indexOf("==");
			int noteend = article_newest.indexOf("==", noteindex);
			int str_len = article_newest.length()-1;
			article_newest.delete(noteindex, noteend+2);
		} else {
			break;
		}
		}
		
		int noteindex = article_newest.indexOf("==Rel");
		if (noteindex != -1){
			article_newest.delete(noteindex, article_newest.length()+1);
		}
		String wiki = article_newest.toString();
		String wiki_final = wiki.replaceAll("\u2013", "-");
		
		wiki = null;
		
		InputStream modelIn = null;
		try {
			modelIn = com.homegrownapps.wikiquiz.MainActivity.Global.context.getAssets().open("en_sent.bin");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SentenceModel model = null;
		try {
		  model = new SentenceModel(modelIn);
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
		
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		String[] sentences = sentenceDetector.sentDetect(wiki_final);
		wiki_final = null;
		return sentences;
		
		//end wiki parsing
		
		
	}
	
	static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
	
	public JSONObject getJSONFromUrl(String url) {
		 
        // Making HTTP get request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
 
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        try {
        	//parsing to string
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
            Log.e("JSON", json);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);            
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        Log.i("Tag", jObj.toString());
        return jObj;
 
    }
	
	public String[] questionParse(String[] article, int question){
		int x = 0;
		int i = 0;
		while (x != 1){
			if (article[i].contains("'''") == true){
				x = 1;
			} else {
				i = i+1;
			}
		}
		
		String sentence_noquotes = article[i].replaceAll("'''", "");
		
		//aec6c18db45c37f30dccd808020f969731a4421c
		//urlencode(query, "utf-8")
		JSONObject subobj = null;
		try {
			subobj = getJSONFromUrl("http://access.alchemyapi.com/calls/text/TextGetRelations?text="+URLEncoder.encode(sentence_noquotes, "UTF-8")+"&apikey=aec6c18db45c37f30dccd808020f969731a4421c&entities=1&keywords=1&outputMode=json");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//parse the json and input into question maker
		JSONArray relations = null;
		try {
			relations = subobj.getJSONArray("relations");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject fullsentence = null;
		try {
			fullsentence = relations.getJSONObject(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject subject = null;
		JSONObject object = null;
		JSONObject verb = null;
		String substring = null;
		String objstring = null;
		String verbstring = null;
		String tense = null;
		try {
			subject = fullsentence.getJSONObject("subject");
			object = fullsentence.getJSONObject("object");
			verb = fullsentence.getJSONObject("action");
			substring = subject.getString("text");
			objstring = object.getString("text");
			verbstring = verb.getString("text");
			tense = verb.getJSONObject("verb").getString("tense"); 
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String sub = parseForBrackets(substring);
		String obj = parseForBrackets(objstring);
		String ver = parseForBrackets(verbstring);
		
		String[] questionfinal = {sub, ver, tense, obj};
		return questionfinal;
		
		/*
		int index = 0;
		int seek = 0;
		for (int i = 0; i<question+1; i++){
			//''' signifies bold in WikiMarkup - the markup language of wikipedia
			index = article_updated.indexOf("'''", seek);
			seek = index+2;
			seek = article_updated.indexOf("'''", seek);
		}
		char character = 'a';
		int seek_number = index;
		while (character != '.'){
			character = article_updated.charAt(seek_number);
			seek_number = seek_number - 1;
			//going back through the numbers to find the beginning of the sentence
		}
		

		
		//TODO: loop back through chars to find full stop, cater for '\n's
		//TODO: find conjugation of 'to be'
		//TODO: return subject, object (not null!)
		return null;
		*/
	}
	
	public String parseForBrackets(String sentence){
		String sentenceUpdated;
		while (true){
			if (sentence.indexOf("(") != -1){
				int photoindex = sentence.indexOf("(");
				int photoend = sentence.indexOf(")", photoindex);
				int str_len = sentence.length()-1;
				sentenceUpdated = sentence.substring(0, photoindex)+sentence.substring(photoend+1);
				sentence = sentenceUpdated;
				//article_newer = article_updated;
				
				
			} else {
				sentenceUpdated = sentence;
				break;
			}
		}
		return sentenceUpdated;
	}
}
