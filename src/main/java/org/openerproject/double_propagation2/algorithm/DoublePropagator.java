package org.openerproject.double_propagation2.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openerproject.double_propagation2.algorithm.rules.DoublePropagationRule;
import org.openerproject.double_propagation2.algorithm.rules.RuleCheckResult;
import org.openerproject.double_propagation2.algorithm.rules.Rule_1_1;
import org.openerproject.double_propagation2.algorithm.rules.Rule_1_2;
import org.openerproject.double_propagation2.algorithm.rules.Rule_2_1;
import org.openerproject.double_propagation2.algorithm.rules.Rule_2_2;
import org.openerproject.double_propagation2.algorithm.rules.Rule_3_1;
import org.openerproject.double_propagation2.algorithm.rules.Rule_3_2;
import org.openerproject.double_propagation2.algorithm.rules.Rule_4_1;
import org.openerproject.double_propagation2.algorithm.rules.Rule_4_2;
import org.openerproject.double_propagation2.graph.DoublePropagationGraph;
import org.openerproject.double_propagation2.graph.DpNode;
import org.openerproject.double_propagation2.model.DoublePropagationWordType;
import org.openerproject.double_propagation2.model.IntraSentenceWordRelations;
import org.openerproject.double_propagation2.model.Word;

import com.google.common.collect.Lists;

public class DoublePropagator {

	private static Logger log=Logger.getLogger(DoublePropagator.class);
	
	/*
	 * REQUIRED FIELDS
	 * 
	 * - Visited words register
	 * - Graph
	 * 
	 * - Initial seeds require be part of the state? They are only used at the very beginning
	 * 		-No, they are used along the algorithm, they are used as the expanded sets
	 * 
	 * - A variable to track the current word coordinates? That was the old mechanism...
	 * 
	 */
	//private String language;
	private DoublePropagationGraph dpGraph;
	private Set<String>alreadyVisitedWords;
//	private Set<Word>opinionWords;
//	private Set<Word>targetWords;
	private List<DoublePropagationRule>opinionWordTriggeredRules;
	private List<DoublePropagationRule>targetWordTriggeredRules;
	private CorpusAnalyzer corpusAnalyzer;
	
	public DoublePropagator(){
		super();
	}
	
	public void executeDoublePropagation(List<String>corpus,Set<Word>seedOpinionWords,Set<Word>seedtargetWords,String language){
		initialize(seedOpinionWords,seedtargetWords);
		List<IntraSentenceWordRelations> intraSentenceWordRelationList = analyzedCorpus(corpus, language);
		executeDoublePropatation(intraSentenceWordRelationList, seedOpinionWords, seedtargetWords);
		
	}
	
	public void executeDoublePropatation(List<IntraSentenceWordRelations>intraSentenceWordRelationList,Set<Word>seedOpinionWords,Set<Word>seedtargetWords){
		int previousNodesCount = 0;
		int round = 0;
		while (previousNodesCount != dpGraph.getNodesCount()) {
			previousNodesCount = dpGraph.getNodesCount();
			round++;
			log.info("Starting double-propagation round "+round+", current number of nodes: "+dpGraph.getNodesCount());
			for(int sentenceNumber=0;sentenceNumber<intraSentenceWordRelationList.size();sentenceNumber++){
				IntraSentenceWordRelations intraSentenceWordRelations = intraSentenceWordRelationList.get(sentenceNumber);
				try{
				processSentence(intraSentenceWordRelations, sentenceNumber);
				}catch(Exception e){
					log.warn("Problem processing sentence "+sentenceNumber,e);
				}
			}
		}
		dpGraph.calculateNodeScores();
		
		///////////////
		log.info("Propagation process completed: "+dpGraph.getNodesCount()+" nodes generated (opinions + targets)");
		///////////////
		
	}
	
	protected void processSentence(IntraSentenceWordRelations intraSentenceWordRelations, int sentenceNumber){
		
		for (int wordNumber = 0; wordNumber < intraSentenceWordRelations.getNumberOfWordsInThisSentence(); wordNumber++) {
			Word word = intraSentenceWordRelations.getWord(wordNumber);
			//The condition to enter the processing is not being a visited word, and being opinion word or target word
			if (!isAlreadyVisitedWord(wordNumber, sentenceNumber) && (isOpinionWord(word)||isTargetWord(word))) {
				List<RuleCheckResult>ruleCheckResults=Lists.newArrayList();
				DpNode sourceNode=null;
				if (isOpinionWord(word)) {
					ruleCheckResults.addAll(processOpinionWord(word, intraSentenceWordRelations, wordNumber, sentenceNumber));
					sourceNode=dpGraph.createOrRetrieveDpNode(word, DoublePropagationWordType.OPINION);
				} else if (isTargetWord(word)) {
					ruleCheckResults.addAll(processTargetWord(word, intraSentenceWordRelations, wordNumber, sentenceNumber));
					sourceNode=dpGraph.createOrRetrieveDpNode(word, DoublePropagationWordType.TARGET);
				}
				//HERE IS THE ONLY PLACE THE INCREASE COUNT MUST BE PERFORMED FOR A NODE
				sourceNode.increaseCount();
				////////////////////////////////////////////////////////////////////////
				//if(!ruleCheckResults.isEmpty()){
					//add results to the dpgraph
				for (RuleCheckResult ruleCheckResult : ruleCheckResults) {
					// NOTE: Here there is a arguable decision of not adding a relation if the target word is a "already visited word"
					Word extractedWord=ruleCheckResult.getExtractedWord();
					int targetWordNumber = intraSentenceWordRelations.getWordNumber(extractedWord);
					if (!isAlreadyVisitedWord(targetWordNumber, sentenceNumber)) {
						DpNode targetNode = dpGraph.createOrRetrieveDpNode(ruleCheckResult.getExtractedWord(),
								ruleCheckResult.getExtractedWordType());
						dpGraph.createOrIncreaseDpEdge(sourceNode, targetNode, ruleCheckResult.getUsedRule());
					}
				}
				//}
			}
				
		}
		
	}
	
	protected List<RuleCheckResult> processOpinionWord(Word word, IntraSentenceWordRelations intraSentenceWordRelations,int wordNumber,int sentenceNumber){
		return processWord(word, intraSentenceWordRelations, opinionWordTriggeredRules, wordNumber,sentenceNumber);
	}
	
	protected List<RuleCheckResult> processTargetWord(Word word, IntraSentenceWordRelations intraSentenceWordRelations,int wordNumber,int sentenceNumber){
		return processWord(word, intraSentenceWordRelations, targetWordTriggeredRules, wordNumber,sentenceNumber);
	}
	
	protected List<RuleCheckResult> processWord(Word word, IntraSentenceWordRelations intraSentenceWordRelations, List<DoublePropagationRule>rules,int wordNumber,int sentenceNumber){
		List<RuleCheckResult>ruleCheckingResults=Lists.newArrayList();
		for(DoublePropagationRule dpRule:rules){
			ruleCheckingResults.addAll(dpRule.applyRule(word, intraSentenceWordRelations));
		}
		setAsAlreadyVisitedWord(wordNumber, sentenceNumber);
		return ruleCheckingResults;
	}
	
	private boolean isAlreadyVisitedWord(int wordNumber,int sentenceNumber){
		return alreadyVisitedWords.contains(getWordCoordinates(wordNumber, sentenceNumber));
	}
	
	private void setAsAlreadyVisitedWord(int wordNumber,int sentenceNumber){
		this.alreadyVisitedWords.add(getWordCoordinates(wordNumber, sentenceNumber));
	}
	
	private String getWordCoordinates(int wordNumber, int sentenceNumber){
		return "w"+wordNumber+"_s"+sentenceNumber;
	}
	
	private boolean isOpinionWord(Word word){
		return dpGraph.containsOpinionWordNode(word);
	}
	
	private boolean isTargetWord(Word word){
		return dpGraph.containsTargetWordNode(word);
	}
	
	private void initialize(Set<Word>seedOpinionWords,Set<Word>seedTargetWords){
		this.dpGraph=new DoublePropagationGraph();
		addSeedOpinionWordsToGraph(seedOpinionWords);
		addSeedTargetWordsToGrah(seedTargetWords);
		this.alreadyVisitedWords=new HashSet<String>();
//		this.opinionWords=seedOpinionWords;
//		this.targetWords=seedtargetWords;
		fillOpinionWordTriggeredRules();
		fillTargetWordTriggeredRules();
	}
	
	private void addSeedOpinionWordsToGraph(Set<Word>seedOpinionWords){
		for(Word word:seedOpinionWords){
			dpGraph.createOrRetrieveDpNode(word, DoublePropagationWordType.OPINION);
		}
	}
	
	private void addSeedTargetWordsToGrah(Set<Word>seedTargetWords){
		for(Word word:seedTargetWords){
			dpGraph.createOrRetrieveDpNode(word, DoublePropagationWordType.TARGET);
		}
	}
	
	private void fillOpinionWordTriggeredRules(){
		this.opinionWordTriggeredRules=Lists.newArrayList();
		this.opinionWordTriggeredRules.add(new Rule_1_1());
		this.opinionWordTriggeredRules.add(new Rule_1_2());
		this.opinionWordTriggeredRules.add(new Rule_4_1());
		this.opinionWordTriggeredRules.add(new Rule_4_2());
	}
	
	private void fillTargetWordTriggeredRules(){
		this.targetWordTriggeredRules=Lists.newArrayList();
		this.targetWordTriggeredRules.add(new Rule_2_1());
		this.targetWordTriggeredRules.add(new Rule_2_2());
		this.targetWordTriggeredRules.add(new Rule_3_1());
		this.targetWordTriggeredRules.add(new Rule_3_2());
	}
	
	private List<IntraSentenceWordRelations>analyzedCorpus(List<String>corpus,String language){
		return corpusAnalyzer.analyzeCorpus(corpus, language);
	}
	
	public void setCorpusAnalyzer(CorpusAnalyzer corpusAnalyzer) {
		this.corpusAnalyzer = corpusAnalyzer;
	}
	
	
	
}
