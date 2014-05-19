package org.openerproject.double_propagation2.graph.utils;

import java.io.IOException;

import org.openerproject.double_propagation2.graph.DoublePropagationGraph;

public interface DoublePropagationGraphSerializer {

	public void serialize(DoublePropagationGraph dpGraph, String path) throws IOException;
	
	public DoublePropagationGraph deserialize(String path) throws IOException, ClassNotFoundException;
	
}
