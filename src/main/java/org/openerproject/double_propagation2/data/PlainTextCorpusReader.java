package org.openerproject.double_propagation2.data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openerproject.double_propagation2.utils.CorpusHandlingUtils;

import com.google.common.collect.Lists;

public class PlainTextCorpusReader implements CorpusReader{

	@Override
	public String readCorpusFileContent(String pathToFile) {
		try {
			String fileContent=FileUtils.readFileToString(new File(pathToFile), "UTF-8");
			return fileContent;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<String> readCorpusDirectoryFileContent(String dirPath) {
		try {
			File f=new File(dirPath);
			List<File>files= null;
			if(f.exists() && f.isFile()){
				files=Lists.newArrayList(f);
			}else{
				files = CorpusHandlingUtils.getFilesFromDir(dirPath);
			}
			
			List<String>results=Lists.newArrayList();
			for(File corpusFile:files){
				String fileContent=FileUtils.readFileToString(corpusFile, "UTF-8");
				String[] fileContentLines = fileContent.split("\n");
				results.addAll(Arrays.asList(fileContentLines));
			}
			return results;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
