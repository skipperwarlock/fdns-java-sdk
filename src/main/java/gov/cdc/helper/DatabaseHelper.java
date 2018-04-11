package gov.cdc.helper;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;

import org.apache.log4j.Logger;

public class DatabaseHelper {

	public static String dbUrl = null;
	public static String dbTable = null;
	public static String dbUsername = null;
	public static String dbPassword = null;

	private static final Logger logger = Logger.getLogger(DatabaseHelper.class);

	public static void insert(Map<String, String> data, byte[] fileData) {
		try {
			Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

			StringBuilder fields = new StringBuilder();
			StringBuilder values = new StringBuilder();
			for (String field : data.keySet()) {
				if (!field.startsWith("_")) {
					if (fields.length() > 0)
						fields.append(",");
					fields.append(field);
					if (values.length() > 0)
						values.append(",");
					values.append("'");
					values.append(data.get(field));
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

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setClob(1, fileBlob);
			pstmt.executeUpdate();

			conn.close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
