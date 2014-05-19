package org.openerproject.double_propagation2.graph;

import java.io.Serializable;

public class DpEdge implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String edgeID;
	
	private DpNode node1;
	private DpNode node2;

	private int count;
	private String usedRule;

	private DpEdge(DpNode node1,DpNode node2, String usedRule){
		this.node1=node1;
		this.node2=node2;
		this.usedRule=usedRule;
		this.edgeID=buildDpEdgeID(node1, node2, usedRule);
		this.count=0;
	}
	
	public static DpEdge createDpEdge(DpNode node1,DpNode node2, String usedRule){
		DpEdge dpEgde=new DpEdge(node1, node2, usedRule);
		
		return dpEgde;
	}
	
	public DpNode getNode1() {
		return node1;
	}

	public void setNode1(DpNode node1) {
		this.node1 = node1;
	}

	public DpNode getNode2() {
		return node2;
	}

	public void setNode2(DpNode node2) {
		this.node2 = node2;
	}

	public int getCount() {
		return count;
	}

	public void increaseCount(){
		this.count++;
	}

	public String getUsedRule() {
		return usedRule;
	}

	public void setUsedRule(String usedRule) {
		this.usedRule = usedRule;
	}

	public String getEdgeID() {
		return edgeID;
	}
	
	public static String buildDpEdgeID(DpNode node1, DpNode node2, String usedRule){
		return node1.getNodeID()+"#"+node2.getNodeID()+"#"+usedRule;
	}

}
