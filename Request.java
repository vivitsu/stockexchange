/* Request class. Fields of this class will
 * be populated by the incoming client 
 * requests. The fields are:
 * 1. Client ID
 * 2. Stock symbol
 * 3. Order Type
 * 4. Quantity
 * 5. Quote
 */
public class Request {

	private int clientID;
	private String stockSymbol;
	private String orderType;
	private int quantity;
	private int quote;
	
	public Request(int clientID, String stockSymbol, String orderType, int quantity, int quote) {
		
		this.clientID = clientID;
		this.stockSymbol = stockSymbol;
		this.orderType = orderType;
		this.quantity = quantity;
		this.quote = quote;
		
	}
	
	public int getQuote() {
		return this.quote;
	}
	
	public int getClientID() {
		return this.clientID;
	}
	
	public int getQuantity() {
		
		return this.quantity;
	}
	
	public void printRequest() {
		
		System.out.print("[C" + clientID + " " + orderType + " " + stockSymbol + " " + quote + " " + quantity + "]" + " ");
	}
}
