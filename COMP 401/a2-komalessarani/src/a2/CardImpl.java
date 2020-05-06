package a2;

public class CardImpl implements Card {
	
	private int rank;
	private Card.Suit suit;
	
	public CardImpl(int rank, Card.Suit suit) {
		this.rank = rank;
		this.suit = suit;
		if(rank < 2 || rank > 14)
			throw new RuntimeException("Rank is out of range");
	}

	public int getRank() {
		return rank;
	}

	public Suit getSuit() {
		return suit;
	}
	
	public String toString() {
		String name = "";
		switch(rank) {
		case 2: name = "Two";
		case 3: name = "Three";
		case 4: name = "Four";
		case 5: name = "Five";
		case 6: name = "Six";
		case 7: name = "Seven";
		case 8: name = "Eight";
		case 9: name = "Nine";
		case 10: name = "Ten";
		case 11: name = "Jack";
		case 12: name = "Queen";
		case 13: name = "King";
		case 14: name = "Ace";
		}
		
		name += " of ";
		
		switch(suit) {
		case SPADES: name += "Spades"; 
		case HEARTS: name += "Hearts"; 
		case DIAMONDS: name += "Diamonds"; 
		case CLUBS: name += "Clubs"; 
		}
		
		return name;
	}
	
	public boolean equals(Card other) {
		if(rank == (other.getRank()) && suit.equals(other.getSuit()))
			return true;
		else
			return false;
	}

}
