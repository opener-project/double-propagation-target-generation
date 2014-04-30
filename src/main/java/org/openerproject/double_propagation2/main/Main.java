package org.openerproject.double_propagation2.main;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.openerproject.double_propagation2.multiwords.MultiwordGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;

public class Main {

private static Logger log=Logger.getLogger(Main.class);
	
	private static ApplicationContext appContext;
	
	private static Options options;
	private static Set<String>validLangs=new HashSet<String>(Lists.newArrayList("en","es","fr","it","nl","de"));

	static{
		options = new Options();
		options.addOption("h", "help",false,"Prints this message");
		options.addOption("op","operation",true,"Operation to perform: [collocs|doubleprop]");
		options.addOption("kaf", "already-in-kaf", false, "Flag to state if the input corpus is already in kaf or not (i.e. plain text)");
		options.addOption("d","corpus-dir",true, "Path to the directory containing the corpus files");
		options.addOption("collocsFile","collocations-file",true, "Path to the file containing precomputed collocations");
		options.addOption("collocNgramSize","collocactions-ngram-size",true, "Maximum length, in tokens, of the computed collocations (default 2)");
		options.addOption("collocListSize","collocactions-list-size",true, "Number of (ranked) words that will be used from the collocation list (default 100)");
		options.addOption("noCollocs","no-collocations",false, "Avoid using collocations (do not generate nor read from a given file)");
		options.addOption("lang", "language", true, "Language of the corpus files content, to use when preprocessing them");
		options.addOption("out", "output-folder", true, "Folder in which collocations and/or double-propagation results will be stored");
	}
	
	public static void main(String[] args) {
		if(System.console()==null){
			//if we are not launching from the console (e.g. launching from Eclipse)
			//then we can simulate the arguments
			args=new String[]{"-op","collocs","-d","../../target-properties/EN_REVIEWS_KAF","-lang","en","-out","MAIN_OUTPUT","-kaf"};
		}
		execute(args);
	}
	
	public static void execute(String[]args){
		CommandLineParser parser = new BasicParser();
		try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        if(line.hasOption("op")){
	        	String operation=line.getOptionValue("op");
	        	boolean isKaf=line.hasOption("kaf");
	        	String pathToCorpusDir=line.getOptionValue("d");
	        	String outputPath=line.getOptionValue("out");
	        	if(pathToCorpusDir==null || pathToCorpusDir.length()==0){
	        		throw new RuntimeException("Path to folder with the corpus is missing!");
	        	}
	        	if(outputPath==null || outputPath.length()==0){
	        		log.info("No output path defined, defaulting to current directory");
	        		outputPath=".";//throw new RuntimeException("Path to folder with the corpus is missing!");
	        	}
	        	String lang=line.getOptionValue("lang");
	        	if(!validLangs.contains(lang)){
	        		throw new RuntimeException("Invalid language: "+lang+"\nAllowed languages: "+validLangs.toString());
	        	}
	        	String collocNgramSize=line.getOptionValue("collocNgramSize");
	        	if(collocNgramSize==null || collocNgramSize.length()==0){
	        		log.info("No collocNgramSize set, using default: 2");
	        		collocNgramSize="2";
	        	}
	        	String collocListSizeLimit=line.getOptionValue("collocListSize");
	        	if(collocListSizeLimit==null || collocListSizeLimit.length()==0){
	        		log.info("No collocListSizeLimit set, using default: 100");
	        		collocListSizeLimit="100";
	        	}
	        	//SemanticVectorProcess semanticVectorProcess=(SemanticVectorProcess) getBeanFromContainer("SemanticVectorProcess");
	        	if(operation.equals("collocs")){
	        		MultiwordGenerator multiwordGenerator=(MultiwordGenerator) getBeanFromContainer("multiwordGenerator");
	        		log.info("Launching multiword generator with params: corpus-dir="+pathToCorpusDir+" ; lang="+lang+" ; alreadyInKaf="+isKaf+" ; output-folder"+outputPath);
	        		multiwordGenerator.generateMultiwords(pathToCorpusDir, lang, isKaf, Integer.parseInt(collocNgramSize), Integer.parseInt(collocListSizeLimit), outputPath);
	        	}
	        	
	        }else{
	        	HelpFormatter formatter = new HelpFormatter();
	        	formatter.printHelp( "java -jar [NAME_OF_THE_JAR] [OPTION]", options );
	        }
	        
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected static Object getBeanFromContainer(String beanName){
		if(appContext==null){
			appContext=new ClassPathXmlApplicationContext("spring-config.xml");
		}
		return appContext.getBean(beanName);
	}
	
}
