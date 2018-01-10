package Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 
 * @author Chris Jenkins
 *
 */

public class Proj5Main extends Application{
	
	public Proj5Main(){
		
	}
	public static void main(String [] args){
		launch(args);
	}
	
	public static BorderPane rootPane;
	@Override
	public void start(Stage stage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
		MenuController controller = new MenuController();
		
		loader.setController(controller);
		Parent view = loader.load();
		MasterController.getInstance().setRootPane((BorderPane) view);
		Scene scene = new Scene(view);
		stage.setScene(scene);
		stage.setTitle("BOOKS?");
		stage.show();
	}
	
	@Override
	public void init() throws Exception {
		super.init();
				
		MasterController.getInstance();
	}
	
	@Override
	public void stop() throws Exception {
		super.stop();
		MasterController.getInstance().close();
	}
}