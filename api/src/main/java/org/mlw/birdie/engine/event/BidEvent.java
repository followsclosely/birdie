package org.mlw.birdie.engine.event;

import org.mlw.birdie.Bid;

public class BidEvent extends BasicEvent {
    private Bid bid;
    public BidEvent(Object source, Bid bid) {
        super(source);
        this.bid = bid;
    }

    public Bid getBid() { return bid; }
}