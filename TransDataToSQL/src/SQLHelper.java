import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLHelper {

	static private String url = "jdbc:mysql://localhost/";
	static private String database = "future_data";
	static private String username = "root";
	static private String password = "root";
	static private Connection con;
	static private SQLHelper sqlHelperInstance = null;
	private Statement stmt = null;

	private SQLHelper() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url + database, username,
					password);
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Singleton
	static public SQLHelper getSQLHelperInstance() {
		try {
			return ((sqlHelperInstance == null) || con.isClosed() || con
					.isValid(0)) ? (sqlHelperInstance = new SQLHelper())
					: sqlHelperInstance;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[Get SQL Instance Error]New Connection.");
			return sqlHelperInstance = new SQLHelper();
		}
	}

	static public SQLHelper getSQLHelperInstance(String databaseName) {
		database = databaseName;
		return sqlHelperInstance = new SQLHelper();
	}

	public void createStatement() {
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String insert(String tableName, String[] fieldsName,
			String[] fieldsType, String[] insertData) {
		String msg = "success";
		String sql;
		if (fieldsType.length == insertData.length) {
			try {
				sql = "INSERT INTO " + tableName + " (";
				for (int i = 0; i < fieldsName.length; i++) {
					sql += fieldsName[i];
					if (i != fieldsName.length - 1)
						sql += ",";
				}

				sql += ") VALUES(";
				for (int i = 0; i < insertData.length; i++) {
					if (fieldsType[i].equals("String"))
						sql += "'" + insertData[i] + "'";
					else
						sql += insertData[i];
					if (i != insertData.length - 1)
						sql += ",";
				}
				sql += ")";
				stmt.addBatch(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				msg = "[SQLHelper]Execute Single Insert Batch Exception!!";
			}
		} else {
			msg = "fail";
		}
		return msg;
	}

	public void execute() {
		try {
			stmt.executeBatch();
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
