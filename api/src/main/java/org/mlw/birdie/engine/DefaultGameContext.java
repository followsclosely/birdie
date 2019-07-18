package org.mlw.birdie.engine;

import org.mlw.birdie.GameContext;
import org.mlw.birdie.Hand;


public class DefaultGameContext implements GameContext {

    private int dealerIndex = 0;
    private int numberOfPlayers;

    private Hand hand = null;

    public DefaultGameContext(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public Hand newHand() {
        return (this.hand = new Hand(4, dealerIndex));
    }

    public Hand getHand() { return hand; }

    public int getNumberOfPlayers() { return numberOfPlayers; }

    public int getDealerIndex() { return dealerIndex; }
}