package mainview;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Main.MasterController;
import authorPackage.Author;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
/**
 * 
 * @author Chris Jenkins
 *
 */
public class ViewDetailController {
	private static Logger logger = LogManager.getLogger();
	private List<Author> authors;
	@FXML private ListView<Author> listView;
	public ViewDetailController(List<Author> authors){
		this.authors = authors;
	}
	@FXML private void onMouseClick(MouseEvent action) throws IOException, SQLException, ParseException{
		if(action.getClickCount() == 2){
			Author author = listView.getSelectionModel().getSelectedItem();
			Object source = action.getSource();
			if(author == null){
				return;
			}
			if(source == listView){
				logger.info("clicked on " + author);
            	MasterController.getInstance().changeView(authorPackage.ViewType.CAR_DETAIL, author);                	
			}
		}
	}
	
	
	public void initialize() throws SQLException{
		ObservableList<Author> items = listView.getItems();
		for(Author c : authors) {
			items.add(c);
		}
		authors.clear();
		
	}
}
