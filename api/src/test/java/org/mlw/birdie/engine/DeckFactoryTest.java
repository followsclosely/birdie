package org.mlw.birdie.engine;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DeckFactoryTest {

    @Test
    public void getStandardDeckOf57Cards() {
        Deck deck = DeckFactory.getStandardDeck();
        List<Card> cards = deck.getCards();
        Assert.assertEquals(57, cards.size());
    }

    @Test
    public void getStandardDeckOHasRook() {
        Deck deck = DeckFactory.getStandardDeck();
        List<Card> cards = deck.getCards();
        Assert.assertEquals(57, cards.size());

        int count = 0;
        for (Card card : cards) {
            if (card.getSuit() == null) {
                count++;
            }
        }
        Assert.assertEquals("Should be 1 rook card in the deck.",1, count);
    }

    @Test
    public void getStandardDeckWith14CardsPerSuit() {
        Deck deck = DeckFactory.getStandardDeck();
        List<Card> cards = deck.getCards();
        Assert.assertEquals(57, cards.size());

        for(Card.Suit suit : Card.Suit.values()) {
            int count = 0;
            for (Card card : cards) {
                if (Card.Suit.Black.equals(card.getSuit())) {
                    count++;
                }
            }
            Assert.assertEquals("Should be 14 cards in the " + suit + " suit.",14, count);
        }
    }
}