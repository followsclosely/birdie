package org.mlw.birdie.engine;

import org.mlw.birdie.*;

import java.util.List;

public class DefaultGameContext implements GameContext {

    private int dealerIndex = 0;
    private int numberOfPlayers;

    private Hand hand = null;

    public DefaultGameContext(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    @Override
    public List<Card> getCards(PlayerAdapter player) {
        return hand.getCards(player);
    }

    public Hand newHand() {
        return (this.hand = new Hand(4, dealerIndex++));
    }

    public Hand getHand() { return hand; }

    public int getNumberOfPlayers() { return numberOfPlayers; }
}