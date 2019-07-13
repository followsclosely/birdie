package org.mlw.birdie;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class CardTest {

    @Test
    public void testToString() {
        Deck deck = DeckFactory.getStandardDeck();
        for(Card card : deck.getCards())
        {
            Assert.assertNotNull(card.toString());
        }
    }

    @Test
    public void testCompareTo() {

        ArrayList<Card> cards = new ArrayList<>();
        for(int i=0; i<20; i++)
        {
            cards.add(new Card(Card.Suit.Black,i,i,i));
        }

        Collections.shuffle(cards);
        Collections.sort(cards);

        Assert.assertEquals(0, cards.get(0).getNumber());

    }
}