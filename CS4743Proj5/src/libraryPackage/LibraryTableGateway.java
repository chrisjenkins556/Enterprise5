package libraryPackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import Main.MasterController;
import audit.auditTrailEntry;
import authorPackage.Author;
import bookPackage.Book;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
/**
 * 
 * @author Chris Jenkins
 *
 */
public class LibraryTableGateway {
	private static Logger logger = LogManager.getLogger();
	private MysqlDataSource ds = null;
	Statement stmt = null; 
	Statement stmt2 = null; 
	ResultSet rs = null;
	ResultSet rsb = null;
	ResultSet rsa = null;
	ResultSet rsc = null;
	Connection conn = null;
	private List<Library> listView = new ArrayList<Library>();
	private List<LibraryBook> listViewBook = new ArrayList<LibraryBook>();	
	public LibraryTableGateway() throws SQLException{
		try{
			this.ds = new MysqlDataSource(); 
			ds.setURL("jdbc:mysql://easel2.fulgentcorp.com:3306/rmr581"); 
			ds.setUser("rmr581");
			ds.setPassword("54ZjfznJLWACUBzyYWyz");
		}catch (Exception e){
			logger.error(e);
		}		
	}	
	
	public List<Library> getLibrarys() throws SQLException{
		conn = ds.getConnection();
		try { 
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rsb = stmt.executeQuery("SELECT * FROM library");
			while(rsb.next()) {
				stmt = conn.createStatement();
				rsa = stmt.executeQuery("SELECT * FROM `library_book` WHERE `library_id` = " + rsb.getInt("id"));
				while(rsa.next()){
					stmt = conn.createStatement();
					rs = stmt.executeQuery("SELECT * FROM book WHERE id = " + rsa.getInt("book_id"));
					while(rs.next()) {
						stmt = conn.createStatement();
						rsc = stmt.executeQuery("SELECT * FROM `Authors` WHERE `id` = " + rs.getInt("author_id"));
						if(rsc.next()){
							listViewBook.add(new LibraryBook((new Book(rs.getInt("id"), rs.getString("title"),rs.getString("publisher"),
									rs.getDate("date_published").toString(),rs.getString("summary"),new Author(rsc.getString("first_name"),rsc.getString("last_name"),
											rsc.getString("gender"),rsc.getString("web_site"),rsc.getDate("dob"),rsc.getInt("id"), rsc.getTimestamp("last_modified").toLocalDateTime()), rs.getTimestamp("last_modified").toLocalDateTime())),rsa.getInt("quantity"),true));		
						}
					}
				}
	
				listView.add(new Library(rsb.getInt("id"),rsb.getString("library_name"),listViewBook,rsb.getTimestamp("last_modified").toLocalDateTime()));
				listViewBook = new ArrayList<LibraryBook>() ;
			}
		    conn.commit();
		} catch(SQLException e) { 
			conn.rollback();
			logger.error("Failed reading database" + e);
		} finally { 
			if(rsa != null) 
				rsa.close(); 
			if(rsb != null) 
				rsb.close(); 
			if(stmt != null)  
				stmt.close(); 
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close(); 
			}
		}
		return listView;		
	}
	
	public void deleteLibrary(Library library) throws SQLException {
		conn = ds.getConnection(); 
		try { 
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("DELETE from `autdit_trail` WHERE record_id = ? AND record_type = 'L'");
			ps.setInt(1, library.getId());
			ps.executeUpdate();			
			ps.close();			
			ps = conn.prepareStatement("DELETE from `library_book` WHERE library_id = ?");
			ps.setInt(1, library.getId());
			ps.executeUpdate();		
			ps.close();			
			ps = conn.prepareStatement("DELETE from `library` WHERE id = ?");
			ps.setInt(1, library.getId());
			ps.executeUpdate();		
			ps.close();			
			conn.commit();
		} catch(SQLException e) { 
			logger.error("Failed to Delete entry in database: \n" +e.getMessage());
			conn.rollback();
		} finally { 
			if(stmt != null)  
				stmt.close(); 
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close(); 
			}
		}				
	}
	
	public void insertLibrary(Library library) throws SQLException {
		conn = ds.getConnection(); 
		ResultSet rs = null;
		try { 
			conn.setAutoCommit(false);
		    PreparedStatement ps = conn.prepareStatement("INSERT INTO `library`( `library_name`) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS);
		    ps.setString(1,library.getLibraryName());
		    ps.executeUpdate();
		    rs = ps.getGeneratedKeys();		    
		    if(rs != null && rs.next()) {
		    	library.setId(rs.getInt(1));
			}
		    ps.close();		    
		    ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
		    ps.setInt(1, library.getId());
		    ps.setString(2, "Added " + library.toString());
		    ps.executeUpdate();
		    logger.info("start");
		    if(library.getBooks() != null){
			    List<LibraryBook> librarybooks = library.getBooks();
			    for(LibraryBook b : librarybooks){
			    	Book books = b.getBook();
			    	ps = conn.prepareStatement("insert into `library_book` (library_id, book_id, quantity) values (?, ?, ?)");
				    ps.setInt(1, library.getId());
				    ps.setInt(2, books.getId());
				    ps.setInt(3, b.getQuantity());
				    ps.executeUpdate();
				    ps.close();
				    ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
				    ps.setInt(1, library.getId());
				    ps.setString(2, "Added Book" + books.toString());
				    ps.executeUpdate();	
			    }
		    }		    
		    conn.commit();
		} catch(SQLException e) { 
			logger.error("Failed to insert new entry in database: \n" +e.getMessage());
			conn.rollback();
		} finally { 
			if(rs != null) 
				rs.close(); 
			if(stmt != null)  
				stmt.close(); 
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close(); 
			}
		}				
	}
	
	public void updateLibrary(Library library, Library oldlibrary) throws ParseException, SQLException {
		conn = ds.getConnection();
		PreparedStatement ps;
		try { 
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT `last_modified` FROM library WHERE `id` = " + library.getId());
		    rs.next();
			if(!library.getLastModified().toString().equals(rs.getTimestamp("last_modified").toLocalDateTime().toString())){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Update Error");
				alert.setContentText("Library not up to date. please try again.");
				alert.showAndWait(); 
				if(rs != null) 
					rs.close(); 
				if(conn != null) {
					conn.setAutoCommit(true);
					conn.close(); 
				}
				MasterController.getInstance().changeView(authorPackage.ViewType.Library_List_View, library);
		    }			
		    ps = conn.prepareStatement("UPDATE library SET library_name = ? WHERE id = ?");
		    ps.setString(1,library.getLibraryName());
		    ps.setInt(2, library.getId());
		    ps.executeUpdate();
		    stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT `last_modified` FROM library WHERE `id` = " + library.getId());
		    rs.next();
		    library.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
		    ps.close();
		    if(!library.getLibraryName().equals(oldlibrary.getLibraryName())){
		    	 	ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
				    ps.setInt(1, library.getId());
				    ps.setString(2, "Library name changed from " + oldlibrary.getLibraryName() + " to " + library.getLibraryName());
				    ps.executeUpdate();
		    }
		    List<LibraryBook> libraryBook = library.getBooks();
		    stmt = conn.createStatement();
			rsb = stmt.executeQuery("SELECT * FROM library_book WHERE library_id = "+library.getId());
			for(LibraryBook books : libraryBook){
					int flag = 0;
					while(rsb.next()) {
						Book boook = books.getBook();
						if(boook.getId() == rsb.getInt("book_id")){
							flag = 1;
							if(books.getQuantity() != rsb.getInt("quantity")){
								ps = conn.prepareStatement("UPDATE library_book SET quantity = ? WHERE library_id = ? AND book_id = ?");
								ps.setInt(1,books.getQuantity());
								ps.setInt(2, library.getId());
								ps.setInt(3, boook.getId());
								ps.executeUpdate();						    
								ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
								ps.setInt(1, library.getId());
								ps.setString(2, "Book quantity changed from " + rsb.getInt("quantity") + " to " + books.getQuantity());
								ps.executeUpdate();
							}
							rsb.first();
							break;
						}					
					}
				if(flag == 0){
					 	ps = conn.prepareStatement("insert into `library_book` (library_id, book_id, quantity) values (?, ?, ?)");
					    ps.setInt(1, library.getId());
					    ps.setInt(2,books.getBook().getId() );
					    ps.setInt(3, books.getQuantity());
					    ps.executeUpdate();
					    ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
						ps.setInt(1, library.getId());
						ps.setString(2, "Added new book: " + books.getBook());
						ps.executeUpdate();
						rsb.first();
				}
			}		    
		    conn.commit();
		} catch(SQLException e) { 
			logger.error("Failed updating database" + e);
			conn.rollback();
		} finally { 
			if(stmt != null)  
				stmt.close(); 
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close(); 
			}
		}
	}
	
	public List<auditTrailEntry> auditTrail(Library library) throws SQLException {
		List<auditTrailEntry> list = new ArrayList<auditTrailEntry>();
		conn = ds.getConnection(); 
		try { 
		    stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM `autdit_trail` WHERE `record_id` = "+library.getId()+" AND `record_type` = 'L' ORDER BY `date_added` ASC"); 
			while(rs.next()) {
				list.add(new auditTrailEntry(rs.getString("record_type"),rs.getTimestamp("date_added"),
						rs.getString("entry_msg")));		
			}
		} catch(SQLException e) { 
			logger.error("Failed reading database" + e);
		} finally { 
			if(rs != null) 
				rs.close(); 
			if(stmt != null)  
				stmt.close(); 
			if(conn != null) {
				conn.close(); 
			}
		}
		return list;
	}
	
	public void close() throws SQLException { 
		if(stmt != null)  
			stmt.close(); 
		if(conn != null) {
			conn.close(); 
			logger.info("closed");
		}		
	}


	public List<LibraryBook> getLibraryBooks(int id) throws SQLException {
		conn = ds.getConnection();
		List<LibraryBook> iHateYou = new ArrayList<LibraryBook>();

		try { 
			conn.setAutoCommit(false);
				stmt = conn.createStatement();
				rsa = stmt.executeQuery("SELECT * FROM `library_book` WHERE `library_id` = " + id);
				while(rsa.next()){
					stmt = conn.createStatement();
					rs = stmt.executeQuery("SELECT * FROM book WHERE id = " + rsa.getInt("book_id"));
					while(rs.next()) {
						stmt = conn.createStatement();
						rsc = stmt.executeQuery("SELECT * FROM `Authors` WHERE `id` = " + rs.getInt("author_id"));
						if(rsc.next()){
							iHateYou.add(new LibraryBook((new Book(rs.getInt("id"), rs.getString("title"),rs.getString("publisher"),
									rs.getDate("date_published").toString(),rs.getString("summary"),new Author(rsc.getString("first_name"),rsc.getString("last_name"),
											rsc.getString("gender"),rsc.getString("web_site"),rsc.getDate("dob"),rsc.getInt("id"), rsc.getTimestamp("last_modified").toLocalDateTime()), rs.getTimestamp("last_modified").toLocalDateTime())),rsa.getInt("quantity"),true));		
						}
					}
				}
	
		    conn.commit();

		} catch(SQLException e) { 
			conn.rollback();
			logger.error("Failed reading database" + e);
		} finally { 
			if(rsa != null) 
				rsa.close(); 
			if(rsb != null) 
				rsb.close(); 
			if(stmt != null)  
				stmt.close(); 
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close(); 
			}
		}
		return listViewBook;
		
	}


	public void deleteLibraryBook(Library library, LibraryBook selectedItem) throws SQLException {
		conn = ds.getConnection(); 
		try { 
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("DELETE from `library_book` WHERE library_id = ? AND book_id = ?");
			ps.setInt(2, selectedItem.getBook().getId());
			ps.setInt(1, library.getId());
			ps.executeUpdate();		
			ps.close();			
			conn.commit();
		} catch(SQLException e) { 
			logger.error("Failed to Delete entry in database: \n" +e.getMessage());
			conn.rollback();
		} finally { 
			if(stmt != null)  
				stmt.close(); 
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close(); 
			}
		}		
	}
}

