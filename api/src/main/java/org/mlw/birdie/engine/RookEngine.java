package org.mlw.birdie.engine;

import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.*;
import org.mlw.birdie.engine.event.*;
import org.mlw.birdie.engine.handler.BidEventHandler;
import org.mlw.birdie.engine.handler.CardPlayedEventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RookEngine {

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

        System.out.println("Starting Game...");
        this.clients.post(new GameStartedEvent(this, this.context));

        System.out.println("Starting Hand...");

        Hand hand = this.context.newHand();

        deck.shuffle();
        List<Card>[] dealerPiles = new List[context.getNumberOfPlayers()+1];
        for(int i=0, length=context.getNumberOfPlayers(); i<length; i++){
            dealerPiles[i] = new ArrayList<>();
        }
        dealerPiles[this.context.getNumberOfPlayers()] = new ArrayList<>(5);

        System.out.println("  Dealing Cards...");
        int i = 0;
        for(Card card : deck.getCards())
        {
            if ( dealerPiles[this.context.getNumberOfPlayers()].size() <5 ){
                dealerPiles[this.context.getNumberOfPlayers()].add(card);
            }
            else {
                dealerPiles[i++].add(card);
                i = i%4;
            }
        }

        Collections.sort(dealerPiles[this.context.getNumberOfPlayers()]);
        hand.getKitty().addAll(dealerPiles[this.context.getNumberOfPlayers()]);
        System.out.println("    Cat: " + dealerPiles[this.context.getNumberOfPlayers()]);

        for(i=0; i<context.getNumberOfPlayers(); i++){
            Collections.sort(dealerPiles[i]);
            System.out.println("    " + this.clients.players[i].getName() + ": " + dealerPiles[i]);
            this.context.getHand().getCards(this.clients.players[i]).clear();
            this.context.getHand().getCards(this.clients.players[i]).addAll(dealerPiles[i]);
        }

        this.bidEventHandler = new BidEventHandler(clients, context);

        this.clients.post(new HandDealtEvent(hand));

        int currentBidder = (context.getDealerIndex()+1)%context.getNumberOfPlayers();
        this.clients.post(new BidRequestEvent(this, context.getHand(), currentBidder), currentBidder);
    }

    @Subscribe
    public void onBidEvent(BidEvent event){
        this.bidEventHandler.onBidEvent(event);
    }

    @Subscribe
    public void onTrumpSelectedEvent(TrumpSelectedEvent event){
        System.out.println("############################################################");
    }

    public void processHand(DefaultGameContext context) {
        Hand hand = context.getHand();

        Card.Suit trump = hand.getTrump();

        int leader = hand.getMaxBid().getSeat();

        while (context.getHand().getCards(clients.players[leader]).size()>0) {
            Trick trick = hand.createTrick(leader);
            for (int i = 0; i < context.getNumberOfPlayers(); i++) {
                PlayerAdapter player = clients.players[(i + leader) % context.getNumberOfPlayers()];

                Card card = null; //player.handleTurn(context);

                //todo: this does not feel right...
                context.getHand().getCards(player).remove(card);

                trick.getCards().add(card);


                clients.post(new CardPlayedEvent(hand));


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

            System.out.println(trick.getCards() + " " + clients.getName(leader) + " won the trick with a " + trick.getCards().get(winner));
            System.out.println();
        }

        //Print a summary of all the tricks.
        for(Trick trick : hand.getTricks()) {
            System.out.print(clients.getName(trick.getLeader()) + " ");
            for(int i=0; i<context.getNumberOfPlayers(); i++){
                int index = (i+trick.getLeader()) % context.getNumberOfPlayers();
                char x = ( trick.getWinner() == index ? '*' : ' ');
                System.out.print( "|" + x + trick.getCards().get(index) + x);
            }
            System.out.print("| " + clients.getName((trick.getLeader() + trick.getWinner()) % context.getNumberOfPlayers()));
            System.out.println(" " + trick.getPoints() + " points");
        }

        int[] scores = hand.getScores();

        int total=0;
        for(int i=0; i<context.getNumberOfPlayers(); i++){
            total+=scores[i];
            System.out.println("Player" + i + ": " + scores[i] + " points.");
        }

        System.out.println("Total:    " + total + " points.");
    }
}