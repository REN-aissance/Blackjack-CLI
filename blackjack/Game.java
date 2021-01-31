package blackjack;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {

	/*
	 * Blackjack for Terminal
	 * 
	 * Made by REN on January 30, 2021
	 * 
	 * This code is a disaster, but it works.
	 */

	public static Scanner scnr = new Scanner(System.in);
	public static final int MIN_BET = 50;

	public static int numDecks;
	public static ArrayList<Card> playerHand = new ArrayList<Card>();
	public static ArrayList<Card> dealerHand = new ArrayList<Card>();
	public static ArrayList<Card> split1 = new ArrayList<Card>();
	public static ArrayList<Card> split2 = new ArrayList<Card>();
	public static int coins = 1000;
	public static int bet = 0;

	public static Deck deck;

	public static void welcome() {
		System.out.println();
		System.out.println("Welcome to Blackjack!\nType H to hit, S to stand, or D to double down.");
		System.out.println("The minimum bet is 50 coins. Press CTRL+C to exit.");
		System.out.println("Made by REN 2020-01-30");
		System.out.println();
	}

	public static void getNumDecks() {
		boolean valid = false;
		while (!valid) {
			try {
				System.out.print("How many decks would you like to play with? ");
				numDecks = Integer.parseInt(scnr.nextLine());
				valid = true;
			} catch (Exception e) {
				System.out.println("Please enter a valid number");
				System.out.println();
			}
		}
		System.out.println();
	}

	public static void genDeck() {
		deck = new Deck(numDecks);
	}

	public static void startGame() {
		welcome();
		getNumDecks();
		genDeck();
		deck.shuffle();
	}

	public static int placeBet() {
		boolean valid = false;
		int bet = -1;
		while (!valid) {
			try {
				System.out.println("Current balance: " + coins);
				System.out.print("Bet: ");
				bet = Integer.parseInt(scnr.next());
				valid = true;
				while (!(bet >= MIN_BET && bet <= coins)) {
					System.out.print("Please enter a valid number: ");
					bet = scnr.nextInt();
				}
				System.out.println();
				coins -= bet;
				return bet;
			} catch (Exception e) {
				System.out.println("Please enter a valid number");
				System.out.println();
			}
		}
		return bet;
	}

	public static void deal() {
		playerHand.add(deck.draw());
		dealerHand.add(deck.draw());
		playerHand.add(deck.draw());
		dealerHand.add(deck.draw());
	}

	public static int scoreOf(ArrayList<Card> hand) {
		int score = 0;
		for (Card c : hand) {
			score += c.getValue(score);
		}
		return score;
	}

	public static void printHand(ArrayList<Card> hand) {
		for (Card c : hand) {
			c.print();
		}
		System.out.println();
		System.out.println("Score: " + scoreOf(hand) + "\n");
	}

	public static void printCensoredHand(ArrayList<Card> hand) {
		hand.get(0).print();
		System.out.println("##");
		System.out.println();
	}

	public static void executeDealerTurn() {
		System.out.println("--DEALER--");
		printHand(dealerHand);
		int dealerScore = scoreOf(dealerHand);
		while (dealerScore < 17) {

			// Improves readability
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			dealerHand.add(deck.draw());
			dealerScore = scoreOf(dealerHand);
			System.out.println("--DEALER--");
			printHand(dealerHand);
		}
	}

	public static void executePlayerTurn(ArrayList<Card> hand) {
		System.out.println("--PLAYER--");
		printHand(hand);
		boolean stand = false;
		int playerScore = scoreOf(hand);
		while (playerScore < 21 && !stand) {

			// Special case for split Aces
			if (hand.size() == 1 && hand.get(0).id == 1) {
				stand = true;
			}

			doSplit(hand, playerScore);
			System.out.print("(H/S/D): ");
			char command = scnr.next().charAt(0);
			switch (command) {
			case 'D':
				if (coins < bet * 2) {
					System.out.println("You cannot afford to double down...");
					break;
				} else {
					coins -= bet;
					bet *= 2;
					stand = true;
				} // CAUTION! Do not add break
			case 'H':
				System.out.println();
				hand.add(deck.draw());
				playerScore = scoreOf(hand);
				System.out.println("--PLAYER--");
				printHand(hand);
				break;
			case 'S':
				System.out.println();
				stand = true;
				break;
			}
		}
	}

	private static void doSplit(ArrayList<Card> hand, int playerScore) {
		if ((split1.isEmpty() || split2.isEmpty()) && hand.size() == 2) {
			if (hand.get(0).getValue(playerScore) == hand.get(1).getValue(playerScore)) {
				System.out.print("Would you like to split? (Y/N): ");
				char command = scnr.next().charAt(0);
				System.out.println();
				if (command == 'Y' && split1.isEmpty()) {
					coins -= bet;
					Card c = hand.get(1);
					split1.add(c);
					hand.remove(1);
				} else if (command == 'Y' && split2.isEmpty()) {
					coins -= bet;
					Card c = hand.get(1);
					split2.add(c);
					hand.remove(1);
				}
				printHand(hand);
			}
		}
	}

	public static void endRound(ArrayList<Card> hand, int bet) {
		int pScore = scoreOf(hand);
		int dScore = scoreOf(dealerHand);
		System.out.println("Player Score: " + pScore);
		System.out.println("Dealer Score: " + dScore);
		calculatePayout(hand, bet, pScore, dScore);
		System.out.println();
	}

	private static void calculatePayout(ArrayList<Card> hand, int bet, int pScore, int dScore) {
		if (pScore <= 21) {
			if (pScore == 21 && dScore != 21 && hand.size() == 2) {
				System.out.println("Blackjack! You win! Payout: " + bet * 3);
				coins += bet * 3;
			} else if (dScore > 21) {
				System.out.println("Dealer bust! You win! Payout: " + bet * 2);
				coins += bet * 2;
			} else if (pScore > dScore) {
				System.out.println("You win! Payout: " + bet * 2);
				coins += bet * 2;
			} else if (pScore == dScore) {
				System.out.println("Push! Your bet has been returned");
				coins += bet;
			} else {
				if (dScore == 21 && dealerHand.size() == 2)
					System.out.print("Dealer Blackjack! ");
				System.out.println("You lost...");
			}
		}
	}

	public static void doInsurance(int bet) {
		if (coins < bet / 2) {
			System.out.println("You cannot afford insurance...");
		} else {
			boolean dBlackjack = dealerHand.get(1).getValue(11) == 10;
			System.out.print("Would you like insurance? (Y/N): ");
			if (scnr.next().charAt(0) == 'Y') {
				System.out.println();
				if (dBlackjack) {
					System.out.println("Dealer has Blackjack! Insurance payout: " + bet);
					coins += bet;
				} else {
					System.out.println("Dealer does not have Blackjack. You lost " + bet / 2 + " coins...");
					coins -= bet / 2;
				}
			}
		}
		System.out.println();
		printHand(dealerHand);
	}

	public static void doHand(ArrayList<Card> hand, boolean split) {
		executePlayerTurn(hand);
		if (scoreOf(hand) > 21) {
			System.out.println("Bust! You lose...");
			System.out.println();
		}
		if (scoreOf(hand) <= 21 || !split1.isEmpty() || !split2.isEmpty()) {
			executeDealerTurn();
			endRound(hand, bet);
		}
	}

	public static void doRound(int bet) {
		System.out.println("--DEALER--");
		if (dealerHand.get(0).getValue() == 10) { // CAUTION
			printHand(dealerHand);
		} else {
			printCensoredHand(dealerHand);
		}

		// Check for insurance
		if (dealerHand.get(0).id == 1) {
			doInsurance(bet);
		}
		if (scoreOf(dealerHand) != 21) {
			doHand(playerHand, false);
			if (!split1.isEmpty())
				doHand(split1, true);
			if (!split2.isEmpty())
				doHand(split2, true);
		} else {
			System.out.println("Dealer has Blackjack! You lose...");
		}

		// Check shuffle
		if (deck.topCard > deck.cards.length - 35 || (int) (Math.random() * numDecks * 2) == 1) {
			deck.shuffle();
			System.out.println("SHUFFLED!\n");
		}

		// Finish up
		playerHand.clear();
		split1.clear();
		split2.clear();
		dealerHand.clear();
	}

	public static void main(String[] args) {
		startGame();
		while (coins >= MIN_BET) {
			bet = placeBet();
			deal();
			doRound(bet);
		}
		System.out.println("You do not have enough coins to continue...");
		System.out.println("The casino thanks you for your patronage. Goodbye.\n");
	}
}
