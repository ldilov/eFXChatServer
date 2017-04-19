package server;

import java.net.Socket;
import java.util.Vector;

public class MessageDispatcher extends Thread {
	private Vector mMessageQueue = new Vector(); 
    private Vector mClients = new Vector(); 
    public MainController controller = null;
    
    public MessageDispatcher(MainController controller) {
    	this.controller = controller;
    }
    
    public synchronized void addClient(Client aClient) { 
        mClients.add(aClient); 
    } 
 
    public synchronized void deleteClient(Client aClient) { 
        int clientIndex = mClients.indexOf(aClient); 
        if (clientIndex != -1) 
            mClients.removeElementAt(clientIndex); 
    } 
 
    public synchronized void dispatchMessage(Client client, String message) { 
        Socket socket = client.cSocket; 
        String senderIP = client.getIP(); 
        String senderName = client.clientName;
        message = senderIP + " @ <" + senderName + ">" + " : " + message; 
        mMessageQueue.add(message); 
        notify(); 
    } 
 
    private synchronized String getNextMessageFromQueue() throws InterruptedException { 
        while (mMessageQueue.size() == 0) 
            wait(); 
        String message = (String) mMessageQueue.get(0); 
        mMessageQueue.removeElementAt(0); 
        return message; 
    } 
 
    private void sendMessageToAllClients(String msg) { 
    	try {
			controller.printChat(msg);
		} catch (InterruptedException e) {
			System.err.println("---- Error in printChat() function -----");
			e.printStackTrace();
		}
    	for (int i = 0; i < mClients.size(); i++) { 
            Client client = (Client) mClients.get(i); 
            client.mClientSender.sendMessage(msg); 
        } 
    } 
 
    /*
     * Read messages infinitly
     * @see java.lang.Thread#run()
     */
    public void run() { 
        try { 
            while (true) { 
                String message = getNextMessageFromQueue(); 
                sendMessageToAllClients(message); 
            } 
        } catch (InterruptedException ie) { 
            // Thread interrupted. 
        } 
    } 
}
