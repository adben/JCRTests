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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for book {@link javax.jcr.Node} proposes
 */
public final class BookUtils {
	private static final Logger LOG = LoggerFactory.getLogger(BookUtils.class);

	private static final String BOOK_PRIMARY_TYPE = "adben:basebook";
	private static final String BOOK_NAME_PROPERTY = "adben:bookname";
	private static final String CHAPTER_PRIMARY_TYPE = "adben:chapter";
	private static final String CHAPTER_NAME_PROPERTY = "adben:chaptername";
	private static final String PARAGRAPH_PRIMARY_TYPE = "adben:paragraph";
	private static final String PARAGRAPH_NAME_PROPERTY = "adben:paragraphtitle";
	private static final String PARAGRAPH_CONTENT_PROPERTY = "adben:content";
	private static final String JCR_PRIMARY_TYPE = "jcr:primaryType";

	/**
	 * utility class not meant to be instantiated
	 */
	private BookUtils() {
	}

	private static String obtainRandomTitleName(String bookPart) {
		return obtainRandomTitleName(bookPart, 0, 0);
	}

	private static String obtainRandomTitleName(String bookPart, int part, int total) {
		StringBuilder title = new StringBuilder();
		title.append("Generated ").append(bookPart);
		if (part > 0) {
			title.append(" ").append(part).append(" of ").append(total);
		} else {
			title.append(" at ").append(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").toString());
		}
		return title.toString();
	}


	protected static String obtainRandomContent() {

		return "";
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

	private static void showBook(Book book) {
		LOG.info("Rendering Book => {}", book.toString());
	}

	private static Book obtainCompleteBook(Node node) {
		List<Chapter> chapters = new ArrayList<Chapter>();
		Book book = new Book();
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
		book.setTitle(title);
		book.setChapters(chapters);
		return book;
	}

	private static Chapter obtainChapter(Node node) {
		List<Paragraph> paragraphs = new ArrayList<Paragraph>();
		Chapter chapter = new Chapter();
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
		chapter.setTitle(title);
		chapter.setParagraphs(paragraphs);
		return chapter;
	}

	private static Paragraph obtainParagraph(Node node) {
		Paragraph paragraph = new Paragraph();
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
		paragraph.setTitle(title);
		paragraph.setContent(content);
		return paragraph;
	}

	//create and attach a chapter node,
	//create and attach a paragraph node,
	//persist,
	//Return the node path
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
}
