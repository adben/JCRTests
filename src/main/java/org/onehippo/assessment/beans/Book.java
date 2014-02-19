package org.onehippo.assessment.beans;

import java.util.List;

/**
 * Book representation
 */
public class Book {
	private final String title;

	private final List<Chapter> chapters;

	public Book(BookBuilder bookBuilder) {
		this.title = bookBuilder.title;
		this.chapters = bookBuilder.chapters;
	}

	public List<Chapter> getChapters() {
		return chapters;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getTitle().toUpperCase())
				.append("\n");
		for (Chapter chapter : this.getChapters()) {
			sb.append(chapter.toString());
		}
		return sb.toString();
	}

	public static class BookBuilder {
		private String title;

		private List<Chapter> chapters;

		public BookBuilder withChapters(final List<Chapter> chaptersInst) {
			this.chapters = chaptersInst;
			return this;
		}

		public BookBuilder(final String titleInst) {
			this.title = titleInst;
		}

		public Book build() {
			return new Book(this);
		}
	}

}
