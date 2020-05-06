package a2;

import java.util.Scanner;

public class HandEvaluator {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		while(true) {
			int numWin = 0;
			double percent = 0.0;
			int numOpp = scanner.nextInt();
			if(numOpp == 0)
				break;
			if(numOpp < 1 || numOpp > 9)
				throw new RuntimeException("Invalid number of opponents");
			Card[] newCard = new CardImpl[5];
			for(int i = 0; i < newCard.length; i++) {
				newCard[i] = new CardImpl(scanner.nextInt(), suitToString(scanner.next()));
			}
			PokerHand hand = new PokerHandImpl(newCard);	
			for(int j = 0; j < 10000; j++) {
				numWin += getTalliedWin(hand, numOpp);
			}
			percent = (((double)numWin*100)/10000) + 0.5;
			System.out.println((int)percent);
		}
		scanner.close();
	}

	public static Card.Suit suitToString(String suitString) {
		switch (suitString) {
		case "S":
			return Card.Suit.SPADES;
		case "H":
			return Card.Suit.HEARTS;
		case "D":
			return Card.Suit.DIAMONDS;
		case "C":
			return Card.Suit.CLUBS;
		}
		return null;
	}
	
	public static int getTalliedWin(PokerHand yourHand, int nOpp) {
		int win = 0;
		Deck deckOfCards = new DeckImpl();
		for(int i = 0; i < 5; i++) {
			deckOfCards.findAndRemove(yourHand.getCards()[i]);
		}
		for(int j = 0; j < nOpp; j++) {
			PokerHand oppHand = (deckOfCards.dealHand());
			if(yourHand.compareTo(oppHand)==1)
				win++;
		}
		if(win == nOpp)
			return 1;
		else 
			return 0;
	}
}