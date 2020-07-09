package UnitTest;

import java.io.FileNotFoundException;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import trades.Order;
import trades.TradeReader;

class TradeReaderTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testTextFileReaderNullCheck() throws FileNotFoundException {
		TradeReader trTest = new TradeReader();
		List<String> list = trTest.textFileReader(null);
		Assert.assertEquals(true, list.isEmpty());
	}
	
	@Test
	void testTextFileReaderEmptyStringCheck() {
		TradeReader trTest = new TradeReader();
		try {
			List<String> list = trTest.textFileReader("");
		} catch (FileNotFoundException e) {
			exception.expect(FileNotFoundException.class);
		}
	}
	
	@Test
	void testTextFileReaderSpaceStringCheck() {
		TradeReader trTest = new TradeReader();
		try {
			List<String> list = trTest.textFileReader("  ");
		} catch (FileNotFoundException e) {
			exception.expect(FileNotFoundException.class);
		}
	}
	
	@Test
	void testTextFileReaderCommaStringCheck() {
		TradeReader trTest = new TradeReader();
		try {
			List<String> list = trTest.textFileReader(",");
		} catch (FileNotFoundException e) {
			exception.expect(FileNotFoundException.class);
		}
	}
	
	@Test
	void testTextFileReaderSymbolsCountCheck() throws FileNotFoundException {
		TradeReader trTest = new TradeReader();
		List<String> list = trTest.textFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\symbols.txt");
		Assert.assertEquals(10, list.size());
	}
	
	@Test
	void testTextFileReaderBrokersCountCheck() throws FileNotFoundException {
		TradeReader trTest = new TradeReader();
		List<String> list = trTest.textFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\firms.txt");
		Assert.assertEquals(12, list.size());
	}
	
	@Test
	void testTextFileReaderEmptyFileCheck() throws FileNotFoundException {
		TradeReader trTest = new TradeReader();
		List<String> list = trTest.textFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\firms_empty.txt");
		Assert.assertEquals(true, list.isEmpty());
	}
	
	@Test
	void testTextFileReaderFileNotFoundCheck() {
		TradeReader trTest = new TradeReader();
		try {
			List<String> list = trTest.textFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\abcd.txt");
		} catch (FileNotFoundException e) {
			exception.expect(FileNotFoundException.class);
		}
	}
		
	@Test
	void testCsvFileReaderNullCheck() throws FileNotFoundException {
		TradeReader trTest = new TradeReader();
		List<Order> list = trTest.csvFileReader(null);
		Assert.assertEquals(true, list.isEmpty());
	}
	
	@Test
	void testCsvFileReaderEmptyStringCheck() {
		TradeReader trTest = new TradeReader();
		try {
			List<Order> list = trTest.csvFileReader("");
		} catch (FileNotFoundException e) {
			exception.expect(FileNotFoundException.class);
		}
	}
	
	@Test
	void testCsvFileReaderSpaceStringCheck() {
		TradeReader trTest = new TradeReader();
		try {
			List<Order> list = trTest.csvFileReader("  ");
		} catch (FileNotFoundException e) {
			exception.expect(FileNotFoundException.class);
		}
	}
	
	@Test
	void testCsvFileReaderCommaStringCheck() {
		TradeReader trTest = new TradeReader();
		try {
			List<Order> list = trTest.csvFileReader(",");
		} catch (FileNotFoundException e) {
			exception.expect(FileNotFoundException.class);
		}
	}
	
	@Test
	void testCsvFileReaderCountCheck() throws FileNotFoundException {
		TradeReader trTest = new TradeReader();
		List<Order> list1 = trTest.csvFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\trades.csv");
		List<Order> list2 = trTest.csvFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\trades_test.csv");
		Assert.assertEquals(554, list1.size());
		Assert.assertEquals(565, list2.size());
	}
	
	@Test
	void testCsvFileReaderEmptyFileCheck() throws FileNotFoundException {
		TradeReader trTest = new TradeReader();
		List<Order> list = trTest.csvFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\trades_empty.csv");
		Assert.assertEquals(true, list.isEmpty());
	}
	
	@Test
	void testCsvFileReaderFileNotFoundCheck() {
		TradeReader trTest = new TradeReader();
		try {
			List<Order> list = trTest.csvFileReader("\\Users\\20775586\\eclipse-workspace\\LTSE\\data\\abcd.csv");
		} catch (FileNotFoundException e) {
			exception.expect(FileNotFoundException.class);
		}
	}

}
