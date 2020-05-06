package a2;

public class DeckImpl implements Deck {

	// Instance fields
	
	private Card[] _cards;			// Array of cards in the deck.
	private int _num_left_to_deal;  // Number of cards still left to deal.
	
	// Constructor creates and initializes a shuffled standard 52 card deck of cards.
	public DeckImpl() {
		_num_left_to_deal = 52;
		_cards = new Card[_num_left_to_deal];

		int cidx = 0;
		// Generate cards
		for (Card.Suit s : Card.Suit.values()) {
			for (int rank = 2; rank <= Card.ACE; rank++) {
				_cards[cidx] = new CardImpl(rank, s);
				cidx += 1;
			}
		}
		
		// Shuffle
		for (int i=0; i<_cards.length; i++) {
			int swap_idx = i + ((int) (Math.random() * (_cards.length - i)));
			Card tmp = _cards[i];
			_cards[i] = _cards[swap_idx];
			_cards[swap_idx] = tmp;
		}		
	}

	// hasHand() indicates whether the deck has enough cards to deal another
	// five card poker hand or not.
	public boolean hasHand() {
		return (_num_left_to_deal >= 5);
	}

	// dealNextCard() deals the next card from the deck. 
	public Card dealNextCard() {
		if (_num_left_to_deal== 0) {
			throw new RuntimeException("No more cards left to deal in deck");
		}
		Card dealt_card = _cards[nextUndealtIndex()];
		_num_left_to_deal -= 1;
		return dealt_card;
	}

	// dealHand() deals the next five cards and returns the poker hand
	// formed by them.
	public PokerHand dealHand() {
		if (!hasHand()) {
			throw new RuntimeException("Deck does not have enough cards to deal another hand");
		}
		
		Card[] hand_cards = new Card[5];
		for (int i=0; i<hand_cards.length; i++) {
			hand_cards[i] = dealNextCard();
		}
		
		return new PokerHandImpl(hand_cards);
	}	

	// findAndRemove(Card c) looks for the Card c among the cards still left to deal.
	// If found, removes the card from the deck by swapping it with the next card to
	// deal and dealing it.
	public void findAndRemove(Card c) {
		if (_num_left_to_deal == 0) {
			return;
		}
		
		for (int i=nextUndealtIndex(); i<52; i++) {
			if (_cards[i].equals(c)) {
				// Found card as undealt. Swap with next card to deal and then
				// deal it to remove.
				Card tmp = _cards[i];
				_cards[i] = _cards[nextUndealtIndex()];
				_cards[nextUndealtIndex()] = tmp;
				dealNextCard();
				return;
			}
		}
		// If we are here, then we did not find the card as one of the cards
		// to still be dealt. Must have already been dealt, so simply return.
		return;
	}

	// nextUndealtIndex() is a private helper functions that calcuates the index
	// of the next undealt card.
	private int nextUndealtIndex() {
		return 52-_num_left_to_deal;
	}
}
