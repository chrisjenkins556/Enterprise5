package bookPackage;

import java.time.LocalDateTime;

import authorPackage.Author;
/**
 * 
 * @author Chris Jenkins
 *
 */
public class Book {
	private int id;
	private String title;
	private String publisher;
	private String datePublished;
	private String summary;
	private Author author;
	private LocalDateTime lastModified;
	
	public Book(int id, String title, String publisher, String datePublished, String summary, Author author, LocalDateTime date) {
		this.id = id;
		this.title = title;
		this.publisher = publisher;
		this.datePublished = datePublished;
		this.summary = summary;
		this.author = author;
		this.lastModified = date;
	}
	
	public Book() {
		this.id = 0;
		this.title = "";
		this.publisher = "";
		this.datePublished = "";
		this.summary = "";
		this.author = new Author();
		this.lastModified = null;
	}
	
	@Override
	public String toString() {
		return author.getFirstName() + " " + author.getLastName() + " - " + title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(String datePublished) {
		this.datePublished = datePublished;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

}
