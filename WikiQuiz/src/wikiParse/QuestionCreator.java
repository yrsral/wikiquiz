package wikiParse;

import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

public class QuestionCreator {
	Lexicon lexicon = Lexicon.getDefaultLexicon();
    NLGFactory nlgFactory = new NLGFactory(lexicon);
    Realiser realiser = new Realiser(lexicon);
    
	public String questionMake(String subject, String verb, String tense){
		//make the sentence
		SPhraseSpec p = nlgFactory.createClause();
		p.setVerb(verb);
		p.setSubject(subject);
		if (tense == "past"){
			p.setFeature(Feature.TENSE, Tense.PAST);
		} else if (tense == "present"){
			p.setFeature(Feature.TENSE, Tense.PRESENT);
		} else if (tense == "future"){
			p.setFeature(Feature.TENSE, Tense.FUTURE);
		}
		//eg. what WERE the vikings
		p.setFeature(Feature.INTERROGATIVE_TYPE, InterrogativeType.WHAT_OBJECT);
		String output = realiser.realiseSentence(p);
		//return the sentence
		return output;
		
	}
}