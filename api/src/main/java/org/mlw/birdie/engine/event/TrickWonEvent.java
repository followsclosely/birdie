package org.mlw.birdie.engine.event;

import org.mlw.birdie.Trick;

public class TrickWonEvent extends BasicEvent {
    private Trick trick;

    public TrickWonEvent(Object source, Trick trick) {
        super(source);
        this.trick = trick;
    }

    public Trick getTrick() { return trick; }
}