package org.onehippo.assessment.event;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.onehippo.assessment.RepoConnector;
import org.onehippo.assessment.util.BookUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Test for {@link org.onehippo.assessment.event.HippoObservation}
 */
public class HippoObservationTest extends TestCase {
	private static final Logger LOG = LoggerFactory.getLogger(HippoObservationTest.class);

	HippoObservation hippoObservation;

	@Before
	public void setUp() throws Exception {
		hippoObservation = new HippoObservation();
	}

	@Test
	public void testObservation() throws Exception {
		hippoObservation.activate();
		//This will trigger 3 Event changes at the repo
		Node emptyBook = createEmptyBook(RepoConnector.INSTANCE.getSession(), "/content/books/");
		Thread.sleep(20000);

		while (Counter.INSTANCE.currentValue() < 5) {
			LOG.info("Counter at {}, executing some property changes ", Counter.INSTANCE.currentValue());
			String nameBook = BookUtils.obtainRandomTitleName("Book") + BookUtils.getRandom(10);
			emptyBook.setProperty(BookUtils.BOOK_NAME_PROPERTY, nameBook);
			LOG.info("new book name '{}'", nameBook);
			Thread.sleep(20000);
			RepoConnector.INSTANCE.getSession().save();
		}

		LOG.info("Counter at {}, reached max events. Done", Counter.INSTANCE.currentValue());
		hippoObservation.deactivate();
	}

	/**
	 * Book creator for testing proposes
	 *
	 * @param session                 the session
	 * @param repositoryBooksLocation the book parent node path
	 * @return the created node
	 */
	protected static Node createEmptyBook(Session session, String repositoryBooksLocation) {
		LOG.info(":: About to create the book node ::");
		Node booksParent = null;
		if (null == session) {
			LOG.error("Invalid session, please confirm the state of the observer");
		}
		try {
			assert session != null;
			booksParent = session.getNode(repositoryBooksLocation);
		} catch (RepositoryException e) {
			LOG.error("the given path {} for persist the Books does not exist on the repository", repositoryBooksLocation);
		}
		if (null != booksParent) {
			try {
				String nameBook = BookUtils.obtainRandomTitleName("Book") + BookUtils.getRandom(10);
				return booksParent.addNode(BookUtils.obtainJcrName(nameBook), BookUtils.BOOK_PRIMARY_TYPE);
			} catch (RepositoryException e) {
				LOG.error("Cannot persist the just created book", e);
			}
		}
		return null;
	}


}
