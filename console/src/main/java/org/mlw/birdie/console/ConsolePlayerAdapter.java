package org.mlw.birdie.console;

import org.mlw.birdie.*;
import org.mlw.birdie.engine.AbstractPlayerAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ConsolePlayerAdapter extends AbstractPlayerAdapter {

    public ConsolePlayerAdapter() throws IOException {
        this.seat = 0;
        System.out.println("Enter your Name: ");
        this.name = readline();
        if( this.name == null || this.name.trim().length() == 0){
            this.name = "Player 0";
        }
    }

    @Override
    public Bid handleBid(GameContext context) {

        int maxBid = context.getHand().getMaxBid().getValue();

        System.out.println("Enter bid greater than or equal to " + maxBid + " (leave blank to pass): ");
        String line = readline();
        Integer bid = ( line != null && line.trim().length() > 0) ? Integer.parseInt(line) : null;
        return new Bid(seat, bid);
    }

    @Override
    public void handleKitty(GameContext context) {

        List<Card> kitty = new ArrayList<>(context.getHand().getKitty());
        List<Card> cards = new ArrayList<>(context.getCards(this));
        cards.addAll(kitty);
        cards.sort(DeckFactory.getCardComparator());

        System.out.println("Select a card to place back into the kitty  (ie: 1,3,5,7,9): ");
        for(int i=0, length=cards.size(); i<length; i++){
            if( kitty.contains(cards.get(i))) {
                System.out.print(" *" + String.format("%02d", i) + "* ");
            } else {
                System.out.print("  " + String.format("%02d", i) + "  ");
            }
        }

        kitty.clear();
        System.out.println();

        System.out.println(cards);

        String line = readline();

        int delta = 0;
        for(String value : line.split(",")){
            kitty.add(cards.remove(Integer.parseInt(value) - delta++));
        }

        System.out.println(cards + "  |  " + kitty);

        System.out.println("Select Trump: ");
        for(Card.Suit suit : Card.Suit.values()){
            System.out.println("  " + suit.ordinal() + ": " + suit);
        }
        context.getHand().setTrump(Card.Suit.values()[Integer.parseInt(readline())]);

        System.out.println( context.getHand().getTrump() + " is Trump!");

        context.getHand().getKitty().clear();
        context.getHand().getKitty().addAll(kitty);

        context.getCards(this).clear();
        context.getCards(this).addAll(cards);


    }

    @Override
    public Card handleTurn(GameContext context) {
        Trick trick = context.getHand().getTrick();
        System.out.println("Cards played: " + trick.getCards());

        for(int i=0, length=cards.size(); i<length; i++){
            System.out.print("  "+ String.format("%02d", i) + "  ");
        }
        System.out.println();
        System.out.println(cards);


        System.out.println("Select a Card: ");

        return cards.get(Integer.parseInt(readline()));
    }

    private BufferedReader reader = null;
    private synchronized String readline() {
        if( reader==null ){
            reader = new BufferedReader(new InputStreamReader(System.in));
        }
        try {
            return reader.readLine();
        } catch (Exception ignore){
            return readline();
        }
    }
}