package audit;

import java.util.Date;
/**
 * 
 * @author Chris Jenkins
 *
 */
public class auditTrailEntry {
	private String recordDescriptor;
	private Date dateAdded;
	private String message;
	
	public auditTrailEntry(String RD, Date date, String msg){
		this.recordDescriptor = RD;
		this.dateAdded = date;
		this.message = msg;		
	}
	public auditTrailEntry(){
		this.message = "";
		this.dateAdded = null;
		this.recordDescriptor = "";
	}

	@Override
	public String toString() {
		return "auditTrailEntry Date: " + dateAdded + "\n\tMessage: " + message;
	}
	public String getRecordDescriptor() {
		return recordDescriptor;
	}

	public void setRecordDescriptor(String recordDescriptor) {
		this.recordDescriptor = recordDescriptor;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
