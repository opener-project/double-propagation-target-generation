package org.openerproject.double_propagation2.model;

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

	private Word() {

	}

	private Word(String wordForm, String lemma, PartOfSpeech postag, int beginPosition, int endPosition) {
		this.wordForm = wordForm;
		this.lemma = lemma;
		this.postag = postag;
		this.span = new CharacterSpan(beginPosition, endPosition);
		composingWords = Lists.newArrayList();
		// Cuidado con esta chorrrada que es peligrosa, lo pongo sólo
		// provisional!
		// composingWords.add(this);
	}

	public static Word createWord(String wordForm, String lemma, PartOfSpeech postag, int beginPosition, int endPosition) {
		// TODO EL TAG MAPPING NO SE TIENE QUE HACER AQUI!!!
		// DEPENDE DE LA LIBRERÍA QUE SE ESTE USANDO, ASÍ QUE VA EN SU
		// IMPLEMENTACIÓN DE ANALYZER PARTICULAR
		return new Word(wordForm, lemma, postag, beginPosition, endPosition);
	}

	public static Word createWord(List<String> wordForms, List<String> lemmas, List<PartOfSpeech> postags,
			int beginPosition, int endPosition) {

		return null;
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
