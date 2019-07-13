package org.mlw.birdie.engine;

import org.mlw.birdie.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RookEngine {
    private final Deck deck;
    private final PlayerAdapter[] players;

    public RookEngine(Deck deck, int numberOfPlayers) {
        this.deck = deck;
        this.players = new PlayerAdapter[numberOfPlayers];
    }

    public void addPlayer(PlayerAdapter player){
        for(int i=0, length=players.length; i<length; i++){
            if( players[i] == null){
                System.out.println("Adding player: " + player.getName());
                players[i] = player;
                return;
            }
        }
    }

    public DefaultGameContext createGame(){
        System.out.println("Starting Game...");
        return new DefaultGameContext(players.length);
    }

    public void processDeal(DefaultGameContext context) {
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
            context.getCards(players[i]).clear();
            context.getCards(players[i]).addAll(dealerPiles[i]);
        }

        for(PlayerAdapter player : players){
            player.handleDeal(context);
        }
    }

    public void processBidding(DefaultGameContext context) {
        Hand hand = context.getHand();

        int bidderIndex = (hand.getDealerIndex()+1)%context.getNumberOfPlayers();

        Bid previousBid = null;
        List<PlayerAdapter> bidders = new ArrayList(Arrays.asList(players));
        while (bidders.size() > 1) {
            PlayerAdapter player = bidders.get(bidderIndex);
            Bid bid = player.handleBid(context);

            //If the bid is not higher than the last then do nothing.
            if( previousBid == null || bid.getValue() == null || bid.getValue() > previousBid.getValue() ){

                //If the bid is null then remove the bidder from rest of the bidding process.
                if( bid.getValue() == null){
                    bidders.remove(player);
                }
                else {
                    hand.getBids().add(previousBid = bid);
                    bidderIndex++;
                }

                if (bidderIndex >= bidders.size()){
                    bidderIndex = 0;
                }
            }
        }

        for(Bid bid : context.getHand().getBids())
        {
            System.out.println(String.format(" -- Player %s  bid %d", players[bid.getSeat()].getName(), bid.getValue()));
        }

        Bid bid = context.getHand().getMaxBid();
        System.out.println(String.format("  Player %s won the bid for %d", players[bid.getSeat()].getName(), bid.getValue()));
    }

    public void processKitty(DefaultGameContext context) {
        Bid bid = context.getHand().getMaxBid();
        players[bid.getSeat()].handleKitty(context);
    }

    public void processHand(DefaultGameContext context) {
        Hand hand = context.getHand();

        Card.Suit trump = hand.getTrump();

        int leader = hand.getMaxBid().getSeat();

        while (context.getCards(players[leader]).size()>0) {
            Trick trick = hand.createTrick(leader);
            for (int i = 0; i < context.getNumberOfPlayers(); i++) {
                PlayerAdapter player = players[(i + leader) % context.getNumberOfPlayers()];

                Card card = player.handleTurn(context);

                //todo: this does not feel right...
                context.getCards(player).remove(card);

                trick.getCards().add(card);
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
}