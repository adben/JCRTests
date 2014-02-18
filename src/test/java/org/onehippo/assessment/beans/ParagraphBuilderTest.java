package org.onehippo.assessment.beans;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * test for {@link org.onehippo.assessment.beans.Paragraph}
 */
public class ParagraphBuilderTest {
	final String content = "this is better than the loremipsum";
	final String title = "this is the title of the paragraph";
	Paragraph p;

	@Before
	public void setUp() throws Exception {
		p = new Paragraph.ParagraphBuilder().withContent(content).withTitle(title).build();
	}

	@Test
	public void testToStrong() throws Exception {
		final String plainParagraph = "\nTHIS IS THE TITLE OF THE PARAGRAPH\nthis is better that the loremipsum\n";
		assertThat(p.toString(), is(plainParagraph));
	}
}
