package org.mlw.birdie.engine.event;

import org.mlw.birdie.GameContext;

public class CheatEvent {
    private GameContext context;

    public CheatEvent(GameContext context) {
        this.context = context;
    }

    public GameContext getContext() {
        return context;
    }
}
