package bk.devoxx17.front;

import bk.devoxx17.emulators.*;

public class Dispatcher {

	public static VulnerabilityEmulator selectEmulator(InjectionMethod method) {
		switch (method) {
		case CLASSICAL : 
			return new VulnerabilityEmulatorSQLClassical();
		case CLASSICAL_DELUXE : 		
			return new VulnerabilityEmulatorSQLClassicalDeluxe();
		case UNION_EXPLOIT : 	
			return new VulnerabilityEmulatorSQLUnionExploit();
		case UNION_EXPLOIT_DELUXE : 	
			return new VulnerabilityEmulatorSQLUnionExploitDeluxe();
		default:
			break;
		}
		return null;
	}
}
