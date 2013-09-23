import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;


public class ClientSocketHandler implements Runnable {

	Socket clientSocket;
	LinkedBlockingQueue<String> clientRequestQueue = new LinkedBlockingQueue<String>();
	
	public ClientSocketHandler(Socket clientSocket, LinkedBlockingQueue<String> requestQueue) {
		
		this.clientSocket = clientSocket;
		this.clientRequestQueue = requestQueue;
	}
	
	/*
	 * This method receives the object from the socket and
	 * adds it to the request queue which has all the requests
	 * sent from clients to the server.
	 */
	
	@Override
	public void run() {
		
		System.out.println("[Client Socket Handler]: Listening for objects..");
		
		try {
				ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
					
				while(true) {
			
				String request = (String)ois.readObject();
					
					if (request != null) {
						
						request = request.toUpperCase();
					
						System.out.println("[Client Socket Handler]: Adding to client request queue..");
					
						clientRequestQueue.add(request);
					
						ois.close();
						clientSocket.close();
						return;
					}

			}
				
		} catch (Exception e) { e.printStackTrace(); }
				
	}

}
