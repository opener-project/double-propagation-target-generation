package org.openerproject.double_propagation2.analysis;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openerproject.double_propagation2.model.IntraSentenceWordRelations;
import org.openerproject.double_propagation2.model.PartOfSpeech;
import org.openerproject.double_propagation2.model.RelationTypes;
import org.openerproject.double_propagation2.model.Word;
import org.openerproject.double_propagation2.model.WordToWordRelation;

import com.google.common.collect.Lists;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.util.CoreMap;

public class StanfordBasedCorpusAnalyzer implements CorpusAnalyzer{

	private static Logger log=Logger.getLogger(StanfordBasedCorpusAnalyzer.class);
	
	private StanfordCoreNLP pipeline;
	
	public StanfordBasedCorpusAnalyzer(){
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, parse");
		pipeline = new StanfordCoreNLP(props);
	}
	
	@Override
	public List<IntraSentenceWordRelations> analyzeCorpus(List<String> texts, String language) {
		if(!language.equalsIgnoreCase("en")){
			throw new RuntimeException("StanfordBasedCorpusAnalyzer only admits English for now, requested lang: "+language);
		}
		List<IntraSentenceWordRelations> intraSentenceRelations=Lists.newArrayList();
		int textCount=0;
		int total=texts.size();
		for(String text:texts){
			textCount++;
			log.info("Analyzing text number "+textCount+" of "+total+" (Text length: "+text.length()+")");
			intraSentenceRelations.addAll(getIntraSentenceWordRelations(text));
		}
		return intraSentenceRelations;
	}

	protected List<IntraSentenceWordRelations> getIntraSentenceWordRelations(String text) {
		
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<IntraSentenceWordRelations> intraSentenceWordRelationList = Lists.newArrayList();
		for (CoreMap sentence : sentences) {
			try{
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			IntraSentenceWordRelations intraSentenceWordRelations = mapAnalysis(dependencies);
			intraSentenceWordRelationList.add(intraSentenceWordRelations);
			}catch(Exception e){
				log.warn("Some error analyzing a sentence: "+e.getMessage());
			}
		}
		return intraSentenceWordRelationList;
	}
	
	protected IntraSentenceWordRelations mapAnalysis(SemanticGraph semanticGraph){
		IntraSentenceWordRelations intraSentenceWordRelations=IntraSentenceWordRelations.createRelations();
		for(IndexedWord iw:semanticGraph.descendants(semanticGraph.getFirstRoot())){
			List<SemanticGraphEdge> incomingEdges = semanticGraph.getIncomingEdgesSorted(iw);
			for(SemanticGraphEdge edge:incomingEdges){
				WordToWordRelation wordToWordRelation=WordToWordRelation.createWordToWordRelation(mapIndexedWordToWord(edge.getGovernor()), mapIndexedWordToWord(edge.getDependent()), mapRelationTypes(edge.getRelation().getShortName()));
				intraSentenceWordRelations.addWordToWordRelation(wordToWordRelation);
			}
		}
		//intraSentenceWordRelations.detectAndMergeMultiwords();
		return intraSentenceWordRelations;
	}
	
	//MULTIWORDS WHERE??? DURING THE PROPAGATION?
	protected Word mapIndexedWordToWord(IndexedWord iw){
		Word word=Word.createWord(iw.originalText(), iw.lemma(), mapPartOfSpeech(iw.tag()), iw.beginPosition(), iw.endPosition());
		return word;
	}
	
	protected PartOfSpeech mapPartOfSpeech(String postag){
		PartOfSpeech partOfSpeech=PartOfSpeech.OTHER;
		switch (postag) {
		case "NN":
		case "NNS":
		case "NNP":
		case "NNPS":
			partOfSpeech=PartOfSpeech.NOUN;
			break;
		case "VB":
		case "VBD":
		case "VBG":
		case "VBN":
		case "VBP":
		case "VBZ":
			partOfSpeech=PartOfSpeech.VERB;
			break;
		case "JJ":
		case "JJR":
		case "JJS":
			partOfSpeech=PartOfSpeech.ADJECTIVE;
			break;
		case "RB":
		case "RBR":
		case "RBS":
			partOfSpeech=PartOfSpeech.ADVERB;
			break;
		default:
			partOfSpeech=PartOfSpeech.OTHER;
			break;
		}
		return partOfSpeech;
	}
	
	/*
	 * POSTAGS STANFORD
	 * 
	 * 

        CC Coordinating conjunction
        CD Cardinal number
        DT Determiner
        EX Existential there
        FW Foreign word
        IN Preposition or subordinating conjunction
        JJ Adjective
        JJR Adjective, comparative
        JJS Adjective, superlative
        LS List item marker
        MD Modal
        NN Noun, singular or mass
        NNS Noun, plural
        NNP Proper noun, singular
        NNPS Proper noun, plural
        PDT Predeterminer
        POS Possessive ending
        PRP Personal pronoun
        PRP$ Possessive pronoun
        RB Adverb
        RBR Adverb, comparative
        RBS Adverb, superlative
        RP Particle
        SYM Symbol
        TO to
        UH Interjection
        VB Verb, base form
        VBD Verb, past tense
        VBG Verb, gerund or present participle
        VBN Verb, past participle
        VBP Verb, non­3rd person singular present
        VBZ Verb, 3rd person singular present
        WDT Wh­determiner
        WP Wh­pronoun
        WP$ Possessive wh­pronoun
        WRB Wh­adverb


	 * 
	 */
	
	//CHECK AND EXTEND THIS WITH THE STANFORD TOOLS DOCUMENTATION
	protected RelationTypes mapRelationTypes(String dependencyShortName){
		RelationTypes mappedRelType=RelationTypes.OTHER;
		switch (dependencyShortName) {
		case "amod":
			mappedRelType= RelationTypes.AdjectivalModifier;
			break;
		case "nn":
			mappedRelType= RelationTypes.NominalSubject;
			break;
		case "nsubj":
			mappedRelType= RelationTypes.NominalSubject;
			break;
		case "dobj":
			mappedRelType= RelationTypes.DirectObject;
			break;
		case "conj":
			mappedRelType= RelationTypes.Conjunction;
			break;
		default:
			mappedRelType=RelationTypes.OTHER;
			break;
		}
		return mappedRelType;
	}
	
}
