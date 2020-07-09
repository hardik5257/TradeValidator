package UnitTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import trades.Order;
import trades.TradeReader;
import trades.TradeValidator;

class TradeValidatorTest {

	static String symbolsFilePath;
	static String brokersFilePath;
	static String csvFilePath;
	static String allAcceptedOrdersFilePath;
	static String allRejectedOrdersFilePath;
	static String ordersAcceptedByBrokerFilePath;
	static String ordersRejectedByBrokerFilePath;
	static List<String> tickers = null;
	static List<String> brokers = null;
	static List<Order> trades = null;
	static int perMinuteOrderLimit = 3;
	static HashMap<String, Integer> brokersOrderCountMap = null;
	static HashMap<String, String> dupeTradeIdCheck = null;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		tickers = TradeReader.textFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\symbols.txt");
		brokers = TradeReader.textFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\firms.txt");
		trades = TradeReader.csvFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\trades.csv");
	}

	@BeforeEach
	void setUp() throws Exception {
		symbolsFilePath = "\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\symbols.txt";
		brokersFilePath = "\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\firms.txt";
		csvFilePath = "\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\trades.csv";
		allAcceptedOrdersFilePath = "\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\AllAcceptedOrders.txt";
		allRejectedOrdersFilePath = "\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\AllRejectedOrders.txt";
		ordersAcceptedByBrokerFilePath = "\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\OrdersAcceptedByBroker.txt";
		ordersRejectedByBrokerFilePath = "\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\OrdersRejectedByBroker.txt";
		brokersOrderCountMap = new HashMap<String, Integer>();
		dupeTradeIdCheck = new HashMap<String, String>();
	}

	@Test
	void testIsValidOrderUniqueTradeId() {
		TradeValidator tv = new TradeValidator();
		Order trade1 = new Order("10/5/2017  10:00:00 AM", "Fidelity", "10", "2", "BARK", "100", "1.195", "Buy");
		Order trade2 = new Order("10/5/2017  10:00:00 AM", "Fidelity", "10", "2", "HOOF", "100", "1.185", "Buy");
		tv.isValidOrder(trade1, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		boolean result = tv.isValidOrder(trade2, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}
	
	@Test
	void testIsValidOrderThreeOrdersPerMinute() {
		TradeValidator tv = new TradeValidator();
		Order trade1 = new Order("10/5/2017  10:00:00 AM", "LPL Financial", "1", "2", "BARK", "100", "1.195", "Buy");
		Order trade2 = new Order("10/5/2017  10:00:00 AM", "LPL Financial", "2", "2", "HOOF", "100", "1.185", "Buy");
		Order trade3 = new Order("10/5/2017  10:00:00 AM", "LPL Financial", "3", "2", "KRIL", "100", "1.175", "Buy");
		Order trade4 = new Order("10/5/2017  10:00:00 AM", "LPL Financial", "4", "2", "GLOO", "100", "1.165", "Buy");
		tv.isValidOrder(trade1, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		tv.isValidOrder(trade2, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		tv.isValidOrder(trade3, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		boolean result = tv.isValidOrder(trade4, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}
	
	@Test
	void testIsValidOrderNotTradedSymbol() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("10/5/2017  10:00:00 AM", "Fidelity", "1", "2", "XYZ", "100", "1.195", "Buy");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}
	
	@Test
	void testIsValidOrderNotTradedBroker() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("10/5/2017  10:00:00 AM", "ABCD", "1", "2", "XYZ", "100", "1.195", "Buy");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}
	
	@Test
	void testIsValidOrderTestOne() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("10/5/2017  10:00:00 AM", "Fidelity", "1", "2", "BARK", "100", "1.195", "Buy");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(true, result);
	}
	
	@Test
	void testIsValidOrderTestTwo() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("10/5/2017  10:00:00 AM", "", "1", "2", "BARK", "100", "1.195", "Buy");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}
	
	@Test
	void testIsValidOrderTestThree() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("10/5/2017  10:00:00 AM", "Fidelity", "", "2", "BARK", "100", "1.195", "Buy");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}
	
	@Test
	void testIsValidOrderTestFour() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("10/5/2017  10:00:00 AM", "Fidelity", "1", "", "BARK", "100", "1.195", "Buy");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}
	
	@Test
	void testIsValidOrderTestFive() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("10/5/2017  10:00:00 AM", "Fidelity", "1", "2", "", "100", "1.195", "Buy");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}
	
	@Test
	void testIsValidOrderTestSix() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("10/5/2017  10:00:00 AM", "Fidelity", "1", "2", "BARK", "", "1.195", "Buy");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}
	
	@Test
	void testIsValidOrderTestSeven() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("10/5/2017  10:00:00 AM", "Fidelity", "1", "2", "BARK", "100", "", "Buy");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}
	
	@Test
	void testIsValidOrderTestEight() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("10/5/2017  10:00:00 AM", "Fidelity", "1", "2", "BARK", "100", "1.195", "");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(false, result);
	}

	@Test
	void testIsValidOrderTestNine() {
		TradeValidator tv = new TradeValidator();
		Order trade = new Order("", "Fidelity", "1", "2", "BARK", "100", "1.195", "Buy");
		boolean result = tv.isValidOrder(trade, tickers, brokers, brokersOrderCountMap, dupeTradeIdCheck, perMinuteOrderLimit);
		Assert.assertEquals(true, result);
	}
	
	@Test 
	void testCreateAcceptedRejectedOrders() throws IOException { 
		TradeValidator tv = new TradeValidator();
		String result = tv.createAcceptedRejectedOrders(allAcceptedOrdersFilePath, allRejectedOrdersFilePath, ordersAcceptedByBrokerFilePath, ordersRejectedByBrokerFilePath, symbolsFilePath, brokersFilePath, csvFilePath, perMinuteOrderLimit);
		Assert.assertEquals("Success", result);
	}
	 
	@Test 
	void testCreateAcceptedRejectedOrdersEmptyTradesFile() throws IOException { 
		csvFilePath = "\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\trades_empty.csv";
		TradeValidator tv = new TradeValidator();
		String result = tv.createAcceptedRejectedOrders(allAcceptedOrdersFilePath, allRejectedOrdersFilePath, ordersAcceptedByBrokerFilePath, ordersRejectedByBrokerFilePath, symbolsFilePath, brokersFilePath, csvFilePath, perMinuteOrderLimit);
		Assert.assertEquals(csvFilePath + " is an empty file", result);
	}
	
	@Test 
	void testCreateAcceptedRejectedOrdersEmptyBrokersFile() throws IOException { 
		brokersFilePath = "\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\firms_empty.txt";
		TradeValidator tv = new TradeValidator();
		String result = tv.createAcceptedRejectedOrders(allAcceptedOrdersFilePath, allRejectedOrdersFilePath, ordersAcceptedByBrokerFilePath, ordersRejectedByBrokerFilePath, symbolsFilePath, brokersFilePath, csvFilePath, perMinuteOrderLimit);
		Assert.assertEquals(brokersFilePath + " is an empty file", result);
	}
	
	@Test 
	void testCreateAcceptedRejectedOrdersEmptyTickersFile() throws IOException { 
		symbolsFilePath = "\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\symbols_empty.txt";
		TradeValidator tv = new TradeValidator();
		String result = tv.createAcceptedRejectedOrders(allAcceptedOrdersFilePath, allRejectedOrdersFilePath, ordersAcceptedByBrokerFilePath, ordersRejectedByBrokerFilePath, symbolsFilePath, brokersFilePath, csvFilePath, perMinuteOrderLimit);
		Assert.assertEquals(symbolsFilePath + " is an empty file", result);
	}
	
	@Test 
	void testCreateAcceptedRejectedOrdersEmptyAcceptedFile() { 
		allAcceptedOrdersFilePath = "";
		TradeValidator tv = new TradeValidator();
		try {
			String result = tv.createAcceptedRejectedOrders(allAcceptedOrdersFilePath, allRejectedOrdersFilePath, ordersAcceptedByBrokerFilePath, ordersRejectedByBrokerFilePath, symbolsFilePath, brokersFilePath, csvFilePath, perMinuteOrderLimit);
		} catch (IOException e) {
			exception.expect(IOException.class);
		}
	}
	
	@Test 
	void testCreateAcceptedRejectedOrdersEmptyRejectedFile() { 
		allRejectedOrdersFilePath = "";
		TradeValidator tv = new TradeValidator();
		try {
			String result = tv.createAcceptedRejectedOrders(allAcceptedOrdersFilePath, allRejectedOrdersFilePath, ordersAcceptedByBrokerFilePath, ordersRejectedByBrokerFilePath, symbolsFilePath, brokersFilePath, csvFilePath, perMinuteOrderLimit);
		} catch (IOException e) {
			exception.expect(IOException.class);
		}		
	}
	
	@Test 
	void testCreateAcceptedRejectedOrdersEmptyAcceptedByBrokerFile() throws IOException { 
		ordersAcceptedByBrokerFilePath = "";
		TradeValidator tv = new TradeValidator();
		try {
			String result = tv.createAcceptedRejectedOrders(allAcceptedOrdersFilePath, allRejectedOrdersFilePath, ordersAcceptedByBrokerFilePath, ordersRejectedByBrokerFilePath, symbolsFilePath, brokersFilePath, csvFilePath, perMinuteOrderLimit);
		} catch (IOException e) {
			exception.expect(IOException.class);
		}
	}
	
	@Test 
	void testCreateAcceptedRejectedOrdersEmptyRejectedByBrokerFile() throws IOException { 
		ordersRejectedByBrokerFilePath = "";
		TradeValidator tv = new TradeValidator();
		try {
			String result = tv.createAcceptedRejectedOrders(allAcceptedOrdersFilePath, allRejectedOrdersFilePath, ordersAcceptedByBrokerFilePath, ordersRejectedByBrokerFilePath, symbolsFilePath, brokersFilePath, csvFilePath, perMinuteOrderLimit);
		} catch (IOException e) {
			exception.expect(IOException.class);
		}
	}
	
	@Test 
	void testCreateAcceptedRejectedOrdersCountCheck() throws IOException { 
		TradeValidator tv = new TradeValidator();
		try {
			String result = tv.createAcceptedRejectedOrders(allAcceptedOrdersFilePath, allRejectedOrdersFilePath, ordersAcceptedByBrokerFilePath, ordersRejectedByBrokerFilePath, symbolsFilePath, brokersFilePath, csvFilePath, perMinuteOrderLimit);
			List<String> list1 = new ArrayList<String>();
			List<String> list2 = new ArrayList<String>();
			List<String> list3 = new ArrayList<String>();
			List<String> list4 = new ArrayList<String>();
			if (allAcceptedOrdersFilePath != null && allRejectedOrdersFilePath != null && ordersAcceptedByBrokerFilePath != null && ordersRejectedByBrokerFilePath != null) {
				Scanner sc1 = new Scanner(new File(allAcceptedOrdersFilePath.trim()));
				Scanner sc2 = new Scanner(new File(allRejectedOrdersFilePath.trim()));
				Scanner sc3 = new Scanner(new File(ordersAcceptedByBrokerFilePath.trim()));
				Scanner sc4 = new Scanner(new File(ordersRejectedByBrokerFilePath.trim()));

				while (sc1.hasNextLine()) {
					list1.add(sc1.nextLine()); 
				}
				while (sc2.hasNextLine()) {
					list2.add(sc2.nextLine()); 
				}
				while (sc3.hasNextLine()) {
					list3.add(sc3.nextLine()); 
				}
				while (sc4.hasNextLine()) {
					list4.add(sc4.nextLine()); 
				}
				sc1.close();
				sc2.close();
				sc3.close();
				sc4.close();
			}
			String array1 = list3.get(0).split("->")[1].trim();
			String array2 = list4.get(0).split("->")[1].trim();
			Assert.assertEquals(514, list1.size());
			Assert.assertEquals(40, list2.size());
			String accepted = "2,3,4,5,6,8,10,11,12,13,14,16,17,18,19,20,21,22,23,24,25,26,27,28,29,31,32,34,35,36,37,38,39,40,41,42,43,45,46,47,48,49,50";
			String rejected = "1,7,9,15,30,33,44";
			Assert.assertEquals(accepted, array1);
			Assert.assertEquals(rejected, array2);
		} catch (IOException e) {
			exception.expect(IOException.class);
		}
	}
}
