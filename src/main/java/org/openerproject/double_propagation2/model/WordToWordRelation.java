package org.openerproject.double_propagation2.model;

public class WordToWordRelation {

	private Word sourceWord;
	private RelationTypes relType;
	private Word targetWord;

	public Word getTargetWord() {
		return targetWord;
	}

	public void setTargetWord(Word targetWord) {
		this.targetWord = targetWord;
	}

	public RelationTypes getRelType() {
		return relType;
	}

	public void setRelType(RelationTypes relType) {
		this.relType = relType;
	}

	public Word getSourceWord() {
		return sourceWord;
	}

	public void setSourceWord(Word sourceWord) {
		this.sourceWord = sourceWord;
	}

}
