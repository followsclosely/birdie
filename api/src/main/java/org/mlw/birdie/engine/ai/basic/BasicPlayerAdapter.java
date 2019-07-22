package org.mlw.birdie.engine.ai.basic;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.Bid;
import org.mlw.birdie.Card;
import org.mlw.birdie.Trick;
import org.mlw.birdie.engine.AbstractPlayerAdapter;
import org.mlw.birdie.engine.event.*;
import org.mlw.birdie.engine.handler.BidEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BasicPlayerAdapter extends AbstractPlayerAdapter {

    private static final Logger log = LoggerFactory.getLogger(BasicPlayerAdapter.class);

    public BasicPlayerAdapter(EventBus server, String name, int seat){
        super(server, name, seat);
    }

    @Subscribe
    public void onTurnEvent(TurnEvent event){
        if( event.getSeat() == seat) {
            Trick trick = event.getTrick();

            //If you are the leader, play the last card in your hand.
            if( trick.getCards().size()==0 ){
                post(new CardPlayedEvent(this, cards.get(cards.size()-1), this.seat));
            }

            else {
                Card lead = trick.getCards().get(0);
                Card card = cards.stream().filter(c -> c.getSuit().equals(lead.getSuit())).findAny().orElse(null);
                if (card != null) {
                    post(new CardPlayedEvent(this, card, this.seat));
                } else {
                    post(new CardPlayedEvent(this, cards.get(cards.size() - 1), this.seat));
                }
            }
        } else {
            log.error("A " + event.getClass() + " was received but not handled.");
        }
    }

    @Subscribe
    public void onBidRequestEvent(BidRequestEvent event) {
        if( event.getSeat() == seat) {
            int maxBid = event.getHand().getMaxBid().getValue();

            if ((maxBid < 120)) {
                post(new BidEvent(this, new Bid(seat, maxBid + 5)));
            } else {
                post(new BidEvent(this, new Bid(seat, null)));
            }
        } else {
            log.error("A " + event.getClass() + " was received but not handled.");
        }
    }

    @Subscribe
    public void onBidWonEvent(BidWonEvent event) {
        if( event.getSeat() == seat) {
            event.getHand().setTrump(Card.Suit.Black);
            post(new TrumpSelectedEvent(this, event.getHand().getKitty(), Card.Suit.Black, seat));
        } else {
            log.error("A " + event.getClass() + " was received but not handled.");
        }
    }
}