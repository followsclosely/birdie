package org.mlw.birdie.engine.event;

import org.mlw.birdie.Card;

public class CardPlayedEvent extends BasicEvent {
    private Card card;

    public CardPlayedEvent(Object source, Card card, int seat) {
        super(source, seat);
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
