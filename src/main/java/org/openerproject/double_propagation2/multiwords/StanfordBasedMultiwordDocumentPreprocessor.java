package org.openerproject.double_propagation2.multiwords;

import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordBasedMultiwordDocumentPreprocessor implements DocumentPreprocessor{

	private static Logger log=Logger.getLogger(StanfordBasedMultiwordDocumentPreprocessor.class);
	
private StanfordCoreNLP pipeline;
	
	public StanfordBasedMultiwordDocumentPreprocessor() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}
	
	@Override
	public List<String> preprocessDocument(String content, String language, boolean isKaf) {
		if(isKaf){
//			List<String> preprocessedResults = getPreprocessedContentFromKAF(content);
//			return preprocessedResults;
			throw new RuntimeException("Configuration points to the StanfordBasedMultiwordDocumentPreprocessor, but the input is signaled as being KAF!");
		}else{
			//throw new RuntimeException("Let's assume that for now only KAF is accepted!");
			log.info("Preprocessing using Stanford NLP Tools...");
			return getPreprocessedContentUsingStanfordTools(content);
		}
	}
	
	/**
	 * Get a list of String, one per sentence, with the format lemma1_pos1 lemma2_pos2 ... lemmaN_posN
	 * @param kaf
	 * @return
	 */
	protected List<String> getPreprocessedContentUsingStanfordTools(String plainText){
		Annotation document = new Annotation(plainText);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		List<String>results=Lists.newArrayList();
		for(CoreMap sentence:sentences){
			StringBuffer sb=new StringBuffer();
			for(CoreLabel token:sentence.get(TokensAnnotation.class)){
				sb.append(token.lemma());
				sb.append("_");
				sb.append(token.tag());
				sb.append(" ");
			}
			results.add(sb.toString().trim());
		}
		return results;
	}

}
