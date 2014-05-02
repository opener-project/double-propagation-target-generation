package org.openerproject.double_propagation2.algorithm.rules;

import java.util.List;

import org.openerproject.double_propagation2.model.DoublePropagationWordType;
import org.openerproject.double_propagation2.model.IntraSentenceWordRelations;
import org.openerproject.double_propagation2.model.PartOfSpeech;
import org.openerproject.double_propagation2.model.RelationTypes;
import org.openerproject.double_propagation2.model.Word;

import com.google.common.collect.Lists;

/**
 * This rule is the opposite of the R_1_2 (targets as seed word, opinion as extraction one)
 * @author yo
 *
 */
public class Rule_2_2 implements DoublePropagationRule{

	public static final String RULE_NAME=Rule_2_2.class.getName();
	
	private static final PartOfSpeech REQUIRED_TARGETWORD_POSTAG=PartOfSpeech.ADJECTIVE;
	public static final DoublePropagationWordType TRIGGERING_WORD_TYPE = DoublePropagationWordType.TARGET;
	
	@Override
	public List<RuleCheckResult> applyRule(Word word, IntraSentenceWordRelations intraSentenceWordRelations) {

		List<RuleCheckResult>ruleCheckResults=Lists.newArrayList();
		
		List<Word> intermediateWords = intraSentenceWordRelations.getWordsGoverningThis(word, RelationTypes.NominalSubject);
		for(Word intermediateWord:intermediateWords){
			List<Word> candidateWords = intraSentenceWordRelations.getWordsGovernedByThis(intermediateWord, RelationTypes.DirectObject);
			for(Word candidateWord:candidateWords){
				if(candidateWord.getPostag()==REQUIRED_TARGETWORD_POSTAG){
					RuleCheckResult ruleCheckResult = new RuleCheckResult();
					ruleCheckResult.setTriggeringWord(word);
					ruleCheckResult.setTriggeringWordType(TRIGGERING_WORD_TYPE);
					ruleCheckResult.setExtractedWord(candidateWord);
					ruleCheckResult.setExtractedWordType(DoublePropagationWordType.OPINION);
					ruleCheckResult.setRelType(RelationTypes.NominalSubject);
					ruleCheckResult.setUsedRule(RULE_NAME);
					ruleCheckResults.add(ruleCheckResult);
				}
			}
		}
		return ruleCheckResults;
	}

	
	
}
