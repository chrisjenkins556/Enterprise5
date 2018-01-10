package bookPackage;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Main.MasterController;
import authorPackage.Author;
import authorPackage.AuthorTableGateway;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import mainview.ViewDetailController;
import secondview.DetailController;
/**
 * 
 * @author Chris Jenkins
 *
 */
@SuppressWarnings("unused")
public class BookDetailController {
	private static Logger logger = LogManager.getLogger();
	private BookTableGateway DB;
	private AuthorTableGateway adb;
	private Book book;
	private Book oldBook;
	public Book getOldBook() {
		return oldBook;
	}
	
	public void setOldBook(Book oldBook) {
		this.oldBook = oldBook;
	}
	
	private Author author;
	@FXML private Button Delete;
	@FXML private Button Save;
	@FXML private Button Audit;
	@FXML private TextField Title;
	@FXML private TextField Summary;
	@FXML private TextField Publisher;
	@FXML private TextField date;
	@FXML private ComboBox<Author> Author;
	private List<authorPackage.Author> authors;
	
	public BookDetailController(){		
	}
	
	public BookDetailController(Book book, List<Author> authors, BookTableGateway DB){
		this.authors = authors;
		this.DB = DB;
		this.book = book;
	}

	@FXML private void onButtonPress(ActionEvent action) throws IOException, SQLException, ParseException{
		Object source = action.getSource();
		adb = new AuthorTableGateway();
		if(source == Save){
			try{
				if(book.getId() != 0){
					oldBook = new Book(book.getId(), book.getTitle(), book.getPublisher(), book.getDatePublished(), book.getSummary(), book.getAuthor(), book.getLastModified());
				}
				MasterController.getInstance().setCheck(0);
				if(titleValid(Title.getText()) && publisherValid(Publisher.getText()) && summaryValid(Summary.getText()) && (author = authorValid(Author.getSelectionModel().getSelectedItem())) != null && dateValid(date.getText())){
					book.setTitle(Title.getText());
					book.setDatePublished(date.getText());
					book.setPublisher(Publisher.getText());
					book.setSummary(Summary.getText());
					book.setAuthor(author);
					try {
						if(book.getId() == 0){
							DB.insertBook(book);
							MasterController.getInstance().changeView(authorPackage.ViewType.Book_View, book);
						}else{
							DB.updateBook(book,oldBook);
							MasterController.getInstance().changeView(authorPackage.ViewType.Book_View, book);
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}else{
					return;
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}else if(source == Delete){
			MasterController.getInstance().setCheck(0);
			if(book.getId() == 0){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Delete Error");
				alert.setContentText("New Books cannot be deleted");
				alert.showAndWait(); 
			}else{
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete Book?");
				alert.setContentText("Are you sure you want to delete " + book.toString());
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					DB.deleteBook(book);
					MasterController.getInstance().changeView(authorPackage.ViewType.Book_View, book);
				} else {
				    logger.info("Deletion Cancled");
				}
			}
		}else if(source == Audit){
			if(book.getId() == 0){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Audit Error");
				alert.setContentText("New Books do not have an Audit trail");
				alert.showAndWait(); 
			}else{
				MasterController.getInstance().changeView(authorPackage.ViewType.book_AUDIT_TRAIL, book);
			}
		}		
	}

	private boolean dateValid(String text) {
		if(text.matches("[0-2][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]") ){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Date Error");
			alert.setContentText("Please enter a valid date\n form of yyyy-MM-dd");
			alert.showAndWait(); 
			return false;
		}
	}
	private authorPackage.Author authorValid(authorPackage.Author author2) throws SQLException {
		Author rauthor = author2;
		if(rauthor == null){
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Author Error");
			alert.setContentText("Please choose an Author");
			alert.showAndWait(); 
			return null;		
			}else{
			return rauthor;
		}
	}
	private boolean summaryValid(String text) {
		if(!text.isEmpty() && text.length() <= 100){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Summary Error");
			alert.setContentText("Summary can not be more than 100 characters.\n And can not be blank");
			alert.showAndWait(); 
			return false;
		}
	}
	private boolean publisherValid(String text) {
		if(text == null){
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Publisher Error");
			alert.setContentText("Publisher must not be empty.");
			alert.showAndWait(); 
			return false;
		}
		if(!text.isEmpty() && text.length()<=100 ){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Publisher Error");
			alert.setContentText("Publisher can not be more than 100 characters.");
			alert.showAndWait(); 
			return false;
		}	
	}
	private boolean titleValid(String text) {
		if(text.length()<=100 && !text.isEmpty()){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Title Error");
			alert.setContentText("Title must not be empty \n and can not be more than 100 characters.");
			alert.showAndWait(); 
			return false;
		}	
	}
	public void initialize()throws SQLException, ParseException{
		Title.setText(book.getTitle());
		Author.getItems().addAll(authors);
		Author.getSelectionModel().select(book.getAuthor());
		Summary.setText(book.getSummary());
		Publisher.setText(book.getPublisher());
		date.setText(book.getDatePublished());
		oldBook = new Book(book.getId(),book.getTitle(),book.getPublisher(),book.getDatePublished(),book.getSummary(),book.getAuthor(),book.getLastModified());
		authors.clear();
		MasterController.getInstance().setBDC(this);
	}
	
	public int check(Book data) throws ParseException {
		if(data.getId() == 0){
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Save Data?");
			alert.setContentText("yes = save, no = don't save, cancle = stay");

			ButtonType buttonTypeOne = new ButtonType("yes");
			ButtonType buttonTypeTwo = new ButtonType("no");
			ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

			alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo,  buttonTypeCancel);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeOne){
				MasterController.getInstance().setCheck(0);
				Save.fire();
				return 1;

			} else if (result.get() == buttonTypeTwo) {
			    return 0;
			} else {
				return 1;
			}
		}
		BookDetailController BDC = MasterController.getInstance().getBDC();
		if(book.equals(data) && book.getId() > 0){
			return(0);
		}else{
			if(!Title.getText().equals(book.getTitle()) || !Author.getSelectionModel().getSelectedItem().toString().equals(book.getAuthor().toString()) || !Summary.getText().equals(book.getSummary()) || !Publisher.getText().equals(book.getPublisher()) || !date.getText().equals(book.getDatePublished())){
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Save Data?");
				alert.setContentText("yes = save, no = don't save, cancle = stay");
	
				ButtonType buttonTypeOne = new ButtonType("yes");
				ButtonType buttonTypeTwo = new ButtonType("no");
				ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	
				alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo,  buttonTypeCancel);
	
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == buttonTypeOne){
					MasterController.getInstance().setCheck(0);
					Save.fire();
					return 0;
	
				} else if (result.get() == buttonTypeTwo) {
				    return 0;
				} else {
					return 1;
				}
			}
		}
		return 0;
	}
}
