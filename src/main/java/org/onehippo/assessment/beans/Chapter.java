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

	public static class ChapterBuilder {
		private String title;

		private List<Paragraph> paragraphs;

		public ChapterBuilder withTitle(String title) {
			this.title = title;
			return this;
		}

		public ChapterBuilder withParagraphs(List<Paragraph> paragraphs) {
			this.paragraphs = paragraphs;
			return this;
		}

		public Chapter build() {
			return new Chapter(this);
		}
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
}
