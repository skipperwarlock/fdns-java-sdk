package gov.cdc.helper;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

public class DatabaseHelper {
	
	private static String dbUrl;
	private static String dbTable;
	private static String dbUsername;
	private static String dbPassword;

	/**
	 * DatabaseHelper constructor
	 *
	 * @param dbUrlOpt database url
	 * @param dbTableOpt table name
	 * @param dbUsernameOpt username
	 * @param dbPasswordOpt password
	 */
	public DatabaseHelper(String dbUrlOpt, String dbTableOpt, String dbUsernameOpt, String dbPasswordOpt) {
		dbUrl = dbUrlOpt;
		dbTable = dbTableOpt;
		dbUsername = dbUsernameOpt;
		dbPassword = dbPasswordOpt;
	}

	private static final Logger logger = Logger.getLogger(DatabaseHelper.class);

	/**
	 * Insert row into database
	 *
	 * @param data field and value pairs
	 * @param fileData file data to be written as blob
	 * @throws SQLException
	 */
	public void insert(Map<String, String> data, byte[] fileData) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

			StringBuilder fields = new StringBuilder();
			StringBuilder values = new StringBuilder();
			for (Map.Entry<String,String> entry : data.entrySet()) {
				String field = entry.getKey();
				String value = entry.getValue();
				if (!field.startsWith("_")) {
					if (fields.length() > 0)
						fields.append(",");
					fields.append(field);
					if (values.length() > 0)
						values.append(",");
					values.append("'");
					values.append(value);
					values.append("'");
				}
			}

			// Handle the blob
			fields.append(",payloadTextContent");
			values.append(",?");

			String sql = String.format("INSERT INTO %s(%s) VALUES (%s)", dbTable, fields, values);

			logger.debug(String.format("SQL: %s", sql));

			Clob fileBlob = conn.createClob();
			fileBlob.setString(1, new String(fileData));

			pstmt = conn.prepareStatement(sql);
			pstmt.setClob(1, fileBlob);
			pstmt.executeUpdate();
		} catch (Exception e) {
			logger.error(e);
		} finally {
			if (conn != null)
				conn.close();
			if (pstmt != null)
				pstmt.close();
		}
	}

}
