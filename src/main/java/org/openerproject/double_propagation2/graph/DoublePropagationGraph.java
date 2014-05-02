package org.openerproject.double_propagation2.graph;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;
import org.openerproject.double_propagation2.algorithm.rules.RuleCheckResult;
import org.openerproject.double_propagation2.model.DoublePropagationWordType;
import org.openerproject.double_propagation2.model.Word;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

public class DoublePropagationGraph {

	private static Logger log=Logger.getLogger(DoublePropagationGraph.class);
	
	Map<String, DpNode> nodes;
	Map<String, DpEdge> edges;

	private Graph<DpNode, DpEdge> jungGraph;
	private Transformer<DpEdge, Double> edgeWeightTransformer = new Transformer<DpEdge, Double>() {
		public Double transform(DpEdge dpEdge) {
			return (double) dpEdge.getCount();
		}
	};

	public DoublePropagationGraph(){
		nodes=new HashMap<String,DpNode>();
		edges=new HashMap<String,DpEdge>();
	}
	
	public int getNodesCount(){
		return nodes.size();
	}
	
	public boolean containsOpinionWordNode(Word word){
		String nodeID = DpNode.buildNodeID(word.getLemma(), word.getPostag(), DoublePropagationWordType.OPINION);
		return nodes.containsKey(nodeID);
	}
	
	public boolean containsTargetWordNode(Word word){
		String nodeID = DpNode.buildNodeID(word.getLemma(), word.getPostag(), DoublePropagationWordType.TARGET);
		return nodes.containsKey(nodeID);
	}
	
	public void calculateNodeScores() {
		jungGraph = new DirectedSparseMultigraph<DpNode, DpEdge>();
		//First add all vertex, to prevent the nodes not participating in any relation (i.e. unused seed words) to cause problems
		//Since all the time are the very same nodes (references) it should not affect the final number of nodes in the jung graph
		for(String dpNodeID:nodes.keySet()){
			DpNode dpNode=nodes.get(dpNodeID);
			jungGraph.addVertex(dpNode);
		}
		
		for (String dpEdgeID : edges.keySet()) {
			DpEdge dpEdge = edges.get(dpEdgeID);
			//addRelationToJungGraph(dpEdge.getNode1(), dpEdge.getNode2(), dpEdge);
			log.debug("Adding edge (and nodes to jung graph:\n"+dpEdge.getNode1()+"\n"+dpEdge.getNode2());
			jungGraph.addEdge(dpEdge, dpEdge.getNode1(), dpEdge.getNode2());
		}
		PageRank<DpNode, DpEdge> pageRank = new PageRank<>(jungGraph, edgeWeightTransformer, 0.1);
		pageRank.evaluate();
		for (String dpNodeID : nodes.keySet()) {
			DpNode dpNode = nodes.get(dpNodeID);
			Double score = pageRank.getVertexScore(dpNode);
			dpNode.setScore(score);
		}
		////////////////////////////////////////
		log.debug("Resulting nodes with score:");
		for(String dpNodeID:nodes.keySet()){
			DpNode dpNode=nodes.get(dpNodeID);
			log.debug(dpNode);
		}
		////////////////////////////////////////
	}

//	protected void addRelationToJungGraph(DpNode sourceNode, DpNode targetNode, DpEdge dpEdge) {
//		jungGraph.addEdge(dpEdge, sourceNode, targetNode);
//	}

	public void addRelation(RuleCheckResult ruleCheckResult) {
		DpNode sourceNode = createOrRetrieveDpNode(ruleCheckResult.getTriggeringWord(),
				ruleCheckResult.getTriggeringWordType());
		DpNode targetNode = createOrRetrieveDpNode(ruleCheckResult.getExtractedWord(),
				ruleCheckResult.getExtractedWordType());
		createOrIncreaseDpEdge(sourceNode, targetNode, ruleCheckResult.getUsedRule());
	}

	public DpNode createOrRetrieveDpNode(Word word, DoublePropagationWordType type) {
		// Word sourceWord=ruleCheckResult.getTriggeringWord();
		DpNode sourceNode = null;
		if (type == DoublePropagationWordType.OPINION) {
			String sourceNodeID = DpNode.buildNodeID(word.getLemma(), word.getPostag(),
					DoublePropagationWordType.OPINION);
			sourceNode = nodes.get(sourceNodeID);
			// if this node does not still exist, create it and add to the node
			// map
			if (sourceNode == null) {
				sourceNode = DpNode.createOpinionNode(word.getLemma(), word.getPostag());
				nodes.put(sourceNodeID, sourceNode);
				log.debug("Adding node to dpGraph: "+sourceNode);
			}
			// newly created or already existing, increase the count in both
			// cases (newly created nodes start with count=0)
			//sourceNode.increaseCount();

		} else if (type == DoublePropagationWordType.TARGET) {
			String sourceNodeID = DpNode.buildNodeID(word.getLemma(), word.getPostag(),
					DoublePropagationWordType.TARGET);
			sourceNode = nodes.get(sourceNodeID);
			// if this node does not still exist, create it and add to the node
			// map
			if (sourceNode == null) {
				sourceNode = DpNode.createTargetNode(word.getLemma(), word.getPostag());
				nodes.put(sourceNodeID, sourceNode);
			}
			// newly created or already existing, increase the count in both
			// cases (newly created nodes start with count=0)
			//sourceNode.increaseCount();

		} else {
			throw new RuntimeException("A double propagation node type must be OPINION or TARGET, current type: "
					+ type);
		}
		return sourceNode;
	}

	public void createOrIncreaseDpEdge(DpNode sourceNode, DpNode targetNode, String usedRule) {
		String dpEdgeID = DpEdge.buildDpEdgeID(sourceNode, targetNode, usedRule);
		DpEdge dpEdge = edges.get(dpEdgeID);
		if (dpEdge == null) {
			dpEdge = DpEdge.createDpEdge(sourceNode, targetNode, usedRule);
			edges.put(dpEdgeID, dpEdge);
		}
		dpEdge.increaseCount();
		// return dpEdge;
	}

}
