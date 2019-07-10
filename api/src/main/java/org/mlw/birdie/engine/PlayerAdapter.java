package org.mlw.birdie.engine;

import java.util.List;

public interface PlayerAdapter {
    String getName();
    void handleDeal(GameContext context, List<Card> cards);
    Card.Suit handleCat(GameContext context, List<Card> cat);
}