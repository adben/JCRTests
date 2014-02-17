package org.onehippo.assessment.beans;

/**
 * Paragraph representation
 */
public class Paragraph {
	private String title;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private String content;
}
