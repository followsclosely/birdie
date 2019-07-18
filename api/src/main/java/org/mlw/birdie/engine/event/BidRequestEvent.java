package org.mlw.birdie.engine.event;

import org.mlw.birdie.Hand;

public class BidRequestEvent extends BasicEvent {
    private Hand hand;

    public BidRequestEvent(Object source, Hand hand, Integer seat) {
        super(source, seat);
        this.hand = hand;
    }

    public Hand getHand() { return hand; }
}