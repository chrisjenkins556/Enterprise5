package bookPackage;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
/**
 * 
 * @author Chris Jenkins
 *
 */
public class BookTableGateway {
	private static Logger logger = LogManager.getLogger();
	private MysqlDataSource ds = null;
	Statement stmt = null; 
	Statement stmt2 = null; 
	ResultSet rs = null;
	ResultSet rsb = null;
	ResultSet rsa = null;
	Connection conn = null;
	List<Book> listView = new ArrayList<Book>();
	
	public BookTableGateway() throws SQLException{
		try{
			this.ds = new MysqlDataSource(); 
			ds.setURL("jdbc:mysql://easel2.fulgentcorp.com:3306/rmr581"); 
			ds.setUser("rmr581");
			ds.setPassword("54ZjfznJLWACUBzyYWyz");
		}catch (Exception e){
			logger.error(e);
		}		
	}
	
	public List<Book> getBooks() throws SQLException{
		conn = ds.getConnection();
		try { 
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rsb = stmt.executeQuery("SELECT * FROM book");
			while(rsb.next()) {
				stmt = conn.createStatement();
				rsa = stmt.executeQuery("SELECT * FROM `Authors` WHERE `id` = " + rsb.getInt("author_id"));
				if(rsa.next()){
					listView.add(new Book(rsb.getInt("id"), rsb.getString("title"),rsb.getString("publisher"),
							rsb.getDate("date_published").toString(),rsb.getString("summary"),new Author(rsa.getString("first_name"),rsa.getString("last_name"),
									rsa.getString("gender"),rsa.getString("web_site"),rsa.getDate("dob"),rsa.getInt("id"), rsa.getTimestamp("last_modified").toLocalDateTime()), rsb.getTimestamp("last_modified").toLocalDateTime()));		
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
		return listView;		
	}

	public void deleteBook(Book book) throws SQLException {
		conn = ds.getConnection(); 
		try { 
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("DELETE from `autdit_trail` WHERE record_id = ? AND record_type = 'B'");
			ps.setInt(1, book.getId());
			ps.executeUpdate();			
			ps.close();			
			ps = conn.prepareStatement("DELETE from `book` WHERE id = ?");
			ps.setInt(1, book.getId());
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

	public void insertBook(Book book) throws SQLException {
		conn = ds.getConnection(); 
		ResultSet rs = null;
		try { 
			conn.setAutoCommit(false);
		    PreparedStatement ps = conn.prepareStatement("INSERT INTO `book`( `title`, `publisher`, `date_published`, `summary`, `author_id`) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
		    ps.setString(1,book.getTitle());
		    ps.setString(2,book.getPublisher());
		    ps.setDate(3,java.sql.Date.valueOf(book.getDatePublished()));
		    ps.setString(4,book.getSummary());
		    ps.setInt(5, book.getAuthor().getId());
		    ps.executeUpdate();
		    rs = ps.getGeneratedKeys();		    
		    if(rs != null && rs.next()) {
		    	book.setId(rs.getInt(1));
			}
		    ps.close();		    
		    ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('B', ?, ?)");
		    ps.setInt(1, book.getId());
		    ps.setString(2, "Added " + book.toString());
		    ps.executeUpdate();
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

	public void updateBook(Book book, Book oldBook) throws ParseException, SQLException {
		conn = ds.getConnection();
		PreparedStatement ps;
		try { 
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT `last_modified` FROM book WHERE `id` = " + book.getId());
		    rs.next();
			if(!book.getLastModified().toString().equals(rs.getTimestamp("last_modified").toLocalDateTime().toString())){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Update Error");
				alert.setContentText("Book not up to date. please try again.");
				alert.showAndWait(); 
				if(rs != null) 
					rs.close(); 
				if(conn != null) {
					conn.setAutoCommit(true);
					conn.close(); 
				}
				MasterController.getInstance().changeView(authorPackage.ViewType.Book_View, book);

		    }			
		    ps = conn.prepareStatement("UPDATE book SET title = ?, publisher = ?, date_published = ?, summary = ?, author_id = ? WHERE id = ?");
		    ps.setString(1,book.getTitle());
		    ps.setString(2,book.getPublisher());
		    ps.setDate(3,java.sql.Date.valueOf(book.getDatePublished()));
		    ps.setString(4,book.getSummary());
		    ps.setInt(5, book.getAuthor().getId());
		    ps.setInt(6, book.getId());
		    ps.executeUpdate();
		    stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT `last_modified` FROM book WHERE `id` = " + book.getId());
		    rs.next();
		    book.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
		    ps.close();
		    if(book.equals(oldBook)){
			    ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('B', ?, ?)");
			    ps.setInt(1, book.getId());
			    ps.setString(2, "Updated");
			    ps.executeUpdate();
		    }
		    if(!book.getTitle().equals(oldBook.getTitle())){
		    	 ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('B', ?, ?)");
				    ps.setInt(1, book.getId());
				    ps.setString(2, "Title changed from " + oldBook.getTitle() + " to " + book.getTitle());
				    ps.executeUpdate();
		    }
		    if(!book.getPublisher().equals(oldBook.getPublisher())){
		    	 ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('B', ?, ?)");
				    ps.setInt(1, book.getId());
				    ps.setString(2, "Publisher changed from " + oldBook.getPublisher() + " to " + book.getPublisher());
				    ps.executeUpdate();
		    }
		    if(!book.getSummary().equals(oldBook.getSummary())){
		    	 ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('B', ?, ?)");
				    ps.setInt(1, book.getId());
				    ps.setString(2, "Summary changed from " + oldBook.getSummary() + " to " + book.getSummary());
				    ps.executeUpdate();
		    }
		    if(!book.getAuthor().equals(oldBook.getAuthor())){
		    	 ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('B', ?, ?)");
				    ps.setInt(1, book.getId());
				    ps.setString(2, "Author changed from " + oldBook.getAuthor() + " to " + book.getAuthor());
				    ps.executeUpdate();
		    }
		    if(!book.getDatePublished().equals(oldBook.getDatePublished())){
		    	 ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('B', ?, ?)");
				    ps.setInt(1, book.getId());
				    ps.setString(2, "Date Published changed from " + oldBook.getDatePublished() + " to " + book.getDatePublished());
				    ps.executeUpdate();
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
	
	public List<auditTrailEntry> auditTrail(Book book) throws SQLException {
		List<auditTrailEntry> list = new ArrayList<auditTrailEntry>();
		conn = ds.getConnection(); 
		try { 
		    stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM `autdit_trail` WHERE `record_id` = "+book.getId()+" AND `record_type` = 'B' ORDER BY `date_added` ASC"); 
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
}
