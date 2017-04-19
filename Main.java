package server;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.show();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
			loader.load();
			MainController controller = loader.getController();
			controller.setStage(primaryStage);

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
