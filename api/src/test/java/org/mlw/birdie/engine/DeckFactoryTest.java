package org.mlw.birdie.engine;

import org.junit.Assert;
import org.junit.Test;
import org.mlw.birdie.Card;
import org.mlw.birdie.Deck;
import org.mlw.birdie.DeckFactory;

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
            if (card.getNumber() == 0) {
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