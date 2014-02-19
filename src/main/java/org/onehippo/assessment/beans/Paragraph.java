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

	@Override
	public String toString() {
		return '\n' +
				getTitle().toUpperCase() + '\n' +
				getContent() + '\n';
	}

	public static class ParagraphBuilder {
		private String title;

		private String content;

		public ParagraphBuilder withTitle(final String titleInst) {
			this.title = titleInst;
			return this;
		}

		public ParagraphBuilder withContent(final String contentInst) {
			this.content = contentInst;
			return this;
		}

		public Paragraph build() {
			return new Paragraph(this);
		}
	}
}
