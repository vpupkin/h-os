package org.lzy.fwswaper.htmlhandler.htmlparserex;

import org.htmlparser.nodes.TagNode;

/**
 * Identifies a link tag in head.
 */
@SuppressWarnings("serial")
public class HeaderLinkTag extends TagNode {

	private static final String[] mIds = new String[] { "LINK" };

	private static final String[] mEndTagEnders = new String[] { "HEAD", "HTML" };

	private String link = null;

	public String[] getIds() {
		return (mIds);
	}

	public String[] getEndTagEnders() {
		return (mEndTagEnders);
	}

	public String getRel() {
		return this.getAttribute("REL");
	}

	public String getType() {
		return this.getAttribute("TYPE");
	}

	public String getMedia() {
		return this.getAttribute("MEDIA");
	}

	public String getTitle() {
		return this.getAttribute("TITLE ");
	}

	public String getLink() {

		if (this.link == null)
			this.link = this.getAttribute("HREF");

		return this.link;
	}

	public void setLink(String link) {
		this.link = link;
		setAttribute("HREF", link);
	}

	public String getLinkText() {

		String ret = null;

		if (null != getChildren())
			ret = getChildren().asString();
		else
			ret = new String();

		return ret;
	}
}
