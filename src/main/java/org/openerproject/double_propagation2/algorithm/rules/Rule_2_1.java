package org.openerproject.double_propagation2.algorithm.rules;

import java.util.List;

import org.openerproject.double_propagation2.model.DoublePropagationWordType;
import org.openerproject.double_propagation2.model.IntraSentenceWordRelations;
import org.openerproject.double_propagation2.model.PartOfSpeech;
import org.openerproject.double_propagation2.model.RelationTypes;
import org.openerproject.double_propagation2.model.Word;

import com.google.common.collect.Lists;

/**
 * The mirrored rule of the 1_1, with the target as the seed word and the opinion as extraction one.
 * e.g.  good screen
 * In the cases of "The screen is big" the relation is nsubj(big,screen) so this
 * is also handled here to extract "screen" as a dependent in the nsubj relation
 * 
 * @author yo
 * 
 */
public class Rule_2_1 implements DoublePropagationRule{

	public static final String RULE_NAME=Rule_2_1.class.getName();
	
	public static final DoublePropagationWordType TRIGGERING_WORD_TYPE = DoublePropagationWordType.TARGET;

	private static final PartOfSpeech REQUIRED_TARGETWORD_POSTAG = PartOfSpeech.ADJECTIVE;
	private static final RelationTypes REQUIRED_RELATION_TYPE_1 = RelationTypes.AdjectivalModifier;
	private static final RelationTypes REQUIRED_RELATION_TYPE_2 = RelationTypes.NominalSubject;

	@Override
	public List<RuleCheckResult> applyRule(Word word, IntraSentenceWordRelations intraSentenceWordRelations) {
		List<RuleCheckResult> ruleCheckResults = Lists.newArrayList();
		// Se trata de comprobar para las palabras relacionadas con la palabra
		// dada si alguna de las relaciones cumple con las condiciones
		// de la regla
		List<Word> candidateWords = intraSentenceWordRelations.getWordsGovernedByThis(word, REQUIRED_RELATION_TYPE_1);
		for (Word candidateWord : candidateWords) {
			if (candidateWord.getPostag() == REQUIRED_TARGETWORD_POSTAG) {
				RuleCheckResult ruleCheckResult = new RuleCheckResult();
				ruleCheckResult.setTriggeringWord(word);
				ruleCheckResult.setTriggeringWordType(TRIGGERING_WORD_TYPE);
				ruleCheckResult.setExtractedWord(candidateWord);
				ruleCheckResult.setExtractedWordType(DoublePropagationWordType.OPINION);
				ruleCheckResult.setRelType(REQUIRED_RELATION_TYPE_1);
				ruleCheckResult.setUsedRule(RULE_NAME);
				ruleCheckResults.add(ruleCheckResult);
			}
		}

		candidateWords = intraSentenceWordRelations.getWordsGoverningThis(word, REQUIRED_RELATION_TYPE_2);
		for (Word candidateWord : candidateWords) {
			if (candidateWord.getPostag() == REQUIRED_TARGETWORD_POSTAG) {
				RuleCheckResult ruleCheckResult = new RuleCheckResult();
				ruleCheckResult.setTriggeringWord(word);
				ruleCheckResult.setTriggeringWordType(TRIGGERING_WORD_TYPE);
				ruleCheckResult.setExtractedWord(candidateWord);
				ruleCheckResult.setExtractedWordType(DoublePropagationWordType.OPINION);
				ruleCheckResult.setRelType(REQUIRED_RELATION_TYPE_2);
				ruleCheckResult.setUsedRule(RULE_NAME);
				ruleCheckResults.add(ruleCheckResult);
			}
		}
		return ruleCheckResults;
	}
	
}
