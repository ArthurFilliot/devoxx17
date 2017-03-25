package bk.devoxx17.front;

public enum InjectionMethod {
	SOCIAL_HACK_KEYBOARD("Social Hack Keyboard",1),
	SOCIAL_HACK_HUMAN("Social Hack Human",1),
	SOCIAL_HACK_KONAMI("Social Hack Konami",1),
	CLASSICAL("Very Classical",2), 
	CLASSICAL_DELUXE("Classical",5),
	UNION_EXPLOIT("Union Exploit (Classical)",10), 
	STACKED_QUERIES("Stacked Queries",15),
	UNION_EXPLOIT_DELUXE("Union Exploit (Deluxe)",20);
	
	private final String label;
	private final int score;
	private InjectionMethod(String label, int score) {
		this.label = label;
		this.score = score;
	}
	public String getLabel() {
		return label;
	}
	public int getScore() {
		return score;
	}
}
