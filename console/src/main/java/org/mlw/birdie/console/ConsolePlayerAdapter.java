package org.mlw.birdie.console;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.mlw.birdie.Bid;
import org.mlw.birdie.Card;
import org.mlw.birdie.Trick;
import org.mlw.birdie.engine.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ConsolePlayerAdapter extends AbstractConsolePlayerAdapter {

    private static final Logger log = LoggerFactory.getLogger(ConsolePlayerAdapter.class);

    protected List<Card> cards;

    public ConsolePlayerAdapter(EventBus server) {
        this(server, null);
    }
    public ConsolePlayerAdapter(EventBus server, EventBus next) {
        super(server, next);

        //System.out.println("Enter your Name: ");
        //this.name = readLine();
        if( this.name == null || this.name.trim().length() == 0){
            this.name = "Console0";
        }
    }

    @Subscribe
    public void onHandDealtEvent(HandDealtEvent event) {
        this.passEventDown(event);
        this.cards = new ArrayList<>(event.getCards());
        System.out.println(this.cards);
    }

    @Subscribe
    public void onBidRequestEvent(BidRequestEvent event) {
        this.passEventDown(event);
        int maxBid = event.getHand().getMaxBid().getValue();
        System.out.println("  Enter bid greater than or equal to " + (maxBid+5) + " (leave blank to pass): ");
        Integer bid = readInteger();

        post(new BidEvent(this, new Bid(seat, bid)));
    }

    @Subscribe
    public void onBidEvent(BidEvent event) {
        this.passEventDown(event);
        System.out.println(String.format("  Player%d bid %d", event.getBid().getSeat(), event.getBid().getValue()));
    }

    @Subscribe
    public void onBidWonEvent(BidWonEvent event) {
        this.passEventDown(event);

        //Set the cards locally...
        this.cards.clear();
        this.cards = new ArrayList<>(event.getHand().getCards(this.getSeat()));

        List<Card> kitty = new ArrayList<>();
        //List<Card> cards = new ArrayList<>(event.getHand().getCards(this.getSeat()));

        System.out.println("Select a card to place back into the kitty  (ie: 1,3,5,7,9): ");
        for(int i=0, length=cards.size(); i<length; i++){
            if( event.getHand().getKitty().contains(cards.get(i))) {
                System.out.print(" *" + String.format("%02d", i) + "* ");
            } else {
                System.out.print("  " + String.format("%02d", i) + "  ");
            }
        }

        kitty.clear();
        System.out.println();

        System.out.println(cards);

        String line = readLine();

        for(String value : line.split(",")){
            kitty.add(cards.get(Integer.parseInt(value)));
        }

        System.out.println(cards + "  |  " + kitty);

        System.out.println("Select Trump: ");
        for(Card.Suit suit : Card.Suit.values()){
            System.out.println("  " + suit.ordinal() + ": " + suit);
        }
        Card.Suit trump = Card.Suit.values()[Integer.parseInt(readLine())];

        post(new TrumpSelectedEvent(this, kitty, trump, seat));
    }

    @Subscribe
    public void onTurnEvent(TurnEvent event){
        this.passEventDown(event);
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
    public void onTrumpSelectedEvent(TrumpSelectedEvent event){
        this.passEventDown(event);
        this.cards.removeAll(event.getKitty());
    }

    @Subscribe
    public void onCardPlayedEvent(CardPlayedEvent event){
        this.cards.remove(event.getCard());
        this.passEventDown(event);
    }

    @Subscribe
    public void handleDeadEvent(DeadEvent deadEvent) {
        this.passEventDown(deadEvent.getEvent());
    }
}