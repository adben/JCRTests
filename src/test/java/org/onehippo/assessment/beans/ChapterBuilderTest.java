package org.onehippo.assessment.beans;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test for {@link org.onehippo.assessment.beans.Chapter}
 */
public class ChapterBuilderTest {
	Chapter chapter;
	Paragraph p1, p2;
	List<Paragraph> paragraphs;
	final String chap1 = "chap1";

	@Before
	public void setUp() throws Exception {
		p1 = new Paragraph.ParagraphBuilder().withTitle("p1").withContent("p1c").build();
		p2 = new Paragraph.ParagraphBuilder().withTitle("p2").withContent("p2c").build();
		paragraphs = ImmutableList.of(p1, p2);
		chapter = new Chapter.ChapterBuilder().withParagraphs(paragraphs).withTitle(chap1).build();
	}

	@Test
	public void itShouldBuildAChapter() throws Exception {
		assertThat(chapter.getTitle(), is(chap1));
		assertThat(chapter.getParagraphs().size(), is(2));
	}

	@Test
	public void testToString() throws Exception {
		String fullContent = "\n\n\nCHAP1\n\nP1\np1c\n\nP2\np2c\n";
		assertThat(chapter.toString(), is(fullContent));

	}
}
