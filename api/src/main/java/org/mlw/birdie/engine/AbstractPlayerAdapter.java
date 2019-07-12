package org.mlw.birdie.engine;

import org.mlw.birdie.Card;
import org.mlw.birdie.GameContext;
import org.mlw.birdie.PlayerAdapter;

import java.util.List;

public abstract class AbstractPlayerAdapter implements PlayerAdapter {
    protected String name;
    protected int seat;
    protected List<Card> cards;

    @Override
    public void handleDeal(GameContext context) {
        this.cards = context.getCards(this);
    }

    @Override
    public String getName() { return name; }

    @Override
    public int getSeat() {
        return seat;
    }
}
