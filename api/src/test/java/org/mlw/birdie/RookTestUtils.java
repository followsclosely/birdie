package org.mlw.birdie;

public class RookTestUtils {

    private static final Deck deck = DeckFactory.getStandardDeck();

    public static Deck getDeck(){
        return deck;
    }

    public static Card getCard(GameContext context, Card.Suit suit, int number, Integer addToHand){
        Card card = deck.getCards().stream().filter(c -> c.getSuit().equals(suit)).filter(c -> c.getNumber() == number).findFirst().get();
        if( addToHand != null){
            context.getHand().getCards(addToHand).add(card);
        }
        return card;
    }
}
