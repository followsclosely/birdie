package org.mlw.birdie.engine.event;

import org.mlw.birdie.Hand;

public class CardPlayedEvent {
    private Hand hand;

    public CardPlayedEvent(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() { return hand; }
}
