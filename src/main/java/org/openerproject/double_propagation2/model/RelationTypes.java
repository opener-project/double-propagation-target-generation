package org.openerproject.double_propagation2.model;

public enum RelationTypes {

	AdjectivalModifier, //amod in Stanford
	NounCompoundModifier, //nn in Stanford
	NominalSubject,  //nsubj in Stanford
	DirectObject, //dobj in Stanford
	Conjunction, //conj in Stanford
	
	//MISCELLANEOUS ONE, FOR UNINTERESTING AND UNMAPPED RELATIONS
	OTHER;
}
