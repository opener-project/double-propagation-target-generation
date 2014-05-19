package org.openerproject.double_propagation2.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openerproject.double_propagation2.multiwords.MultiwordChecker;

import com.google.common.collect.Lists;

public class IntraSentenceWordRelationsTest {

	private IntraSentenceWordRelations intraSentenceWordRelations;
	
	@Before
	public void setUp() throws Exception {
		intraSentenceWordRelations=IntraSentenceWordRelations.createRelations();
	}

	@Test
	public void testAddWordToWordRelation() {
		Word sourceWord=Word.createWord("form1", "lemma1", PartOfSpeech.OTHER, 0, 10);
		Word targetWord=Word.createWord("form2", "lemma2", PartOfSpeech.OTHER, 11, 13);
		WordToWordRelation wordRelation=WordToWordRelation.createWordToWordRelation(sourceWord, targetWord, RelationTypes.OTHER);
		//wordRelation.setSourceWord(Word.createWord("word1", "word2", PartOfSpeech.OTHER, 0, 10));
		intraSentenceWordRelations.addWordToWordRelation(wordRelation);
		
		Word newSourceWord=Word.createWord("form1", "lemma1", PartOfSpeech.OTHER, 0, 10);
		List<Word> wordGoverned = intraSentenceWordRelations.getWordsGovernedByThis(newSourceWord, RelationTypes.OTHER);
		for(Word word:wordGoverned){
			System.out.println(word);
		}
		Word newTargetWord=Word.createWord("form2", "lemma2", PartOfSpeech.OTHER, 11, 13);
		List<Word> governorWords = intraSentenceWordRelations.getWordsGoverningThis(newTargetWord, RelationTypes.OTHER);
		for(Word word:governorWords){
			System.out.println(word);
		}
	}

	@Test
	public void testGetWordsGovernedByThis() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetWordsGoverningThis() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetWord() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNumberOfWordsInThisSentence() {
		fail("Not yet implemented");
	}

	@Test
	public void testPopulateSpanToWorNumberMap() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetWordNumber() {
		fail("Not yet implemented");
	}

	@Test
	public void testDetectAndMergeMultiwords() {
		Word word1=Word.createWord("the", "the", PartOfSpeech.OTHER, 0, 3);
		Word word2=Word.createWord("train", "train", PartOfSpeech.OTHER, 4, 6);
		Word word3=Word.createWord("station", "station", PartOfSpeech.OTHER, 7, 9);
		Word word4=Word.createWord("whatever", "whatever", PartOfSpeech.OTHER, 9, 13);
		//List<Word>words=Lists.newArrayList(word1,word2,word3,word4);
		WordToWordRelation wordRelation1=WordToWordRelation.createWordToWordRelation(word3, word1, RelationTypes.OTHER);
		WordToWordRelation wordRelation2=WordToWordRelation.createWordToWordRelation(word3, word2, RelationTypes.OTHER);
		WordToWordRelation wordRelation3=WordToWordRelation.createWordToWordRelation(word3, word4, RelationTypes.OTHER);
		intraSentenceWordRelations.addWordToWordRelation(wordRelation1);
		intraSentenceWordRelations.addWordToWordRelation(wordRelation2);
		intraSentenceWordRelations.addWordToWordRelation(wordRelation3);
		
		for(int i=0;i<intraSentenceWordRelations.getNumberOfWordsInThisSentence();i++){
			System.out.println(intraSentenceWordRelations.getWord(i).getLemma());
		}
		
		System.out.println("=============");
		MultiwordChecker multiwordChecker=MultiwordChecker.createMultiworChecker(Lists.newArrayList("train station"));
		intraSentenceWordRelations.detectAndMergeMultiwords(multiwordChecker);
		for(int i=0;i<intraSentenceWordRelations.getNumberOfWordsInThisSentence();i++){
			System.out.println(intraSentenceWordRelations.getWord(i).getLemma());
			System.out.println("   Governors of this: ");
			for(Word gov:intraSentenceWordRelations.getWordsGoverningThis(intraSentenceWordRelations.getWord(i),RelationTypes.OTHER)){
				System.out.println("      - "+gov.getLemma()+" Span: "+gov.getSpanSignature());
			}
			System.out.println("   Dependents of this: ");
			for(Word dep:intraSentenceWordRelations.getWordsGovernedByThis(intraSentenceWordRelations.getWord(i),RelationTypes.OTHER)){
				System.out.println("      - "+dep.getLemma()+" Span: "+dep.getSpanSignature());
			}
			System.out.println("=============");
		}
		System.out.println("=============");
		
	}

}
