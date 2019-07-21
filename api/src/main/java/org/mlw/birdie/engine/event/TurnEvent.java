package org.mlw.birdie.engine.event;

import org.mlw.birdie.Trick;

public class TurnEvent extends BasicEvent {

    private Trick trick;

    public TurnEvent(Object source, Trick trick, Integer seat) {
        super(source, seat);
        this.trick = trick;
    }

    public Trick getTrick() {
        return trick;
    }
}
