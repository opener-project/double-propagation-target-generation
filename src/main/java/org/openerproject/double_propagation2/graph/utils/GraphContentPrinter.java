package org.openerproject.double_propagation2.graph.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Set;

import org.openerproject.double_propagation2.graph.DoublePropagationGraph;
import org.openerproject.double_propagation2.graph.DpNode;
import org.openerproject.double_propagation2.model.DoublePropagationWordType;

import com.google.common.collect.Lists;

public class GraphContentPrinter {

	public static void printAspectTerms(DoublePropagationGraph dpGraph,
			OutputStream os) {
		try {
			boolean ascendingOrder = false;
			Set<DpNode> nodesOrderedByScore = dpGraph.getDpNodesOrderedByScore(ascendingOrder);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			for (DpNode node : nodesOrderedByScore) {
				if (node.getType() == DoublePropagationWordType.TARGET) {
					bw.write(node.getLemma() + "\n");
				}
			}
			bw.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void printOpinionWords(DoublePropagationGraph dpGraph) {
		Set<DpNode> nodesOrderedByScore = dpGraph
				.getDpNodesOrderedByScore(false);

		for (DpNode node : nodesOrderedByScore) {
			if (node.getType() == DoublePropagationWordType.OPINION) {
				System.out.println(node);
			}
		}
	}

	public static void printNodeScoreVsFrequency(DoublePropagationGraph dpGraph) {
		Set<DpNode> nodesOrderedByScore = dpGraph
				.getDpNodesOrderedByScore(false);
		List<DpNode> scoreAspects = Lists.newArrayList();
		for (DpNode node : nodesOrderedByScore) {
			if (node.getType() == DoublePropagationWordType.TARGET) {
				scoreAspects.add(node);
			}
		}
		Set<DpNode> nodesOrderedByFrequency = dpGraph
				.getDpNodesOrderedByFrequency(false);
		List<DpNode> freqAspects = Lists.newArrayList();
		for (DpNode node : nodesOrderedByFrequency) {
			if (node.getType() == DoublePropagationWordType.TARGET) {
				freqAspects.add(node);
			}
		}
		System.out.println("SIZES: "
				+ (nodesOrderedByFrequency.size() + " , " + nodesOrderedByScore
						.size()));
		for (int i = 0; i < scoreAspects.size(); i++) {
			System.out.println("SCORE --> " + scoreAspects.get(i).getLemma()
					+ "  --  " + freqAspects.get(i).getLemma() + " <-- FREQ");
		}
	}

	public static void main(String[] args) throws ClassNotFoundException,
			IOException {
		DoublePropagationGraphObjectSerializer doublePropagationGraphObjectSerializer = new DoublePropagationGraphObjectSerializer();
		DoublePropagationGraph dpGraph = doublePropagationGraphObjectSerializer
				.deserialize("MAIN_OUTPUT/DoublePropagationGraph_" + 20140529
						+ ".obj");
		printAspectTerms(dpGraph, System.out);
		// printOpinionWords(dpGraph);
		// printNodeScoreVsFrequency(dpGraph);
	}

}
