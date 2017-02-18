package bk.devoxx17.front;

import bk.devoxx17.emulators.VulnerabilityEmulator;
import bk.devoxx17.emulators.VulnerabilityEmulatorSQLClassical;
import bk.devoxx17.emulators.VulnerabilityEmulatorSQLUnionExploit;
import bk.devoxx17.emulators.databases.DatabaseSQL;

public class Dispatcher {

	private static DatabaseSQL db = new DatabaseSQL();
	
	public static void setDb(DatabaseSQL db) {
		Dispatcher.db = db;
	}

	/**
	 * Dispatch given login/password
	 * to the current injectionMethodToFind
	 **/
	public static boolean check(String login, String password) {
		VulnerabilityEmulator emulator=selectEmulator(ApplicationScope.getInstance().getMethodToFind());
		return emulator.check(login, password);
	}
	
	public static VulnerabilityEmulator selectEmulator(InjectionMethod method) {
		switch (method) {
		case CLASSICAL : 		return new VulnerabilityEmulatorSQLClassical(db);
		case UNION_EXPLOIT : 	return new VulnerabilityEmulatorSQLUnionExploit(db);
		}
		return null;
	}
}
