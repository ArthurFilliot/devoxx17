package bk.devoxx17.emulators.databases;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDatabaseSQL {
	
	private static DatabaseSQL db = new DatabaseSQL();
	
	@Before
	public void init() { 
		db.openConnection();
		db.openTransaction();
	}
	 
	@After
	public void terminate() {
		db.rollbackTransaction();
		db.closeConnection();
	}
	
	@Test
	public void testGetScript() {
		assertNotNull(db.getScript("/sql/schema.sql"));
	}
	
	@Test
	public void testExecuteScript() {
		assertNotNull(db.executeScript("CREATE TABLE Users (\r\n" + 
				"	ID 			INT 	PRIMARY KEY	NOT NULL,\r\n" + 
				"    LOGIN   	TEXT    NOT NULL,\r\n" + 
				"    PASSWORD    INT		NOT NULL\r\n" + 
				");"));
	}
	
	@Test
	public void testExecuteSelection() {
		String createSchema = db.getScript("/sql/schema.sql");
		String insertUsers = db.getScript("/sql/users.sql");
		db.executeScript(createSchema);
		db.executeScript(insertUsers);
		assertNotNull(db.executeSelection("SELECT * FROM Users"));
	}
}
