package bk.devoxx17.front;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;

import bk.devoxx17.emulators.VulnerabilityEmulator;
import bk.devoxx17.emulators.VulnerabilityEmulatorSQL;
import bk.devoxx17.emulators.databases.DatabaseSQL;
import bk.devoxx17.utils.PasswordGenerator;

public class Front {

	private static Logger log = Logger.getLogger(Front.class);
	
	private static DatabaseSQL db = new DatabaseSQL();
	static {
		initDb();
	}
	public static void initDb() {
		db.openConnection();
		String createSchema = db.getScript("/sql/schema.sql");
		String insertUsers = db.getScript("/sql/users.sql");
		db.executeScript(createSchema);
		db.executeScript(insertUsers);
		try {
			ArrayListMultimap<String, String> result = db.executeSelection("SELECT ID FROM Users");
			for (String id : result.get("ID")) {
				db.executeScript("UPDATE Users SET password='" + PasswordGenerator.nextPassword() + "' WHERE id="+id+";");
			}
		}catch(SQLException e) {
			System.exit(0);
		}
	}
	public static void terminateDb() {
		db.closeConnection();
	}
	public static void setDb(DatabaseSQL db) {
		Front.db.closeConnection();
		Front.db = db;
		initDb();
	}
	public static void init() {
		log.info("Dispatcher init:");
	}
	
	/**
	 * Dispatch given login/password
	 * to the current injectionMethodToFind
	 **/
	public static boolean check(String login, String password) {
		VulnerabilityEmulator emulator=Dispatcher.selectEmulator(ApplicationScope.getInstance().getMethodToFind());
		if (emulator instanceof VulnerabilityEmulatorSQL) {
			((VulnerabilityEmulatorSQL) emulator).setDb(db);
		}
		return emulator.check(login, password);
	}
	
}
