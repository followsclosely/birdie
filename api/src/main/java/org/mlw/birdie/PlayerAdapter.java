package org.mlw.birdie;

import org.mlw.birdie.engine.event.BidRequestEvent;
import org.mlw.birdie.engine.event.CardPlayedEvent;
import org.mlw.birdie.engine.event.HandDealtEvent;

public interface PlayerAdapter {
    String getName();
    int getSeat();

    void handleKitty(GameContext context);
    Card handleTurn(GameContext context);

    void onBidRequestEvent(BidRequestEvent event);
    void onCardPlayedEvent(CardPlayedEvent event);
    void onHandDealtEvent(HandDealtEvent event);
}