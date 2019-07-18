package org.mlw.birdie.engine.event;

import org.mlw.birdie.Bid;
import org.mlw.birdie.PlayerAdapter;

public class BidEvent extends BasicEvent {
    private Bid bid;
    public BidEvent(PlayerAdapter source, Bid bid) {
        super(source);
        this.bid = bid;
    }

    public Bid getBid() { return bid; }
}