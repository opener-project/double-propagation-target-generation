package org.openerproject.double_propagation2.algorithm.rules;

import java.util.List;

import org.openerproject.double_propagation2.model.IntraSentenceWordRelations;
import org.openerproject.double_propagation2.model.Word;

public interface DoublePropagationRule {

	public List<RuleCheckResult> applyRule(Word word, IntraSentenceWordRelations intraSentenceWordRelations);
	
}
