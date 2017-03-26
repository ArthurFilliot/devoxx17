package bk.devoxx17.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import bk.devoxx17.global.ApplicationScope;

public class DatabaseSQL {
    private static final Logger log = Logger.getLogger(DatabaseSQL.class);
	
	private Connection c;

	public boolean openConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
		} catch (Exception e) {
			log.error(e.getMessage() + ':' + e.getStackTrace());
			System.exit(0);
		}
		log.info("Opened database successfully");
		return c != null;
	}

	public boolean closeConnection() {
		try {
			c.close();
		} catch (SQLException e) {
			log.error(e.getMessage() + ':' + e.getStackTrace());
			System.exit(0);
		}
		log.info("Closed database successfully");
		return c == null;
	}
	
	public void openTransaction() {
		try {
			c.setAutoCommit(false);
		} catch (SQLException e) {
			log.error(e.getMessage() + ':' + e.getStackTrace());
			System.exit(0);
		}
		log.info("New transaction initialized");
	}
	
	public void rollbackTransaction() {
		try {
			c.rollback();
		} catch (SQLException e) {
			log.error(e.getMessage() + ':' + e.getStackTrace());
			System.exit(0);
		}
		log.info("Transaction rollbacked");
	}

	public Statement createStatement() {
		Statement stmt = null;
		try {
			stmt = c.createStatement();
		} catch (SQLException e) {
			log.error(e.getMessage() + ':' + e.getStackTrace());
			System.exit(0);
		}
		return stmt;
	}

	public String getScript(String resourcePath) {
		String script = "";
		try {
			URL file = this.getClass().getResource(resourcePath);
			log.info("open script file:" + file);
			BufferedInputStream b;

			b = new BufferedInputStream(file.openStream());

			byte[] contents = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = b.read(contents)) != -1) {
				script += new String(contents, 0, bytesRead);
			}
		} catch (IOException e) {
			log.error(e.getMessage() + ':' + e.getStackTrace());
			System.exit(0);
		}
		return script;
	}

	public Integer executeScript(String script) throws SQLException {
		Integer affectedRows = null;
		try {
			Statement stmt = createStatement();
			affectedRows = stmt.executeUpdate(script);
			stmt.close();
		} catch (SQLException e) {
			ApplicationScope.getInstance().setErrorMessage(e.getMessage());
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			throw e;
		}
		log.debug("executedScript:\n" + script);
		log.info("affectedRows:" + affectedRows);
		return affectedRows;
	}

	public ArrayListMultimap<String, String> executeSelection(String select) throws SQLException {
		log.debug("Selection query:\n" + select);
		ArrayListMultimap<String, String> m = ArrayListMultimap.create();
		try {
			List<String> columnLabels = Lists.newArrayList();

			Statement stmt = createStatement();
			ResultSet rs = stmt.executeQuery(select);
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				columnLabels.add(rsmd.getColumnLabel(i));
			}
			while (rs.next()) {
				for (String label : columnLabels) {
					m.put(label, rs.getString(label));
				}
			}
			stmt.close();
			log.debug("Selection Results:\n" + printSelection(columnLabels, m));
			
		} catch (SQLException e) {
			ApplicationScope.getInstance().setErrorMessage(e.getMessage());
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			throw e;
		}
		return m;
	}
	
	private String printSelection(List<String> columnLabels, Multimap<String, String> m) {
		String results="";
		String columns = "";
		char sep = '|';
		for (String columnLabel : columnLabels) {
			columns += sep + columnLabel;
		}
		results = columns + sep + '\n';
		if (m.size() > 0) {
			int i=0;
			for (Object o : m.get(columnLabels.get(0))) {
				String row = sep+(String)o;
				for (int j=1;j<columnLabels.size();j++) {
					row += sep + (String)((List<String>)m.get(columnLabels.get(j))).get(i);
				}
				results += row + sep+ '\n';
				i++;
			}
		}
		return results;
	}
}
