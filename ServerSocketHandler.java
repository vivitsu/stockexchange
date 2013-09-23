import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;


public class ServerSocketHandler implements Runnable {

	ServerSocket serverSocket;
	LinkedBlockingQueue<String> clientRequestQueue = new LinkedBlockingQueue<String>();
	
	public ServerSocketHandler(ServerSocket serverSocket, LinkedBlockingQueue<String> requestQueue) {
		this.serverSocket = serverSocket;
		this.clientRequestQueue = requestQueue;
	}
	
	/*
	 * This method receives connections from a client, creates a client socket
	 * and passes this socket to the client socket handler thread.
	 */
	
	@Override
	public void run() {
		
		while(true) {
			try {
					System.out.println("[Server Socket Handler]: Listening for connection on server port");
				
					Socket clientSocket = serverSocket.accept();
		
					ClientSocketHandler clientSocketHandler = new ClientSocketHandler(clientSocket, clientRequestQueue);
				
					Thread clientSocketThread = new Thread(clientSocketHandler);
				
					clientSocketThread.start();
					
					System.out.println("[Server Socket Handler]: Client Socket Handler thread created");
				
			} catch (Exception e) { e.printStackTrace(); }

		}

	}
}
