import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

public class VSynchrony extends ReceiverAdapter {

	static int processID;
	private static int numClients;
	private static int port;
	private static HashMap<String, Stock> stockState = new HashMap<String, Stock>();
	private static Account[] accountList; 
	private LinkedBlockingQueue<String> clientRequestQueue = new LinkedBlockingQueue<String>();
	private LinkedList<String> requestHistory = new LinkedList<String>();
	int orderNumber = 1;
	int tradeNumber = 1;
	JChannel channel;

	
	public VSynchrony(int pid, int clients, int serverPort) {
		processID = pid;
		numClients = clients;
		port = serverPort;
		accountList = new Account[numClients];
	}

	public void viewAccepted(View new_view) {
      
		System.out.println("** view: " + new_view);
		
    }
	
	public void receive(Message msg) {
		
		String clientRequest = (String)msg.getObject();
		
		requestHistory.add(clientRequest);
		
		processRequest(clientRequest);						// Check for thread-safety here
		
		trade();							// Do the trading
		
	}
	
	public void getState(OutputStream output) throws Exception {
        
		synchronized(requestHistory) {
            
			Util.objectToStream(requestHistory, new DataOutputStream(output));
			
        }
    }
	
	@SuppressWarnings("unchecked")
	public void setState(InputStream input) throws Exception {
        
		LinkedList<String> list = (LinkedList<String>)Util.objectFromStream(new DataInputStream(input));
        
		synchronized(requestHistory) {
            
			requestHistory.clear();
			requestHistory.addAll(list);
            
        }
		
		while (!list.isEmpty()) {
			
			processRequest(list.poll());
			trade();
			
		}
		
    }
	
	public static void main(String[] args) {
		
		/* Parse command line args */
		int pid = Integer.valueOf(args[0]);
		int clients = Integer.valueOf(args[1]);
		int serverPort = Integer.valueOf(args[2]);
		
		try {
				new VSynchrony(pid, clients, serverPort).start();
				
		} catch (Exception e) { e.printStackTrace(); }
	}
		
    private void start() throws Exception {

    	initializeStocks();					// Read the index.properties file and initialize the stockList[] array
		createAccounts();					// Create accounts for clients based on numClients
		
        channel = new JChannel("protocol.xml"); 			// use the default config, udp.xml
        channel.setReceiver(this);
        channel.connect("Vivitsu_Maharaja");
        channel.getState(null,10000);					// system state, the entire request history is received and the system state is rebuilt
		eventLoop();
		channel.close();
    }
    
    /*
     *  The main event loop. This method creates a server socket and passes it to
     *  a server socket handler thread which will continuously listen for client
     *  connections and process them.
     */
    
    private void eventLoop() {
    
		
		try {
				System.out.println("Now listening on socket at port " + port);
			
				ServerSocket serverSock = new ServerSocket(port); 	// Create a new server socket where clients will send requests
				
				ServerSocketHandler serverSocketHandler = new ServerSocketHandler(serverSock, clientRequestQueue);
				
				Thread serverSocketThread = new Thread(serverSocketHandler);	// Start a server socket handler thread which will receive client connections
				serverSocketThread.start();
				
				System.out.println("Server socket thread started..");
		
		} catch(Exception e) { e.printStackTrace(); }
		
		while (true) {
			try {				
					
					if (!clientRequestQueue.isEmpty()) {
						
						System.out.println("Polling client request queue..");
						
						String clientRequest = clientRequestQueue.poll();
					
						Message msg = new Message(null, null, clientRequest);
					
						channel.send(msg);
					
// 						processRequest(clientRequest);		// This is redundant
					
//						trade();							// Do the trading
					}
				
				    
			} catch (Exception e) { e.printStackTrace(); }
			
		}
		
    }
    
	/* 
	 * This method will read the index.properties file & initialize
	 * the stockList[] array of this class. Each member of the stockList[]
	 * array is an object of class Stock initialized with the stockName,
	 * stockSymbol & initial price of $100.  
	 */
	
	private void initializeStocks() {
		
		try {
			
			FileReader fr = new FileReader("index.properties");
			BufferedReader br = new BufferedReader(fr);						// open a buffered reader on file index.properties
			
			String str = br.readLine();										// read the first line
//			int i = 0;

			
			while (str != null) {
				
				if (str.charAt(0) == '#') {
					
					str = br.readLine();
					continue;
					
				}
				
//				System.out.println(str);
				
				String[] stockInfo = str.split("\\t"); 					// split the strings on the space
			
//				System.out.println(stockInfo[0]);
//				System.out.println(stockInfo[1]);
				
				Stock stock = new Stock(stockInfo[1], stockInfo[0], 100);	// initialize an object of class Stock 
				
				stockState.put(stockInfo[0], stock);
				
				str = br.readLine(); 										// read next line
//				i++;
			}
			
			br.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/*
	 * This method will create account for numClients no of Clients
	 * with client ID starting from 1 and increasing by 1 for each 
	 * client. An object of class Account is created initialized
	 * with the client ID & initial balance. The stock info of each
	 * account is also initialized with 0 stocks for each type of
	 * stock.
	 */
	
	private void createAccounts() {
		
		for (int i = 0; i < numClients; i++) {
			
			accountList[i] = new Account(i+1, 10000);
			
/*			for (int j = 0; j < stockList.length; j++) {
				
				accountList[i].setStockInfo(stockList[j].getName(), 0);
				
			} */
			}
		
		}
  
    
    private void processRequest(String clientRequest) {
    	
    	String[] parsedRequest = clientRequest.split("\\s+");
    	
    	int clientID = Integer.valueOf(parsedRequest[0].substring(1));
    	String orderType = parsedRequest[1];
    	String stockSymbol = parsedRequest[2];
    	int quote = Integer.valueOf(parsedRequest[3]);
    	int quantity = Integer.valueOf(parsedRequest[4]);
    	
    	Request request = new Request(clientID, stockSymbol, orderType, quantity, quote);
    	
    	// Get the stock object 
    	Stock stockObj = stockState.get(stockSymbol);
    	
    	System.out.println("Order #" + orderNumber + ": " + clientRequest);
    	
    	// Print stock info
    	stockObj.printStockInfo();
    	
    	System.out.printf("\n");
    	System.out.print("[");
    	
    	// Print account info
    	for (int i = 0; i < accountList.length; i++) {
    		
    		accountList[i].printAccountInfo();
    		
    	}
    	
    	System.out.print("]");
    	System.out.printf("\n");
    	System.out.println("------------------------------------------------------------");
    	
    	orderNumber++;
    	
    	// Insert this request in the sell/buy list
    	if (orderType.equalsIgnoreCase("BUY")) {
    		
    		stockObj.buyList.add(request);
    		
    	} else if (orderType.equalsIgnoreCase("SELL")) {
    		
    		stockObj.sellList.add(request);
    		
    	}
    	
    	
    }
    
    private void trade() {
    	
    	
    	for (Stock stock : stockState.values()) {
    		
    		if (stock.sellList.peek() == null || stock.buyList.peek() == null) {
    			
    			continue;
    			
    		}
    		
    		int sellQuote = stock.sellList.peek().getQuote();
    		int sellerID = stock.sellList.peek().getClientID();
    		
    		Iterator<Request> it = stock.buyList.iterator();
    		
    		if (sellQuote > stock.buyList.peek().getQuote()) {
				
				continue;
			} 
    		
    		while (it.hasNext()) {
    			
    			Request r = it.next();
    		
    			if (r.getQuote() > sellQuote) {
    				
    				stock.setPrice(sellQuote);
    			
    				int buyerID = r.getClientID();
    			
    				/* Should be updated to call method in Account class which updates balance & set portfolio value */
	    			accountList[buyerID - 1].setBalance((accountList[buyerID - 1].getBalance() - (sellQuote * r.getQuantity())));
	    			accountList[sellerID - 1].setBalance((accountList[sellerID - 1].getBalance() + (sellQuote * r.getQuantity())));	
	    			
	    			it.remove();
	    			stock.sellList.remove();
	    			tradeNumber++;
	    			break;
	    			
	    		}
    			
    		}
    		
    	}
    	
    }
    
}