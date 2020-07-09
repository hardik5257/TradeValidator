package trades;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TradeReader {

	static final String DELIMITER = ",";

	public static List<String> textFileReader(String filePath) throws FileNotFoundException {
		List<String> list = new ArrayList<String>();
		if (filePath != null) {
			Scanner sc = new Scanner(new File(filePath.trim()));
			//new Scanner(new InputStreamReader(TradeReader.class.getClassLoader().getResourceAsStream(filePath))); 
			//TradeReader.class.getClassLoader().getResource(filePath.trim())
			while (sc.hasNextLine()) {
				list.add(sc.nextLine()); 
			}
			sc.close();
		}
		return list;
	}
	
	// Take file path as an argument and parse the file to create a list of trade orders
	public static List<Order> csvFileReader(String filePath) throws FileNotFoundException {
		int counter = 0;
		int columns = 0;
		List<Order> trades = new ArrayList<Order>();
		if (filePath != null) {
			Scanner sc = new Scanner(new File(filePath.trim()));
			sc.useDelimiter(DELIMITER);
			
			// Reading Records One by One in a String array
			while (sc.hasNextLine()) {
				if (counter == 0) { //skip the headers
					columns = sc.nextLine().split(DELIMITER).length;
					counter++;
					continue;				
				}

				String timeStamp = "";
				String broker = "";
				String sequenceId = "";
				String type = "";
				String symbol = "";
				String quantity = "";
				String price = "";
				String side = "";
				String[] orderDetails = sc.nextLine().split(",", columns);
				for (int i=0; i<columns; i++) {
					if (!orderDetails[i].trim().equals("")) {
						switch (i) {
						case 0: timeStamp = orderDetails[i];
						break;
						case 1: broker = orderDetails[i];
						break;
						case 2: sequenceId = orderDetails[i];
						break;
						case 3: type = orderDetails[i];
						break;
						case 4: symbol = orderDetails[i];
						break;
						case 5: quantity = orderDetails[i];
						break;
						case 6: price = orderDetails[i];
						break;
						case 7: side = orderDetails[i];
						break;
						default: break;
						}
					}
				}
				Order order = new Order(timeStamp, broker, sequenceId, type, symbol, quantity, price, side);
				trades.add(order);
				counter++;
			}
			sc.close();
		}	
		return trades;
	}
}
