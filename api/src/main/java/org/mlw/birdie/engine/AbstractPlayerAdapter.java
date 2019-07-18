package org.mlw.birdie.engine;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.Card;
import org.mlw.birdie.PlayerAdapter;
import org.mlw.birdie.engine.event.BidRequestEvent;
import org.mlw.birdie.engine.event.HandDealtEvent;

import java.util.List;

public abstract class AbstractPlayerAdapter implements PlayerAdapter {

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
        server.post(object);
    }

    @Override
    public String getName() { return name; }

    @Override
    public int getSeat() {
        return seat;
    }

    @Subscribe
    public void onHandDealtEvent(HandDealtEvent event) {
        this.cards = event.getHand().getCards(this);
    }

    @Subscribe
    public void onBidRequestEvent(BidRequestEvent event) {
        //System.out.println("Seat=" + event.getSeat());
        if (event.getSeat()!=null && event.getSeat() == seat){
            onMyBidRequestEvent(event);
        } else {
            //System.out.println( this.name + ": Not my turn!");
        }
    }
    public abstract void onMyBidRequestEvent(BidRequestEvent event);
}
