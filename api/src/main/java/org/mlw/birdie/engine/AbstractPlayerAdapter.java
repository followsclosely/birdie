package org.mlw.birdie.engine;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.Card;
import org.mlw.birdie.engine.event.HandDealtEvent;

import java.util.List;

public abstract class AbstractPlayerAdapter {

    private EventBus server;
    protected String name;
    protected int seat;
    protected List<Card> cards;

    public AbstractPlayerAdapter(EventBus server) {
        this.server = server;
    }
    public AbstractPlayerAdapter(EventBus server, String name, int seat) {
        this(server);
        this.name = name;
        this.seat = seat;
    }

    public void post(Object object){
        if( server != null && object != null) {
            server.post(object);
        }
    }

    @Subscribe
    public void onHandDealtEvent(HandDealtEvent event) {
        this.cards = event.getCards();
    }

    public String getName() { return name; }
    public int getSeat() {
        return seat;
    }
}
