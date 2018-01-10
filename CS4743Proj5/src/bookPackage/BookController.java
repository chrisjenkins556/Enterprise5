package bookPackage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Main.MasterController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
/**
 * 
 * @author Chris Jenkins
 *
 */
public class BookController {
	private static Logger logger = LogManager.getLogger();
	private List<Book> books;
	@FXML private ListView<Book> listView;

	public BookController(List<Book> books) {
		this.books = books;
	}
	
	@FXML private void onMouseClick(MouseEvent action) throws IOException, SQLException, ParseException{
		if(action.getClickCount() == 2){
			Book book = listView.getSelectionModel().getSelectedItem();
			if(book == null){
				return;
			}
			Object source = action.getSource();
			if(source == listView){
				logger.info("clicked on " + book);
            	MasterController.getInstance().changeView(authorPackage.ViewType.Book_Detail, book);           	
			}
		}
	}
	
	public void initialize() throws SQLException{
		ObservableList<Book> items = listView.getItems();
		for(Book c : books) {
			items.add(c);
		}
		books.clear();		
	}
}
