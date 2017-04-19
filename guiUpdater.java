package server;

import javafx.scene.control.ListView;

public class guiUpdater extends Thread{
	private MainController controller;
	
	public guiUpdater(MainController controller){
		this.controller = controller;
	}
	

	public void run() {
		while(true){
			System.out.println("["+this.getName()+"] --- guiUpdater is running ---");
			try {
				controller.setUsersOnline(ServerHandler.getUsers());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
