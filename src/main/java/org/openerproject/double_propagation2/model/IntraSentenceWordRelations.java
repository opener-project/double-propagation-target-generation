package org.openerproject.double_propagation2.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class IntraSentenceWordRelations {

	private Multimap<Word,WordToWordRelation>wordRelationMap;
	
	private IntraSentenceWordRelations(){
		
	}
	
	/**
	 * THIS REQUIRES A LOT OF LOGIC TO MAP THE INFO COMING FROM WHATEVER SOURCE (STANFORD PARSER...) TO THIS STRUCTURE
	 * @return
	 */
	public static IntraSentenceWordRelations createRelations(){
		return new IntraSentenceWordRelations();
	}
	
	public void addWordToWordRelation(WordToWordRelation wordToWordRelation){
		wordRelationMap.put(wordToWordRelation.getSourceWord(), wordToWordRelation);
	}
	
	public List<Word>getWordsByRelation(Word aWord,RelationTypes relType){
		Collection<WordToWordRelation> relatedWords = wordRelationMap.get(aWord);
		Iterator<WordToWordRelation> relatedWordsIterator = relatedWords.iterator();
		List<Word>results=Lists.newArrayList();
		while(relatedWordsIterator.hasNext()){
			WordToWordRelation wordToWordRelation = relatedWordsIterator.next();
			if(wordToWordRelation.getRelType()==relType){
				results.add(wordToWordRelation.getTargetWord());
			}
		}
		return results;
	}
	
}
