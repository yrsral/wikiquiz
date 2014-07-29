package wikiParse;

public class QuestionFinder {
	public String[] questionParse(String article, int question){
		String article_new = article.toLowerCase();
		int index;
		int seek = 0;
		for (int i = 0; i<question+1; i++){
			index = article_new.indexOf("'''", seek);
			seek = index;
			seek = article_new.indexOf("'''", seek);
		}
		//loop back through chars to find full stop, cater for '/n's
		return null;
		
	}
}
