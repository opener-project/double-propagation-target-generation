package org.openerproject.double_propagation2.graph;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

public class DoublePropagationGraph {

	public static void main(String[]args){
		nothing();
	}
	
	public static void nothing(){
		
		Graph<DpNode,DpEdge> g = new DirectedSparseMultigraph<DpNode, DpEdge>();
		
		PageRank<DpNode, DpEdge>pr=new PageRank<>(g, null, 0.1);
		
		//Problema con la gestión de nodos
		//un nodo debe representar todas las apariciones de una misma palabra
		//así que no debe introducirse más de una vez, hay que controlar los nodos que existen
		
//		DpNode node1=new DpNode(1);
//		DpNode node2=new DpNode(2);
//		DpNode node3=new DpNode(3);
//		
//		DpEdge dpEdge=new DpEdge();
//		
//		g.addEdge(dpEdge, node1, node2);
//		g.addEdge(new DpEdge(), node1, node2);
//		node1.incrCount();
//		g.addEdge(new DpEdge(), node1, node2);
//		node1.incrCount();
//		g.addEdge(new DpEdge(), node1, node2);
//		node1.incrCount();node2.incrCount();
//		g.addEdge(new DpEdge(), node1, node3);
//		
//		System.out.println(g.toString());

		
	}
	
}
