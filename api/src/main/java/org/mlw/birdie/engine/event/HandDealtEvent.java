package org.mlw.birdie.engine.event;

import org.mlw.birdie.Card;
import org.mlw.birdie.Hand;

import java.util.List;

public class HandDealtEvent {
    private Hand hand;
    private List<Card> cards;

    public HandDealtEvent(Hand hand, List<Card> cards) {
        this.hand = hand;
        this.cards = cards;
    }

    public Hand getHand() { return hand; }
    public List<Card> getCards() { return cards; }
}
