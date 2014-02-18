package org.onehippo.assessment.beans;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tes
 */
public class BookTest {
	Book book;
	Chapter c1, c2;
	Paragraph p1, p2;

	@Before
	public void setUp() throws Exception {
		p1 = new Paragraph.ParagraphBuilder().withTitle("p1").withContent("p1c").build();
		p2 = new Paragraph.ParagraphBuilder().withTitle("p2").withContent("p2c").build();
		c1 = new Chapter.ChapterBuilder().withParagraphs(ImmutableList.of(p1, p2)).withTitle("X1").build();
		c2 = new Chapter.ChapterBuilder().withParagraphs(ImmutableList.of(p2, p1)).withTitle("X2").build();
		book = new Book.BookBuilder("theBook").withChapters(ImmutableList.of(c1, c2)).build();

	}

	@Test
	public void shouldBuildABookOrdered() throws Exception {
		assertThat(book.getChapters().size(), is(2));
		final Paragraph paragraph = book.getChapters().get(1).getParagraphs().get(0);
		assertThat(paragraph.getTitle(), is("p2"));
	}

	@Test
	public void testToString() throws Exception {
		final String fullbook = "THEBOOK\n\n\n\nX1\n\nP1\np1c\n\nP2\np2c\n\n\n\nX2\n\nP2\np2c\n\nP1\np1c\n";
		assertThat(book.toString(), is(fullbook));

	}
}
