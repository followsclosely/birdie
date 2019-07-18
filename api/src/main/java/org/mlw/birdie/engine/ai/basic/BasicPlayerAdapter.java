package org.mlw.birdie.engine.ai.basic;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.Bid;
import org.mlw.birdie.Card;
import org.mlw.birdie.GameContext;
import org.mlw.birdie.Trick;
import org.mlw.birdie.engine.AbstractPlayerAdapter;
import org.mlw.birdie.engine.event.BidEvent;
import org.mlw.birdie.engine.event.BidRequestEvent;
import org.mlw.birdie.engine.event.CardPlayedEvent;


public class BasicPlayerAdapter extends AbstractPlayerAdapter {
    public BasicPlayerAdapter(EventBus server, String name, int seat){
        super(server, name, seat);
    }

    @Override
    public void handleKitty(GameContext context) {
        context.getHand().setTrump(Card.Suit.Black);
    }

    @Override
    public Card handleTurn(GameContext context){
        Trick trick = context.getHand().getTrick();

        //If you are the leader, play the last card in your hand.
        if( trick.getCards().size()==0 ){
            return cards.remove(cards.size()-1);
        }

        Card lead = trick.getCards().get(0);
        Card card = cards.stream().filter(c -> c.getSuit().equals(lead.getSuit())).findAny().orElse(null);
        if( card != null ){
            cards.remove(card);
            return card;
        }

        return cards.remove(0);
    }

    public BasicPlayerAdapter(EventBus server) {
        super(server);
    }

    public void onMyBidRequestEvent(BidRequestEvent event) {
        int maxBid = event.getHand().getMaxBid().getValue();

        if ((maxBid < 120)) {
            System.out.println(String.format("  Player %s bid %d", name, maxBid + 5));
            post(new BidEvent(this, new Bid(seat, maxBid + 5)));
        } else {
            System.out.println(String.format("  Player %s passed", name));
            post(new BidEvent(this, new Bid(seat, null)));
        }

    }

    @Subscribe
    public void onCardPlayedEvent(CardPlayedEvent event) {
    }
}