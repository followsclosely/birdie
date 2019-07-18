package org.mlw.birdie.engine.event;

import org.mlw.birdie.Hand;

public class HandDealtEvent {
    private Hand hand;

    public HandDealtEvent(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() { return hand; }
}
