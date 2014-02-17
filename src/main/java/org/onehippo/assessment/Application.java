package org.onehippo.assessment;

import org.onehippo.assessment.util.BookUtils;
import org.onehippo.assessment.util.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;


/**
 * @version "$Id$"
 */
public final class Application {
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);
	//private static final String JCR_QUERY = "//element(*,hst:abstractcomponent)";
	private static final String JCR_QUERY_PREFIX = "//*[jcr:contains(.,'";
	private static final String JCR_QUERY_SUFFIX = "')]";

	public static void main(String[] args) {
		try {
			RepoConnector.INSTANCE.initializeConnection();
			Session session = RepoConnector.INSTANCE.getSession();
			secondTask(session, "/content");
			thirdTask(session, "hippo");
			fourTask(session, "/content/books");
		} catch (RepositoryException e) {
			LOG.info("Error establishing connection to the repo " + e);
		} finally {
			RepoConnector.INSTANCE.logout();
		}
	}

	private static void secondTask(Session session, String scope) throws RepositoryException {
		LOG.info(":: Second ::");
		Node root = session.getNode(scope);
		NodeIterator nodeIt = root.getNodes();
		NodeUtils.navigateNode("", nodeIt);
	}

	private static void thirdTask(Session session, String queryTerm) throws RepositoryException {
		LOG.info(":: Third ::");
		final String query = JCR_QUERY_PREFIX + queryTerm + JCR_QUERY_SUFFIX;
		final QueryResult queryResult = session.getWorkspace()
				.getQueryManager()
				.createQuery(query, Query.XPATH)
				.execute();
		LOG.debug("Query executed :: " + query);
		NodeIterator resultNodes = queryResult.getNodes();
		NodeUtils.navigateQueryResponseNode(resultNodes);
	}

	private static void fourTask(Session session, String scope) {
		LOG.info(":: Four ::");
		Node booksParent = null;
		String bookPath = null;

		try {
			booksParent = session.getNode(scope);
		} catch (RepositoryException e) {
			LOG.error("the given path {} for persist the Books does not exist on the repository", scope);
		}
		if (null != booksParent) {
			Node bookNode = BookUtils.createBook(booksParent);
			BookUtils.attachChapters(bookNode, BookUtils.getRandom(10));
			try {
				bookPath = bookNode.getPath();
				session.save();
			} catch (RepositoryException e) {
				LOG.error("Cannot persist the just created book at {}", bookPath);
			}
			if (null != bookPath) {
				BookUtils.displayBook(session, bookPath);
			} else {
				LOG.error("Cannot display the recently created book!");
			}
		}
	}


}
