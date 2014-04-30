package org.openerproject.double_propagation2.analysis;

import java.util.HashMap;

import org.openerproject.double_propagation2.model.PartOfSpeech;
import org.openerproject.double_propagation2.model.RelationTypes;

import static org.openerproject.double_propagation2.model.RelationTypes.*;
import static org.openerproject.double_propagation2.model.PartOfSpeech.*;

public class StanfordNLPToolsTagsetMapper extends AbstractTagsetMapper{

	
	public StanfordNLPToolsTagsetMapper() {
		super();
	}

	@Override
	protected void loadMappings() {
		this.postagsMap=new HashMap<PartOfSpeech,String>();
		postagsMap.put(ADJECTIVE, "J");
		postagsMap.put(NOUN, "N");
		postagsMap.put(ADVERB, "R");
		postagsMap.put(VERB, "V");
		
		
		this.dependencyRelationsMap=new HashMap<RelationTypes,String>();
		dependencyRelationsMap.put(AdjectivalModifier, "amod");
		//MISSING THE REST OF THE DEPENDENCY RELATION MAPPINGS
	}

}
