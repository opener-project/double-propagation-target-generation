package org.openerproject.double_propagation2.graph;

import java.io.Serializable;
import java.util.Comparator;

import org.openerproject.double_propagation2.model.DoublePropagationWordType;
import org.openerproject.double_propagation2.model.PartOfSpeech;

public class DpNode implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nodeID;
	private String lemma;
	private PartOfSpeech pos;
	private DoublePropagationWordType type;
	private int count;

	private double score;

	private DpNode(String lemma, PartOfSpeech pos, DoublePropagationWordType type) {
		this.setLemma(lemma);
		this.setPos(pos);
		this.setType(type);
		this.nodeID = buildNodeID(lemma, pos, type);
		this.count = 0;
	}

	/**
	 * Creates a new "target" node, with count initialized to 0, so it requires
	 * a call to increaseCount
	 * 
	 * @param lemma
	 * @param pos
	 * @return
	 */
	public static DpNode createTargetNode(String lemma, PartOfSpeech pos) {
		return createNode(lemma, pos, DoublePropagationWordType.TARGET);
	}

	/**
	 * Creates a new "opinion" node, with count initialized to 0, so it requires
	 * a call to increaseCount
	 * 
	 * @param lemma
	 * @param pos
	 * @return
	 */
	public static DpNode createOpinionNode(String lemma, PartOfSpeech pos) {
		return createNode(lemma, pos, DoublePropagationWordType.OPINION);
	}

	private static DpNode createNode(String lemma, PartOfSpeech pos, DoublePropagationWordType type) {
		DpNode dpNode = new DpNode(lemma, pos, type);
		return dpNode;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public PartOfSpeech getPos() {
		return pos;
	}

	public void setPos(PartOfSpeech pos) {
		this.pos = pos;
	}

	public DoublePropagationWordType getType() {
		return type;
	}

	public void setType(DoublePropagationWordType type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void increaseCount() {
		this.count++;
	}

	public String getNodeID() {
		return nodeID;
	}

	public static String buildNodeID(String lemma, PartOfSpeech pos, DoublePropagationWordType type) {
		return lemma + "_" + pos + "_" + type;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "DpNode [nodeID=" + nodeID + ", lemma=" + lemma + ", pos=" + pos + ", type=" + type + ", count=" + count
				+ ", score=" + score + "]";
	}
	
	public static class DpNodeScoreComparator implements Comparator<DpNode>{

		private boolean ascendingOrder;
		private DpNodeScoreComparator(boolean ascendingOrder){
			super();
			this.ascendingOrder=ascendingOrder;
		}
		
		public static DpNodeScoreComparator create(boolean ascendingOrder){
			return new DpNodeScoreComparator(ascendingOrder);
		}
		
		@Override
		public int compare(DpNode o1, DpNode o2) {
			int compValue;
			if(ascendingOrder){
				compValue= Double.compare(o1.getScore(), o2.getScore());
			}else{//descending order
				compValue= Double.compare(o2.getScore(), o1.getScore());
			}
			if(compValue==0){
				return o1.getNodeID().compareTo(o2.nodeID);
			}else{
				return compValue;
			}
		}
		
	}
	
	public static class DpNodeFrequencyComparator implements Comparator<DpNode>{

		private boolean ascendingOrder;
		private DpNodeFrequencyComparator(boolean ascendingOrder){
			super();
			this.ascendingOrder=ascendingOrder;
		}
		
		public static DpNodeFrequencyComparator create(boolean ascendingOrder){
			return new DpNodeFrequencyComparator(ascendingOrder);
		}
		
		@Override
		public int compare(DpNode o1, DpNode o2) {
			int compValue;
			if(ascendingOrder){
				compValue=Integer.compare(o1.getCount(), o2.getCount());
			}else{//descending order
				compValue= Integer.compare(o2.getCount(), o1.getCount());
			}
			if(compValue==0){
				return o1.getNodeID().compareTo(o2.nodeID);
			}else{
				return compValue;
			}
		}
		
	}
	
}
