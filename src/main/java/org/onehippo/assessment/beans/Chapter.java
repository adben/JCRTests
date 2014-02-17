package org.onehippo.assessment.beans;

import java.util.List;

/**
 * Chapter representation
 */
public class Chapter {
	private String title;
	private List<Paragraph> paragraphs;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(List<Paragraph> paragraphs) {
		this.paragraphs = paragraphs;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getTitle().toUpperCase())
				.append("\n");
		for (Paragraph paragraph : this.getParagraphs()) {
			sb.append(paragraph.toString());
		}
		return sb.toString();
	}
}
