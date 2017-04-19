package server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class ClientSender extends Thread {
	 private Vector msgQueue = new Vector(); 
	 
	    private MessageDispatcher msgDispatcher; 
	    private Client mClient; 
	    private PrintWriter output; 
	 
	    public ClientSender(Client aClient, MessageDispatcher aMessageDispatcher) throws IOException { 
	        mClient = aClient; 
	        msgDispatcher = aMessageDispatcher; 
	        Socket socket = aClient.cSocket; 
	        output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()) ); 
	    } 
	 
	    /** 
	     * Adds given message to the message queue and notifies 
	     * this thread (actually getNextMessageFromQueue method) 
	     * that a message is arrived. sendMessage is always called 
	     * by other threads (MessageDispatcher). 
	     */ 
	    public synchronized void sendMessage(String message) { 
	        msgQueue.add(message); 
	        notify(); 
	    } 
	 
	    /** 
	     * Sends a keep-alive message to the client to check if 
	     * it is still alive. This method is called when the client 
	     * is inactive too long to prevent serving dead clients. 
	     */ 
	    public void sendKeepAlive() { 
	        sendMessage(ServerHandler.KEEP_ALIVE_MESSAGE); 
	    } 
	 
	    /** 
	     * @return and deletes the next message from the message 
	     * queue. If the queue is empty, falls in sleep until 
	     * notified for message arrival by sendMessage method. 
	     */ 
	    private synchronized String getNextMessageFromQueue() throws InterruptedException { 
	        while (msgQueue.size() == 0) 
	            wait(); 
	        String message = (String) msgQueue.get(0); 
	        msgQueue.removeElementAt(0); 
	        return message; 
	    } 
	 
	    /** 
	     * Sends given message to the client's socket. 
	     */ 
	    private void sendMessageToClient(String message) { 
	        output.println(message); 
	        output.flush(); 
	    } 
	 
	    /** 
	     * Until interrupted, reads messages from the message queue 
	     * and sends them to the client's socket. 
	     */ 
	    public void run() { 
	        try { 
	            while (!isInterrupted()) { 
	                String message = getNextMessageFromQueue(); 
	                sendMessageToClient(message); 
	            } 
	        } catch (Exception e) { 
	            // Commuication problem 
	        } 
	 
	        // Communication is broken. Interrupt both listener 
	        // and sender threads 
	        mClient.mClientReceiver.interrupt(); 
	        msgDispatcher.deleteClient(mClient); 
	    } 
}
