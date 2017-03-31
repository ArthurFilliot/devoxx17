package bk.devoxx17.emulators;

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
		case STACKED_QUERIES : 	
			return new VulnerabilityEmulatorSQLStackedQueries();
		case SOCIAL_HACK_KEYBOARD :
			return new VulnerabilityEmulatorSQLSocialPostIt();
		case SOCIAL_HACK_HUMAN :
			return new VulnerabilityEmulatorSQLSocialAskaMember();
		case SOCIAL_HACK_KONAMI :
			return new VulnerabilityEmulatorSQLSocialKonamiCode();
		default:
			break;
		}
		return null;
	}
}
