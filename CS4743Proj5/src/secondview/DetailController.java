package secondview;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
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
import javafx.scene.control.TextField;
/**
 * 
 * @author Chris Jenkins
 *
 */
@SuppressWarnings("unused")
public class DetailController {
	private static Logger logger = LogManager.getLogger();
	private AuthorTableGateway DB;
	private Author author = new Author();
	private Author oldAuthor = new Author();

	public Author getOldAuthor() {
		return oldAuthor;
	}
	public void setOldAuthor(Author oldAuthor) {
		this.oldAuthor = oldAuthor;
	}

	@FXML private Button Save;
	@FXML private Button Delete;
	@FXML private Button Audit;
	@FXML private TextField firstName;
	@FXML private TextField lastName;
	@FXML private TextField gender;
	@FXML private TextField date;
	@FXML private TextField web;

	public DetailController(){
		
	}
	public DetailController(Author author, AuthorTableGateway DB){
		this.DB = DB;
		this.author = author;
	}

	@FXML private void onButtonPress(ActionEvent action) throws IOException, SQLException, ParseException{
		Object source = action.getSource();
		if(source == Save){
			try{
				if(dateValid(date.getText()) && genderValid(gender.getText()) && firstValid(firstName.getText()) && lastValid(lastName.getText()) && webValid(web.getText())){
					if(author.getId() != 0){
						oldAuthor = new Author(author.getFirstName(), author.getLastName(), author.getGender(), author.getWeb(), author.getDateob(), author.getId(), author.getLastModified());
					}
					MasterController.getInstance().setCheck(0);
					author.setFirstName(firstName.getText());
					author.setLastName(lastName.getText());
					author.setGender(gender.getText());
					author.setWeb(web.getText());
					author.setDob(date.getText());
					try {
						if(author.getId() == 0){
							DB.insertAuthor(author);
							MasterController.getInstance().changeView(authorPackage.ViewType.CAR_LIST, author);
						}else{
							DB.updateAuthor(author, this.getOldAuthor());
							MasterController.getInstance().changeView(authorPackage.ViewType.CAR_LIST, author);
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
			if(author.getId() == 0){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Delete Error");
				alert.setContentText("New Authors cannot be deleted");
				alert.showAndWait(); 
			}else{
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Delete Author");
				alert.setContentText("Are you sure you want to delete " + author.toString());
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){
					DB.deleteAuthor(author);
					MasterController.getInstance().changeView(authorPackage.ViewType.CAR_LIST, author);
				} else {
				    logger.info("Deletion Cancled");
				}
			}
		}
		else if(source == Audit){
			MasterController.getInstance().setCheck(0);
			if(author.getId() == 0){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Audit Error");
				alert.setContentText("New Authors do not have an Audit trail");
				alert.showAndWait(); 
			}else{
				MasterController.getInstance().changeView(authorPackage.ViewType.AUDIT_TRAIL, author);
			}
		}
	}
	public boolean dateValid(String promptText) {
		if(promptText.matches("[0-2][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]") ){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Date Error");
			alert.setContentText("Please enter a valid date in the form of yyyy-MM-dd");
			alert.showAndWait(); 
			return false;
		}
	}

	public boolean genderValid(String promptText) {
		if(promptText.toUpperCase().equals("M") || promptText.toUpperCase().equals("F") || promptText.toUpperCase().equals("U")){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Gender Error");
			alert.setContentText("Gender must be either 'm' 'f' or 'u'");
			alert.showAndWait(); 
			return false;
		}
	}

	public boolean webValid(String promptText) {
		if(promptText.length()<=100){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Web Address Error");
			alert.setContentText("Web address can not be more than 100 characters.");
			alert.showAndWait(); 
			return false;
		}
	}

	public boolean lastValid(String promptText) {
		if(promptText.length()<=100 && !promptText.isEmpty()){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Last Name Error");
			alert.setContentText("Last Name must not be empty and can not be more than 100 characters.");
			alert.showAndWait(); 
			return false;
		}	
	}

	public boolean firstValid(String promptText) {
		if(promptText.length()<=100 && !promptText.isEmpty()){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("First Name Error");
			alert.setContentText("First Name must not be empty and can not be more than 100 characters.");
			alert.showAndWait(); 
			return false;
		}
	}

	public void initialize()throws SQLException, ParseException{
			String formattedString = null;
			if(author.getDob() != null){
				formattedString = author.getDob().toString();
			}else{
				formattedString = "";
			}
			firstName.setText(author.getFirstName());
			lastName.setText(author.getLastName());
			gender.setText(author.getGender());
			date.setText(formattedString);
			web.setText(author.getWeb());
			oldAuthor = new Author(author.getFirstName(), author.getLastName(), author.getGender(), author.getWeb(), author.getDateob(), author.getId(), author.getLastModified());
			MasterController.getInstance().setDC(this);

			
	}

	public int check(Author data) throws ParseException {
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
				return 0;

			} else if (result.get() == buttonTypeTwo) {
			    return 0;
			} else {
				return 1;
			}
		}

		DetailController Dc = MasterController.getInstance().getDC();
		if(author.equals(data) && author.getId() > 0){
			return(0);
		}else{
			if(!date.getText().equals(author.getDateob().toString()) ||!firstName.getText().equals(author.getFirstName()) || !lastName.getText().equals(author.getLastName()) || !gender.getText().equals(author.getGender()) || !web.getText().equals(author.getWeb())){
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
		}
		return 0;
	}
}
