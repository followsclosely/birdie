package org.mlw.birdie.console;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.Bid;
import org.mlw.birdie.Card;
import org.mlw.birdie.Hand;
import org.mlw.birdie.Trick;
import org.mlw.birdie.engine.AbstractPlayerAdapter;
import org.mlw.birdie.engine.event.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsolePlayerAdapter extends AbstractPlayerAdapter {

    public ConsolePlayerAdapter(EventBus server) throws IOException {
        super(server);
        this.seat = 0;
        System.out.println("Enter your Name: ");
        this.name = readLine();
        if( this.name == null || this.name.trim().length() == 0){
            this.name = "Player 0";
        }
    }

    private BufferedReader reader = null;
    private synchronized String readLine() {
        if( reader==null ){
            reader = new BufferedReader(new InputStreamReader(System.in));
        }
        try {
            return reader.readLine();
        } catch (Exception ignore){
            return readLine();
        }
    }

    @Subscribe
    public void onBidRequestEvent(BidRequestEvent event) {

        int maxBid = event.getHand().getMaxBid().getValue();

        System.out.println("Enter bid greater than or equal to " + (maxBid+5) + " (leave blank to pass): ");
        String line = readLine();
        Integer bid = ( line != null && line.trim().length() > 0) ? Integer.parseInt(line) : null;

        post(new BidEvent(this, new Bid(seat, bid)));
    }

    @Subscribe
    public void onBidWonEvent(BidWonEvent event) {

        Hand hand = event.getHand();

        List<Card> kitty = new ArrayList<>(hand.getKitty());
        List<Card> cards = new ArrayList<>(hand.getCards(this));
        cards.addAll(kitty);
        Collections.sort(cards);

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

        String line = readLine();

        int delta = 0;
        for(String value : line.split(",")){
            kitty.add(cards.remove(Integer.parseInt(value) - delta++));
        }

        System.out.println(cards + "  |  " + kitty);

        System.out.println("Select Trump: ");
        for(Card.Suit suit : Card.Suit.values()){
            System.out.println("  " + suit.ordinal() + ": " + suit);
        }
        hand.setTrump(Card.Suit.values()[Integer.parseInt(readLine())]);

        System.out.println( hand.getTrump() + " is Trump!");

        hand.getKitty().clear();
        hand.getKitty().addAll(kitty);

        hand.getCards(this).clear();
        hand.getCards(this).addAll(cards);

        super.post(new TrumpSelectedEvent(this, hand.getKitty(), hand.getTrump(), seat));
    }

    @Subscribe
    public void onTurnEvent(TurnEvent event){
        Trick trick = event.getTrick();
        System.out.println("Cards played: " + trick.getCards());

        for(int i=0, length=cards.size(); i<length; i++){
            System.out.print("  "+ String.format("%02d", i) + "  ");
        }
        System.out.println("\n" + cards);

        System.out.println("Select a Card: ");
        Card card = cards.get(Integer.parseInt(readLine()));
        post(new CardPlayedEvent(this, card, this.seat));
    }

    @Subscribe
    public void onCardPlayedEvent(CardPlayedEvent event) {
        System.out.println("########### " + name + "############## " + event);
    }
}