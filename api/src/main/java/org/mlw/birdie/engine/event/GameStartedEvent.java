package org.mlw.birdie.engine.event;

import org.mlw.birdie.GameContext;

public class GameStartedEvent extends BasicEvent {
    private GameContext context;

    public GameStartedEvent(Object source, GameContext context) {
        super(source);
        this.context = context;
    }

    public GameContext getContext() { return context; }
}
