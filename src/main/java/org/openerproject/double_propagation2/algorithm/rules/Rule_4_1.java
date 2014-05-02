package org.openerproject.double_propagation2.algorithm.rules;

import java.util.List;

import org.openerproject.double_propagation2.model.DoublePropagationWordType;
import org.openerproject.double_propagation2.model.IntraSentenceWordRelations;
import org.openerproject.double_propagation2.model.PartOfSpeech;
import org.openerproject.double_propagation2.model.RelationTypes;
import org.openerproject.double_propagation2.model.Word;

import com.google.common.collect.Lists;

/**
 * Opinion joined with a conjunction
 * e.g. the phone is small and nice
 * @author yo
 *
 */
public class Rule_4_1 implements DoublePropagationRule{

	public static final String RULE_NAME=Rule_4_1.class.getName();
	
	private static final PartOfSpeech REQUIRED_TARGETWORD_POSTAG=PartOfSpeech.ADJECTIVE;
	public static final DoublePropagationWordType TRIGGERING_WORD_TYPE = DoublePropagationWordType.OPINION;
	
	@Override
	public List<RuleCheckResult> applyRule(Word word, IntraSentenceWordRelations intraSentenceWordRelations) {
		List<RuleCheckResult>ruleCheckResults=Lists.newArrayList();
		List<Word> candidateWords = intraSentenceWordRelations.getWordsGovernedByThis(word, RelationTypes.Conjunction);
		//as the conjunctions can work in the two directions, get also the other way around
		candidateWords.addAll(intraSentenceWordRelations.getWordsGoverningThis(word, RelationTypes.Conjunction));
		for(Word candidateWord:candidateWords){
			if(candidateWord.getPostag()==REQUIRED_TARGETWORD_POSTAG){
				RuleCheckResult ruleCheckResult = new RuleCheckResult();
				ruleCheckResult.setTriggeringWord(word);
				ruleCheckResult.setTriggeringWordType(TRIGGERING_WORD_TYPE);
				ruleCheckResult.setExtractedWord(candidateWord);
				ruleCheckResult.setExtractedWordType(DoublePropagationWordType.OPINION);
				ruleCheckResult.setRelType(RelationTypes.Conjunction);
				ruleCheckResult.setUsedRule(RULE_NAME);
				ruleCheckResults.add(ruleCheckResult);
			}
		}
		
		return ruleCheckResults;
	}
	
	
}
