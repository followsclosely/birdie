package org.mlw.birdie;

import java.util.ArrayList;
import java.util.List;

public class DeckFactory {
    public static Deck getStandardDeck(){
        List<Card> cards = new ArrayList<>();
        for(int i=1; i<15; i++){
            for(Card.Suit suit : Card.Suit.values()) {
                cards.add(new Card(suit, i==1?15:i, i, i==1?15:i==5?5:i==10?10:i==14?10:0));
            }
        }
        //Add the rook card.
        cards.add(new Card(null, 0, 0, 20));
        return new Deck(cards);
    }
}