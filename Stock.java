import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;


/* Main Stock class. Contains info about the stock.
 * 1. Company Name
 * 2. Symbol
 * 3. Total No. of Shares, initially 10,000
 * 4. Price, initially $100
 * 5. Order Lists
 */
public class Stock {

	private String name;
	String symbol;
//	private int totalShares;
	private int price;
	
	private SellComparator sc = new SellComparator();
	private BuyComparator bc = new BuyComparator();
	
	PriorityQueue<Request> sellList = new PriorityQueue<Request>(10, sc);
	PriorityQueue<Request> buyList = new PriorityQueue<Request>(10, bc);
	
	private LinkedBlockingQueue<Request> printQueue = new LinkedBlockingQueue<Request>();
	
	public Stock(String stockName, String stockSymbol, int initialPrice) {
		
		this.name = stockName;
		this.symbol = stockSymbol;
		this.price = initialPrice;
	}
	
	public void setPrice(int newPrice) {
		
		this.price = newPrice;
	}
	
	public int getPrice() {
		
		return this.price;
	}
	
	public String getName() {
		
		return this.name;
	}
	
	public void printStockInfo() {
		
		
		System.out.println("Price: " + price);
    	System.out.print("BUYS: ");
    	
    	// Print the buy lists of the stock

    	
    	while (!buyList.isEmpty()) {
    		
    		Request tempReq = buyList.poll();
    		tempReq.printRequest();
    		printQueue.add(tempReq);
    	}
    	
    	while (!printQueue.isEmpty()) {
    		
    		buyList.add(printQueue.poll());
    	}
    	
    	System.out.printf("\n");
    	System.out.print("SELLS: ");
    	
    	// Print sell lists
    	
    	while (!sellList.isEmpty()) {
    		
    		Request tempReq = sellList.poll();
    		tempReq.printRequest();
    		printQueue.add(tempReq);
    	
    	}
    	
    	while (!printQueue.isEmpty()) {
    		
    		sellList.add(printQueue.poll());
    		
    	}
    	
	}
	
}
