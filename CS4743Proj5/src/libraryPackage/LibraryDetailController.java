package libraryPackage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.text.DocumentException;

import Main.MasterController;
import authorPackage.Author;
import authorPackage.AuthorTableGateway;
import bookPackage.Book;
import bookPackage.BookDetailController;
import bookPackage.BookTableGateway;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
/**
 * 
 * @author Chris Jenkins
 *
 */
@SuppressWarnings("unused")
public class LibraryDetailController {
	private static Logger logger = LogManager.getLogger();
	private Library library;
	private Library oldLibrary;
	private LibraryTableGateway LTD;
	private BookTableGateway BTD;
	public LibraryDetailController(Library data, LibraryTableGateway LTD) {
		this.library=data;
		this.LTD = LTD;
		this.libraryBooks = data.getBooks();
	}
	
	public LibraryDetailController() {
		this.Book = null;
		this.libraryBooks = new ArrayList<LibraryBook>();
	}
	
	public LibraryDetailController(Library checky, LibraryTableGateway libraryTableGateway, List<LibraryBook> libraryBooks2) {
		this.library=checky;
		this.LTD = libraryTableGateway;
		this.libraryBooks = libraryBooks2;
	}
	
	@FXML private Button DeleteBook;
	@FXML private Button Delete;
	@FXML private Button Save;
	@FXML private Button Audit;
	@FXML private Button InvReport;
	@FXML private Button AddBook;
	@FXML private ComboBox<Book> Books;
	private List<Book> Book;
	@FXML private TextField LibraryName;
	@FXML private TextField Quantity;
	private List<LibraryBook> libraryBooks;
	@FXML private ListView<LibraryBook> listView;
	
	@FXML private void onMouseClick(MouseEvent action) throws IOException, SQLException, ParseException{
		Object source = action.getSource();
		if(source == null){
			return;
		}
		if(source == listView){
			LibraryBook book = listView.getSelectionModel().getSelectedItem();
		}
	}
	
	@FXML private void onButtonPress(ActionEvent action) throws IOException, SQLException, ParseException{
		Object source = action.getSource();
		if(source == Save){
			try{
				if(library.getId() != 0){
					oldLibrary = new Library(library.getId(), library.getLibraryName(), library.getBooks(), library.getLastModified());
				}
				MasterController.getInstance().setCheck(0);
				if(LibraryName(LibraryName.getText()) && QuantityCheck(Quantity.getText())){
					library.setLibraryName(LibraryName.getText());
					library.setBooks(libraryBooks);
					try {
						if(library.getId() == 0){
							LTD.insertLibrary(library);
							MasterController.getInstance().changeView(authorPackage.ViewType.Library_List_View, library);
						}else{
							LTD.updateLibrary(library,oldLibrary);
							MasterController.getInstance().changeView(authorPackage.ViewType.Library_List_View, library);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					return;
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}else if(source == Delete){
			MasterController.getInstance().setCheck(0);
			if(library.getId() == 0){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Delete Error");
				alert.setContentText("New Libraries cannot be deleted");
				alert.showAndWait(); 
			}else{
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete Library?");
				alert.setContentText("Are you sure you want to delete " + library.toString());
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					LTD.deleteLibrary(library);
					MasterController.getInstance().changeView(authorPackage.ViewType.Library_List_View, library);
				} else {
				    logger.info("Deletion Cancled");
				}
			}
		}else if(source == Audit){
				MasterController.getInstance().changeView(authorPackage.ViewType.Library_Audit_Trail, library);
		}else if(source == AddBook){
			library.setLibraryName(LibraryName.getText());
			if(Books.getSelectionModel().getSelectedItem() == null){
				return;
			}
			LibraryBook checks = new LibraryBook(Books.getSelectionModel().getSelectedItem(),Integer.parseInt(Quantity.getText()), false);
			if(libraryBooks == null){
				libraryBooks.add(checks);
			}
			if(libraryBooks.contains(checks)){
				int newCheck = libraryBooks.indexOf(checks);
				libraryBooks.get(newCheck).setQuantity(Integer.parseInt(Quantity.getText()));
			}else{
				libraryBooks.add(checks);
			}
			//initialize();
			if(library.getId() != 0){
				LTD.updateLibrary(library, oldLibrary);
			}else{
				LTD.insertLibrary(library);
			}
			MasterController.getInstance().setCheck(0);
			logger.info(library.getLastModified());
			MasterController.getInstance().changeView(authorPackage.ViewType.Library_List_View, library);

		}else if(source == InvReport){
			pdfgenerator p = new pdfgenerator();
			try {
				p.createPDF(library);
			} catch (DocumentException e) {
			}
		}else if(source == DeleteBook){
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete Library?");
			alert.setContentText("Are you sure you want to delete " +listView.getSelectionModel().getSelectedItem());
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				LTD.deleteLibraryBook(library, listView.getSelectionModel().getSelectedItem());
				MasterController.getInstance().changeView(authorPackage.ViewType.Library_List_View, library);
			} else {
			    logger.info("Deletion Cancled");
			}
		}
	}
	
	private boolean QuantityCheck(String string) {
		int check = Integer.valueOf(string);
		if(check >=0 && check <=100){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Quantity Error");
			alert.setContentText("Quantity can not be more than 100 \n And can not be less than 0");
			alert.showAndWait(); 
			return false;
		}
	}

	private boolean LibraryName(String text) {
		if(!text.isEmpty() && text.length() <= 100){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Name Error");
			alert.setContentText("Name can not be more than 100 characters.\n And can not be blank");
			alert.showAndWait(); 
			return false;
		}
	}
	
	public void initialize()throws SQLException, ParseException{
		BTD = new BookTableGateway();
		LibraryName.setText(library.getLibraryName());
		Books.getItems().addAll(BTD.getBooks());
		Quantity.setText("0");
		oldLibrary = new Library(library.getId(),library.getLibraryName(),library.getBooks(),library.getLastModified());
		MasterController.getInstance().setLDC(this);
		ObservableList<LibraryBook> Items = listView.getItems();
			for(LibraryBook book: libraryBooks){
				Items.add(book);			
			}
	}

	public Library getOldLibrary() {
		return oldLibrary;
	}
	
	public void setOldLibrary(Library oldLibrary) {
		this.oldLibrary = oldLibrary;
	}
	
	public int check(Library data) throws ParseException {
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
		LibraryDetailController LDC = MasterController.getInstance().getLC();
		if(library.equals(data) && library.getId() > 0){
			return(0);
		}else{
			if(!LibraryName.getText().equals(library.getLibraryName())){
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


