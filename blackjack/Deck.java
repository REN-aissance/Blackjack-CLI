package blackjack;

public class Deck {

	private Card[] cards;
	public int topCard = 0;

	Deck(int d) {
		cards = new Card[52 * d];
		for (int i = 0; i < cards.length; i++) {
			cards[i] = new Card((i % 13) + 1, (i % 52) / 13);
		}
	}
	
	private void swap(Card[] arr, int i, int j) {
		Card tmp = arr[i];
		arr[i] = arr[j];
		arr[j] = tmp;
	}
	
	public void shuffle() {
		topCard = 0;
		for (int i = cards.length - 1; i > 0; i--) {
			swap(cards, i, (int)(Math.random() * cards.length));
		}
	}
	
	public Card draw() {
		topCard++;
		return cards[topCard];
	}
	
	public int size() {
		return cards.length;
	}
	
	/**
	 * Debug only
	 */
	public void print() {
		for (Card c : cards) {
			System.out.print(c);
		}
	}
}
