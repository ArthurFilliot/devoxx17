package bk.devoxx17.emulators.databases;

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
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class DatabaseSQL {
	private Connection c;

	public boolean openConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:test.db");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
		return c != null;
	}

	public boolean closeConnection() {
		try {
			c.close();
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return c == null;
	}
	
	public void openTransaction() {
		try {
			c.setAutoCommit(false);
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
	
	public void rollbackTransaction() {
		try {
			c.rollback();
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public Statement createStatement() {
		Statement stmt = null;
		try {
			stmt = c.createStatement();
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return stmt;
	}

	public String getScript(String resourcePath) {
		String script = Strings.EMPTY;
		try {
			URL file = this.getClass().getResource(resourcePath);
			System.out.println("open script file:" + file);
			BufferedInputStream b;

			b = new BufferedInputStream(file.openStream());

			byte[] contents = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = b.read(contents)) != -1) {
				script += new String(contents, 0, bytesRead);
			}
		} catch (IOException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return script;
	}

	public Integer executeScript(String script) {
		Integer affectedRows = null;
		try {
			Statement stmt = createStatement();
			affectedRows = stmt.executeUpdate(script);
			stmt.close();
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("affectedRows:" + affectedRows);
		return affectedRows;
	}

	public Multimap<String, Object> executeSelection(String select) {
		Multimap<String, Object> m = null;
		try {
			m = ArrayListMultimap.create();
			Set<String> columnLabels = Sets.newHashSet();

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
			
			System.out.println("Select Result");
			String columns = Strings.EMPTY;
			char sep = '|';
			for (String columnLabel : columnLabels) {
				columns += sep + columnLabel;
			}
			System.out.println(columns + sep);
			if (m.size() > 0) {
				List<List<Object>> cells = Lists.newArrayList();
				for (String column : m.keySet()) {
					List<Object> columnCells = Lists.newArrayList();
					columnCells.addAll(m.get(column));
					cells.add(columnCells);
				}
				for (int i = 0; i < cells.get(0).size(); i++) {
					String row = Strings.EMPTY;
					for (int j = 0; j < cells.size(); j++) {
						row += sep + (String) cells.get(j).get(i);
					}
					System.out.println(row + sep);
				}
			}
		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return m;
	}
}
