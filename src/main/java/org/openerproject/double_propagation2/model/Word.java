package org.openerproject.double_propagation2.model;

import java.util.List;

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
	private String postag;
	
	private List<Word>composingWords;
	
	private Word(){
		
	}
	
	public static Word createWord(String wordForm,String lemma,String postag){
		
		return new Word();
	}
	
	public static Word createWord(List<String>wordForms,List<String>lemmas,List<String>postags){
		
		return new Word();
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

	public String getPostag() {
		return postag;
	}

	public void setPostag(String postag) {
		this.postag = postag;
	}
	
	
	
}
