package org.onehippo.assessment.util;

import com.google.common.collect.ImmutableList;
import net._01001111.text.LoremIpsum;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Utility class for book {@link javax.jcr.Node} proposes
 */
public final class BookUtils {
	private static final Logger LOG = LoggerFactory.getLogger(BookUtils.class);

	public static final String BOOK_PRIMARY_TYPE = "adben:basebook";
	public static final String BOOK_NAME_PROPERTY = "adben:bookname";
	private static final String CHAPTER_PRIMARY_TYPE = "adben:chapter";
	private static final String CHAPTER_NAME_PROPERTY = "adben:chaptername";
	private static final String PARAGRAPH_PRIMARY_TYPE = "adben:paragraph";
	private static final String PARAGRAPH_NAME_PROPERTY = "adben:paragraphtitle";
	private static final String PARAGRAPH_CONTENT_PROPERTY = "adben:content";
	private static final String JCR_PRIMARY_TYPE = "jcr:primaryType";
	private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";


	/**
	 * utility class not meant to be instantiated
	 */
	private BookUtils() {
	}


	/**
	 * *** Generic routines for Book creation *******
	 */

	/**
	 * Provides an auto-generated title with date, for the specified book part.
	 *
	 * @param bookPart the book section
	 * @return string for the title
	 */
	public static String obtainRandomTitleName(String bookPart) {
		return obtainRandomTitleName(bookPart, 0, 0);
	}

	/**
	 * Provides an auto-generated title for the specified book part:
	 * <ul><li>A String with ordered section information if part and total are indicated</li>
	 * <li>Or a String with date otherwise</li></ul>
	 *
	 * @param bookPart the book section
	 * @return String for the title
	 */
	private static String obtainRandomTitleName(String bookPart, int part, int total) {
		StringBuilder title = new StringBuilder();
		title.append("Generated ").append(bookPart);
		if (total > 0) {
			//Part+1 due is initialized in zero
			title.append(" ").append(part + 1).append(" of ").append(total);
		} else {
			DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			Calendar cal = Calendar.getInstance();
			title.append(" at ").append(dateFormat.format(cal.getTime()));
		}
		return title.toString();
	}

	/**
	 * Provides LoremIpsum paragraph dummy content
	 *
	 * @return String with content
	 */
	protected static String obtainRandomContent() {
		return new LoremIpsum().paragraphs(1, true);
	}

	/**
	 * Escapes the expected invalid characters on the given String
	 *
	 * @param originalString the string to escape
	 * @return the escaped string
	 */
	public static String obtainJcrName(String originalString) {
		String parsedTitle = originalString.replaceAll(" ", "_");
		parsedTitle = parsedTitle.replaceAll(":", "_");
		return parsedTitle.toLowerCase();
	}

	/**
	 * Provides a randomized integer with a delimited {@code max} value
	 *
	 * @param max the max value
	 * @return a random integer
	 */
	public static int getRandom(int max) {
		return (int) (Math.random() * max);
	}

	/**
	 * Builds the Book document in the repository:
	 * <ul><li>creating the book node</li>
	 * <li>attach the randomized chapter nodes</li>
	 *
	 * @param booksParent The {@link javax.jcr.Node Node's repository} location for the books
	 * @return the {@link javax.jcr.Node Book}
	 */
	public static Node buildBook(Node booksParent) {
		Node book = null;
		String title = BookUtils.obtainRandomTitleName("Book");
		try {
			book = booksParent.addNode(BookUtils.obtainJcrName(title), BookUtils.BOOK_PRIMARY_TYPE);
			book.setProperty(BookUtils.BOOK_NAME_PROPERTY, title);
			attachChapters(book, BookUtils.getRandom(10));
		} catch (RepositoryException e) {
			LOG.error("Cannot create the book in the repository with exception, {}", e);
		}
		return book;
	}

	/**
	 * Creates an specific number of Chapters and attach to them a randomized number of paragraphs, to the given {@code bookNode}
	 *
	 * @param bookNode       The {@link javax.jcr.Node Book}
	 * @param numberChapters the number of chapters to create
	 */
	private static void attachChapters(Node bookNode, int numberChapters) {
		for (int i = 0; i < numberChapters; i++) {
			Node chapterNode;
			String title = BookUtils.obtainRandomTitleName("Chapter", i, numberChapters);
			try {
				chapterNode = bookNode.addNode(BookUtils.obtainJcrName(title), BookUtils.CHAPTER_PRIMARY_TYPE);
				chapterNode.setProperty(BookUtils.CHAPTER_NAME_PROPERTY, title);
				attachParagraph(chapterNode, BookUtils.getRandom(20));
			} catch (RepositoryException e) {
				LOG.error("Cannot attach the chapter node to the book!, with exception {}", e);
			}
		}
	}

	/**
	 * Creates an specific number of paragraphs and attach to them a dummy of paragraphs, to the given {@code chapterNode}
	 *
	 * @param chapterNode      The {@link javax.jcr.Node Book}
	 * @param numberParagraphs the number of paragraph to create
	 */
	private static void attachParagraph(Node chapterNode, int numberParagraphs) {
		for (int i = 0; i < numberParagraphs; i++) {
			Node paragraphNode;
			String title = BookUtils.obtainRandomTitleName("Paragraph", i, numberParagraphs);
			String content = BookUtils.obtainRandomContent();
			try {
				paragraphNode = chapterNode.addNode(BookUtils.obtainJcrName(title), BookUtils.PARAGRAPH_PRIMARY_TYPE);
				paragraphNode.setProperty(BookUtils.PARAGRAPH_NAME_PROPERTY, title);
				paragraphNode.setProperty(BookUtils.PARAGRAPH_CONTENT_PROPERTY, content);
			} catch (RepositoryException e) {
				LOG.error("Cannot create the chapter for the book!");
			}
		}
	}


	/**
	 * *** Generic routines for Book manipulation *******
	 */

	/**
	 * Obtains the JCR book from the {@code nodeCreatedPath} and displays the {@link org.onehippo.assessment.beans.Book} representation of it
	 *
	 * @param session         the session
	 * @param nodeCreatedPath the repository's book path
	 */
	public static void displayBook(Session session, String nodeCreatedPath) {
		//display the persisted book node
		try {
			Node bookNode = session.getNode(nodeCreatedPath);
			final Property bookPrimaryProperty = bookNode.getProperty(JCR_PRIMARY_TYPE);
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

	/**
	 * Displays the {@link org.onehippo.assessment.beans.Book}
	 *
	 * @param book the Book to display
	 */
	private static void showBook(Book book) {
		LOG.info("Rendering Book => {}", book.toString());
	}

	/**
	 * Obtains the complete book node with chapters and paragraphs, if any. From the given {@link javax.jcr.Node Book}
	 *
	 * @param node the Book node
	 * @return the {@link org.onehippo.assessment.beans.Book} representation of the book node
	 */
	private static Book obtainCompleteBook(Node node) {
		List<Chapter> chapters = new ArrayList<Chapter>();
		String title = "";
		try {
			title = node.getProperty(BOOK_NAME_PROPERTY).getString();
			final NodeIterator chapterNodes = node.getNodes();
			while (chapterNodes.hasNext()) {
				Chapter chapter = obtainChapter(chapterNodes.nextNode());
				if (null != chapter) {
					chapters.add(chapter);
				}
			}
		} catch (RepositoryException e) {
			LOG.error("There was an error obtaining the chapter node from the book, with exception {}", e);
		}
		return new Book.BookBuilder(title).withChapters(ImmutableList.copyOf(chapters)).build();
	}

	/**
	 * Obtains the complete chapter node with paragraphs, if any. From the given {@link javax.jcr.Node Chapter}
	 *
	 * @param node the Chapter node
	 * @return the {@link org.onehippo.assessment.beans.Chapter} representation of the Chapter node
	 */
	private static Chapter obtainChapter(Node node) {
		List<Paragraph> paragraphs = new ArrayList<Paragraph>();
		String title = "";
		try {
			final String currentType = node.getProperty(JCR_PRIMARY_TYPE).getString();
			if (!CHAPTER_PRIMARY_TYPE.equalsIgnoreCase(currentType)) {
				return null;
			}
			title = node.getProperty(CHAPTER_NAME_PROPERTY).getString();
			final NodeIterator paragraphNodes = node.getNodes();
			while (paragraphNodes.hasNext()) {
				Paragraph paragraph = obtainParagraph(paragraphNodes.nextNode());
				if (null != paragraph) {
					paragraphs.add(paragraph);
				}
			}
		} catch (RepositoryException e) {
			LOG.error("There was an error obtaining the chapter node from the book, with exception {}", e);
		}
		return new Chapter.ChapterBuilder().withTitle(title).withParagraphs(ImmutableList.copyOf(paragraphs)).build();
	}

	/**
	 * Obtains the complete paragraph elements:<ul><li>title</li><li>content</li></ul> if any. From the given {@link javax.jcr.Node paragraph}
	 *
	 * @param node the Chapter node
	 * @return the {@link org.onehippo.assessment.beans.Paragraph} representation of the Paragraph node
	 */
	private static Paragraph obtainParagraph(Node node) {
		String title = "";
		String content = "";
		try {
			final String currentType = node.getProperty(JCR_PRIMARY_TYPE).getString();
			if (!PARAGRAPH_PRIMARY_TYPE.equalsIgnoreCase(currentType)) {
				return null;
			}
			title = node.getProperty(PARAGRAPH_NAME_PROPERTY).getString();
			content = node.getProperty(PARAGRAPH_CONTENT_PROPERTY).getString();

		} catch (RepositoryException e) {
			LOG.error("There was an error obtaining the chapter node from the book, with exception {}", e);
		}
		return new Paragraph.ParagraphBuilder().withTitle(title).withContent(content).build();
	}

}
