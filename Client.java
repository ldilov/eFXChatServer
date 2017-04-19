package server;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public Socket cSocket; 
    public ClientReceiver mClientReceiver; 
    public ClientSender mClientSender;
    public String clientName;
    
    public Client() {
    	cSocket = null;
    	mClientReceiver = null;
    	mClientSender = null;
    	clientName = "Unknown";
    }
    
    public String getIP() {
    	return cSocket.getRemoteSocketAddress().toString();
    }
    
    public void disconnect() throws IOException {
    	mClientReceiver.interrupt();
    	mClientSender.interrupt();
    	if(mClientSender.isInterrupted() && mClientReceiver.isInterrupted())
    		cSocket.close();
    	
    	if(cSocket.isClosed()){
    		cSocket = null;
    		clientName = null;
    	}
    	System.out.println(clientName + " has been kicked from the chat!");
    }
}
