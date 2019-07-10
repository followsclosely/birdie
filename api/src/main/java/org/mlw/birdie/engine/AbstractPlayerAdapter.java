package org.mlw.birdie.engine;

import java.util.List;

public abstract class AbstractPlayerAdapter implements PlayerAdapter {
    protected String name;
    protected List<Card> cards;

    @Override
    public void handleDeal(GameContext context, List<Card> cards) {
        this.cards = cards;
    }

    @Override
    public String getName() { return name; }
}
