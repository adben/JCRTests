package org.onehippo.assessment.beans;

import java.util.List;

/**
 * Book representation
 */
public class Book {
	private String title;

	private List<Chapter> chapters;

	public List<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
}
