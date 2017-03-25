package bk.devoxx17.front;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;

import bk.devoxx17.emulators.CompromisedLogin;
import bk.devoxx17.emulators.VulnerabilityEmulator;
import bk.devoxx17.emulators.VulnerabilityEmulatorSQL;
import bk.devoxx17.emulators.VulnerabilityEmulatorSQLSocial;
import bk.devoxx17.emulators.databases.DatabaseSQL;
import bk.devoxx17.utils.PasswordGenerator;

public class Front {

	private static Logger log = Logger.getLogger(Front.class);

	private static DatabaseSQL db = new DatabaseSQL();
	static {
		try {
			initDb();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void initDb() throws SQLException {
		db.openConnection();
		String createSchema = db.getScript("/sql/schema.sql");
		String insertUsers = db.getScript("/sql/users.sql");
		db.executeScript(createSchema);
		db.executeScript(insertUsers);
		try {
			ArrayListMultimap<String, String> result = db
					.executeSelection("SELECT ID FROM Users WHERE login in ('root','lambda','Konami')");
			for (String id : result.get("ID")) {
				db.executeScript(
						"UPDATE Users SET password='" + PasswordGenerator.nextPassword().replace('\'', '_').replace(';','_') + "' WHERE id=" + id + ";");
			}
		} catch (SQLException e) {
			System.exit(0);
		}
	}

	public static void terminateDb() {
		db.closeConnection();
	}

	public static void setDb(DatabaseSQL db) throws SQLException {
		Front.db.closeConnection();
		Front.db = db;
		initDb();
	}

	public static void init() {
		log.info("Dispatcher init:");
	}

	/**
	 * Dispatch given login/password to the current injectionMethodToFind
	 **/
	public static boolean check(String login, String password) {
		List<InjectionMethod> methodsToFind = ApplicationScope.getInstance().getMethodsToFind();
		Integer score = ApplicationScope.getInstance().getScore();
		Iterator<InjectionMethod> it = methodsToFind.iterator();
		List<InjectionMethod> foundMethods = Lists.newArrayList();
		if (Lists.newArrayList("root", "postIt", "Konami").contains(login)) {

		} else {
			while (it.hasNext()) {
				InjectionMethod method = it.next();

				VulnerabilityEmulator emulator = Dispatcher.selectEmulator(method);
				if (emulator instanceof VulnerabilityEmulatorSQL) {
					((VulnerabilityEmulatorSQL) emulator).setDb(db);
				}
				if (EnumUtils.isValidEnum(CompromisedLogin.class, login)) {
					if (emulator instanceof VulnerabilityEmulatorSQLSocial) {
						if (emulator.doCheck(login, password)) {
							score += method.getScore();
							methodsToFind.remove(method);
							foundMethods.add(method);
						}
					}
				} else {
					if (emulator.doCheck(login, password)) {
						score += method.getScore();
						methodsToFind.remove(method);
						foundMethods.add(method);
					}
				}
			}
		}
		if (foundMethods.size() > 0) {
			String message = "Access granted ! (You have found method : ";
			String sep = "";
			for (InjectionMethod method : foundMethods) {
				message += sep + method.getLabel();
				sep = ", ";
			}
			message += ")";
			ApplicationScope.getInstance().setFoundMethodMessage(message);
			return true;
		}
		return false;
	}

	public static String getKonamiCode() {
		String pwd = "";
		try {
			ArrayListMultimap<String, String> result = db
					.executeSelection("SELECT password FROM Users WHERE login='Konami'");
			pwd = result.get("PASSWORD").get(0);
		} catch (Exception e) {
		}
		return pwd;
	}

}
