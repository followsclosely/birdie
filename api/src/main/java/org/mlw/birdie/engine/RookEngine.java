package org.mlw.birdie.engine;

import java.util.ArrayList;
import java.util.List;

public class RookEngine {
    private Deck deck = null;
    private List<PlayerAdapter> players = new ArrayList<>();

    public void addPlayer(PlayerAdapter player){
        System.out.println("Adding player: " + player.getName());
        players.add(player);
    }

    public void setDeck(Deck deck) { this.deck = deck; }

    public void startGame(){
        System.out.println("Starting Game...");
    }

    public void startHand(){
        System.out.println("Starting Hand...");

        deck.shuffle();
        List<Card>[] dealerPiles = new List[this.players.size()];
        for(int i=0; i<this.players.size(); i++){
            dealerPiles[i] = new ArrayList<>();
        }

        System.out.println("  Dealing Cards...");
        List<Card> cat = new ArrayList<>(5);
        int i = 0;
        for(Card card : deck.getCards())
        {
            if ( cat.size() <5 ){
                cat.add(card);
            }
            else {
                dealerPiles[i++].add(card);
                i = i%4;
            }
        }

        cat.sort(DeckFactory.getCardComparator());
        System.out.println("    Cat: " + cat);
        for(i=0; i<this.players.size(); i++){
            dealerPiles[i].sort(DeckFactory.getCardComparator());
            System.out.println("    " + players.get(i).getName() + ": " + dealerPiles[i]);
            players.get(i).handleDeal(null, dealerPiles[i]);
        }
    }
}