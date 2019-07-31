package org.mlw.birdie.engine;

import org.mlw.birdie.GameContext;
import org.mlw.birdie.Hand;

import java.util.ArrayList;
import java.util.List;


public class DefaultGameContext implements GameContext {

    private int dealerIndex = 0;
    private int numberOfPlayers;

    private Hand hand = null;
    private List<Hand> hands = new ArrayList<>();

    public DefaultGameContext(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public synchronized  Hand newHand() {
        hands.add(this.hand = new Hand(numberOfPlayers, dealerIndex));
        return (hand);
    }

    public Hand getHand() { return hand; }
    public List<Hand> getHands() { return hands; }

    public int getNumberOfPlayers() { return numberOfPlayers; }

    public int getDealerIndex() { return dealerIndex; }
}