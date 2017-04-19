package server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sun.javafx.scene.paint.GradientUtils.Point;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainController {	
	//Member variables
	private static Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private guiUpdater updaterThread = new guiUpdater(this);
	// End of member variables
	
	@FXML BorderPane windows = new BorderPane();
	@FXML ListView<String> usersOnline = new ListView();
	@FXML Pane titleBar = new Pane();
	@FXML Button startBtn = new Button();
	@FXML TextArea chatBox = new TextArea();
	
	public synchronized void setUsersOnline(List<String> users) throws InterruptedException{
		Platform.runLater(new Runnable() {
			public void run(){
				System.out.println("setUsersOnline: setUsersOnline()");
				usersOnline.getItems().clear();
				usersOnline.getItems().addAll(users.toArray(new String[users.size()]));
				usersOnline.setCellFactory(lv -> {

		        ListCell<String> cell = new ListCell<>();
		        ContextMenu contextMenu = new ContextMenu();
		        MenuItem kickUser = new MenuItem();
		        kickUser.textProperty().bind(Bindings.format("Kick"));
		        kickUser.setOnAction(event -> {
		        	String user = cell.getItem();
		                ServerHandler.kick(user);
		            });		         
		        contextMenu.getItems().addAll(kickUser);

		        cell.textProperty().bind(cell.itemProperty());
		        cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
			        if (isNowEmpty) {
			        	cell.setContextMenu(null);
			        } else {
			        	cell.setContextMenu(contextMenu);
			        }
		        });
		            return cell ;
		        });
			}
		});
		System.out.println("-----------Updater thread is waiting!-------");
		wait();
		System.out.println("-----------Updater thread is notified!-------");
	}
	
	public void printChat(String msg) throws InterruptedException{
		Platform.runLater(new Runnable() {
			public void run(){
				System.out.println("printchat: printChat()");
				chatBox.appendText(msg + "\n");
			}
		});
	}
	
	@FXML
	public void serverStart(ActionEvent event) throws InterruptedException {
		MainController controller = this;
		startBtn.setDisable(true);
		Thread serverThread = new Thread (new Runnable() {
			@Override
			public void run() {
				ServerHandler.setController(controller);
				ServerHandler.initialize("localhost", "server", "root", "");
			}
		});
		serverThread.start();
		while(!ServerHandler.isRunning){
			Thread.sleep(500);
			System.out.println("Waiting to load... ");
		}
		updaterThread.start();
	}
	
	public synchronized void update(){
		notify();
	}
	
	@FXML
	public void guiUpdate(ActionEvent event) {
		if(!updaterThread.isAlive() || updaterThread.getState() != Thread.State.WAITING)
		{
			System.out.println("Current updater thread is not waiting! State: " + updaterThread.getState());
			return;
		}
		System.out.println("Performing gui update... ");
		usersOnline.getItems().clear();
		update();
	}
	
	public static void setStage(Stage stg){
		stage = stg;
	}

	
	@FXML
	public void mouseDragStart(MouseEvent mEvent){
		xOffset = mEvent.getSceneX();
		yOffset = mEvent.getSceneY();
	}
	
	@FXML
	public void windowDrag(MouseEvent wEvent){
		stage.setX(wEvent.getScreenX() - xOffset);
        stage.setY(wEvent.getScreenY() - yOffset);
	}
	
	@FXML
	public void windowClose(MouseEvent mEvent){
		System.out.println("Exiting the program!");
        Platform.exit();
        System.exit(0);
	}
	
	@FXML
	public void windowMaximize(MouseEvent mEvent){
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		stage.setX(bounds.getMinX());
		stage.setY(bounds.getMinY());
		stage.setWidth(bounds.getWidth());
		stage.setHeight(bounds.getHeight());
		System.err.println("Currently there is no maximize function!");
	}
}
