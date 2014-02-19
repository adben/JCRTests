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
	//private static final String JCR_QUERY = "//*[jcr:contains(.,'hippo')]";
	private static final String JCR_QUERY_PREFIX = "//*[jcr:contains(.,'";
	private static final String JCR_QUERY_SUFFIX = "')]";

	public static void main(String[] args) {
		try {
			RepoConnector.INSTANCE.initializeConnection();
			secondTask(RepoConnector.INSTANCE.getSession(), "/content");
			thirdTask(RepoConnector.INSTANCE.getSession(), "hippo");
			fourTask(RepoConnector.INSTANCE.getSession(), "/content/books");
		} catch (RepositoryException e) {
			LOG.info("Error establishing connection to the repo " + e);
		} finally {
			RepoConnector.INSTANCE.logout();
		}
	}

	/**
	 * Traverses all nodes within {@code path} part of the tree and:
	 * <ul>
	 * <li>Print the path of each node</li>
	 * <li>Print the properties for every node (type & value(s))</li>
	 * </ul>
	 *
	 * @param session the  session
	 * @param path    the path to print
	 */
	protected static void secondTask(Session session, String path) {
		LOG.info(":: Second ::");
		try {
			Node root = session.getNode(path);
			NodeIterator nodeIt = root.getNodes();
			NodeUtils.navigateNode("", nodeIt);
		} catch (RepositoryException e) {
			LOG.info("Error obtaining the node information at {} with exception {}", path, e);
		}

	}

	/**
	 * Executes a earches for all nodes that contain the {@code queryTerm}. Iterating through these nodes and print their location.
	 *
	 * @param session   the session
	 * @param queryTerm the text to search
	 */
	protected static void thirdTask(Session session, String queryTerm) {
		LOG.info(":: Third ::");
		//::: //*[jcr:contains(.,'hippo')]
		final String query = JCR_QUERY_PREFIX + queryTerm + JCR_QUERY_SUFFIX;
		final QueryResult queryResult;
		try {
			queryResult = session.getWorkspace()
					.getQueryManager()
					.createQuery(query, Query.XPATH)   //XPATH is Deprecated in JCR 2.0, should use JCR_SQL2 or JCR_JQOM instead?
					.execute();
			LOG.info("Query executed :: " + query);
			NodeIterator resultNodes = queryResult.getNodes();
			NodeUtils.navigateQueryResponseNode(resultNodes);
		} catch (RepositoryException e) {
			LOG.info("There was an error trying to execute the query {}, with exception {}", query, e);
		}

	}

	/**
	 * Creates a book with the corresponding chapters and paragraph, if any. and displays the representation of this book
	 *
	 * @param session                 the session
	 * @param repositoryBooksLocation the repository location where the books should be created
	 */
	protected static void fourTask(Session session, String repositoryBooksLocation) {
		LOG.info(":: Four ::");
		Node booksParent = null;
		String bookPath = null;

		try {
			booksParent = session.getNode(repositoryBooksLocation);
		} catch (RepositoryException e) {
			LOG.error("the given path {} for persist the Books does not exist on the repository", repositoryBooksLocation);
		}
		if (null != booksParent) {
			Node bookNode = BookUtils.buildBook(booksParent);
			try {
				bookPath = bookNode.getPath();
				session.save();
			} catch (RepositoryException e) {
				LOG.error("Cannot persist the just created book at {}", bookPath);
			}
			if (null != bookPath) {
				BookUtils.displayBook(session, bookPath);
			} else {
				LOG.error("Cannot display the recently created book!, " +
						"please check this node at the repository console");
			}
		}
	}
}
