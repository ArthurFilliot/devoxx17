package bk.devoxx17.front;

import bk.devoxx17.emulators.VulnerabilityEmulator;
import bk.devoxx17.emulators.VulnerabilityEmulatorClassical;
import bk.devoxx17.emulators.VulnerabilityEmulatorUnionExploit;

public class Dispatcher {

	/**
	 * Dispatch given login/password
	 * to the current injectionMethodToFind
	 **/
	public static boolean check(String login, String password) {
		VulnerabilityEmulator emulator=null;
		switch (ApplicationScope.getInstance().getMethodToFind()) {
		case CLASSICAL : 		emulator = new VulnerabilityEmulatorClassical();
		case UNION_EXPLOIT : 	emulator = new VulnerabilityEmulatorUnionExploit();
		}
		return emulator.check(login, password);
	}
}
