package org.mlw.birdie.engine;

import org.junit.Assert;
import org.junit.Test;

public class DeckTest {

    @Test
    public void getCards() {
        Deck deck = DeckFactory.getStandardDeck();
        Assert.assertNotNull(deck.getCards());
    }

    @Test
    public void shuffle() {
        Deck deck = DeckFactory.getStandardDeck();
        int numberOfCards = deck.getCards().size();
        Card[] cardsPreShuffle = deck.getCards().toArray(new Card[numberOfCards]);
        deck.shuffle();
        Card[] cardsPostShuffle = deck.getCards().toArray(new Card[numberOfCards]);

        boolean sameOrder = true;
        for(int i=0; i<numberOfCards && sameOrder; i++){
            sameOrder = cardsPreShuffle[i] == cardsPostShuffle[i];
        }

        Assert.assertFalse(sameOrder);
    }
}