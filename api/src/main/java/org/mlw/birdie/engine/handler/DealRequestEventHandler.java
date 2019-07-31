package org.mlw.birdie.engine.handler;

import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.Card;
import org.mlw.birdie.Deck;
import org.mlw.birdie.Hand;
import org.mlw.birdie.engine.ClientEventBroker;
import org.mlw.birdie.engine.DefaultGameContext;
import org.mlw.birdie.engine.event.BidRequestEvent;
import org.mlw.birdie.engine.event.DealRequestEvent;
import org.mlw.birdie.engine.event.HandDealtEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DealRequestEventHandler {

    private static final Logger log = LoggerFactory.getLogger(DealRequestEventHandler.class);

    private final Deck deck;
    private final ClientEventBroker clients;
    private final DefaultGameContext context;

    public DealRequestEventHandler(ClientEventBroker clients, DefaultGameContext context, Deck deck) {
        this.clients = clients;
        this.context = context;
        this.deck = deck;
    }

    @Subscribe
    public void onDealRequestEvent(DealRequestEvent event){
        log.info("Starting Hand...");

        Hand hand = this.context.newHand();

        deck.shuffle();
        List<Card>[] dealerPiles = new List[context.getNumberOfPlayers()+1];
        for(int i=0, length=context.getNumberOfPlayers(); i<length; i++){
            dealerPiles[i] = new ArrayList<>();
        }
        dealerPiles[this.context.getNumberOfPlayers()] = new ArrayList<>(5);

        log.info("  Dealing Cards...");
        int i = 0;
        for(Card card : deck.getCards())
        {
            if ( dealerPiles[this.context.getNumberOfPlayers()].size() <5 ){
                dealerPiles[this.context.getNumberOfPlayers()].add(card);
            }
            else {
                dealerPiles[i].add(card);
                i = (i+1)%this.context.getNumberOfPlayers();
            }
        }

        Collections.sort(dealerPiles[this.context.getNumberOfPlayers()]);
        hand.getKitty().addAll(dealerPiles[this.context.getNumberOfPlayers()]);
        log.info("    Cat: " + dealerPiles[this.context.getNumberOfPlayers()]);

        for(i=0; i<context.getNumberOfPlayers(); i++){
            Collections.sort(dealerPiles[i]);
            log.info("    " + this.clients.players[i].getName() + ": " + dealerPiles[i]);
            this.context.getHand().getCards(i).clear();
            this.context.getHand().getCards(i).addAll(dealerPiles[i]);

            this.clients.post(new HandDealtEvent(hand, dealerPiles[i]), i);
        }

        this.clients.postToServer(new HandDealtEvent(null, null));

        int currentBidder = (context.getDealerIndex()+1)%context.getNumberOfPlayers();
        this.clients.post(new BidRequestEvent(this, context.getHand(), currentBidder), currentBidder);
    }
}
