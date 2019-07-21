package org.mlw.birdie.engine.ai.basic;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.Bid;
import org.mlw.birdie.Card;
import org.mlw.birdie.Trick;
import org.mlw.birdie.engine.AbstractPlayerAdapter;
import org.mlw.birdie.engine.event.*;



public class BasicPlayerAdapter extends AbstractPlayerAdapter {
    public BasicPlayerAdapter(EventBus server, String name, int seat){
        super(server, name, seat);
    }

    @Subscribe
    public void onTurnEvent(TurnEvent event){
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
    }

    @Subscribe
    public void onBidRequestEvent(BidRequestEvent event) {
        int maxBid = event.getHand().getMaxBid().getValue();

        if ((maxBid < 120)) {
            post(new BidEvent(this, new Bid(seat, maxBid + 5)));
        } else {
            post(new BidEvent(this, new Bid(seat, null)));
        }
    }

    @Subscribe
    public void onBidWonEvent(BidWonEvent event) {
        event.getHand().setTrump(Card.Suit.Black);
        super.post(new TrumpSelectedEvent(this, event.getHand().getKitty(), Card.Suit.Black, seat));
    }
}