package org.mlw.birdie.engine.handler;

import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.Bid;
import org.mlw.birdie.GameContext;
import org.mlw.birdie.Hand;
import org.mlw.birdie.Trick;
import org.mlw.birdie.engine.ClientEventBroker;
import org.mlw.birdie.engine.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class BidEventHandler {

    private static final Logger log = LoggerFactory.getLogger(BidEventHandler.class);

    private GameContext context;
    private ClientEventBroker clients;

    private int bidderIndex;
    private int biddersLeft;
    private Bid previousBid = null;
    private List<Bid> lastBid = null;

    public BidEventHandler(ClientEventBroker clients, GameContext context) {
        this.clients = clients;
        this.context = context;
    }

    @Subscribe
    public void onHandDealtEvent(HandDealtEvent x){
        this.previousBid = null;
        this.biddersLeft = context.getNumberOfPlayers();
        this.bidderIndex = context.getHand() == null ? 1 : (context.getHand().getDealerIndex()+1)%context.getNumberOfPlayers();
        this.lastBid = new ArrayList<>(context.getNumberOfPlayers());
        for(int i=0; i<context.getNumberOfPlayers(); i++){
            lastBid.add(new Bid(i, 0));
        }
    }

    @Subscribe
    public void onBidEvent(BidEvent event) {
        Hand hand = context.getHand();
        Bid bid = event.getBid();

        //Is the correct person bidding?
        if (bidderIndex != bid.getSeat()) {
            log.info(String.format(" %d, it is not your turn!", bid.getSeat()));
            return;
        }

        if (previousBid == null || bid.getValue() == null || bid.getValue() > previousBid.getValue()) {
            if (bid.getValue() != null) {
                previousBid = bid;
            } else {
                biddersLeft--;
            }

            log.info(String.format("  Player%d bid %d", bid.getSeat(), bid.getValue()));
            lastBid.get(bid.getSeat()).setValue(bid.getValue());
            context.getHand().getBids().add(bid);
            //Forward this event to the clients for consumption...
            clients.post(event);

            //Calculate who is bidding next.
            for (int i = 1; i < context.getNumberOfPlayers(); i++) {
                int nextIndex = (bidderIndex + i) % context.getNumberOfPlayers();
                if (lastBid.get(nextIndex).getValue() != null) {
                    this.bidderIndex = nextIndex;
                    break;
                }
            }

            if (this.biddersLeft == 1) {
                Bid winner = hand.getMaxBid();
                log.info(String.format("  Player%d won the bid for %d points!", winner.getSeat(), winner.getValue()));
                clients.post(new BidWonEvent(this, hand, winner.getSeat()), winner.getSeat());

                Trick trick = hand.createTrick(winner.getSeat());
                clients.post(new TurnEvent(this, trick, winner.getSeat()), winner.getSeat());
                return;
            }

            clients.post(new BidRequestEvent(this, hand, this.bidderIndex), this.bidderIndex);
        }
    }
}