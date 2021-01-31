package blackjack;

public class Card {

	public static String[] suits = { "♥", "♠", "♣", "♦" };

	public int id;
	public String suit;

	Card(int i, int s) {
		id = i;
		suit = suits[s];
	}

	public int getValue(int hand) {
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

	public void print() {
		String value = "INVALID";
		if (id >= 2 && id <= 10) {
			value = Integer.toString(id);
		} else {
			switch (id) {
			case 1:
				value = "A";
				break;
			case 11:
				value = "J";
				break;
			case 12:
				value = "Q";
				break;
			case 13:
				value = "K";
				break;
			}
		}
		System.out.print(value + suit + " ");
	}
}
