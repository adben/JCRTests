package org.onehippo.assessment.util;

import org.onehippo.assessment.beans.Book;
import org.onehippo.assessment.beans.Chapter;
import org.onehippo.assessment.beans.Paragraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Utility class for book {@link javax.jcr.Node} proposes
 */
public final class BookUtils {
	private static final Logger LOG = LoggerFactory.getLogger(BookUtils.class);

	public static final String BOOK_PRIMARY_TYPE = "adben:basebook";
	public static final String BOOK_NAME_PROPERTY = "adben:bookname";
	public static final String CHAPTER_PRIMARY_TYPE = "adben:chapter";
	public static final String CHAPTER_NAME_PROPERTY = "adben:chaptername";
	public static final String PARAGRAPH_PRIMARY_TYPE = "adben:paragraph";
	public static final String PARAGRAPH_NAME_PROPERTY = "adben:paragraphtitle";
	public static final String PARAGRAPH_CONTENT_PROPERTY = "adben:content";

	/**
	 * utility class not meant to be instantiated
	 */
	private BookUtils() {
	}

	protected static String obtainRandomTitleName(String bookPart) {
		return "Generated " + bookPart + " title name at " + new Date().toString();
	}

	protected static String ontainRandomContent() {
		return "The following functional areas will be reviewed by the expert group for possible inclusion:\n" +
				"\n" +
				"Granular Read/Write Access - This is the bi-directional interaction of content elements. Issues with access on a property level and not just on a \"document\" level should be examined. A content transaction is any operation or service invoked as part of a system interaction with a content repository.\n" +
				"\n" +
				"Versioning - Transparent version handling across the entire content repository, would provide the ability to create versions of any content within the repository and select versions for any content access or modification.\n" +
				"\n" +
				"Hard- and Soft-structured Content - An Object Model that defines how hard and soft-structured content could be addressed will be examined.\n" +
				"\n" +
				"Event Monitoring (Observation)- Possible use of JMS based notification framework allowing for subscription on content modification will be examined.\n" +
				"\n" +
				"Full-text Search and filtering - The entire (non-binary) content of the repository could be indexed by a full-text search engine that enables exact and sub-string searching of content. The API will examine ways to standardize how content is queried, whether full-text or parametric. Of course, existing standard query languages will be respected.\n" +
				"\n" +
				"Access Control - Unified, extensible, access control mechanisms will be examined. Standards such as JAAS or ACL standards shall be taken into account.\n" +
				"\n" +
				"Object Classes - Profiles or Document Types could be defined and inherited to allow constraints and to give the application programmer the ability to operate on content object types.\n" +
				"\n" +
				"Namespaces & Standard Properties - Defining default standard properties that will maintain namespace uniqueness and hierarchy will be examined.\n" +
				"\n" +
				"Locking and Concurrency - Standardized access to locking and concurrency features of a repository will be examined.\n" +
				"\n" +
				"Linking - A standard mechanism to soft/hard link items and properties in a repository along with providing a mechanism to create relationships in the repository will be examined.";
	}

	private static String obtainJcrName(String title) {
		String parsedTitle = title.replaceAll(" ", "_");
		parsedTitle = parsedTitle.replaceAll(":", "_");
		return parsedTitle.toLowerCase();
	}

	public static int getRandom(int max) {
		return (int) (Math.random() * max);
	}


	public static void displayBook(Session session, String nodeCreatedPath) {
		//display the persisted book node
		try {
			Node bookNode = session.getNode(nodeCreatedPath);
			final Property bookPrimaryProperty = bookNode.getProperty("jcr:primaryType");
			if (null != bookPrimaryProperty && BOOK_PRIMARY_TYPE.equalsIgnoreCase(bookPrimaryProperty.getString())) {
				Book book = obtainCompleteBook(bookNode);
				showBook(book);
			} else {
				LOG.error("The given path {} does not correspond to the book definition", nodeCreatedPath);
			}
		} catch (RepositoryException e) {
			LOG.error("There was an error displaying the book node at {}, with exception {}", nodeCreatedPath, e);
		}
	}

	private static void showBook(Book book) {
		LOG.info("Rendering Book => {}", book);
	}

	private static Book obtainCompleteBook(Node node) {
		List<Chapter> chapters = new ArrayList<Chapter>();
		Book book = new Book();
		String title = "";
		try {
			title = node.getProperty(BOOK_NAME_PROPERTY).getString();
			final NodeIterator chapterNodes = node.getNodes(CHAPTER_PRIMARY_TYPE);
			while (chapterNodes.hasNext()) {
				Chapter chapter = obtainChapter(chapterNodes.nextNode());
				chapters.add(chapter);
			}
		} catch (RepositoryException e) {
			LOG.error("There was an error obtaining the chapter node from the book, with exception {}", e);
		}
		book.setTitle(title);
		book.setChapters( chapters);
		return book;
	}

	private static Chapter obtainChapter(Node node) {
		List<Paragraph> paragraphs = new ArrayList<Paragraph>();
		Chapter chapter = new Chapter();
		String title = "";
		try {
			title = node.getProperty(CHAPTER_NAME_PROPERTY).getString();
			final NodeIterator paragraphNodes = node.getNodes(PARAGRAPH_PRIMARY_TYPE);
			while (paragraphNodes.hasNext()) {
				Paragraph paragraph = obtainParagraph(paragraphNodes.nextNode());
				paragraphs.add(paragraph);
			}
		} catch (RepositoryException e) {
			LOG.error("There was an error obtaining the chapter node from the book, with exception {}", e);
		}
		chapter.setTitle(title);
		chapter.setParagraphs(paragraphs);
		return chapter;
	}

	private static Paragraph obtainParagraph(Node node) {
		Paragraph paragraph = new Paragraph();
		String title = "";
		String content = "";
		try {
			title = node.getProperty(PARAGRAPH_NAME_PROPERTY).getString();
			content = node.getProperty(PARAGRAPH_CONTENT_PROPERTY).getString();

		} catch (RepositoryException e) {
			LOG.error("There was an error obtaining the chapter node from the book, with exception {}", e);
		}
		paragraph.setTitle(title);
		paragraph.setContent(content);
		return paragraph;
	}

	//create and attach a chapter node,
	//create and attach a paragraph node,
	//persist,
	//Return the node path
	public static Node createBook(Node booksParent) {
		Node book = null;
		String title = BookUtils.obtainRandomTitleName("Book");
		try {
			book = booksParent.addNode(BookUtils.obtainJcrName(title), BookUtils.BOOK_PRIMARY_TYPE);
			book.setProperty(BookUtils.BOOK_NAME_PROPERTY, title);
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return book;
	}


	public static void attachChapters(Node bookNode, int numberChapters) {
		for (int i = 0; i < numberChapters; i++) {
			Node chapterNode;
			String title = BookUtils.obtainRandomTitleName("Chapter");
			try {
				chapterNode = bookNode.addNode(BookUtils.obtainJcrName(title), BookUtils.CHAPTER_PRIMARY_TYPE);
				chapterNode.setProperty(BookUtils.CHAPTER_NAME_PROPERTY, title);
				attachParagraph(chapterNode, BookUtils.getRandom(20));
			} catch (RepositoryException e) {
				LOG.error("Cannot create the chapter for the book!");
			}
		}
	}


	private static void attachParagraph(Node chapterNode, int numberParagraphs) {
		for (int i = 0; i < numberParagraphs; i++) {
			Node paragraphNode;
			String title = BookUtils.obtainRandomTitleName("Paragraph");
			String content = BookUtils.ontainRandomContent();
			try {
				paragraphNode = chapterNode.addNode(BookUtils.obtainJcrName(title), BookUtils.PARAGRAPH_PRIMARY_TYPE);
				paragraphNode.setProperty(BookUtils.PARAGRAPH_NAME_PROPERTY, title);
				paragraphNode.setProperty(BookUtils.PARAGRAPH_CONTENT_PROPERTY, content);
			} catch (RepositoryException e) {
				LOG.error("Cannot create the chapter for the book!");
			}
		}
	}
}
