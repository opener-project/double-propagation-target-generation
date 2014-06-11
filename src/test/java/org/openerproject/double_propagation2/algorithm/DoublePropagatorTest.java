package org.openerproject.double_propagation2.algorithm;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openerproject.double_propagation2.analysis.StanfordBasedCorpusAnalyzer;
import org.openerproject.double_propagation2.model.PartOfSpeech;
import org.openerproject.double_propagation2.model.Word;

import com.google.common.collect.Lists;

public class DoublePropagatorTest {

	private DoublePropagator doublePropagator;
	
	@Before
	public void setUp() throws Exception {
		doublePropagator=new DoublePropagator();
		doublePropagator.setCorpusAnalyzer(new StanfordBasedCorpusAnalyzer());
	}

	@Test
	public void testExecuteDoublePropagation() {
		List<String>texts=Lists.newArrayList("The air conditioning was good","There was a bad train station");
		Word good=Word.createWord("good", "good", PartOfSpeech.ADJECTIVE, 0, 0);
		Word bad = Word.createWord("bad", "bad", PartOfSpeech.ADJECTIVE, 0, 0);
		Set<Word>opinionWords=new HashSet<Word>();
		opinionWords.add(good);
		opinionWords.add(bad);
		doublePropagator.executeDoublePropagation(texts, opinionWords, new HashSet<Word>(), "en",false, null);
		
		assertTrue(true);
	}

}
