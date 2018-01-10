package authorPackage;

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
import org.apache.logging.log4j.core.util.Throwables;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import Main.MasterController;
import audit.auditTrailEntry;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
/**
 * 
 * @author Chris Jenkins
 *
 */
@SuppressWarnings("unused")
public class AuthorTableGateway {
	private static Logger logger = LogManager.getLogger();
	private MysqlDataSource ds = null;
	Statement stmt = null; 
	ResultSet rs = null;
	Connection conn = null;
	List<Author> listView = new ArrayList<Author>();

	
	public AuthorTableGateway() throws SQLException{
		try{
			this.ds = new MysqlDataSource(); 
			ds.setURL("jdbc:mysql://easel2.fulgentcorp.com:3306/rmr581"); 
			ds.setUser("rmr581");
			ds.setPassword("54ZjfznJLWACUBzyYWyz");
		}catch (Exception e){
			logger.error(e);
		}		
	}
	
	public List<Author> getAuthors() throws SQLException{
		conn = ds.getConnection(); 
		try { 
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Authors"); 
			while(rs.next()) {
				listView.add(new Author(rs.getString("first_name"),rs.getString("last_name"),
						rs.getString("gender"),rs.getString("web_site"),rs.getDate("dob"),rs.getInt("id"), rs.getTimestamp("last_modified").toLocalDateTime()));		
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
		return listView;		
	}

	public void updateAuthor(Author author, Author oldAuthor) throws SQLException, ParseException {
		conn = ds.getConnection();
		PreparedStatement ps;
		try { 
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT `last_modified` FROM Authors WHERE `id` = " + author.getId());
		    rs.next();
			if(!author.getLastModified().toString().equals(rs.getTimestamp("last_modified").toLocalDateTime().toString())){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Update Error");
				alert.setContentText("Author not up to date. please try again.");
				alert.showAndWait(); 
				if(rs != null) 
					rs.close(); 
				if(conn != null) {
					conn.setAutoCommit(true);
					conn.close(); 
				}
				MasterController.getInstance().changeView(authorPackage.ViewType.CAR_LIST, author);
		    }			
		    ps = conn.prepareStatement("UPDATE Authors SET first_name = ?, last_name = ?, dob = ?, gender = ?, web_site = ? WHERE id = ?");
		    ps.setString(1,author.getFirstName());
		    ps.setString(2,author.getLastName());
		    ps.setDate(3,java.sql.Date.valueOf(author.getDob()));
		    ps.setString(4,author.getGender());
		    ps.setString(5, author.getWeb());
		    ps.setInt(6, author.getId());
		    ps.executeUpdate();
		    stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT `last_modified` FROM Authors WHERE `id` = " + author.getId());
		    rs.next();
		    author.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
		    ps.close();
		    if(author.equals(oldAuthor)){
			    ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
			    ps.setInt(1, author.getId());
			    ps.setString(2, "Updated");
			    ps.executeUpdate();
		    }
		    if(!author.getFirstName().equals(oldAuthor.getFirstName())){
		    	 ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				    ps.setInt(1, author.getId());
				    ps.setString(2, "First Name changed from " + oldAuthor.getFirstName() + " to " + author.getFirstName());
				    ps.executeUpdate();
		    }
		    if(!author.getLastName().equals(oldAuthor.getLastName())){
		    	 ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				    ps.setInt(1, author.getId());
				    ps.setString(2, "Last Name changed from " + oldAuthor.getLastName() + " to " + author.getLastName());
				    ps.executeUpdate();
		    }
		    if(!author.getGender().equals(oldAuthor.getGender())){
		    	 ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				    ps.setInt(1, author.getId());
				    ps.setString(2, "Gender changed from " + oldAuthor.getGender() + " to " + author.getGender());
				    ps.executeUpdate();
		    }
		    if(!author.getWeb().equals(oldAuthor.getWeb())){
		    	 ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				    ps.setInt(1, author.getId());
				    ps.setString(2, "Web address changed from " + oldAuthor.getWeb() + " to " + author.getWeb());
				    ps.executeUpdate();
		    }
		    if(!author.getDob().equals(oldAuthor.getDob())){
		    	 ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				    ps.setInt(1, author.getId());
				    ps.setString(2, "DOB changed from " + oldAuthor.getDob() + " to " + author.getDob());
				    ps.executeUpdate();
		    }		    
		    conn.commit();
		} catch(SQLException e) { 
			logger.error("Failed updating database" + e);
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

	public void insertAuthor(Author author) throws SQLException, ParseException{
	
		conn = ds.getConnection(); 
		ResultSet rs = null;
		try { 
			conn.setAutoCommit(false);
		    PreparedStatement ps = conn.prepareStatement("INSERT INTO `Authors`( `first_name`, `last_name`, `dob`, `gender`, `web_site`) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
		    ps.setString(1,author.getFirstName());
		    ps.setString(2,author.getLastName());
		    ps.setDate(3,java.sql.Date.valueOf(author.getDob()));
		    ps.setString(4,author.getGender());
		    ps.setString(5, author.getWeb());
		    ps.executeUpdate();
		    rs = ps.getGeneratedKeys();		    
		    if(rs != null && rs.next()) {
		    	author.setId(rs.getInt(1));
			}
		    ps.close();		    
		    ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
		    ps.setInt(1, author.getId());
		    ps.setString(2, "Added " + author.toString());
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

	public void deleteAuthor(Author author) throws SQLException, ParseException{		
		conn = ds.getConnection(); 
		try { 
			conn.setAutoCommit(false);
			PreparedStatement ps;
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * from book WHERE author_id = " + author.getId()); 
			while(rs.next()) {
				ps = conn.prepareStatement("DELETE from `autdit_trail` WHERE record_id = ?");
				ps.setInt(1, rs.getInt("id"));
				ps.executeUpdate();				
				ps.close();
			}			
			ps = conn.prepareStatement("DELETE from book WHERE author_id = ?");
			ps.setInt(1, author.getId());
			ps.executeUpdate();
			ps.close();			
			ps = conn.prepareStatement("DELETE from `autdit_trail` WHERE record_id = ? AND record_type = 'A'");
			ps.setInt(1, author.getId());
			ps.executeUpdate();			
			ps.close();			
			ps = conn.prepareStatement("DELETE from `Authors` WHERE id = ?");
			ps.setInt(1, author.getId());
			ps.executeUpdate();			
			conn.commit();
		} catch(SQLException e) { 
			logger.error("Failed to Delete entry in database: \n" +e.getMessage());
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

	public void close() throws SQLException {
		if(rs != null) 
			rs.close(); 
		if(stmt != null)  
			stmt.close(); 
		if(conn != null) {
			conn.close(); 
			logger.info("closed");
		}		
	}

	public List<auditTrailEntry> auditTrail(Author author) throws SQLException {
		List<auditTrailEntry> list = new ArrayList<auditTrailEntry>();
		conn = ds.getConnection(); 

		try { 
		    stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM `autdit_trail` WHERE `record_id` = "+author.getId()+" AND `record_type` = 'A' ORDER BY `date_added` ASC"); 
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
}
