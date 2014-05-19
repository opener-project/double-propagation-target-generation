package org.openerproject.double_propagation2.data;

import java.util.List;

public interface CorpusReader {

	public String readCorpusFileContent(String corpusFilePath);
	
	public List<String> readCorpusDirectoryFileContent(String dirPath);
	
}
