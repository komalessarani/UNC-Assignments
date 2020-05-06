package a2;

import java.lang.reflect.Array;
import java.util.Arrays;

public class PokerHandImpl implements PokerHand {

	private Card[] cards = new Card[5];
	private int[] rank = new int[cards.length];
	private int[] rankCounter = new int[15];
	private int handRank = 0;
	private int handValue = 0;
	private int handRankSet = 0;

	public PokerHandImpl(Card[] cards) {
		if (this.cards == null)
			throw new RuntimeException("Null values passed into array");
		this.cards = cards.clone();
		sortRankArray();
		counter();
		callHandRankAndValue();
	}

	public Card[] getCards() {
		return cards.clone();
	}

	public boolean contains(Card c) {
		for (int i = 0; i < cards.length; i++)
			if (cards[i].equals(c))
				return true;
		return false;
	}

	private void sortRankArray() {
		for (int i = 0; i < cards.length; i++) {
			rank[i] = cards[i].getRank();
		}
		Arrays.sort(rank);
	}

	private void counter() {
		for (int i = 0; i < rankCounter.length; i++) {
			rankCounter[i] = 0;
		}
		for (int i = 0; i < rank.length; i++) {
			rankCounter[rank[i]] += 1;
		}
	}

	public boolean isHighCard() {
		if (!isOnePair() && !isTwoPair() && !isStraight() && !isThreeOfAKind() && !isFlush() && !isFullHouse()
				&& !isFourOfAKind() && !isStraightFlush()) {
			handValue = 1;
			handRank = rank[4];
			return true;
		} else
			return false;
	}

	public boolean isOnePair() {
		int num = 0;
		for (int i = 0; i < rankCounter.length; i++) {
			if (rankCounter[i] == 2) {
				num++;
				handRankSet = rank[0];
			}
			if (rankCounter[i] == 3)
				return false;
		}
		if (num == 1) {
			handRank = handRankSet;
			handValue = 2;
		}
		return (num == 1);
	}

	public boolean isTwoPair() {
		int num = 0;
		for (int i : rankCounter) {
			if (rankCounter[i] == 2) {
				num++;
				handRankSet = rank[i];
			}
		}
		if (num == 2) {
			handRank = handRankSet;
			handValue = 3;
		}
		return (num == 2);
	}

	public boolean isThreeOfAKind() {
		int num = 0;
		for (int i = 0; i < rankCounter.length; i++) {
			if (rankCounter[i] == 3) {
				num++;
				handRankSet = i;
			}
			if (rankCounter[i] == 2)
				return false;
		}
		if (num == 1) {
			handValue = 4;
			handRank = handRankSet;
		}
		return (num == 1);
	}

	public boolean isStraight() {
		if (rank[0] == 2 && rank[1] == 3 && rank[2] == 4 && rank[3] == 5 && rank[4] == 14) {
			rank[0] = 1;
			rank[1] = 2;
			rank[2] = 3;
			rank[3] = 4;
			rank[4] = 5;
			handValue = 5;
			handRank = 5;
			return true;
		} else if (rank[0] + 1 == rank[1])
			if (rank[1] + 1 == rank[2])
				if (rank[2] + 1 == rank[3])
					if (rank[3] + 1 == rank[4]) {
						handValue = 5;
						handRank = rank[4];
						return true;
					}
		return false;
	}

	public boolean isFlush() {
		if (cards[0].getSuit().equals(cards[1].getSuit()))
			if (cards[1].getSuit().equals(cards[2].getSuit()))
				if (cards[2].getSuit().equals(cards[3].getSuit()))
					if (cards[3].getSuit().equals(cards[4].getSuit())) {
						handValue = 6;
						handRank = rank[4];
						return true;
					}
		return false;
	}

	public boolean isFullHouse() {
		int pair = 0, triple = 0;
		for (int i = 0; i < rankCounter.length; i++) {
			if (rankCounter[i] == 2)
				pair++;
			if (rankCounter[i] == 3) {
				handRankSet = i;
				triple++;
			}
		}
		if (pair == 1 && triple == 1) {
			handValue = 7;
			handRank = handRankSet;
		}
		return (pair == 1 && triple == 1);
	}

	public boolean isFourOfAKind() {
		int num = 0;
		for (int i = 0; i < rankCounter.length; i++) {
			if (rankCounter[i] == 4) {
				handRankSet = i;
				num++;
			}
		}
		if (num == 1) {
			handValue = 8;
			handRank = handRankSet;
		}
		return (num == 1);
	}

	public boolean isStraightFlush() {
		if (isStraight() && isFlush()) {
			handValue = 9;
			return true;
		} else
			return false;
	}

	private void callHandRankAndValue() {
		isHighCard();
		isOnePair();	
		isTwoPair();
		isThreeOfAKind();
		isStraight();
		isFlush();
		isFullHouse();
		isFourOfAKind();
		isStraightFlush();
	}

	public int getHandTypeValue() {
		// get hand value
		return handValue;
	}

	public int getHandRank() {
		return handRank;
	}

	public int compareTo(PokerHand other) {
		// TODO Auto-generated method stub
		if (this.getHandTypeValue() < other.getHandTypeValue())
			return -1;
		if (this.getHandTypeValue() > other.getHandTypeValue())
			return 1;
		else {
			if (this.getHandRank() < other.getHandRank())
				return -1;
			if (this.getHandRank() > other.getHandRank())
				return 1;
			else
				return 0;

		}
	}

}
