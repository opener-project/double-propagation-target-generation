package org.openerproject.double_propagation2.analysis;

import java.util.Map;

import org.openerproject.double_propagation2.model.PartOfSpeech;
import org.openerproject.double_propagation2.model.RelationTypes;

/**
 * This class should load the values from a more easily configurable source
 * (e.g. a properties file) NOTE: All the tags in the constant must be defined.
 * If not, nullpointer exceptions will raise, but it is ok, since missing tags
 * is a error that must be reported
 * 
 * The comparison are done with a previous lowercasing, and only regarding the first character of the incoming tag
 * @author yo
 * 
 */
public abstract class AbstractTagsetMapper {

//	// POS TYPES CONSTANTS
//	public static final String VERB = "verb";
//	public static final String NOUN = "noun";
//	public static final String ADJETIVE = "adjetive";
//	public static final String ADVERB = "adverb";
//
//	// DEPENDENCY RELATION TYPES CONSTANTS
//	public static final String ADJECTIVAL_MODIFIER = "adj_mod";
	// ...

	protected Map<PartOfSpeech, String> postagsMap;
	protected Map<RelationTypes, String> dependencyRelationsMap;

	public AbstractTagsetMapper(){
		loadMappings();
	}
	
	final public boolean isPosType(String aTag, PartOfSpeech posType) {
		return checkInTable(aTag, posType, postagsMap);
	}

	final public boolean isDepRelationType(String relationName, RelationTypes relType) {
		return checkInTable(relationName, relType, dependencyRelationsMap);
	}
	
	final protected boolean checkInTable(String tagToCheck,Object typeToCheck,Map<?,String>table){
		try {
			String expectedTag=table.get(typeToCheck).toLowerCase().substring(0,1);
			tagToCheck=tagToCheck.toLowerCase().substring(0,1);
			return expectedTag.equals(tagToCheck);
		} catch (NullPointerException e) {
			throw new RuntimeException("No mapping for type: " + typeToCheck);
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	final public PartOfSpeech getMappedPosType(String aTag){
		for(PartOfSpeech posType:postagsMap.keySet()){
			if(postagsMap.get(posType).equals(aTag)){
				return posType;
			}
		}
		return PartOfSpeech.OTHER;
	}
	
	protected abstract void loadMappings();

}
