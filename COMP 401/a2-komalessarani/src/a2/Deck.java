package a2;

public interface Deck {

	// hasHand() indicates whether the deck has enough cards to deal another
	// five card poker hand or not.
	boolean hasHand();

	// dealNextCard() deals the next card from the deck. 
	Card dealNextCard();

	// dealHand() deals the next five cards and returns the poker hand
	// formed by them.
	PokerHand dealHand();

	// findAndRemove(Card c) looks for the Card c among the cards still left to deal.
	// If found, removes the card from the deck by swapping it with the next card to
	// deal and dealing it.
	void findAndRemove(Card c);

}