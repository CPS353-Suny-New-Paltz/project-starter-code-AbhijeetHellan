package networkapi;

// Class to hold the characters used to separate the results.
public class Delimiters {
	private char pairSeparator;
	private char keyValueSeparator;

	public Delimiters(char pairSeparator, char keyValueSeparator) {
		this.pairSeparator = pairSeparator;
		this.keyValueSeparator = keyValueSeparator;
	}

	// Default delimiters: ',' for pairs and ':' for key-value
	public static Delimiters createDefault() {
		return new Delimiters(',', ':');
	}

	public char getPairSeparator() {
		return pairSeparator;
	}

	public char getKeyValueSeparator() {
		return keyValueSeparator;
	}
}