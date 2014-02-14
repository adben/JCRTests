package org.onehippo.assessment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

/**
 * provide the routines for the JCR {@link javax.jcr.Node}
 */
public final class NodeUtils {
	private static final String JCR_DATA = "jcr:data";
	private static final Logger LOG = LoggerFactory.getLogger(NodeUtils.class);

	/**
	 * Not meant to be instantiated
	 */
	private NodeUtils() {
	}


	protected static void navigateQueryResponseNode(NodeIterator nodeIt) throws RepositoryException {
		// print out node names of roots child nodes
		while (nodeIt.hasNext()) {
			Node child = nodeIt.nextNode();
			printNodePath("", child);
		}
	}

	protected static void navigateNode(String indent, NodeIterator nodeIt) throws RepositoryException {
		// print out node names of roots child nodes
		while (nodeIt.hasNext()) {
			Node child = nodeIt.nextNode();
			printNodePath(indent, child);
			printNodeProps(indent, child);
			if (child.hasNodes()) {
				navigateNode(indent + "  ", child.getNodes());
			}
		}
	}

	private static void printNodePath(String indent, Node child) throws RepositoryException {
		LOG.info(indent + child.getPath());
	}

	private static void printNodeProps(String indent, Node child) throws RepositoryException {
		final PropertyIterator properties = child.getProperties();
		while (properties.hasNext()) {
			Property property = properties.nextProperty();
			//Not printing "jcr:data" types
			if (!JCR_DATA.equalsIgnoreCase(property.getName())) {
				if (property.getDefinition().isMultiple()) {
					// A multi-valued property, print all values
					Value[] values = property.getValues();
					for (Value value : values) {
						LOG.info(indent + property.getName() + " = " + value.getString());
					}
				} else {
					// A single-valued property
					LOG.info(indent + property.getName() + " = " + property.getString());
				}
			}
		}
	}
}
