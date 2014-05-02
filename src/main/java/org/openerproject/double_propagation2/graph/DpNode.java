package org.openerproject.double_propagation2.graph;

import org.openerproject.double_propagation2.model.DoublePropagationWordType;
import org.openerproject.double_propagation2.model.PartOfSpeech;

public class DpNode {

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
	
	
}
