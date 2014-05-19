package org.openerproject.double_propagation2.model;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

/*
 * FALTA POR TERMINAR:
 * 	-La estructura interna que da soporte a single-word/multiword debe quedar escondida
 *  -Los getters deben devolver de manera adecuada el contenido, tanto si es multiword como si no
 *  -Se puede poner una funcion "isMultiword" que chequee si las composing words son >1
 *  -Cualquier lógica adicional de manipulación de Words, debe ir aquí (evitar anemic domain)
 */
public class Word {

	private String wordForm;
	private String lemma;
	private PartOfSpeech postag;

	private CharacterSpan span;

	private List<Word> composingWords;

//	private Word() {
//
//	}

	private Word(String wordForm, String lemma, PartOfSpeech postag, int beginPosition, int endPosition, List<Word>composingWords) {
		super();
		this.wordForm = wordForm;
		this.lemma = lemma;
		this.postag = postag;
		this.span = new CharacterSpan(beginPosition, endPosition);
		this.composingWords = composingWords;
	}

	public static Word createWord(String wordForm, String lemma, PartOfSpeech postag, int beginPosition, int endPosition) {
		return new Word(wordForm, lemma, postag, beginPosition, endPosition, Collections.<Word>emptyList());
	}

	public static Word createMultiword(List<String> wordForms, List<String> lemmas, List<PartOfSpeech> postags,
			List<Integer> beginPositions, List<Integer> endPositions) {
		List<Word>composingWords=Lists.newArrayList();
		StringBuffer composedWordForm=new StringBuffer();
		StringBuffer composedLemma=new StringBuffer();
		PartOfSpeech composedPosTag=PartOfSpeech.MULTIWORD;
		for(int i=0;i<wordForms.size();i++){
			String wordForm=wordForms.get(i);
			composedWordForm.append(wordForm);
			composedWordForm.append(" ");
			String lemma=lemmas.get(i);
			composedLemma.append(lemma);
			composedLemma.append(" ");
			PartOfSpeech postag=postags.get(i);
			Word word=createWord(wordForm,lemma ,postag , beginPositions.get(i), endPositions.get(i));
			composingWords.add(word);
		}
		Word word=new Word(composedWordForm.toString().trim(), composedLemma.toString().trim(), composedPosTag, beginPositions.get(0), endPositions.get(endPositions.size()-1),composingWords);
		return word;
	}
	
	public static Word createMultiword(List<Word>singleWords) {
		StringBuffer composedWordForm=new StringBuffer();
		StringBuffer composedLemma=new StringBuffer();
		PartOfSpeech composedPosTag=PartOfSpeech.MULTIWORD;
		for(Word singleWord:singleWords){
			composedWordForm.append(singleWord.wordForm);
			composedWordForm.append(" ");
			composedLemma.append(singleWord.getLemma());
			composedLemma.append(" ");
		}
		Word word=new Word(composedWordForm.toString().trim(), composedLemma.toString().trim(), composedPosTag, singleWords.get(0).span.begin, singleWords.get(singleWords.size()-1).span.end,singleWords);
		return word;
	}

	public String getWordForm() {
		return wordForm;
	}

	public void setWordForm(String wordForm) {
		this.wordForm = wordForm;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public boolean isPreviousWordInTheSentenceThan(Word anotherWord) {
		return this.span.isPreviousSpanThan(anotherWord.span);
	}
	
	public boolean isSameWordInTheSentenceThan(Word anotherWord){
		return this.getSpanSignature().equals(anotherWord.getSpanSignature());
	}

	public String getSpanSignature() {
		return span.begin + "_" + span.end;
	}

	public PartOfSpeech getPostag() {
		return postag;
	}

	public void setPostag(PartOfSpeech postag) {
		this.postag = postag;
	}
	
	public boolean isMultiword(){
		return !composingWords.isEmpty();
	}
	
	public List<Word>getComposingWords(){
		return this.composingWords;
	}

	protected static class CharacterSpan {

		private int begin;
		private int end;

		public CharacterSpan(int begin, int end) {
			this.begin = begin;
			this.end = end;
		}

		@Override
		/**
		 * Overriden equality, which just checks the character span of a word (if inside the same sentence that should work, careful in other scenarios)
		 */
		public boolean equals(Object obj) {
			CharacterSpan anotherSpan;
			if (obj instanceof CharacterSpan) {
				anotherSpan = (CharacterSpan) obj;
				return anotherSpan.begin == this.begin && anotherSpan.end == this.end;
			} else {
				return false;
			}

		}

		public boolean isPreviousSpanThan(CharacterSpan anotherSpan) {
			return anotherSpan.begin > this.end;
		}

		@Override
		public String toString() {
			return "CharacterSpan [begin=" + begin + ", end=" + end + "]";
		}
	}

	@Override
	public String toString() {
		return "Word [wordForm=" + wordForm + ", lemma=" + lemma + ", postag=" + postag + ", span=" + span
				+ ", composingWords=" + composingWords + "]";
	}

}
