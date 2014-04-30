package org.openerproject.double_propagation2.utils;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

public class CorpusHandlingUtils {

	private static int fileIdCounter=1;
	
	public static List<File>getFilesFromDir(String pathToDir){
		File dir=new File(pathToDir);
		List<File>corpusFiles=Lists.newArrayList();
		if(dir.exists()){
			for(File f:dir.listFiles()){
				if(f.isFile()){
					corpusFiles.add(f);
				}
			}
			return corpusFiles;
		}else{
			throw new RuntimeException("Provided corpus directory does not exists: "+dir.getAbsolutePath());
		}
	}
	
	public static void writeTextsToFiles(List<String>texts,String parentFolderPath){
		for(int i=0;i<texts.size();i++){
			String text=texts.get(i);
			File f=new File(parentFolderPath+File.separator+fileIdCounter+".txt");
			fileIdCounter++;
			try{
				FileUtils.write(f, text, "UTF-8");
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
	}
	
}
