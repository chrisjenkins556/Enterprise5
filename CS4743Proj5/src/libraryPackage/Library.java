package libraryPackage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author Chris Jenkins
 *
 */
public class Library {
	private int id;
	private String libraryName;
	private List<LibraryBook> books;
	private LocalDateTime lastModified; 	
	
	public Library(){
		this.id = 0;
		this.libraryName = "";
		this.books = new ArrayList<LibraryBook>();
		this.lastModified = null;		
	}
	
	public Library(int id, String name, List<LibraryBook> books, LocalDateTime last){
		this.id = id;
		this.libraryName = name;
		this.books = books;
		this.lastModified = last;		
	}
	
	@Override
	public String toString() {
		return libraryName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	public List<LibraryBook> getBooks() {
		return books;
	}

	public void setBooks(List<LibraryBook> books) {
		this.books = books;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

}
