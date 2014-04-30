package org.openerproject.double_propagation2.data;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class PlainTextCorpusReader implements CorpusReader{

	@Override
	public String readCorpusFileContent(File corpusFile) {
		try {
			String fileContent=FileUtils.readFileToString(corpusFile, "UTF-8");
			return fileContent;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
