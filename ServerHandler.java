package server;

import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javafx.application.Platform; 

public class ServerHandler {
	public static final int PORT = 2002; 
	public static String KEEP_ALIVE_MESSAGE = "!keep-alive"; 
	public static int CLIENT_READ_TIMEOUT = 5*60*1000; 
	public static String host = null;
	public static String db = null;
	public static String username = null;
	public static String password = null;
	public static Vector<Client> usersOnline = new Vector<Client>();
	public static dbConnect dbConnection = null;
	public static MainController controller = null;
	public static Boolean isRunning = false;
	private static ServerSocket serverSocket; 
	 
	private static MessageDispatcher messageDispatcher; 
	 
	public static void setController(MainController controller){
		ServerHandler.controller = controller;
	}
	
	public static void initialize(String host, String db, String username, String password) { 
		ServerHandler.host = host;
		ServerHandler.db = db;
		ServerHandler.username = username;
		ServerHandler.password = password;

	    bindServerSocket(); 

	    messageDispatcher = new MessageDispatcher(controller); 
	    messageDispatcher.start(); 

	    handleClientConnections(); 
	 } 
	 
	 private static void bindServerSocket() { 
	    try { 
	    	serverSocket = new ServerSocket(PORT); 
	        System.out.println("Chat server is listening on port " + PORT); 
	    } catch (IOException e) { 
	    	System.err.println("Can not start listening on port " + PORT); 
	        e.printStackTrace(); 
	        System.exit(-1); 
	    } 
	 } 
	 
	 public static List<String> getUsers(){
		 List<String> users = new ArrayList<String>();
		 for(Client curClient : usersOnline){
			 users.add(curClient.clientName);
		 }
		 return users;
	 }
	 
	 public static void kick(String user){
		 for(Client client: usersOnline){
			 if(client.clientName.equalsIgnoreCase(user)){
				try {
					int clientIndex = usersOnline.indexOf(client);
					if(clientIndex != -1)
						usersOnline.removeElementAt(clientIndex);
					
					client.disconnect();
					client = null;
					controller.update();
				} catch (IOException e) {
					System.err.println("Unable to kick user.");
					e.printStackTrace();
				}
			 }
		 }
	 }
	 
	 private static void handleClientConnections() { 
		 dbConnection = new dbConnect();
		 dbConnection.dbConnect(host, username, password, db);
		 isRunning = true;
		 ServerHandler.isRunning = true;
		 while (true) { 
			 try { 
				 Socket socket = serverSocket.accept(); 
				 System.out.println("User connected to the chat!");
				 Client client = new Client(); 
				 controller.update();
				 usersOnline.add(client);
				 client.cSocket = socket; 
				 ClientReceiver ClientReceiver = new ClientReceiver(client, messageDispatcher); 
				 ClientSender clientSender = new ClientSender(client, messageDispatcher); 
	             client.mClientReceiver = ClientReceiver; 
	             ClientReceiver.start(); 
	             client.mClientSender = clientSender; 
	             clientSender.start(); 
	             messageDispatcher.addClient(client); 
	         } catch (IOException e) { 
	        	 e.printStackTrace(); 
	         } 
	     } 
	}

} 
