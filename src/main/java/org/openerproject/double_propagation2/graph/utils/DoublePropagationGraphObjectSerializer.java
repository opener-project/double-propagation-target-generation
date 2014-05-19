package org.openerproject.double_propagation2.graph.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.SerializationUtils;
import org.openerproject.double_propagation2.graph.DoublePropagationGraph;


public class DoublePropagationGraphObjectSerializer implements DoublePropagationGraphSerializer{

	@Override
	public void serialize(DoublePropagationGraph dpGraph,String path) throws IOException {
		SerializationUtils.serialize(dpGraph, new FileOutputStream(path));
	}

	@Override
	public DoublePropagationGraph deserialize(String path) throws IOException, ClassNotFoundException {
		return (DoublePropagationGraph) SerializationUtils.deserialize(new FileInputStream(path));
	}

}
