package blackjack;

public class Card {

	private static String[] suits = { "♥", "♠", "♣", "♦" };

	public int id;
	private String suit;

	Card(int i, int s) {
		id = i;
		suit = suits[s];
	}

	public int getValue(int hand) { //requires previous hand score for ace calc.
		if (hand <= 10 && id == 1) {
			return 11;
		} else if (id >= 1 && id <= 10) {
			return id;
		} else {
			return 10;
		}
	}
	
	public int getValue() {
		if (id >= 1 && id <= 10) {
			return id;
		} else {
			return 10;
		}
	}

	@Override
	public String toString() {
		String name = null;
		name = getNameFromID(name);
		return (name + suit + " ");
	}

	private String getNameFromID(String name) {
		if (id >= 2 && id <= 10) {
			name = Integer.toString(id);
		} else {
			switch (id) {
			case 1:
				name = "A";
				break;
			case 11:
				name = "J";
				break;
			case 12:
				name = "Q";
				break;
			case 13:
				name = "K";
				break;
			}
		}
		return name;
	}
}
