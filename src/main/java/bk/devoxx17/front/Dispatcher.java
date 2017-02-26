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
		case SOCIAL_HACK_HUMAN:
			break;
		case SOCIAL_HACK_KEYBOARD:
			break;
		case STACKED_QUERIES:
			break;
		default:
			break;
		}
		return null;
	}
}
