package libraryPackage;

import bookPackage.Book;
/**
 * 
 * @author Chris Jenkins
 *
 */
public class LibraryBook {
	private int quantity;
	@Override
	public String toString() {
		return "Book=" + book + "quantity=" + quantity ;
	}
	
	private Book book;
	private boolean newRecord;
	
	public LibraryBook() {
		this.quantity = 0;
		this.book = new Book();
		this.newRecord = true;
	}
	public LibraryBook(Book book, int quantity, boolean newRecord) {
		this.quantity = quantity;
		this.book = book;
		this.newRecord = newRecord;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
}
