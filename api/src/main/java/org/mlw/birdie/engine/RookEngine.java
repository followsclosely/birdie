package org.mlw.birdie.engine;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.*;
import org.mlw.birdie.engine.event.*;
import org.mlw.birdie.engine.handler.BidEventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class RookEngine {

    private final Deck deck;
    private final PlayerAdapter[] players;
    private DefaultGameContext context;

    private EventBus serverBus = new AsyncEventBus(Executors.newSingleThreadExecutor());
    private EventBus clientBus = new AsyncEventBus(Executors.newSingleThreadExecutor());

    private BidEventHandler bidEventHandler = null;

    public RookEngine(Deck deck, int numberOfPlayers) {
        this.deck = deck;
        this.players = new PlayerAdapter[numberOfPlayers];
        this.serverBus.register(this);
    }

    public void addPlayer(PlayerAdapter player){
        for(int i=0, length=players.length; i<length; i++){
            if( players[i] == null){
                System.out.println("Adding player: " + player.getName());
                players[i] = player;
                clientBus.register(player);
                return;
            }
        }
    }

    public DefaultGameContext createGame(){
        System.out.println("Starting Game...");
        this.context = new DefaultGameContext(players.length);
        this.clientBus.post(new GameStartedEvent(this, this.context));
        return this.context;
    }

    public void startGame() {
        System.out.println("Starting Hand...");

        Hand hand = context.newHand();

        deck.shuffle();
        List<Card>[] dealerPiles = new List[context.getNumberOfPlayers()+1];
        for(int i=0, length=this.players.length; i<length; i++){
            dealerPiles[i] = new ArrayList<>();
        }
        dealerPiles[this.players.length] = new ArrayList<>(5);

        System.out.println("  Dealing Cards...");
        int i = 0;
        for(Card card : deck.getCards())
        {
            if ( dealerPiles[this.players.length].size() <5 ){
                dealerPiles[this.players.length].add(card);
            }
            else {
                dealerPiles[i++].add(card);
                i = i%4;
            }
        }

        Collections.sort(dealerPiles[context.getNumberOfPlayers()]);
        hand.getKitty().addAll(dealerPiles[context.getNumberOfPlayers()]);
        System.out.println("    Cat: " + dealerPiles[context.getNumberOfPlayers()]);

        for(i=0; i<this.players.length; i++){
            Collections.sort(dealerPiles[i]);
            System.out.println("    " + players[i].getName() + ": " + dealerPiles[i]);
            context.getHand().getCards(players[i]).clear();
            context.getHand().getCards(players[i]).addAll(dealerPiles[i]);
        }

        this.bidEventHandler = new BidEventHandler(clientBus, context);
        clientBus.post(new HandDealtEvent(hand));
        clientBus.post(new BidRequestEvent(this, context.getHand(), (context.getDealerIndex()+1)%context.getNumberOfPlayers()));
    }


    @Subscribe
    public void onBidEvent(BidEvent event){
        this.bidEventHandler.onBidEvent(event);
    }

    public void processKitty(DefaultGameContext context) {
        Bid bid = context.getHand().getMaxBid();
        players[bid.getSeat()].handleKitty(context);
    }

    public void processHand(DefaultGameContext context) {
        Hand hand = context.getHand();

        Card.Suit trump = hand.getTrump();

        int leader = hand.getMaxBid().getSeat();

        while (context.getHand().getCards(players[leader]).size()>0) {
            Trick trick = hand.createTrick(leader);
            for (int i = 0; i < context.getNumberOfPlayers(); i++) {
                PlayerAdapter player = players[(i + leader) % context.getNumberOfPlayers()];

                Card card = player.handleTurn(context);

                //todo: this does not feel right...
                context.getHand().getCards(player).remove(card);

                trick.getCards().add(card);


                clientBus.post(new CardPlayedEvent(hand));


                System.out.println(player.getName() + " played " + card);
            }

            Card lead = trick.getCards().get(0);
            Card leadTrump = null;
            int winner = 0;

            for (int i = 1; i < context.getNumberOfPlayers(); i++) {
                Card card = trick.getCards().get(i);

                if( !hand.isTrump(lead) && hand.isTrump(card)){
                    if( leadTrump == null || leadTrump.getRank() < card.getRank() ){
                        leadTrump = card;
                        trick.setWinner(winner = i);
                    }
                }
                else if (leadTrump ==null && lead.getSuit().equals(card.getSuit()) && lead.getRank() < card.getRank()) {
                    trick.setWinner(winner = i);
                    lead = card;
                }
            }

            leader = (leader + winner) % context.getNumberOfPlayers();

            System.out.println(trick.getCards() + " " + players[leader].getName() + " won the trick with a " + trick.getCards().get(winner));
            System.out.println();
        }

        //Print a summary of all the tricks.
        for(Trick trick : hand.getTricks()) {
            System.out.print(players[trick.getLeader()].getName() + " ");
            for(int i=0; i<context.getNumberOfPlayers(); i++){
                int index = (i+trick.getLeader()) % context.getNumberOfPlayers();
                char x = ( trick.getWinner() == index ? '*' : ' ');
                System.out.print( "|" + x + trick.getCards().get(index) + x);
            }
            System.out.print("| " + players[(trick.getLeader() + trick.getWinner()) % context.getNumberOfPlayers()].getName());
            System.out.println(" " + trick.getPoints() + " points");
        }

        int[] scores = hand.getScores();

        int total=0;
        for(int i=0; i<context.getNumberOfPlayers(); i++){
            total+=scores[i];
            System.out.println(players[i].getName() + ": " + scores[i] + " points.");
        }

        System.out.println("Total:    " + total + " points.");
    }

    public EventBus getEventBus() {
        return serverBus;
    }


}