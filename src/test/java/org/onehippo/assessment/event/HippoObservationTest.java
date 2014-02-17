package org.onehippo.assessment.event;

import junit.framework.TestCase;
import org.junit.Before;
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

	@org.junit.Test
	public void testActivate() throws Exception {
		hippoObservation.activate();
		final Session session = RepoConnector.INSTANCE.getSession();
		createEmptyBooks(session, "/content/books/");
		//assertThat(session, is(nullValue()));
	}

	private static void createEmptyBooks(Session session, String repositoryBooksLocation) {
		LOG.info(":: try to create the   ::");
		Node booksParent = null;
		String bookPath = null;

		try {
			booksParent = session.getNode(repositoryBooksLocation);
		} catch (RepositoryException e) {
			LOG.error("the given path {} for persist the Books does not exist on the repository", repositoryBooksLocation);
		}
		if (null != booksParent) {
			Node bookNode = buildBook(booksParent);
			try {
				for (int i = 0; i < 6; i++) {
					bookPath = bookNode.getPath();
					session.save();
				}
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


	public static Node buildBook(Node booksParent) {
		Node book = null;
		String title = BookUtils.obtainRandomTitleName("Book") + BookUtils.getRandom(10);

		try {
			book = booksParent.addNode(BookUtils.obtainJcrName(title), BookUtils.BOOK_PRIMARY_TYPE);
			book.setProperty(BookUtils.BOOK_NAME_PROPERTY, title);
		} catch (RepositoryException e) {
			LOG.error("Cannot create the book in the repository with exception, {}", e);
		}
		return book;
	}
}
