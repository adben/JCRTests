package org.onehippo.assessment;

import org.hippoecm.repository.HippoRepository;
import org.hippoecm.repository.HippoRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

/**
 * @version "$Id$"
 */
public final class Application {

	private static final String JCR_DATA = "jcr:data";
	private static Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws RepositoryException {
		final String repoUrl = "rmi://localhost:1099/hipporepository";
		final String username = "admin";
		final char[] password = "admin".toCharArray();
		HippoRepository repo = HippoRepositoryFactory.getHippoRepository(repoUrl);
		Session session = repo.login(username, password);
		Node root = session.getNode("/content");
		NodeIterator nodeIt = root.getNodes();
		navigateNode("", nodeIt);
		//Closing
		session.logout();
	}

	private static void navigateNode(String indent, NodeIterator nodeIt) throws RepositoryException {
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
		System.out.println(indent + child.getPath());
	}

	private static void printNodeProps(String indent, Node child) throws RepositoryException {
		final PropertyIterator properties = child.getProperties();
		while (properties.hasNext()) {
			Property property = properties.nextProperty();
			if (!JCR_DATA.equalsIgnoreCase(property.getName())) {
				if (property.getDefinition().isMultiple()) {
					// A multi-valued property, print all values
					Value[] values = property.getValues();
					for (Value value : values) {
						System.out.println(indent + property.getName() + " = " + value.getString());
					}
				} else {
					// A single-valued property
					System.out.println(indent + property.getName() + " = " + property.getString());
				}
			}
		}
	}
}
