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
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class QuestionFinder {
	public String[] sentenceParse(String article){
		//begin wiki parsing
		String article_new = article.replaceAll("\n", "");
		String article_newer = article_new.replace("\n*", "");
		//.toLowerCase();
		String article_updated;
		while (true){
		if (article_newer.indexOf("[[F") != -1){
			int photoindex = article_newer.indexOf("[[F");
			int photoend = article_newer.indexOf("]]");
			int str_len = article_newer.length()-1;
			article_updated = article_newer.substring(0, photoindex)+article_newer.substring(photoend+2);
			article_newer = article_updated;
			
			
		} else {
			article_updated = article_newer;
			break;
		}
		}
		
		String article_update;
		int photoend;
		while (true){
			if (article_updated.indexOf("<") != -1){
				int photoindex = article_updated.indexOf("<");
				if (article_updated.indexOf("<ref>")==photoindex || article_updated.charAt(article_updated.indexOf(">")-1) != '/'){
					photoend = article_updated.indexOf("</ref>")+6;
				} else {
					photoend = article_updated.indexOf(">");
					int str_len = article_updated.length()-1;
				}
				article_update = article_updated.substring(0, photoindex)+article_updated.substring(photoend+1);
				article_updated = article_update;
				
				
			} else {
				break;
			}
		}
		String wikinoopenbrackets = article_updated.replaceAll("[[", "");
		String wikinobrackets = wikinoopenbrackets.replaceAll("]]", "");
		
		String article_final;
		while (true){
		if (wikinobrackets.indexOf("{{") != -1){
			int noteindex = wikinobrackets.indexOf("{{");
			int noteend = wikinobrackets.indexOf("}}");
			int str_len = wikinobrackets.length()-1;
			article_final = wikinobrackets.substring(0, noteindex)+wikinobrackets.substring(noteend+2);
			wikinobrackets = article_final;
			
			
		} else {
			article_final = wikinobrackets;
			break;
		}
		}
		
		String article_finale;
		while (true){
		if (article_final.indexOf("== ") != -1){
			int noteindex = article_final.indexOf("==");
			int noteend = article_final.indexOf("==", noteindex);
			int str_len = article_final.length()-1;
			article_finale = article_final.substring(0, noteindex)+article_final.substring(noteend+2);
			article_final = article_finale;
			
			
		} else {
			article_finale = article_final;
			break;
		}
		}
		
		int noteindex = article_finale.indexOf("==Rel");
		String wiki = article_finale.substring(noteindex);
		String wiki_final = wiki.replaceAll("\u2013", "-");
		
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream("en-sent.bin");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
                sb.append(line + "n");
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
		JSONObject subobj;
		try {
			subobj = getJSONFromUrl("http://access.alchemyapi.com/calls/text/TextGetRelations?text="+URLEncoder.encode(sentence_noquotes, "UTF-8")+"&apikey=aec6c18db45c37f30dccd808020f969731a4421c&entities=1&keywords=1");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//parse the json and input into question maker
		
		return article;
		
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
}
