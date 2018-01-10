package authorPackage;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import audit.auditTrailEntry;

/**
 * 
 * @author Chris Jenkins
 *
 */

public class Author {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger();
	private int id;
	private String firstName;
	private String lastName;
	private String gender;
	private LocalDate dob;
	private String web ;
	private LocalDateTime lastModified;
	
	public Author() {
		this.firstName = "";
		this.lastName = "";
		this.gender = "";
		this.dob = null;
		this.web = "";
		this.id = 0;
		this.lastModified = null;
	}
	
	public Author(String first, String last, String gender, String web, Date dob, int id, LocalDateTime lastModified) {
		this.firstName = first;
		this.lastName = last;
		this.gender = gender;
		this.web = web;
		if(dob == null){
			this.dob = null;
		}else{
			this.dob= new Date(dob.getTime()).toLocalDate();
		}
		this.id = id;
		this.lastModified = lastModified;
	}

	public Author(String firstName2, String lastName2, String gender2, String web2, LocalDate dob2, int id2, LocalDateTime lastModified2) {
		this.firstName = firstName2;
		this.lastName = lastName2;
		this.gender = gender2;
		this.web = web2;
		this.dob= dob2;
		this.id = id2;
		this.lastModified = lastModified2;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return firstName + " " + lastName;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LocalDate getDob(){
		return dob;
	}
	
	public Date getDateob(){
		if(dob == null){
			return null;
		}
		Date date = java.sql.Date.valueOf(dob);
		return date;
	}

	public void setDob(String dob) throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		formatter = formatter.withLocale( Locale.US );  
		LocalDate date = LocalDate.parse(dob, formatter);
		this.dob = date;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getWeb() {
		return web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public List<auditTrailEntry> getAuditTrail(Author author, AuthorTableGateway authorGateway) throws SQLException {
		return authorGateway.auditTrail(author);
	}

}