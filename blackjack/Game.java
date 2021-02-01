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

	private static Scanner scnr = new Scanner(System.in);

	private static final int MIN_BET = 50;
	private static int numDecks;
	private static ArrayList<Card> playerHand = new ArrayList<>();
	private static ArrayList<Card> dealerHand = new ArrayList<>();
	private static ArrayList<Card> split1 = new ArrayList<>();
	private static ArrayList<Card> split2 = new ArrayList<>();
	private static boolean censorFlag = true;
	private static int coins = 1000;
	private static int bet = 0;
	private static Deck deck;

	private static void calculatePayout(ArrayList<Card> hand, int bet, int pScore, int dScore) {
		if (pScore <= 21) {
			System.out.println();
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
			System.out.println();
		}
	}

	private static void clearAll() {
		playerHand.clear();
		split1.clear();
		split2.clear();
		dealerHand.clear();
	}

	private static void deal(int cards) {
		for (int i = 0; i < cards; i++) {
			playerHand.add(deck.draw());
			dealerHand.add(deck.draw());
		}
	}

	private static void doHand(ArrayList<Card> hand) {
		executePlayerTurn(hand);
		censorFlag = false;
		if (scoreOf(hand) > 21) {
			System.out.println("Bust! You lose...");
		} else if (scoreOf(dealerHand) < 17) {
			executeDealerTurn(hand);
		}
		sleep(1000);
		endRound(hand, bet);
	}

	private static void doInsuranceTick(int bet) {
		if (dealerHand.get(0).id == 1) {
			printHands(playerHand);
			System.out.println();
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
					sleep(1000);
				}
			}
			System.out.println();
			censorFlag = false;
		}
	}

	private static void doRound(int bet) {
		censorFlag = true;
		doInsuranceTick(bet);
		if (scoreOf(dealerHand) != 21) {
			doHand(playerHand);
			if (!split1.isEmpty()) {
				sleep(1000);
				doHand(split1);
			}
			if (!split2.isEmpty()) {
				sleep(1000);
				doHand(split2);
			}
		} else {
			sleep(1000);
			printHands(playerHand);
			System.out.println("Dealer has Blackjack! You lose...");
			System.out.println();
		}
		doShuffleTick();
		clearAll();
	}

	private static void doShuffleTick() {
		if (deck.topCard > deck.size() - 35 || (int) (Math.random() * numDecks * 3) == 1) {
			deck.shuffle();
			System.out.println();
			System.out.println("SHUFFLED!");
			System.out.println();
		}
	}

	private static void doSplitTick(ArrayList<Card> hand, int playerScore) {
		if ((split1.isEmpty() || split2.isEmpty()) && hand.size() == 2) {
			if (hand.get(0).getValue(playerScore) == hand.get(1).getValue(playerScore)) {
				System.out.println();
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
				printHands(hand);
			}
		}
	}

	private static void endGame() {
		System.out.println("You do not have enough coins to continue...");
		System.out.println("The casino thanks you for your patronage. Goodbye.");
		System.out.println();
	}

	private static void endRound(ArrayList<Card> hand, int bet) {
		int pScore = scoreOf(hand);
		int dScore = scoreOf(dealerHand);
		printHands(hand);
		System.out.println("Player Score: " + pScore);
		System.out.println("Dealer Score: " + dScore);
		calculatePayout(hand, bet, pScore, dScore);
		System.out.println();
	}

	private static void executeDealerTurn(ArrayList<Card> hand) {
		printHands(hand);
		System.out.println();
		int dealerScore = scoreOf(dealerHand);
		while (dealerScore < 17) {
			sleep(1000);
			dealerHand.add(deck.draw());
			dealerScore = scoreOf(dealerHand);
			printHands(hand);
			System.out.println();
		}
	}

	private static void executePlayerTurn(ArrayList<Card> hand) {
		printHands(hand);
		boolean stand = false;
		int pScore = scoreOf(hand);
		int dScore = scoreOf(dealerHand);
		while (pScore < 21 && !stand && !(dScore >= 17 && pScore > dScore)) {

			// Split aces may only be hit once
			if (hand.size() == 1 && hand.get(0).id == 1) {
				stand = true;
			}

			doSplitTick(hand, pScore);
			System.out.println();
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
				pScore = scoreOf(hand);
				printHands(hand);
				break;
			case 'S':
				System.out.println();
				stand = true;
				printHands(hand);
				break;
			}
		}
	}

	private static void genDeck() {
		deck = new Deck(numDecks);
	}

	private static void getNumDecks() {
		boolean valid = false;
		while (!valid) {
			try {
				System.out.print("How many decks would you like to play with? ");
				numDecks = Integer.parseInt(scnr.nextLine());
				valid = true;
			} catch (Exception e) {
				System.out.println("Please enter a valid number");
			}
		}
		System.out.println();
	}

	/**
	 * Main game loop
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		startGame();
		while (coins >= MIN_BET) {
			deal(2);
			bet = placeBet();
			doRound(bet);
		}
		endGame();
	}

	private static int placeBet() {
		boolean validInput = false;
		int bet = 0;
		while (!validInput) {
			try {
				System.out.println("Current balance: " + coins);
				System.out.print("Bet: ");
				bet = Integer.parseInt(scnr.next());
				if (bet >= MIN_BET && bet <= coins) {
					validInput = true;
					coins -= bet;
				} else {
					System.out.print("Please enter a valid number");
					System.out.println();
				}
				System.out.println();
			} catch (Exception e) {
				System.out.println("Please enter a number...");
				System.out.println();
			}
		}
		return bet;
	}

	private static void printCensoredHand(ArrayList<Card> hand) {
		System.out.print(hand.get(0));
		System.out.println("##");
		System.out.println();
	}

	private static void printDealerHand() {
		System.out.println("--DEALER--");
		if (dealerHand.get(0).getValue() == 10 || !censorFlag) { // CAUTION
			for (Card c : dealerHand)
				System.out.print(c);
			System.out.println();
			System.out.println("Score: " + scoreOf(dealerHand));
			System.out.println();
		} else {
			printCensoredHand(dealerHand);
		}
	}

	private static void printHands(ArrayList<Card> hand) {
		for (int i = 0; i < 5; i++)
			System.out.println();
		printDealerHand();
		printPlayerHand(hand);
	}

	private static void printPlayerHand(ArrayList<Card> hand) {
		System.out.println("--PLAYER--");
		for (Card c : hand)
			System.out.print(c);
		System.out.println();
		System.out.println("Score: " + scoreOf(hand));
		System.out.println();
	}

	private static int scoreOf(ArrayList<Card> hand) {
		int score = 0;
		for (Card c : hand) {
			score += c.getValue(score);
		}
		return score;
	}

	private static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void startGame() {
		welcome();
		getNumDecks();
		genDeck();
		deck.shuffle();
	}

	private static void welcome() {
		System.out.println();
		System.out.println("Welcome to Blackjack!\nType H to hit, S to stand, or D to double down.");
		System.out.println("The minimum bet is 50 coins. Press CTRL+C to exit.");
		System.out.println("Made by REN 2020-01-30");
		System.out.println();
	}
}
