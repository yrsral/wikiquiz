package wikiParse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
	
	public String[] questionParse(String[] article, int question){
		int x = 0;
		while (x != 1){
			
		}
		
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
