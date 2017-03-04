package bk.devoxx17.emulators.databases;

import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDatabaseSQL {
	
	private static DatabaseSQL db = new DatabaseSQL();
	
	@Test
	public void testGetScript() {
		assertNotNull(db.getScript("/sql/schema.sql"));
	}
	
	@Test
	public void testExecuteScript() {
		db.openConnection();
		db.openTransaction();
		assertNotNull(db.executeScript("DROP TABLE IF EXISTS Users;"
				+ "CREATE TABLE Users (\r\n" + 
				"	ID 			INT 	PRIMARY KEY	NOT NULL,\r\n" + 
				"    LOGIN   	TEXT    NOT NULL,\r\n" + 
				"    PASSWORD    INT		NOT NULL\r\n" + 
				");"));
		db.rollbackTransaction();
		db.closeConnection();
	}
	
	@Test
	public void testExecuteSelection() throws SQLException {
		db.openConnection();
		db.openTransaction();
		String createSchema = db.getScript("/sql/schema.sql");
		String insertUsers = db.getScript("/sql/users.sql");
		db.executeScript(createSchema);
		db.executeScript(insertUsers);
		assertNotNull(db.executeSelection("SELECT * FROM Users"));
		db.rollbackTransaction();
		db.closeConnection();
	}
	
	@Test
	public void testInitializationScript() throws SQLException {
		String createSchema = db.getScript("/sql/schema.sql");
		String insertUsers = db.getScript("/sql/users.sql");
		db.openConnection();
		db.executeScript(createSchema);
		db.executeScript(insertUsers);
		assertNotNull(db.executeSelection("SELECT * FROM Users"));
		db.executeScript(createSchema);
		db.executeScript(insertUsers);
		assertNotNull(db.executeSelection("SELECT * FROM Users"));
		db.closeConnection();
	}
}
