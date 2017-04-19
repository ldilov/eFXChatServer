package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientReceiver extends Thread{
	private MessageDispatcher msgDispatcher; 
    private Client mClient; 
    private BufferedReader mSocketReader; 
 
    public ClientReceiver(Client aClient, MessageDispatcher aSrvDispatcher) throws IOException { 
        mClient = aClient; 
        msgDispatcher = aSrvDispatcher; 
        Socket socket = aClient.cSocket; 
        socket.setSoTimeout(ServerHandler.CLIENT_READ_TIMEOUT); 
        mSocketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()) ); 
    } 
 
    /** 
     * Until interrupted, reads messages from the client 
     * socket, forwards them to the server dispatcher's 
     * queue and notifies the server dispatcher. 
     */ 
    public void run() { 
        try { 
            while (!isInterrupted()) { 
                try { 
                    String message = mSocketReader.readLine(); 
                    if (message == null) 
                        break; 
                    msgDispatcher.dispatchMessage(mClient, message); 
                } catch (SocketTimeoutException ste) { 
                    mClient.mClientSender.sendKeepAlive(); 
                } 
            } 
        } catch (IOException ioex) { 
            // Problem reading from socket (broken connection) 
        } 
 
        // Communication is broken. Interrupt both listener and 
        // sender threads 
        mClient.mClientSender.interrupt(); 
        msgDispatcher.deleteClient(mClient); 
    } 
}
