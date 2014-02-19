package org.onehippo.assessment;

import org.onehippo.assessment.util.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;


/**
 * @version "$Id$"
 */
public final class Application {
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		try {
			RepoConnector.INSTANCE.initializeConnection();
			NodeUtils.secondTask(RepoConnector.INSTANCE.getSession(), "/content");
			NodeUtils.thirdTask(RepoConnector.INSTANCE.getSession(), "hippo");
			NodeUtils.fourthTask(RepoConnector.INSTANCE.getSession(), "/content/books");
		} catch (RepositoryException e) {
			LOG.info("Error establishing connection to the repo " + e);
		} finally {
			RepoConnector.INSTANCE.logout();
		}
	}

}
