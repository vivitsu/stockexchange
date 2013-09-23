import java.util.Comparator;


public class BuyComparator implements Comparator<Request> {

	@Override
	public int compare(Request r1, Request r2) {
		
		if (r1.getQuote() > r2.getQuote()) {
			return -1;
		} else if (r1.getQuote() < r2.getQuote()) {
			return 1;
		} else {
			return 0;
		}
	}

}
