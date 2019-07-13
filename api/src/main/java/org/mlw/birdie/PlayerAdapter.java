package org.mlw.birdie;

public interface PlayerAdapter {
    String getName();
    int getSeat();
    void handleDeal(GameContext context);
    Bid handleBid(GameContext context);
    void handleKitty(GameContext context);
    Card handleTurn(GameContext context);
}