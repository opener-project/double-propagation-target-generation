package org.openerproject.double_propagation2.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CorpusHandlingUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetFilesFromDir() {
		fail("Not yet implemented");
	}

	@Test
	public void testWriteTextsToFiles() {
		fail("Not yet implemented");
	}

	@Test
	public void testGenerateUniqueFileName() {
		String baseFileName="filename.txt";
		String newName=CorpusHandlingUtils.generateUniqueFileName(baseFileName);
		System.out.println(newName);
	}

}
