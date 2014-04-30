package org.openerproject.double_propagation2.multiwords;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.openerproject.double_propagation2.analysis.AbstractTagsetMapper;

import org.openerproject.double_propagation2.data.CorpusReader;
import org.openerproject.double_propagation2.model.PartOfSpeech;
import org.openerproject.double_propagation2.utils.CorpusHandlingUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MultiwordGenerator {

	private static Logger log=Logger.getLogger(MultiwordGenerator.class);
	public static final String MULTIWORDS_FILE_NAME="generated-collocations.txt";
	
	private CorpusReader corpusReader;
	private DocumentPreprocessor documentPreprocessor;
	private NgramCountTable ngramCountTable;
	private AbstractTagsetMapper tagsetMapper;

	public void generateMultiwords(String pathToDir, String language, boolean isKaf,int collocNGramSize, int listSizeLimit, String outputFilePath) {
		ngramCountTable = NgramCountTable.createTable();
		List<File> corpusFiles = CorpusHandlingUtils.getFilesFromDir(pathToDir);
		int textCount = 1;
		int numTexts=corpusFiles.size();
		for (File corpusFile : corpusFiles) {
			log.debug("Obtaining ngrams from text: " + (textCount++) +" of "+numTexts);
			String content = corpusReader.readCorpusFileContent(corpusFile);
			List<String> preprocessedTexts = documentPreprocessor.preprocessDocument(content, language, isKaf);
			addGramsToTable(preprocessedTexts,collocNGramSize);
		}

		scoreGrams();
		List<NGramAndScore> rankedGrams = rankGramsAndCropList(listSizeLimit);
		File outputfile=new File(outputFilePath+File.separator+MULTIWORDS_FILE_NAME);
		try {
			FileUtils.writeLines(outputfile,"UTF-8", rankedGrams);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void addGramsToTable(List<String> preprocessedTexts, int maxNgramSize) {
		for (String processedText : preprocessedTexts) {
			List<String> grams = obtainGrams(processedText, 2, maxNgramSize);
			for (String gram : grams) {
				ngramCountTable.incrementCount(gram);
				String[] split = gram.split(" ", 2);
				if (split.length > 1) {
					String a = split[0];
					String b = split[1];
					ngramCountTable.addAfterToken(a, b);
					ngramCountTable.addBeforeToken(b, a);
				}
			}
		}
	}

	protected List<String> obtainGrams(String input, int min, int max) {
		try {
			Reader reader = new StringReader(input);
			WhitespaceAnalyzer whitespaceAnalyzer = new WhitespaceAnalyzer(Version.LUCENE_47);
			ShingleAnalyzerWrapper shingleAnalyzer = new ShingleAnalyzerWrapper(whitespaceAnalyzer, min, max);
			TokenStream stream = shingleAnalyzer.tokenStream("contents", reader);
			stream.reset();
			CharTermAttribute charTermAttribute = stream.getAttribute(CharTermAttribute.class);
			List<String> gram = new ArrayList<String>();
			while (stream.incrementToken()) {
				gram.add(charTermAttribute.toString());
			}
			stream.end();
			stream.close();
			shingleAnalyzer.close();
			return gram;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void scoreGrams() {
		for (String gram : ngramCountTable.getRowsSet()) {
			System.err.println("Calculating score for: " + gram);
			// Only for multiwords
			if (gram.contains(" ")) {
				int fullGramCount = ngramCountTable.getCount(gram);
				String[] split = gram.split(" ", 2);
				String a = split[0];
				String b = split[1];
				int aCount = ngramCountTable.getCount(a);
				int bCount = ngramCountTable.getCount(b);

				int k11 = fullGramCount;
				int k12 = bCount - fullGramCount;
				int k21 = aCount - fullGramCount;
				int k22 = ngramCountTable.getOverallCount() - (aCount + bCount - fullGramCount);
				// HERE SHOULD COME THE TWEAKING OF THE NUMBERS USING THE
				// COMBINATION NUMBER
				int numberOfCombinationsA = ngramCountTable.getNumberOfDifferentAfterTokens(a);
				int numberOfCombinationsB = ngramCountTable.getNumberOfDifferentBeforeTokens(b);
				k12 *= Math.pow(Math.log(numberOfCombinationsB), 1);// +=(k12*Math.log1p(numberOfCombinationsB));
				k21 *= Math.pow(Math.log(numberOfCombinationsA), 2);// +=(k21*Math.log1p(numberOfCombinationsA));
				// /////////////////////////////////////////////////////////////////////////
				double llr = Mahout08LogLikelihoodRatio.rootLogLikelihoodRatio(k11, k12, k21, k22);// .logLikelihoodRatio(k11,
																									// k12,
																									// k21,
																									// k22);
				ngramCountTable.setScore(gram, llr);
			}
		}
	}

	protected List<NGramAndScore> rankGramsAndCropList(int listSizeLimit) {
		Set<NGramAndScore> rankedNgrams = Sets.newTreeSet();
		for (String gram : ngramCountTable.getRowsSet()) {
			// Only for multiwords
			if (gram.contains(" ")) {
				double score = ngramCountTable.getScore(gram);
				String[] split = gram.split(" ", 2);
				String a = split[0];
				String b = split[1];
				int numberOfCombinationsA = ngramCountTable.getNumberOfDifferentAfterTokens(a);
				int numberOfCombinationsB = ngramCountTable.getNumberOfDifferentBeforeTokens(b);
				NGramAndScore nGramAndScore = new NGramAndScore(gram, score, numberOfCombinationsA,
						numberOfCombinationsB);
				rankedNgrams.add(nGramAndScore);
			}
		}
		int gramCount = 1;
		int limit = listSizeLimit;
		List<NGramAndScore>rankedNgramList=Lists.newArrayList();
		for (NGramAndScore nGramAndScore : rankedNgrams) {
			if (validNgram(nGramAndScore)) {
				log.debug(gramCount + " - " + nGramAndScore);
				rankedNgramList.add(nGramAndScore);
				if (gramCount >= limit) {
					break;
				}
				gramCount++;
			}

		}
		return rankedNgramList;
	}

	public boolean validNgram(NGramAndScore nGramAndScore) {
		try{
		String firstWordPos = nGramAndScore.getGram().split(" ")[0].split("_")[1];
		String lastWordPos = nGramAndScore.getGram().split(" ")[nGramAndScore.getGram().split(" ").length - 1]
				.split("_")[1];
		//Some basic filtering rules, but these should be configurable from outside, not harcoded
		if((tagsetMapper.isPosType(firstWordPos, PartOfSpeech.NOUN)  || tagsetMapper.isPosType(firstWordPos, PartOfSpeech.ADJECTIVE) )
				&& 
				(tagsetMapper.isPosType(lastWordPos, PartOfSpeech.NOUN) || tagsetMapper.isPosType(lastWordPos, PartOfSpeech.ADJECTIVE))){
			return true;
		}else{
			return false;
		}
		}catch(Exception e){
			log.warn("Problem with: "+nGramAndScore);
			return false;
		}
	}

	public void setCorpusReader(CorpusReader corpusReader) {
		this.corpusReader = corpusReader;
	}

	public void setDocumentPreprocessor(DocumentPreprocessor documentPreprocessor) {
		this.documentPreprocessor = documentPreprocessor;
	}

	public void setTagsetMapper(AbstractTagsetMapper tagsetMapper) {
		this.tagsetMapper = tagsetMapper;
	}

}
