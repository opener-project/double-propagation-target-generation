package org.openerproject.double_propagation2.multiwords;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openerproject.double_propagation2.model.Word;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * This class loads a multiword list, and checks if a list of single words are part of a multiword
 * @author yo
 *
 */
public class MultiwordChecker {

	private static Multimap<String,List<String>>multiwordMap;//=createMultiwordMap(null);
	
	private MultiwordChecker(List<String>multiwords){
		multiwordMap=createMultiwordMap(multiwords);
	}
	
	/**
	 * Static factory method to retrieve an instance with the given multiword terms loaded
	 * @param multiwords
	 * @return
	 */
	public static MultiwordChecker createMultiworChecker(List<String>multiwords){
		return new MultiwordChecker(multiwords);
	}
	
	/**
	 * Receives a list with the words of a sentence in order. Returns another list with the words pertaining to a multiword merged
	 * @param wordsInASentence
	 * @return
	 */
	public List<Word> checkMultiwords(List<Word>wordsInASentence){
		List<Word>resultingWords=Lists.newArrayList();
		for(int i=0;i<wordsInASentence.size();i++){
			Word currentWord=wordsInASentence.get(i);
			Collection<List<String>> multiwordCandidates = multiwordMap.get(currentWord.getLemma());
			Iterator<List<String>> multiWordCandidatesIterator=multiwordCandidates.iterator();
			boolean multiwordMatches=false;
			while(multiWordCandidatesIterator.hasNext()){
				List<String>multiwordComponents=multiWordCandidatesIterator.next();
				List<Word>followingWordsInSentence=wordsInASentence.subList(i, Math.min(i+multiwordComponents.size(), wordsInASentence.size()));
				multiwordMatches=check(multiwordComponents, followingWordsInSentence);
				if(multiwordMatches){
					Word multiword=Word.createMultiword(followingWordsInSentence);
					resultingWords.add(multiword);
					i+=followingWordsInSentence.size()-1;
					break;
				}
			}
			if(!multiwordMatches){
				resultingWords.add(currentWord);
			}
		}
		return resultingWords;
	}
	
	/**
	 * Receives a list of the words composing a multiword, and the sublist of words in a sentence after a certain index. Returns if the sublist is the given multiword
	 * @param multiwordComponents
	 * @param followingWordsInSentence
	 * @return
	 */
	private boolean check(List<String>multiwordComponents,List<Word>followingWordsInSentence){
		if(multiwordComponents.size()!=followingWordsInSentence.size()){
			return false;
		}
		for(int i=0;i<multiwordComponents.size();i++){
			if(!multiwordComponents.get(i).equalsIgnoreCase(followingWordsInSentence.get(i).getLemma())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Read the list of strings containing multiword and creates a multimap mapping the beginnings of the multiword to the possible endings
	 * @param multiwords
	 * @return
	 */
	private static Multimap<String,List<String>> createMultiwordMap(List<String>multiwords){
		////////////////////////////AD-HOC STUFF TO LOAD MULTIWORDS, REMOVE LATER!
//		File mwFile=new File("MAIN_OUTPUT/generated-collocations.txt");
//		multiwords=Lists.newArrayList();
//		try {
//			List<String> lines = FileUtils.readLines(mwFile);
//			for(String line:lines){
//				multiwords.add(line.split("->")[1].trim());
//			}
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
		/////////////////////////////////////////////////////////////////////
		Multimap<String,List<String>> multiwordMap=ArrayListMultimap.create();
		for(String multiword:multiwords){
			List<String>components=Arrays.asList(multiword.split(" "));
			String firstComp=components.get(0);
			multiwordMap.put(firstComp, components);
		}
		return multiwordMap;
	}
}
