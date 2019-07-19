package org.mlw.birdie.engine.event;

import org.mlw.birdie.Card;

import java.util.List;

public class TrumpSelectedEvent extends BasicEvent {

    private List<Card> kitty;
    private Card.Suit trump;

    public TrumpSelectedEvent(Object source, List<Card> kitty, Card.Suit trump, Integer seat) {
        super(source, seat);
        this.kitty = kitty;
        this.trump = trump;
    }

    public List<Card> getKitty() { return kitty; }
    public Card.Suit getTrump() { return trump; }
}
