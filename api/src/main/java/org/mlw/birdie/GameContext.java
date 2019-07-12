package org.mlw.birdie;

import java.util.List;

public interface GameContext {

    int getNumberOfPlayers();

    List<Card> getCards(PlayerAdapter player);

    Hand getHand();
}
