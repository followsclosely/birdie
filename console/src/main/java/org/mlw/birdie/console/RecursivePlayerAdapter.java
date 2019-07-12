package org.mlw.birdie.console;

import org.mlw.birdie.Bid;
import org.mlw.birdie.Card;
import org.mlw.birdie.GameContext;
import org.mlw.birdie.PlayerAdapter;

public class RecursivePlayerAdapter implements PlayerAdapter {

    private PlayerAdapter player;

    public RecursivePlayerAdapter(PlayerAdapter player) {
        this.player = player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public int getSeat() {
        return player.getSeat();
    }

    @Override
    public void handleDeal(GameContext context) {
        try {
            player.handleDeal(context);
        } catch(Exception retry) {
            retry.printStackTrace();
            handleDeal(context);
        }
    }

    @Override
    public Bid handleBid(GameContext context) {
        try {
            return player.handleBid(context);
        } catch(Exception retry) {
            retry.printStackTrace();
            return handleBid(context);
        }
    }

    @Override
    public void handleKitty(GameContext context) {
        try {
            player.handleKitty(context);
        } catch(Exception retry) {
            retry.printStackTrace();
            handleKitty(context);
        }
    }

    @Override
    public Card handleTurn(GameContext context) {
        try {
            return player.handleTurn(context);
        } catch(Exception retry) {
            return handleTurn(context);
        }
    }
}
