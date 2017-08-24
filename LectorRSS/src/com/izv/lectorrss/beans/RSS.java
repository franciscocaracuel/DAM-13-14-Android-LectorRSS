package com.izv.lectorrss.beans;

public class RSS {

	private String title, link, pubDate;
	
	public RSS(){
		
	}

	public RSS(String title, String link, String pubDate) {
		this.title = title;
		this.link = link;
		this.pubDate = pubDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	@Override
	public String toString() {
		return "RSS [title=" + title + ", link=" + link + ", pubDate="
				+ pubDate + "]";
	}

}
