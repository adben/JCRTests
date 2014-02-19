package org.onehippo.assessment.beans;

import java.util.List;

/**
 * Chapter representation
 */
public class Chapter {
	private final String title;

	private final List<Paragraph> paragraphs;

	public Chapter(ChapterBuilder chapterBuilder) {
		this.title = chapterBuilder.title;
		this.paragraphs = chapterBuilder.paragraphs;
	}

	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n").append("\n").append("\n")
				.append(this.getTitle().toUpperCase())
				.append("\n");
		for (Paragraph paragraph : this.getParagraphs()) {
			sb.append(paragraph.toString());
		}
		return sb.toString();
	}

	public static class ChapterBuilder {
		private String title;

		private List<Paragraph> paragraphs;

		public ChapterBuilder withTitle(final String titleInst) {
			this.title = titleInst;
			return this;
		}

		public ChapterBuilder withParagraphs(final List<Paragraph> paragraphList) {
			this.paragraphs = paragraphList;
			return this;
		}

		public Chapter build() {
			return new Chapter(this);
		}
	}
}
