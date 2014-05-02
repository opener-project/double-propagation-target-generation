package org.openerproject.double_propagation2.algorithm.rules;

import org.openerproject.double_propagation2.model.DoublePropagationWordType;
import org.openerproject.double_propagation2.model.RelationTypes;
import org.openerproject.double_propagation2.model.Word;

public class RuleCheckResult {

	private Word triggeringWord;
	private DoublePropagationWordType triggeringWordType;
	private Word extractedWord;
	private DoublePropagationWordType extractedWordType;
	private String usedRule;
	private RelationTypes relType;

	public Word getTriggeringWord() {
		return triggeringWord;
	}

	public void setTriggeringWord(Word triggeringWord) {
		this.triggeringWord = triggeringWord;
	}

	public DoublePropagationWordType getTriggeringWordType() {
		return triggeringWordType;
	}

	public void setTriggeringWordType(DoublePropagationWordType triggeringWordType) {
		this.triggeringWordType = triggeringWordType;
	}

	public Word getExtractedWord() {
		return extractedWord;
	}

	public void setExtractedWord(Word extractedWord) {
		this.extractedWord = extractedWord;
	}

	public DoublePropagationWordType getExtractedWordType() {
		return extractedWordType;
	}

	public void setExtractedWordType(DoublePropagationWordType extractedWordType) {
		this.extractedWordType = extractedWordType;
	}

	public String getUsedRule() {
		return usedRule;
	}

	public void setUsedRule(String usedRule) {
		this.usedRule = usedRule;
	}

	public RelationTypes getRelType() {
		return relType;
	}

	public void setRelType(RelationTypes relType) {
		this.relType = relType;
	}

}
