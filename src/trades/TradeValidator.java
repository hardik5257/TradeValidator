package trades;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TradeValidator {  

	private static Properties prop;
	private static String pastOrderDateTime = "";
	private static final String PROPERTIY_FILE_PATH = "trade.properties";
	private static int PER_MINUTE_ORDER_LIMIT;
	
	static{
		InputStream is = null;
		try {
			prop = new Properties();
			is = TradeValidator.class.getClassLoader().getResourceAsStream(PROPERTIY_FILE_PATH);
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Validating four main business logic filters
	public static boolean isValidOrder(Order trade, List<String> tickers, List<String> brokers, HashMap<String, Integer> brokersOrderCountMap, 
			HashMap<String, String> dupeTradeIdCheck, int PER_MINUTE_ORDER_LIMIT) {

		if (!(trade.getBroker().isEmpty() || trade.getSymbol().isEmpty() || trade.getType().isEmpty() || trade.getQuantity().isEmpty() || trade.getSide().isEmpty()
				|| trade.getSequenceId().isEmpty() || trade.getPrice().isEmpty()) && tickers.contains(trade.getSymbol()) && brokers.contains(trade.getBroker())) {

			if (!dupeTradeIdCheck.isEmpty() && dupeTradeIdCheck.containsKey(trade.getBroker()) && dupeTradeIdCheck.get(trade.getBroker()).equals(trade.getSequenceId())) {
				return false;
			}
			dupeTradeIdCheck.put(trade.getBroker(), trade.getSequenceId());
			String currentOrderDateTime = trade.getTimeStamp();
			if (pastOrderDateTime.equals(currentOrderDateTime)) {
				if (brokersOrderCountMap.containsKey(trade.getBroker())) {
					int value = brokersOrderCountMap.get(trade.getBroker());
					if (value < PER_MINUTE_ORDER_LIMIT) {
						brokersOrderCountMap.put(trade.getBroker(), value + 1);
					} else {
						return false;
					}
				} else {
					brokersOrderCountMap.put(trade.getBroker(), 1);
				}
			} else {
				brokersOrderCountMap.clear();
				brokersOrderCountMap.put(trade.getBroker(), 1);
				pastOrderDateTime = currentOrderDateTime;
			}
		} else {
			return false;
		}
		return true; 
	}

	// Creating four files (1) AllAcceptedOrders (2) AllRejectedOrders (3) OrdersAcceptedByBroker (4) OrdersRejectedByBroker
	public static String createAcceptedRejectedOrders(String allAcceptedOrdersFilePath, String allRejectedOrdersFilePath, String ordersAcceptedByBrokerFilePath, String ordersRejectedByBrokerFilePath, 
			String symbolsFilePath, String brokersFilePath, String csvFilePath, int PER_MINUTE_ORDER_LIMIT) throws IOException {

		FileWriter allAccepted = null;
		FileWriter allRjected = null;
		FileWriter brokerAccepted = null;
		FileWriter brokerRjected = null;
		BufferedWriter allAcceptedOrder = null;
		BufferedWriter allRejectedOrder = null;
		BufferedWriter acceptedOrderByBroker = null;
		BufferedWriter rejectedOrderByBroker = null;
		try {
			// Parsing a symbols file & creating a list of Tickers
			List<String> tickers = TradeReader.textFileReader(symbolsFilePath);
			// Parsing a firms file & creating a list of Brokers
			List<String> brokers = TradeReader.textFileReader(brokersFilePath);
			// Parsing a trades file & creating a list of trades
			List<Order> trades = TradeReader.csvFileReader(csvFilePath);

			allAccepted = new FileWriter(allAcceptedOrdersFilePath, false);
			allRjected = new FileWriter(allRejectedOrdersFilePath, false);
			brokerAccepted = new FileWriter(ordersAcceptedByBrokerFilePath, false);
			brokerRjected = new FileWriter(ordersRejectedByBrokerFilePath, false);

			allAcceptedOrder = new BufferedWriter(allAccepted);
			allRejectedOrder = new BufferedWriter(allRjected);
			acceptedOrderByBroker = new BufferedWriter(brokerAccepted);
			rejectedOrderByBroker = new BufferedWriter(brokerRjected);

			HashMap<String, Integer> brokersOrderCountMap = new HashMap<String, Integer>();
			HashMap<String, String> dupeTradeIdCheck = new HashMap<String, String>();
			HashMap<String, String> acceptedTradesByBroker = new HashMap<String, String>();
			HashMap<String, String> rejectedTradesByBroker = new HashMap<String, String>();
			if (!(trades.isEmpty() || brokers.isEmpty() || tickers.isEmpty())) {
				for(Order trade: trades) {
					if (isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, PER_MINUTE_ORDER_LIMIT)) {
						if (acceptedTradesByBroker.isEmpty() || !acceptedTradesByBroker.containsKey(trade.getBroker())) {
							acceptedTradesByBroker.put(trade.getBroker(), trade.getSequenceId());
						} else {
							String acceptedIds = acceptedTradesByBroker.get(trade.getBroker()) + "," + trade.getSequenceId();
							acceptedTradesByBroker.put(trade.getBroker(), acceptedIds);
						}
						allAcceptedOrder.write(trade + "\n");
					} else {
						if (rejectedTradesByBroker.isEmpty() || !rejectedTradesByBroker.containsKey(trade.getBroker())) {
							rejectedTradesByBroker.put(trade.getBroker(), trade.getSequenceId());
						} else {
							String rejectedIds = rejectedTradesByBroker.get(trade.getBroker()) + "," + trade.getSequenceId();
							rejectedTradesByBroker.put(trade.getBroker(), rejectedIds);
						}
						allRejectedOrder.write(trade + "\n");
					}
				}
				for (Map.Entry<String, String> entry: acceptedTradesByBroker.entrySet()) {
					acceptedOrderByBroker.write(entry.getKey() + " -> " + entry.getValue() + "\n");
				}
				for (Map.Entry<String, String> entry: rejectedTradesByBroker.entrySet()) {
					rejectedOrderByBroker.write(entry.getKey() + " -> " + entry.getValue() + "\n");
				}
			} else {
				if (trades.isEmpty())
					return csvFilePath + " is an empty file";
				if (brokers.isEmpty())
					return brokersFilePath + " is an empty file";
				if (tickers.isEmpty())
					return symbolsFilePath + " is an empty file";
			}
		} catch (IOException e) {
			throw new IOException(e);
		} finally {
			if (allAcceptedOrder != null) {
				allAcceptedOrder.close();
			}
			if (allRejectedOrder != null) {
				allRejectedOrder.close();
			}
			if (acceptedOrderByBroker != null) {
				acceptedOrderByBroker.close();
			}
			if (rejectedOrderByBroker != null) {
				rejectedOrderByBroker.close();
			}
		}

		return "Success";
	}

	public static void main(String[] args) 
	{  
		String allAcceptedOrdersFilePath = prop.getProperty("all.accepted.orders.file.path");
		String allRejectedOrdersFilePath = prop.getProperty("all.rejected.orders.file.path");
		String ordersAcceptedByBrokerFilePath = prop.getProperty("orders.accepted.by.broker.file.path");
		String ordersRejectedByBrokerFilePath = prop.getProperty("orders.rejected.by.broker.file.path");
		String csvFilePath = prop.getProperty("csv.file.path");
		String symbolsFilePath = prop.getProperty("symboles.file.path");
		String brokersFilePath = prop.getProperty("brokers.file.path");
		PER_MINUTE_ORDER_LIMIT = Integer. parseInt(prop.getProperty("per.minute.order.limit"));
		try {
			String result = createAcceptedRejectedOrders(allAcceptedOrdersFilePath, allRejectedOrdersFilePath, ordersAcceptedByBrokerFilePath, ordersRejectedByBrokerFilePath, 
					symbolsFilePath, brokersFilePath, csvFilePath, PER_MINUTE_ORDER_LIMIT);
			System.out.println(result);
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
	}  
}  