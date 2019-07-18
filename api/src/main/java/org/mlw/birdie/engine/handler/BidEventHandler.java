package org.mlw.birdie.engine.handler;

import com.google.common.eventbus.EventBus;
import org.mlw.birdie.Bid;
import org.mlw.birdie.GameContext;
import org.mlw.birdie.Hand;
import org.mlw.birdie.engine.event.BidEvent;
import org.mlw.birdie.engine.event.BidRequestEvent;

import java.util.ArrayList;
import java.util.List;


public class BidEventHandler {

    private GameContext context = null;
    private EventBus eventBus = null;

    private int bidderIndex;
    private int biddersLeft;
    private Bid previousBid = null;
    private List<Bid> lastBid = null;

    public BidEventHandler(EventBus eventBus, GameContext context) {
        this.eventBus = eventBus;
        this.context = context;
        this.biddersLeft = context.getNumberOfPlayers();
        this.bidderIndex = (context.getHand().getDealerIndex()+1)%context.getNumberOfPlayers();
        this.lastBid = new ArrayList<>(context.getNumberOfPlayers());
        for(int i=0; i<context.getNumberOfPlayers(); i++){
            lastBid.add(new Bid(i, 0));
        }
    }

    public void onBidEvent(BidEvent event) {
        Hand hand = context.getHand();
        Bid bid = event.getBid();

        //Is the correct person bidding?
        if (bidderIndex != bid.getSeat()) {
            System.out.println(String.format(" %d, it is not your turn!", bid.getSeat()));
            return;
        }

        if (previousBid == null || bid.getValue() == null || bid.getValue() > previousBid.getValue()) {
            if (bid.getValue() != null) {
                previousBid = bid;
            } else {
                biddersLeft--;
            }

            lastBid.get(bid.getSeat()).setValue(bid.getValue());
            context.getHand().getBids().add(bid);

            //Calculate who is bidding next.
            for (int i = 1; i < context.getNumberOfPlayers(); i++) {
                int nextIndex = (bidderIndex + i) % context.getNumberOfPlayers();
                if (lastBid.get(nextIndex).getValue() != null) {
                    this.bidderIndex = nextIndex;
                    break;
                }
            }

            if (this.biddersLeft == 1) {
                System.out.println("Player " + hand.getMaxBid().getSeat() + " won the bid!");
                //eventBus.post(new );
                return;
            }

            eventBus.post(new BidRequestEvent(this, hand, this.bidderIndex));
        }
    }
}