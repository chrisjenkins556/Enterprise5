package libraryPackage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Main.MasterController;
import bookPackage.Book;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
/**
 * 
 * @author Chris Jenkins
 *
 */
@SuppressWarnings("unused")
public class LibraryController {
	
	private static Logger logger = LogManager.getLogger();
	private List<Library> libraries;
	@FXML private ListView<Library> listView;

	public LibraryController(List<Library> libraries) {
		this.libraries = libraries;
	}
	
	@FXML private void onMouseClick(MouseEvent action) throws IOException, SQLException, ParseException{
		if(action.getClickCount() == 2){
			Library library = listView.getSelectionModel().getSelectedItem();
			if(library == null){
				return;
			}
			Object source = action.getSource();
			if(source == listView){
				logger.info("clicked on " + library);
            	MasterController.getInstance().changeView(authorPackage.ViewType.Library_Detail_View, library); 
            	}
		}
	}
	
	public void initialize() throws SQLException{
		ObservableList<Library> items = listView.getItems();
		for(Library c : libraries) {
			items.add(c);
		}
		libraries.clear();		
	}
}
