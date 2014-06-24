package org.openerproject.double_propagation2.main;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.openerproject.double_propagation2.algorithm.DoublePropagator;
import org.openerproject.double_propagation2.data.PlainTextCorpusReader;
import org.openerproject.double_propagation2.graph.DoublePropagationGraph;
import org.openerproject.double_propagation2.graph.utils.DoublePropagationGraphObjectSerializer;
import org.openerproject.double_propagation2.graph.utils.DoublePropagationGraphSerializer;
import org.openerproject.double_propagation2.graph.utils.GraphContentPrinter;
import org.openerproject.double_propagation2.model.PartOfSpeech;
import org.openerproject.double_propagation2.model.Word;
import org.openerproject.double_propagation2.multiwords.MultiwordReader;
import org.openerproject.double_propagation2.utils.CorpusHandlingUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.collect.Lists;

public class CLI {

	private static Logger log=Logger.getLogger(CLI.class);
	
	private static ApplicationContext appContext;
	
	private static Options options=loadOptions();
	
	@SuppressWarnings("static-access")
	private static Options loadOptions(){
		Options options = new Options();
		
		Option inputDir=OptionBuilder.hasArg(true).isRequired(true).create("i");
		inputDir.setDescription("Path to the directory containing the corpus files");
		Option outputDir=OptionBuilder.hasArg(true).isRequired(false).create("o");
		outputDir.setDescription("Folder in which double-propagation results will be stored");
		Option multiwordsFile=OptionBuilder.hasArg(true).isRequired(false).create("m");
		multiwordsFile.setDescription("Path to the file containing precomputed multiword terms");
		Option language=OptionBuilder.hasArg(true).isRequired(false).create("l");
		language.setDescription("Language of the corpus files content, to use when preprocessing them (NOTE: for now only English allowed)");
		
		Option help=OptionBuilder.isRequired(false).create("h");
		help.setDescription("Prints this message");
		
		options.addOption(inputDir);
		options.addOption(outputDir);
		options.addOption(multiwordsFile);
		options.addOption(language);
		options.addOption(help);

		return options;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(System.console()==null){
			//if we are not launching from the console (e.g. launching from Eclipse)
			//then we can simulate the arguments
			//args=new String[]{"-op","collocs","-d","../../target-properties/EN_REVIEWS_KAF","-lang","en","-out","MAIN_OUTPUT","-kaf"};
			//Call for the doubleprop
//			args=new String[]{"-i","C:\\Users\\agarciap\\Data\\REVIEWS_DATA\\ENGLISH_REVIEWS_MINI.txt","-o","MAIN_OUTPUT"};
			args=new String[]{"-i","C:\\Users\\agarciap\\Data\\DATASETS_FOR_EXPERIMENTS\\HU_LIU_CUSTOMER_REVIEW\\PLAIN\\PLAIN_HU_LIU_CREATIVE_LABS_NOMAD_JUKEBOX.txt","-o","MAIN_OUTPUT"
					,"-m","C:\\Users\\agarciap\\Data\\DATASETS_FOR_EXPERIMENTS\\HU_LIU_CUSTOMER_REVIEW\\GENERATED_MULTIWORDS\\creative_labs_nomad_jukebox_zen_xtra_40gb_multiwords_20140624.txt"};
		}
		execute(args);
		
	}
	
	public static void execute(String[]args){
		log.debug("Arguments: "+Arrays.toString(args));
		CommandLineParser parser=new BasicParser();
		try{
			CommandLine line=parser.parse(options, args);
			String inputPath=line.getOptionValue("i");
			String outputDirPath=returnUserValueOrDefault(line, "o", ".");
			String multiwordFilePath=line.getOptionValue("m");
			String language=line.getOptionValue("l");
			
			if(language==null){
				language="en";
			}
			
			DoublePropagator doublePropagator=(DoublePropagator) getBeanFromContainer("doublePropagator");
			List<String> multiwords=Lists.newArrayList();
			if(multiwordFilePath!=null){
				multiwords=MultiwordReader.readMultiwordFile(multiwordFilePath);
			}
			log.debug("Inpur dir: "+inputPath);
			PlainTextCorpusReader plainTextCorpusReader=new PlainTextCorpusReader();
    		List<String> corpusFilesContent = plainTextCorpusReader.readCorpusDirectoryFileContent(inputPath);//"C:\\Users\\yo\\Desktop\\git_repos\\target-properties\\CORPUS\\ALL_ENGLISH_REVIEWS.txt"));
    		////DELETE THIS; JUST A QUICK TEST
    		//corpusFilesContent=corpusFilesContent.subList(0, 30);
    		/////////////////////////////////////
    		//List<String>corpus=Lists.newArrayList(corpusContent.split("\n"));
    		Set<Word> seedOpinionWords=new HashSet<Word>();
    		seedOpinionWords.add(Word.createWord("good", "good", PartOfSpeech.ADJECTIVE, 0, 0));
    		seedOpinionWords.add(Word.createWord("bad", "bad", PartOfSpeech.ADJECTIVE, 0, 0));
    		//seedOpinionWords.add(Word.createWord("short", "short", PartOfSpeech.ADVERB, 0, 0));
    		Set<Word> seedtargetWords=new HashSet<Word>();
    		boolean detectMultiwords=!multiwords.isEmpty();
			doublePropagator.executeDoublePropagation(corpusFilesContent, seedOpinionWords, seedtargetWords, language, detectMultiwords, multiwords);
			DoublePropagationGraph dpGraph= doublePropagator.getDoublePropagationGraph();
    		DoublePropagationGraphSerializer doublePropagationGraphSerializer=new DoublePropagationGraphObjectSerializer();
    		doublePropagationGraphSerializer.serialize(dpGraph, CorpusHandlingUtils.generateUniqueFileName(outputDirPath+File.separator+"DoublePropagationGraph.obj"));
			GraphContentPrinter.printAspectTerms(dpGraph,new FileOutputStream(new File(CorpusHandlingUtils.generateUniqueFileName(outputDirPath+File.separator+"ranked-aspect-term-list.txt"))));
    		
		}catch(Exception e){
			//e.printStackTrace();
			log.error(e);
			HelpFormatter formatter = new HelpFormatter();
        	formatter.printHelp( "java -jar [NAME_OF_THE_JAR] [OPTION_1] .. [OPTION_N]", options );
		}
	}

	
	protected static Integer returnUserValueOrDefault(CommandLine line,String param,Integer defaultValue){
		if(line.hasOption(param)){
			return Integer.parseInt(line.getOptionValue(param));
		}else{
			return defaultValue;
		}
	}
	
	protected static String returnUserValueOrDefault(CommandLine line,String param,String defaultValue){
		if(line.hasOption(param)){
			return line.getOptionValue(param);
		}else{
			return defaultValue;
		}
	}
	
	protected static Object getBeanFromContainer(String beanName){
		if(appContext==null){
			appContext=new ClassPathXmlApplicationContext("spring-config.xml");
		}
		return appContext.getBean(beanName);
	}
}
