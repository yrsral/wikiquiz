package wikiParse;

public class QuestionFinder {
	public String[] questionParse(String article, int question){
		String article_new = article.replaceAll("\n", "");
		//.toLowerCase();
		
		int index = 0;
		int seek = 0;
		for (int i = 0; i<question+1; i++){
			//''' signifies bold in WikiMarkup - the markup language of wikipedia
			index = article_new.indexOf("'''", seek);
			seek = index+2;
			seek = article_new.indexOf("'''", seek);
		}
		char character = 'a';
		int seek_number = index;
		while (character != '.'){
			character = article_new.charAt(seek_number);
			seek_number = seek_number - 1;
			//going back through the numbers to find the beginning of the sentence
		}
		

		
		//TODO: loop back through chars to find full stop, cater for '\n's
		//TODO: find conjugation of 'to be'
		//TODO: return subject, object (not null!)
		return null;
		
	}
}
