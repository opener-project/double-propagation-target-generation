package org.openerproject.double_propagation2.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

public class CorpusHandlingUtils {

	private static int fileIdCounter=1;
	
	public static List<File>getFilesFromDir(String pathToDir){
		File dir=new File(pathToDir);
		List<File>corpusFiles=Lists.newArrayList();
		if(dir.exists() && dir.isDirectory()){
			for(File f:dir.listFiles()){
				if(f.isFile()){
					corpusFiles.add(f);
				}
			}
			return corpusFiles;
		}else{
			throw new RuntimeException("Provided corpus path does not exist or is not directory: "+dir.getAbsolutePath());
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
	
	public static String generateUniqueFileName(String baseFilePath){
		int extensionSeparatorIndex=baseFilePath.lastIndexOf(".");
		String baseFilePathWithoutExtension=baseFilePath;
		String extension="";
		if(extensionSeparatorIndex==-1){
			extensionSeparatorIndex=baseFilePath.length();
		}else{
			baseFilePathWithoutExtension=baseFilePath.substring(0, extensionSeparatorIndex);
			extension=baseFilePath.substring(extensionSeparatorIndex,baseFilePath.length());
		}
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
		Date date=new Date();
		String formattedDateString=dateFormat.format(date);
		String composedNewBaseFilePath=baseFilePathWithoutExtension+"_"+formattedDateString;
		File f=new File(composedNewBaseFilePath+extension);
		int count=1;
		while(f.exists()){
			f=new File(composedNewBaseFilePath+"_"+count+extension);
			count++;
		}
		return f.getAbsolutePath();
	}
	
}
