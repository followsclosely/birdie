package org.mlw.birdie.engine.ai.basic;


import org.mlw.birdie.Bid;
import org.mlw.birdie.Card;
import org.mlw.birdie.GameContext;
import org.mlw.birdie.Trick;
import org.mlw.birdie.engine.AbstractPlayerAdapter;

import java.util.List;
import java.util.Optional;

public class BasicPlayerAdapter extends AbstractPlayerAdapter {
    public BasicPlayerAdapter(String name, int seat){
        this.name = name;
        this.seat = seat;
    }

//        int countMax = 0;
//        Card.Suit maxSuit = Card.Suit.Black;
//        for(Card.Suit suit: Card.Suit.values()) {
//            int countSuit = (int) this.cards.stream().filter(card -> card.getSuit() == suit).count();
//            if( countSuit > countMax)
//            {
//                countMax = (int)countSuit;
//                maxSuit = suit;
//            }
//        }
//        return maxSuit;

    @Override
    public Bid handleBid(GameContext context) {
        int maxBid = context.getHand().getMaxBid().getValue();

        if( (maxBid < 120 ) ){
            System.out.println(String.format("  Player %s bid %d", name, maxBid+5));
            return new Bid(seat,maxBid + 5);
        } else {
            System.out.println(String.format("  Player %s passed", name));
            return new Bid(seat,null);
        }
    }

    @Override
    public void handleKitty(GameContext context) {
        context.getHand().setTrump(Card.Suit.Black);
    }

    @Override
    public Card handleTurn(GameContext context){
        Trick trick = context.getHand().getTrick();

        //If you are the leader, play the last card in your hand.
        if( trick.getCards().size()==0 ){
            return cards.remove(cards.size()-1);
        }

        Card lead = trick.getCards().get(0);
        Card card = cards.stream().filter(c -> c.getSuit().equals(lead.getSuit())).findAny().orElse(null);
        if( card != null ){
            cards.remove(card);
            return card;
        }

        return cards.remove(0);
    }
}