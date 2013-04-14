import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class Execute {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// readFuture_trans("J:/Download/log/log/", "future_data",
		// "transaction");
		readFuture_record("J:/Download/log/log2/", "future_data", "five_price");
		// readStock_record("J:/Download/final/", "stock_data", "five_price");
	}

	private static void readStock_record(String inputDirName, String database,
			String table) {
		try {
			String[] fieldsName = { "stock_name", "record_time",
					"before_close_hide_five", "Column_D", "trade_is_success",
					"has_price_limits", "cur_price", "total_volume",
					"buy_best_num", "buy_has_price_limits", "buy_price_1",
					"buy_volume_1", "buy_price_2", "buy_volume_2",
					"buy_price_3", "buy_volume_3", "buy_price_4",
					"buy_volume_4", "buy_price_5", "buy_volume_5",
					"sell_best_num", "sell_has_price_limits", "sell_price_1",
					"sell_volume_1", "sell_price_2", "sell_volume_2",
					"sell_price_3", "sell_volume_3", "sell_price_4",
					"sell_volume_4", "sell_price_5", "sell_volume_5",
					"record_date" };
			String[] fieldsType = { "String", "String", "String", "String",
					"String", "String", "Double", "Integer", "Integer",
					"String", "Double", "Integer", "Double", "Integer",
					"Double", "Integer", "Double", "Integer", "Double",
					"Integer", "Integer", "String", "Double", "Integer",
					"Double", "Integer", "Double", "Integer", "Double",
					"Integer", "Double", "Integer", "String" };
			long beginTime = System.currentTimeMillis();
			SQLHelper helper = SQLHelper.getSQLHelperInstance(database);
			helper.createStatement();
			File inputDir = new File(inputDirName);
			for (File inputFile : inputDir.listFiles()) {
				String inputFileName = inputFile.getName();
				if (!inputFileName.endsWith(".xls"))
					continue;
				System.out.println(inputFileName);
				Workbook workbook = Workbook.getWorkbook(inputFile);
				Sheet sheet = workbook.getSheet(0);
				String[] insertData = new String[fieldsName.length];
				// 讀取每一橫行資料
				for (int i = 0; i < sheet.getRows(); i++) {
					// 讀取每一個資料
					for (int j = 0; j < sheet.getColumns() - 1; j++) {
						String content = sheet.getCell(j, i).getContents();
						insertData[j] = content;
					}
					helper.insert(table, fieldsName, fieldsType, insertData);
				}
				System.out.println("	Execute batch begin: " + inputDirName
						+ inputFileName + "...");
				helper.execute();
				System.out.println("	Execute batch end: " + inputDirName
						+ inputFileName + ".");
			}
			System.out.println("Total time: "
					+ (System.currentTimeMillis() - beginTime) / 1000
					+ " Seconds.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		}
	}

	private static void readFuture_record(String inputDirName, String database,
			String table) {
		try {
			String[] fieldsName = { "time", "future_name", "month",
					"lot_buy_1", "price_buy_1", "lot_buy_2", "price_buy_2",
					"lot_buy_3", "price_buy_3", "lot_buy_4", "price_buy_4",
					"lot_buy_5", "price_buy_5", "lot_sell_1", "price_sell_1",
					"lot_sell_2", "price_sell_2", "lot_sell_3", "price_sell_3",
					"lot_sell_4", "price_sell_4", "lot_sell_5", "price_sell_5",
					"date" };
			String[] fieldsType = { "String", "String", "String", "Double",
					"Integer", "Double", "Integer", "Double", "Integer",
					"Double", "Integer", "Double", "Integer", "Double",
					"Integer", "Double", "Integer", "Double", "Integer",
					"Double", "Integer", "Double", "Integer", "String" };
			long beginTime = System.currentTimeMillis();
			SQLHelper helper = SQLHelper.getSQLHelperInstance(database);
			helper.createStatement();
			File inputDir = new File(inputDirName);
			BufferedReader reader;
			for (File inputFile : inputDir.listFiles()) {
				String inputFileName = inputFile.getName();
				if (!inputFileName.endsWith(".log2"))
					continue;
				String[] inputFileNameArray = inputFileName.split("SRKT|\\.");
				String dateString = inputFileNameArray[2]
						+ inputFileNameArray[1];
				System.out.println(inputFileName);
				reader = new BufferedReader(new FileReader(inputDirName
						+ inputFileName));
				String line = "";
				while ((line = reader.readLine()) != null) {
					String[] oriInsertData = line
							.split("\\|\\s+|\\s+\\||\\s+|\\||/\\s+");
					String[] insertData = new String[fieldsName.length];
					if (insertData.length < 1)
						continue;
					boolean isContinue = false;
					for (int i = 0; i < oriInsertData.length; i++) {
						insertData[i] = oriInsertData[i] = oriInsertData[i]
								.trim();
						if (oriInsertData[i].endsWith("/"))
							insertData[i] = oriInsertData[i] = oriInsertData[i]
									.replace("/", "");
						if (oriInsertData[i].equals(""))
							isContinue = true;
					}
					if (isContinue)
						continue;
					insertData[insertData.length - 1] = dateString;
					helper.insert(table, fieldsName, fieldsType, insertData);
				}
				reader.close();
				System.out.println("	Execute batch begin: " + inputDirName
						+ inputFileName + "...");
				helper.execute();
				System.out.println("	Execute batch end: " + inputDirName
						+ inputFileName + ".");
			}
			System.out.println("Total time: "
					+ (System.currentTimeMillis() - beginTime) / 1000
					+ " Seconds.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void readFuture_trans(String inputDirName, String database,
			String table) {
		try {
			String[] fieldsName = { "future_name", "month", "lot", "price",
					"total_lot", "transaction_time", "date" };
			String[] fieldsType = { "String", "String", "Integer", "Double",
					"Integer", "String", "String" };
			long beginTime = System.currentTimeMillis();
			SQLHelper helper = SQLHelper.getSQLHelperInstance(database);
			helper.createStatement();
			File inputDir = new File(inputDirName);
			BufferedReader reader;
			for (File inputFile : inputDir.listFiles()) {
				String inputFileName = inputFile.getName();
				if (!inputFileName.endsWith(".log"))
					continue;
				String[] inputFileNameArray = inputFileName.split("SRKT|\\.");
				String dateString = inputFileNameArray[2]
						+ inputFileNameArray[1];
				System.out.println(inputFileName);
				reader = new BufferedReader(new FileReader(inputDirName
						+ inputFileName));
				String line = "";
				while ((line = reader.readLine()) != null) {
					String[] oriInsertData = line.split("\\|");
					String[] insertData = new String[fieldsName.length];
					if (insertData.length < 1)
						continue;
					boolean isContinue = false;
					for (int i = 0; i < oriInsertData.length; i++) {
						insertData[i] = oriInsertData[i] = oriInsertData[i]
								.trim();
						if (oriInsertData[i].equals(""))
							isContinue = true;
					}
					if (isContinue)
						continue;
					insertData[insertData.length - 1] = dateString;
					helper.insert(table, fieldsName, fieldsType, insertData);
				}
				reader.close();
				System.out.println("	Execute batch begin: " + inputDirName
						+ inputFileName + "...");
				helper.execute();
				System.out.println("	Execute batch end: " + inputDirName
						+ inputFileName + ".");
			}
			System.out.println("Total time: "
					+ (System.currentTimeMillis() - beginTime) / 1000
					+ " Seconds.");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
