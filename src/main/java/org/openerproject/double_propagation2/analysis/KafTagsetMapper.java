package org.openerproject.double_propagation2.analysis;

import java.util.HashMap;

import org.openerproject.double_propagation2.model.PartOfSpeech;

import static org.openerproject.double_propagation2.model.PartOfSpeech.*;


public class KafTagsetMapper extends AbstractTagsetMapper{

	@Override
	protected void loadMappings() {
		this.postagsMap=new HashMap<PartOfSpeech,String>();
		postagsMap.put(ADJECTIVE, "G");
		postagsMap.put(NOUN, "N");
		postagsMap.put(ADVERB, "A");
		postagsMap.put(VERB, "V");
		
		//MISSING DEPENDENCY TAGMAPPING...
		
	}

}
