package org.mlw.birdie.engine.client;

import org.mlw.birdie.engine.event.*;

public interface PlayerAdapter {

    void onGameStartedEvent(GameStartedEvent event);

    void onHandDealtEvent(HandDealtEvent event);

    void onBidRequestEvent(BidRequestEvent event);
    void onBidEvent(BidEvent event);
    void onBidWonEvent(BidWonEvent event);
    void onTrumpSelectedEvent(TrumpSelectedEvent event);

    void onTurnEvent(TurnEvent event);
}
