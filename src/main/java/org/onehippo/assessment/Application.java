package org.onehippo.assessment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import static org.onehippo.assessment.NodeUtils.navigateNode;
import static org.onehippo.assessment.NodeUtils.navigateQueryResponseNode;

/**
 * @version "$Id$"
 */
public final class Application {
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);
	//private static final String JCR_QUERY = "//element(*,hst:abstractcomponent)";
	private static final String JCR_QUERY_PREFIX = "//*[jcr:contains(.,'";
	private static final String JCR_QUERY_SUFFIX = "')]";

	public static void main(String[] args) {
		Session session = null;
		try {
			session = RepoConnector.INSTANCE.getSession();
			secondTask(session, "/content");
			thirdTask(session, "hippo");
		} catch (RepositoryException e) {
			LOG.info("Error establishing connection to the repo " + e);
		} finally {
			if (session != null) {
				session.logout();
			}
		}
	}

	private static void thirdTask(Session session, String queryTerm) throws RepositoryException {
		LOG.info("::Third::");
		final String query = JCR_QUERY_PREFIX + queryTerm + JCR_QUERY_SUFFIX;
		final QueryResult queryResult = session.getWorkspace()
				.getQueryManager()
				.createQuery(query, Query.XPATH)
				.execute();
		LOG.debug("Query executed :: " + query);
		NodeIterator resultNodes = queryResult.getNodes();
		navigateQueryResponseNode(resultNodes);
	}

	private static void secondTask(Session session, String scope) throws RepositoryException {
		LOG.info("::Second::");
		Node root = session.getNode(scope);
		NodeIterator nodeIt = root.getNodes();
		navigateNode("", nodeIt);
	}

}
