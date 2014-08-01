package wikiParse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
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

	/**
	 * Print an error.
	 * <p>
	 * The use of this method is to make debugging easier for us, since we
	 * only have the Android SDK on one computer: this can be changed between
	 * logging for Android and printing without the Android SDK a lot easier
	 * than renaming all the instances of Log.e or System.out.println.
	 *
	 * @param	error	General error
	 * @param	desc	Detailed description
	 * @return	None	none
	 */
	public void printError(String error, String desc) {
		//System.out.println("Error: " + error + " - " + desc);
		Log.e(error, desc);
	}

	/**
	 * Print some info.
	 * <p>
	 * See printError(). This maps to Log.i() rather than Log.e().
	 *
	 * @param	info	Type of info
	 * @param	desc	Actual info (string, note that things are working)
	 * @return	None	none
	 */
	public void printInfo(String info, String desc) {
		//System.out.println("Info: " + info + " - " + desc);
		Log.i(info, desc);
	}
	
	public JSONObject getJSONFromUrl(String url) {
		 
		// Making HTTP get request
		HttpEntity httpEntity;
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
 
			HttpResponse httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
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
			//String json = IOUtils.toString(is, "iso-8859-1");
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
	
	public String wikiDownload(String name, String lang){
		//parse json with wiki name
		String key = "";
		String redircheck = "";
		String article = "";
		JSONObject wikiData = null;
		try {
			// Wikipedia's language is set by a DNS subdomain
			// English ones (we want) are simple and en(.wikipedia.org/...)
			wikiData = getJSONFromUrl("http://"+lang+".wikipedia.org/w/api.php?format=json&action=query&titles="+URLEncoder.encode(name, "UTF-8")+"&prop=revisions&rvprop=content");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//going through the JSON
			JSONObject page = (wikiData.getJSONObject("query")).getJSONObject("pages");
			Iterator<String> keys = page.keys();
			if (keys.hasNext() == true){
				key = keys.next();
			}
			redircheck = (page.getJSONObject(key)).getJSONArray("revisions").getJSONObject(0).getString("*");
			if (redircheck.contains("#REDIRECT")){
				int startOfBrackets = redircheck.indexOf("[[")+2;
				int endOfBrackets = redircheck.indexOf("]]");
				String redirect = redircheck.substring(startOfBrackets, endOfBrackets);
				article = wikiDownload(redirect, true);
			} else {
				article = redircheck;
			}
			return article;
			
		} catch (JSONException e) {
			// if there is no article
			e.printStackTrace();
			return "none";
		}
		
		
	}
}
