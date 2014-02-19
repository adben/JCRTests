package org.onehippo.assessment.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onehippo.assessment.RepoConnector;

/**
 */
public class NodeUtilsTest {
	@Before
	public void setUp() throws Exception {
		RepoConnector.INSTANCE.initializeConnection();
	}

	@After
	public void tearDown() throws Exception {
		RepoConnector.INSTANCE.logout();
	}

	@Test
	public void testSecondTask() throws Exception {
		NodeUtils.secondTask(RepoConnector.INSTANCE.getSession(), "/content");
	}

	@Test
	public void testThirdTask() throws Exception {
		NodeUtils.thirdTask(RepoConnector.INSTANCE.getSession(), "hippo");
	}

	@Test
	public void testFourthTask() throws Exception {
		NodeUtils.fourthTask(RepoConnector.INSTANCE.getSession(), "/content/books");
	}
}
