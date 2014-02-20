package org.onehippo.assessment.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

/**
 * provide routines for the JCR {@link javax.jcr.Node Nodes}
 */
public final class NodeUtils {
	private static final Logger LOG = LoggerFactory.getLogger(NodeUtils.class);
	private static final String JCR_DATA = "jcr:data";
	private static final String JCR_QUERY_PREFIX = "//*[jcr:contains(.,'";
	private static final String JCR_QUERY_SUFFIX = "')]";

	/**
	 * Not meant to be instantiated
	 */
	private NodeUtils() {
	}

	/**
	 * Printout the {@link javax.jcr.Node} names of child nodes
	 *
	 * @param nodeIt the parent {@link javax.jcr.Node}
	 * @throws RepositoryException
	 */
	public static void navigateQueryResponseNode(NodeIterator nodeIt) throws RepositoryException {
		//
		while (nodeIt.hasNext()) {
			Node child = nodeIt.nextNode();
			printNodePath("", child);
		}
	}

	/**
	 * Printout node names of roots child nodes
	 *
	 * @param indent indentation as deep node info
	 * @param nodeIt the parent {@link javax.jcr.Node}
	 * @throws RepositoryException
	 */
	public static void navigateNode(String indent, NodeIterator nodeIt) throws RepositoryException {
		while (nodeIt.hasNext()) {
			Node child = nodeIt.nextNode();
			printNodePath(indent, child);
			printNodeProps(indent, child);
			if (child.hasNodes()) {
				navigateNode(indent + "  ", child.getNodes());
			}
		}
	}

	/**
	 * Printout node paths of the given {@link javax.jcr.Node}
	 *
	 * @param indent indentation as deep node info
	 * @param node   the {@link javax.jcr.Node}
	 * @throws RepositoryException
	 */
	private static void printNodePath(String indent, Node node) throws RepositoryException {
		LOG.info(indent + node.getPath());
	}

	/**
	 * Printout the node properties of the given {@link javax.jcr.Node}
	 *
	 * @param indent indentation as deep node info
	 * @param node   the {@link javax.jcr.Node}
	 * @throws RepositoryException
	 */
	private static void printNodeProps(String indent, Node node) throws RepositoryException {
		final PropertyIterator properties = node.getProperties();
		while (properties.hasNext()) {
			Property property = properties.nextProperty();
			//Not printing "jcr:data" types
			if (!JCR_DATA.equalsIgnoreCase(property.getName())) {
				if (property.getDefinition().isMultiple()) {
					// A multi-valued property, print all values
					Value[] values = property.getValues();
					for (Value value : values) {
						LOG.info(indent + property.getName() + " = (" + obtainPropertyType(property) + ")" + value.getString());
					}
				} else {
					// A single-valued property
					LOG.info(indent + property.getName() + " = (" + obtainPropertyType(property) + ")" + property.getString());
				}
			}
		}
	}

	/**
	 * Returns the JCR's property Type
	 *
	 * @param property the property
	 * @return the property type as a string
	 */
	private static String obtainPropertyType(Property property) {
		try {
			return PropertyType.nameFromValue(property.getType());
		} catch (RepositoryException e) {
			LOG.info("Error obtaining the property type information, with exception {}", e);
		}
		return "";
	}

	/******************** The Tasks *****************/


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
	public static void secondTask(Session session, String path) {
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
	public static void thirdTask(Session session, String queryTerm) {
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
	public static void fourthTask(Session session, String repositoryBooksLocation) {
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
