package Main;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import authorPackage.Author;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import libraryPackage.Library;
import secondview.DetailController;
/**
 * 
 * @author Chris Jenkins
 *
 */
@SuppressWarnings("unused")
public class MenuController implements Initializable {
	private static Logger logger = LogManager.getLogger();
	@FXML private MenuItem AuthorListView;
	@FXML private MenuItem AddAuthor;
	@FXML private MenuItem Quit;
	@FXML private MenuItem BookView;
	@FXML private MenuItem newBookView;
	@FXML private MenuItem newLibraryView;
	@FXML private MenuItem LibraryListView;
	public MenuController(){
		
	}	
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
		@FXML private void handleMenuItem(ActionEvent action) throws IOException, SQLException, ParseException {
			Object source = action.getSource();			
			if(source == Quit){
				Platform.exit();
			}
			if(source == AuthorListView){
				MasterController.getInstance().changeView(authorPackage.ViewType.CAR_LIST, new Author());
				return;
			}
			if(source == AddAuthor){
				MasterController.getInstance().changeView(authorPackage.ViewType.CAR_DETAIL, new Author());
				return;
			}		
			if(source == BookView){
				MasterController.getInstance().changeView(authorPackage.ViewType.Book_View, new Author());
				return;
			}
			if(source == newBookView){
				MasterController.getInstance().changeView(authorPackage.ViewType.new_Book_View, new Author());
				return;
			}
			if(source == LibraryListView){
				MasterController.getInstance().changeView(authorPackage.ViewType.Library_List_View, new Author());
				return;
			}
			if(source == newLibraryView){
				MasterController.getInstance().changeView(authorPackage.ViewType.New_Library, new Library());
				return;
			}
		}
}

