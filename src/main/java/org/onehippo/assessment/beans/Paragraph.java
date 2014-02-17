package org.onehippo.assessment.beans;

/**
 * Paragraph representation
 */
public class Paragraph {
	private final String title;

	private final String content;

	public Paragraph(ParagraphBuilder builder) {
		this.title = builder.title;
		this.content = builder.content;
	}

	public String getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public static class ParagraphBuilder {
		private String title;

		private String content;

		public ParagraphBuilder withTitle(String title) {
			this.title = title;
			return this;
		}

		public ParagraphBuilder withContent(String content) {
			this.content = content;
			return this;
		}

		public Paragraph build() {
			return new Paragraph(this);
		}
	}

	@Override
	public String toString() {
		return '\n' +
				title.toUpperCase() + '\n' +
				content + '\n';
	}
}
