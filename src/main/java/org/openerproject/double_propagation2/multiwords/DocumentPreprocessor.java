package org.openerproject.double_propagation2.multiwords;

import java.util.List;

public interface DocumentPreprocessor {

	public List<String> preprocessDocument(String content, String language,boolean isKaf);
	
}
