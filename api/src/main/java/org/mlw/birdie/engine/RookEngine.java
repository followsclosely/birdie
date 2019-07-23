package org.mlw.birdie.engine;

import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.*;
import org.mlw.birdie.engine.event.*;
import org.mlw.birdie.engine.handler.BidEventHandler;
import org.mlw.birdie.engine.handler.CardPlayedEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RookEngine {

    private static final Logger log = LoggerFactory.getLogger(RookEngine.class);
    
    private final Deck deck;

    private final DefaultGameContext context;
    private final ClientEventBroker clients;

    private BidEventHandler bidEventHandler = null;
    private CardPlayedEventHandler cardPlayedEventHandler = null;

    public RookEngine(Deck deck, ClientEventBroker clients) {
        this.deck = deck;
        this.clients = clients;
        this.context = new DefaultGameContext(clients.getNumberOfSeats());
    }

    public void addPlayer(PlayerAdapter player){
        clients.addPlayer(player);
    }

    public void startGame() {
        log.info("Starting Game...");
        this.clients.post(new GameStartedEvent(this, this.context));
        this.clients.getServer().post(new DealRequestEvent());
    }

    public void startHand() {
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
            this.context.getHand().getCards(this.clients.players[i]).clear();
            this.context.getHand().getCards(this.clients.players[i]).addAll(dealerPiles[i]);
        }

        //Create the EventHandlers
        this.bidEventHandler = new BidEventHandler(clients, context);
        this.cardPlayedEventHandler = new CardPlayedEventHandler(clients, context);

        this.clients.post(new HandDealtEvent(hand));

        int currentBidder = (context.getDealerIndex()+1)%context.getNumberOfPlayers();

        this.clients.post(new BidRequestEvent(this, context.getHand(), currentBidder), currentBidder);
    }

    @Subscribe
    public void onBidEvent(BidEvent event){
        this.bidEventHandler.onBidEvent(event);
    }

    @Subscribe
    public void onCardPlayedEvent(CardPlayedEvent event){ this.cardPlayedEventHandler.onCardPlayedEvent(event); }

    @Subscribe
    public void onTrumpSelectedEvent(TrumpSelectedEvent event){
        log.info("############################################################");
    }

    @Subscribe
    public void onDealRequestEvent(DealRequestEvent event){
        startHand();
    }

    public void xxx(DefaultGameContext context) {
        Hand hand = context.getHand();



        int leader = hand.getMaxBid().getSeat();

        while (context.getHand().getCards(clients.players[leader]).size()>0) {
            Trick trick = hand.createTrick(leader);
            for (int i = 0; i < context.getNumberOfPlayers(); i++) {
                PlayerAdapter player = clients.players[(i + leader) % context.getNumberOfPlayers()];

                Card card = null; //player.handleTurn(context);
                //todo: this does not feel right...
                context.getHand().getCards(player).remove(card);
                trick.getCards().add(card);
                //clients.post(new CardPlayedEvent(hand));
                log.info(player.getName() + " played " + card);
            }


        }

        //Print a summary of all the tricks.
        for(Trick trick : hand.getTricks()) {
            log.info(clients.getName(trick.getLeader()) + " ");
            for(int i=0; i<context.getNumberOfPlayers(); i++){
                int index = (i+trick.getLeader()) % context.getNumberOfPlayers();
                char x = ( trick.getWinner() == index ? '*' : ' ');
                log.info( "|" + x + trick.getCards().get(index) + x);
            }
            log.info("| " + clients.getName((trick.getLeader() + trick.getWinner()) % context.getNumberOfPlayers()));
            log.info(" " + trick.getPoints() + " points");
        }

        int[] scores = hand.getScores();

        int total=0;
        for(int i=0; i<context.getNumberOfPlayers(); i++){
            total+=scores[i];
            log.info("Player" + i + ": " + scores[i] + " points.");
        }

        log.info("Total:    " + total + " points.");
    }
}