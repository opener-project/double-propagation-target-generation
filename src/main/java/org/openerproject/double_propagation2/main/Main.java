package org.openerproject.double_propagation2.main;

import java.io.File;
import java.util.Arrays;
//import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
//import org.apache.commons.cli.Option;
//import org.apache.commons.cli.OptionBuilder;
//import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.openerproject.double_propagation2.algorithm.DoublePropagator;
import org.openerproject.double_propagation2.data.PlainTextCorpusReader;
import org.openerproject.double_propagation2.graph.DoublePropagationGraph;
import org.openerproject.double_propagation2.graph.utils.DoublePropagationGraphObjectSerializer;
import org.openerproject.double_propagation2.graph.utils.DoublePropagationGraphSerializer;
import org.openerproject.double_propagation2.model.PartOfSpeech;
import org.openerproject.double_propagation2.model.Word;
import org.openerproject.double_propagation2.multiwords.MultiwordGenerator;
import org.openerproject.double_propagation2.multiwords.MultiwordReader;
import org.openerproject.double_propagation2.multiwords.NGramAndScore;
import org.openerproject.double_propagation2.utils.CorpusHandlingUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;

@Deprecated
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
		options.addOption("mwf","collocations-file",true, "Path to the file containing precomputed collocations");
		options.addOption("mwns","collocactions-ngram-size",true, "Maximum length, in tokens, of the computed collocations (default 2)");
		options.addOption("mwls","collocactions-list-size",true, "Number of (ranked) words that will be used from the collocation list (default 100)");
		options.addOption("nomw","no-collocations",false, "Avoid using collocations (do not generate nor read from a given file)");
		options.addOption("lang", "language", true, "Language of the corpus files content, to use when preprocessing them");
		options.addOption("out", "output-folder", true, "Folder in which collocations and/or double-propagation results will be stored");
		
		////////////
		//options=loadOptions();
	}
	
	@SuppressWarnings("static-access")
	private static Options loadOptions(){
		Options options=new Options();
		OptionGroup operations=new OptionGroup();
		Option multiwordDetection=OptionBuilder.withLongOpt("multiword-generation").withDescription("Generates a multiword list from the input courpus").create("mw");
		Option doublepropagatation=OptionBuilder.withLongOpt("double-propagation").withDescription("Executes the double propagation algorithm on the input corpus").create("dp");
		operations.addOption(multiwordDetection);
		operations.addOption(doublepropagatation);
		operations.setRequired(true);
		options.addOptionGroup(operations);
		Option alreadyInKAF=OptionBuilder.withLongOpt("already-in-kaf").withDescription("States whether the input is already in KAF or plain text. Default is false (i.e. plain text)").create("k");
		options.addOption(alreadyInKAF);
		Option inputDir=OptionBuilder.withLongOpt("input-dir").hasArg(true).withDescription("Directory containing the input corpus").create("i");
		inputDir.setRequired(true);
		options.addOption(inputDir);
		Option outputDir=OptionBuilder.withLongOpt("output-dir").hasArg(true).withDescription("Directory to write the output of the processes").create("o");
		outputDir.setRequired(true);
		options.addOption(outputDir);
		Option multiwordsFile=OptionBuilder.withLongOpt("multiword-file").hasArg(true).withDescription("File containing a multiword list, one per line. If no file provided the multiwords will be generated on the fly.").create("mwf");
		options.addOption(multiwordsFile);
		Option multiwordNgramSize=OptionBuilder.withLongOpt("multiword-ngram-size").hasArg(true).withDescription("Max ngram per generated multiword. Default 2.").create("mwns");
		options.addOption(multiwordNgramSize);
		Option multiwordListLimit=OptionBuilder.withLongOpt("multiword-list-limit").hasArg(true).withDescription("Max number of top ranked multiword employed. Default 100.").create("mwll");
		options.addOption(multiwordListLimit);
		Option disableMultiwords=OptionBuilder.hasArg(false).withLongOpt("disable-multiwords").withDescription("Disables multiwords during double propagation process").create("nomw");
		options.addOption(disableMultiwords);
		Option language=OptionBuilder.withLongOpt("language").hasArg(true).withDescription("Language on the input corpus").create("l");
		language.setRequired(true);
		options.addOption(language);
		return options;
	}
	
//	@SuppressWarnings("static-access")
//	private static Options createOptions(){
//		Options options=new Options();
//		
//		OptionGroup operationTypes=new OptionGroup();
//		operationTypes.setRequired(true);
//		Option doubleprop=OptionBuilder.withArgName("dp").withLongOpt("double-propatation").isRequired().withDescription("Execute double propagation method on the given corpus").create();
//		Option collocs=OptionBuilder.withArgName("mw").withLongOpt("multiword-generation").isRequired().withDescription("Generate multiword collocations").create();
//		operationTypes.addOption(doubleprop);
//		operationTypes.addOption(collocs);
//		
//		Option corpusDir=OptionBuilder.withArgName("d").withLongOpt("corpus-dir").isRequired().hasArg().withDescription("Folder with the corpus").create();
//		Option alreadyInKaf=OptionBuilder.withArgName("").isRequired().withDescription("").withType(Integer.class).create();
//		
//		OptionGroup collocsSource=new OptionGroup();
//		Option collocsFile=OptionBuilder.withArgName("d").withLongOpt("corpus-dir").isRequired().hasArg().withDescription("File").create();
//		Option calculateMultiwordsOnTheFly=OptionBuilder.withArgName("d").withLongOpt("corpus-dir").isRequired().hasArg().withDescription("File").create();
//		collocsSource.addOption(collocsFile);
//		collocsSource.addOption(calculateMultiwordsOnTheFly);
//		
//		Option collocNgramSize=OptionBuilder.withArgName("d").withLongOpt("corpus-dir").isRequired().hasArg().withDescription("Maximum size of the generated multiword. Setting less than 2 disables multiword generation. Default is 2.").create();
//		Option collocListSize=OptionBuilder.withArgName("d").withLongOpt("corpus-dir").isRequired().hasArg().withDescription("Folder with the corpus").create();
//		
//		Option lang=OptionBuilder.withArgName("d").withLongOpt("corpus-dir").isRequired().hasArg().withDescription("File").create();
//		
//		Option outputFolder=OptionBuilder.withArgName("d").withLongOpt("corpus-dir").isRequired().hasArg().withDescription("File").create();
//		
//		options.addOptionGroup(operationTypes);
//		options.addOption(alreadyInKaf);
//		options.addOption(corpusDir);
//		options.addOptionGroup(collocsSource);
//		options.addOption(collocNgramSize);
//		options.addOption(collocListSize);
//		options.addOption(lang);
//		options.addOption(outputFolder);
//		
//		return options;
//	}
	
//	/**
//	 * Options that are required both for multiword and double-prop
//	 * @return
//	 */
//	@SuppressWarnings("static-access")
//	private static Options createTopLevelOptions(){
//		OptionGroup operationTypes=new OptionGroup();
//		operationTypes.setRequired(true);
//		Option doubleprop=OptionBuilder.withLongOpt("double-propatation").isRequired().withDescription("Execute double propagation method on the given corpus").create("dp");
//		Option collocs=OptionBuilder.withLongOpt("multiword-generation").isRequired().withDescription("Generate multiword collocations").create("mw");
//		operationTypes.addOption(doubleprop);
//		operationTypes.addOption(collocs);
//		
//		Option corpusDir=OptionBuilder.withLongOpt("corpus-dir").isRequired().hasArg().withDescription("Folder with the corpus").create("i");
//		Option alreadyInKaf=OptionBuilder.withLongOpt("already-in-kaf").withDescription("In the corpus is already in KAF format").withType(Integer.class).create("kaf");
//		Option outputFolder=OptionBuilder.withLongOpt("output-dir").isRequired().hasArg().withDescription("File").create("o");
//		Option lang=OptionBuilder.withLongOpt("lang").isRequired().hasArg().withDescription("File").create("l");
//		
//		Options options=new Options();
//		options.addOptionGroup(operationTypes);
//		options.addOption(corpusDir);
//		options.addOption(alreadyInKaf);
//		options.addOption(outputFolder);
//		options.addOption(lang);
//		return options;
//	}
//	
//	@SuppressWarnings("static-access")
//	private static Options createMultiwordGenerationOptions(){
//		Option collocNgramSize=OptionBuilder.withLongOpt("multiword-ngram-size").isRequired().hasArg().withDescription("Maximum size of the generated multiword. Setting less than 2 disables multiword generation.").create('n');
//		Option collocListSize=OptionBuilder.withLongOpt("multiword-list-size").isRequired().hasArg().withDescription("Number of top elements of the generated multiword list that will keeped").create("s");
//		Options options=new Options();
//		options.addOption(collocNgramSize);
//		options.addOption(collocListSize);
//		return options;
//	}
//	
//	@SuppressWarnings("static-access")
//	private static Options createDoublePropagationExecutionOptions(){
//		OptionGroup collocsSource=new OptionGroup();
//		Option collocsFile=OptionBuilder.withLongOpt("multiword-input-file").hasArg().withDescription("Input file containing multiword. If it is not specified but multiword options has bee").create("mwfile");
//		Option noCollocs=OptionBuilder.withLongOpt("disable-multiword").hasArg().withDescription("Disable multiword support").create("mwoff");
//		collocsSource.setRequired(true);
//		collocsSource.addOption(collocsFile);
//		collocsSource.addOption(noCollocs);
//		
//		Options options=new Options();
//		options.addOptionGroup(collocsSource);
//		return options;
//	}
//	
//	private static Options createHelpAndVersionOptions(){
//		OptionGroup helpAndVersion=new OptionGroup();
//		Option helpOpt=new Option("h", "Prints help");
//		Option versionOpt=new Option("v","Version info");
//		helpAndVersion.addOption(helpOpt);
//		helpAndVersion.addOption(versionOpt);
//		Options options=new Options();
//		options.addOptionGroup(helpAndVersion);
//		return options;
//	}
	
	public static void main2(String[] args) {
		if(System.console()==null){
			//if we are not launching from the console (e.g. launching from Eclipse)
			//then we can simulate the arguments
			//args=new String[]{"-op","collocs","-d","../../target-properties/EN_REVIEWS_KAF","-lang","en","-out","MAIN_OUTPUT","-kaf"};
			//Call for the doubleprop
			args=new String[]{"-op","doubleprop","-d","C:\\Users\\yo\\Desktop\\git_repos\\target-properties\\CORPUS\\","-lang","en","-out","MAIN_OUTPUT","-kaf"};
		}
		execute(args);
	}
//	
	public static void main(String[] args) {
		options=loadOptions();
		if(System.console()==null){
			//if we are not launching from the console (e.g. launching from Eclipse)
			//then we can simulate the arguments
			//args=new String[]{"-op","collocs","-d","../../target-properties/EN_REVIEWS_KAF","-lang","en","-out","MAIN_OUTPUT","-kaf"};
			//Call for the doubleprop
			args=new String[]{"-dp","-i","C:\\Users\\yo\\Desktop\\git_repos\\target-properties\\CORPUS\\","-o","MAIN_OUTPUT_2","-l","en","-mwns","3","-mwll","100"};
		}
		System.out.println("GOING TO EXECUTE...");
		execute2(args);
	}
//	
//	public static void execute2(String args[]){
//		CommandLineParser parser=new BasicParser();
//		Options helpAndVerssionOpts=createHelpAndVersionOptions();
//		Options topLevelOpts=createTopLevelOptions();
//		Options doublePropagationOptions=createDoublePropagationExecutionOptions();
//		Options multiwordOptions=createMultiwordGenerationOptions();
//		try{
//			CommandLine helpAndVersionLine=parser.parse(helpAndVerssionOpts, args,true);
//			if(helpAndVersionLine.hasOption("h")){
//				HelpFormatter formatter = new HelpFormatter();
//	        	formatter.printHelp( "java -jar [NAME_OF_THE_JAR] [OPTION]", topLevelOpts);
//			}else if(helpAndVersionLine.hasOption("v")){
//				System.out.println("VERSION: Double propagation reworked Alpha 0.0.1");
//			}
//			
//			CommandLine topLevelLine=parser.parse(topLevelOpts, args,true);
//			String pathToCorpusDir=topLevelLine.getOptionValue("d");
//			String lang=topLevelLine.getOptionValue("lang");
//			boolean isKaf=topLevelLine.hasOption("kaf");
//			String outputPath=topLevelLine.getOptionValue("output");
//			if(topLevelLine.hasOption("dp")){
//				CommandLine doublePropLine=parser.parse(doublePropagationOptions, args,true);
//				
//				System.err.println("Not yet implemented");
//				
//			}else if(topLevelLine.hasOption("mw")){
//				System.out.println(Arrays.toString(args));
//				CommandLine multiwordLine=parser.parse(multiwordOptions, args,true);
//				int collocNgramSize = Integer.parseInt(multiwordLine.getOptionValue("n"));
//				int collocListSizeLimit=Integer.parseInt(multiwordLine.getOptionValue("s"));
//				
//				MultiwordGenerator multiwordGenerator=(MultiwordGenerator) getBeanFromContainer("multiwordGenerator");
//				log.info("Launching multiword generator with params: corpus-dir="+pathToCorpusDir+" ; lang="+lang+" ; alreadyInKaf="+isKaf+" ; output-folder"+outputPath);
//        		multiwordGenerator.generateMultiwords(pathToCorpusDir, lang, isKaf,collocNgramSize, collocListSizeLimit, outputPath);
//			}else{
//				throw new Exception("No main operation parameter set, -mw or -dp");
//			}
//			
//		}catch(Exception e){
//			e.printStackTrace();
//			HelpFormatter formatter = new HelpFormatter();
//        	formatter.printHelp( "java -jar [NAME_OF_THE_JAR] [OPTION]", topLevelOpts );
//		}
//		
//	}
	
	public static void execute2(String[]args){
		log.debug("Arguments: "+Arrays.toString(args));
		CommandLineParser parser=new BasicParser();
		try{
			CommandLine line=parser.parse(options, args);
			String inputDir=line.getOptionValue("i");
			String outputDir=line.getOptionValue("o");
			String multiwordsFile=line.hasOption("mwf")?line.getOptionValue("mwf"):null;
			int multiwordNgramSize=line.hasOption("mwns")?Integer.parseInt(line.getOptionValue("mwns")):2;
			int multiwordListLimit=line.hasOption("mwll")?Integer.parseInt(line.getOptionValue("mwll")):100;
			boolean alreadyInKaf=line.hasOption("kaf");
			boolean disableMultiwords=line.hasOption("nomw");
			String language=line.getOptionValue("l");
			
			if(line.hasOption("mw")){
				MultiwordGenerator multiwordGenerator=(MultiwordGenerator) getBeanFromContainer("multiwordGenerator");
        		log.info("Launching multiword generator with params: corpus-dir="+inputDir+" ; lang="+language+" ; alreadyInKaf="+alreadyInKaf+" ; output-folder"+outputDir);
        		List<NGramAndScore> rankedNgrams = multiwordGenerator.generateMultiwords(inputDir, language, alreadyInKaf, multiwordNgramSize, multiwordListLimit, outputDir);
        		String pathToFile=multiwordGenerator.writeMultiwordsToFile(rankedNgrams, outputDir);
        		log.info("Generated multiwords written to "+pathToFile);
			}else if(line.hasOption("dp")){
				DoublePropagator doublePropagator=(DoublePropagator) getBeanFromContainer("doublePropagator");
				List<String> multiwords=Lists.newArrayList();
				if(!disableMultiwords){
					if(multiwordsFile!=null){
						multiwords=MultiwordReader.readMultiwordFile(multiwordsFile);
					}else{
						MultiwordGenerator multiwordGenerator=(MultiwordGenerator) getBeanFromContainer("multiwordGenerator");
		        		log.info("Launching multiword generator with params: corpus-dir="+inputDir+" ; lang="+language+" ; alreadyInKaf="+alreadyInKaf+" ; output-folder"+outputDir);
		        		List<NGramAndScore> rankedNgrams = multiwordGenerator.generateMultiwords(inputDir, language, alreadyInKaf, multiwordNgramSize, multiwordListLimit, outputDir);
		        		String pathToFile=multiwordGenerator.writeMultiwordsToFile(rankedNgrams, outputDir);
		        		log.info("Generated multiwords written to "+pathToFile);
		        		multiwords=MultiwordReader.readMultiwordFile(pathToFile);
					}
				}else{
					log.info("Multiwords disabled");
				}
				PlainTextCorpusReader plainTextCorpusReader=new PlainTextCorpusReader();
        		List<String> corpusFilesContent = plainTextCorpusReader.readCorpusDirectoryFileContent(inputDir);//"C:\\Users\\yo\\Desktop\\git_repos\\target-properties\\CORPUS\\ALL_ENGLISH_REVIEWS.txt"));
        		//List<String>corpus=Lists.newArrayList(corpusContent.split("\n"));
        		Set<Word> seedOpinionWords=new HashSet<Word>();
        		seedOpinionWords.add(Word.createWord("good", "good", PartOfSpeech.ADJECTIVE, 0, 0));
        		seedOpinionWords.add(Word.createWord("bad", "bad", PartOfSpeech.ADJECTIVE, 0, 0));
        		Set<Word> seedtargetWords=new HashSet<Word>();
        		boolean detectMultiwords=!disableMultiwords;
				doublePropagator.executeDoublePropagation(corpusFilesContent, seedOpinionWords, seedtargetWords, language, detectMultiwords, multiwords);
				DoublePropagationGraph dpGraph= doublePropagator.getDoublePropagationGraph();
        		DoublePropagationGraphSerializer doublePropagationGraphSerializer=new DoublePropagationGraphObjectSerializer();
        		doublePropagationGraphSerializer.serialize(dpGraph, CorpusHandlingUtils.generateUniqueFileName(outputDir+File.separator+"DoublePropagationGraph.obj"));
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
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
	        	//String pathToCollocsFile=line.getOptionValue("c")
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
	        	//if()
	        	
	        	//SemanticVectorProcess semanticVectorProcess=(SemanticVectorProcess) getBeanFromContainer("SemanticVectorProcess");
	        	if(operation.equals("collocs")){
	        		MultiwordGenerator multiwordGenerator=(MultiwordGenerator) getBeanFromContainer("multiwordGenerator");
	        		log.info("Launching multiword generator with params: corpus-dir="+pathToCorpusDir+" ; lang="+lang+" ; alreadyInKaf="+isKaf+" ; output-folder"+outputPath);
	        		multiwordGenerator.generateMultiwords(pathToCorpusDir, lang, isKaf, Integer.parseInt(collocNgramSize), Integer.parseInt(collocListSizeLimit), outputPath);
	        	}else if(operation.equals("doubleprop")){
	        		DoublePropagator doublePropagator=(DoublePropagator) getBeanFromContainer("doublePropagator");
	        		////////// AD-HOC STUFF, REMOVE AFTER TEST IT ///////////
	        		PlainTextCorpusReader plainTextCorpusReader=new PlainTextCorpusReader();
	        		List<String> corpusContent = plainTextCorpusReader.readCorpusDirectoryFileContent(pathToCorpusDir);//"C:\\Users\\yo\\Desktop\\git_repos\\target-properties\\CORPUS\\ALL_ENGLISH_REVIEWS.txt"));
	        		//List<String>corpus=Lists.newArrayList(corpusContent.split("\n"));
	        		Set<Word> seedOpinionWords=new HashSet<Word>();
	        		seedOpinionWords.add(Word.createWord("good", "good", PartOfSpeech.ADJECTIVE, 0, 0));
	        		seedOpinionWords.add(Word.createWord("bad", "bad", PartOfSpeech.ADJECTIVE, 0, 0));
	        		Set<Word> seedtargetWords=new HashSet<Word>();
					/////////////////////////////////////////////////////////
	        		doublePropagator.executeDoublePropagation(corpusContent.subList(0, 1000), seedOpinionWords, seedtargetWords, lang,false,null);
	        		DoublePropagationGraph dpGraph= doublePropagator.getDoublePropagationGraph();
	        		DoublePropagationGraphSerializer doublePropagationGraphSerializer=new DoublePropagationGraphObjectSerializer();
	        		doublePropagationGraphSerializer.serialize(dpGraph, outputPath+File.separator+"DoublePropagationGraph.obj");
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
