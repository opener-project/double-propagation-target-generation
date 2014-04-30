package org.openerproject.double_propagation2.graph;

public class DpNode {

	public static enum DpNodeType{
		TARGET,OPINION;
	}
	
	private String lemma;
	private String pos;
	private DpNodeType type;
	private int count;
	
	private DpNode(){
		this.count=0;
	}
	
	public static DpNode createTargetNode(String lemma,String pos){
		return createNode(lemma, pos, DpNodeType.TARGET);
	}
	
	public static DpNode createOpinionNode(String lemma, String pos){
		return createNode(lemma, pos, DpNodeType.OPINION);
	}
	
	private static DpNode createNode(String lemma,String pos,DpNodeType type){
		DpNode dpNode=new DpNode();
		dpNode.setLemma(lemma);
		dpNode.setPos(pos);
		dpNode.setType(type);
		return dpNode;
	}
	
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public DpNodeType getType() {
		return type;
	}
	public void setType(DpNodeType type) {
		this.type = type;
	}
	public int getCount() {
		return count;
	}
	public void increaseCount(){
		this.count++;
	}
	
}
