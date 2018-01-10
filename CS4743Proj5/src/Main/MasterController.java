package Main;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import audit.AuditTrailController;
import audit.auditTrailEntry;
import authorPackage.Author;
import authorPackage.AuthorTableGateway;
import authorPackage.ViewType;
import bookPackage.Book;
import bookPackage.BookController;
import bookPackage.BookDetailController;
import bookPackage.BookTableGateway;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import libraryPackage.Library;
import libraryPackage.LibraryController;
import libraryPackage.LibraryDetailController;
import libraryPackage.LibraryTableGateway;
import mainview.ViewDetailController;
import secondview.DetailController;
/**
 * 
 * @author Chris Jenkins
 *
 */

@SuppressWarnings("unused")
public class MasterController {
	private static Logger logger = LogManager.getLogger();
	private static MasterController instance = null;
	private BorderPane rootPane;
	private Author author;
	private DetailController DC = new DetailController();
	private BookDetailController BDC = new BookDetailController();
	private LibraryDetailController LC = new LibraryDetailController();
	public BookDetailController getBDC() {
		return BDC;
	}

	public void setBDC(BookDetailController bDC) {
		BDC = bDC;
	}

	private AuthorTableGateway DB;	
	private BookTableGateway BDB;
	private LibraryTableGateway LDB;
	private List<Author> authors;
	private int check =0;
	public int getCheck() {
		return check;
	}

	public void setCheck(int check) {
		this.check = check;
	}

	private int complicated = 0;
	
	private MasterController() {
		try {
			DB = new AuthorTableGateway();
			BDB = new BookTableGateway();	
			LDB = new LibraryTableGateway();
		} catch (Exception e) {
			logger.error(e);
			Platform.exit();
		}
	}
		
	public boolean changeView(ViewType vType, Object data) throws SQLException, ParseException {
		DetailController Dc = MasterController.getInstance().getDC();
		BookDetailController BDC = MasterController.getInstance().getBDC();		
		if(check == 1){
			Author author = DC.getOldAuthor();
			complicated = DC.check(author);
			if(complicated == 1){
				return false;
			}
		}
		if(check == 2){
			Book book = BDC.getOldBook();
			complicated = BDC.check(book);
			if(complicated == 1){
				return false;
			}
		}
		if(check == 3){
			Library library = LC.getOldLibrary();
			complicated = LC.check(library);
			if(complicated == 1){
				return false;
			}
		}
		FXMLLoader loader = null;
		if(vType == ViewType.CAR_LIST) {
			check = 0;			
			loader = new FXMLLoader(getClass().getResource("/mainview/firstView.fxml"));
			loader.setController(new ViewDetailController(DB.getAuthors()));
			
		} else if(vType == ViewType.CAR_DETAIL) {
			check =1;
			loader = new FXMLLoader(getClass().getResource("/secondview/lastView.fxml"));
			loader.setController(new DetailController((Author) data, DB));
		
		} else if(vType == ViewType.book_AUDIT_TRAIL) {
			check = 0;
			Book book = (Book) data;
			logger.info(book.getId());
			List<auditTrailEntry> trails = BDB.auditTrail(book);
			loader = new FXMLLoader(getClass().getResource("/audit/audittrailview.fxml"));
			loader.setController(new AuditTrailController(book, trails));
		}else if(vType == ViewType.AUDIT_TRAIL) {
			check = 0;
			Author author = (Author) data;
			List<auditTrailEntry> trails = DB.auditTrail(author);
			loader = new FXMLLoader(getClass().getResource("/audit/audittrailview.fxml"));
			loader.setController(new AuditTrailController(author, trails));
		} else if(vType == ViewType.Book_View) {
			check = 0;
			List<Book> books = BDB.getBooks();
			loader = new FXMLLoader(getClass().getResource("/bookPackage/bookview.fxml"));
			loader.setController(new BookController(books));
		} else if(vType == ViewType.Book_Detail) {
			check = 2;
			loader = new FXMLLoader(getClass().getResource("/bookPackage/bookdetailview.fxml"));
			loader.setController(new BookDetailController((Book) data, DB.getAuthors(), new BookTableGateway()));
		} else if(vType == ViewType.new_Book_View) {
			check = 2;
			loader = new FXMLLoader(getClass().getResource("/bookPackage/bookdetailview.fxml"));
			loader.setController(new BookDetailController( new Book(), DB.getAuthors(), new BookTableGateway()));
		}else if(vType == ViewType.Library_List_View) {
			check = 0;
			loader = new FXMLLoader(getClass().getResource("/libraryPackage/LibraryListView.fxml"));
			loader.setController(new LibraryController( LDB.getLibrarys()));
		}else if(vType == ViewType.Library_Detail_View) {
			check = 3;
			Library checky = (Library) data;
			loader = new FXMLLoader(getClass().getResource("/libraryPackage/LibraryDetailView.fxml"));
			loader.setController(new LibraryDetailController(checky, new LibraryTableGateway()));
		}else if(vType == ViewType.Library_Audit_Trail) {
			check = 0;
			Library library = (Library) data;
			List<auditTrailEntry> trails = LDB.auditTrail(library);
			loader = new FXMLLoader(getClass().getResource("/audit/audittrailview.fxml"));
			loader.setController(new AuditTrailController(library, trails));
		}else if(vType == ViewType.New_Library) {
			check = 3;
			loader = new FXMLLoader(getClass().getResource("/libraryPackage/LibraryDetailView.fxml"));
			loader.setController(new LibraryDetailController( new Library(),new LibraryTableGateway()));
		}		
		Parent view = null;
		try {
			view = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		rootPane.setCenter(view);	
		return true;
	}

	public void close() throws SQLException {
		DB.close();
	}
	
	public static MasterController getInstance() {
		if(instance == null)
			instance = new MasterController();
		return instance;
	}
	
	public BorderPane getRootPane() {
		return rootPane;
	}

	public void setRootPane(BorderPane rootPane) {
		this.rootPane = rootPane;
	}

	public AuthorTableGateway getAuthorGateway() {
		return DB;
	}

	public void setAuthorGateway(AuthorTableGateway carGateway) {
		this.DB = carGateway;
	}

	public void setDC(DetailController detailController) {
		DC = detailController;		
	}

	public DetailController getDC() {
		return DC;
	}

	public void setLDC(LibraryDetailController libraryDetailController) {
		LC = libraryDetailController;
	}
	
	public LibraryDetailController getLC(){
		return LC;
	}


}
