package org.openerproject.double_propagation2.analysis;

import java.util.List;

import org.openerproject.double_propagation2.model.IntraSentenceWordRelations;

public interface CorpusAnalyzer {

	public List<IntraSentenceWordRelations> analyzeCorpus(List<String>texts, String language);
	
}
