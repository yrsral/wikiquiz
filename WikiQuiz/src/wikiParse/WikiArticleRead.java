package wikiParse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WikiArticleRead {
	
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
	
	public String wikiDownload(String name, boolean simple){
		//parse json with wiki name
		String lang = "en";
		String key = "";
		String article = "";
		if (simple == true){
			lang = "simple";
		}
		JSONObject wikiData = getJSONFromUrl("http://"+lang+".wikipedia.org/w/api.php?format=json&action=query&titles="+name+"&prop=revisions&rvprop=content");
		try {
			//going through the JSON
			JSONObject page = (wikiData.getJSONObject("query")).getJSONObject("pages");
			Iterator<String> keys = page.keys();
			if (keys.hasNext() == true){
				key = keys.next();
			}
			article = (page.getJSONObject(key)).getJSONArray("revisions").getJSONObject(0).getString("*");
			
		} catch (JSONException e) {
			// if there is no article
			e.printStackTrace();
			return "none";
		}
		
		return article;
		
	}
}
